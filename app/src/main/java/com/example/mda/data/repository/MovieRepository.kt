package com.example.mda.data.repository

import android.util.Log
import com.example.mda.data.local.LocalRepository
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.remote.model.Genre
import com.example.mda.data.remote.model.Movie
import com.example.mda.data.remote.model.MovieResponse
import com.example.mda.data.repository.mappers.toMediaEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class MoviesRepository(
    private val api: TmdbApi,
    private val localRepo: LocalRepository
) {

    /** ---------------------------------------------------------------------
     *  SAFE API CALL  (يحافظ على الكاش في حالة فشل الاتصال)
     *  --------------------------------------------------------------------*/
    private suspend fun safeApiCall(
        apiCall: suspend () -> MovieResponse?,
        fallback: suspend () -> List<MediaEntity>,
        typeFilter: String? = null,
        genreId: Int? = null
    ): List<MediaEntity> {
        return try {
            val response = apiCall()
            if (response != null && !response.results.isNullOrEmpty()) {
                var entities = response.results
                    .filter { it.adult != true }
                    .map { it.toMediaEntity() }

                // إجبار الـ mediaType لو ناقص
                entities = entities.map {
                    if (it.mediaType.isNullOrBlank() && typeFilter != null)
                        it.copy(mediaType = typeFilter)
                    else it
                }

                // فلترة إضافية عند الحاجة
                if (typeFilter != null) entities = entities.filter { it.mediaType == typeFilter }
                if (genreId != null) entities =
                    entities.filter { it.genreIds?.contains(genreId) == true }

                // حفظ في الكاش المحلي
                localRepo.addOrUpdateAllFromApi(entities)
                entities
            } else {
                fallback()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fallback()
        }
    }

    /** ---------------------------------------------------------------------
     *  MOVIES
     *  --------------------------------------------------------------------*/
    suspend fun getPopularMovies(): List<MediaEntity> = safeApiCall(
        apiCall = {
            val res = api.getPopularMovies()
            if (res.isSuccessful) res.body() else null
        },
        fallback = { localRepo.getAll().first().filter { it.mediaType == "movie" } },
        typeFilter = "movie"
    )

    suspend fun getTopRatedMovies(): List<MediaEntity> = safeApiCall(
        apiCall = {
            val res = api.getTopRatedMovies()
            if (res.isSuccessful) res.body() else null
        },
        fallback = { localRepo.getAll().first().filter { it.mediaType == "movie" } },
        typeFilter = "movie"
    )

    suspend fun getMoviesByGenre(genreId: Int, page: Int = 1): List<MediaEntity> = safeApiCall(
        apiCall = {
            val res = api.getMoviesByGenre(genreId, page)
            if (res.isSuccessful) res.body() else null
        },
        fallback = {
            localRepo.getAll().first()
                .filter { it.mediaType == "movie" && it.genreIds?.contains(genreId) == true }
        },
        typeFilter = "movie",
        genreId = genreId
    )

    suspend fun getGenres(): List<Genre> {
        return try {
            val res = api.getGenres()
            if (res.isSuccessful) res.body()?.genres ?: emptyList()
            else emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /** ---------------------------------------------------------------------
     *  TV SHOWS
     *  --------------------------------------------------------------------*/
    suspend fun getPopularTvShows(): List<MediaEntity> = safeApiCall(
        apiCall = {
            val res = api.getPopularTvShows()
            if (res.isSuccessful) res.body() else null
        },
        fallback = { localRepo.getAll().first().filter { it.mediaType == "tv" } },
        typeFilter = "tv"
    ).map {
        if (it.mediaType.isNullOrBlank()) it.copy(mediaType = "tv") else it
    }

    /** ---------------------------------------------------------------------
     *  TRENDING
     *  --------------------------------------------------------------------*/
    suspend fun getTrendingMedia(
        mediaType: String = "all",
        timeWindow: String = "day"
    ): List<MediaEntity> = safeApiCall(
        apiCall = {
            val res = api.getTrendingMedia(mediaType, timeWindow)
            if (res.isSuccessful) res.body() else null
        },
        fallback = { localRepo.getAll().first() }
    )

    /** ---------------------------------------------------------------------
     *  SEARCH
     *  --------------------------------------------------------------------*/
    suspend fun searchMulti(query: String): List<MediaEntity> = safeApiCall(
        apiCall = {
            val res = api.searchMulti(query = query)
            if (res.isSuccessful) res.body() else null
        },
        fallback = {
            val list = localRepo.getAll().first()
            list.filter { (it.title ?: it.name ?: "").contains(query, ignoreCase = true) }
        }
    )

    suspend fun searchByType(query: String, type: String): List<MediaEntity> = safeApiCall(
        apiCall = {
            val res = when (type) {
                "movie" -> api.searchMovies(query)
                "tv" -> api.searchTvShows(query)
                else -> api.searchMulti(query)
            }
            if (res.isSuccessful) res.body() else null
        },
        fallback = {
            val list = localRepo.getAll().first()
            list.filter {
                (it.title ?: it.name ?: "").contains(query, ignoreCase = true) &&
                        (type == "all" || it.mediaType == type)
            }
        },
        typeFilter = if (type == "all") null else type
    )

    /** ---------------------------------------------------------------------
     *  SMART RECOMMENDATIONS  (دمج القديمة + الذكية)
     *  --------------------------------------------------------------------*/
    suspend fun getSmartRecommendations(
        accountId: Int,
        sessionId: String
    ): List<MediaEntity> = try {

        val collected = mutableListOf<Movie>()

        // 1️⃣  حساب الـ Rated Movies & TV Shows (لكن مش هنرجعهم نفسهم)
        val ratedMoviesRes = api.getRatedMovies(accountId, sessionId)
        val ratedTvRes = api.getRatedTvShows(accountId, sessionId)

        val ratedMovies = ratedMoviesRes.body()?.results.orEmpty()
        val ratedTv = ratedTvRes.body()?.results.orEmpty()

        // 🧠 هانجيب الـ related (recommendations) بس، مش الـ rated
        ratedMovies.take(3).forEach { rated ->
            val rec = api.getMovieRecommendations(rated.id)
            if (rec.isSuccessful) {
                val related = rec.body()?.results.orEmpty()
                    .filterNot { r -> r.id == rated.id }              // استبعاد الفيلم نفسه
                    .map { m -> m.copy(mediaType = "movie") }         // ✅ تأكيد النوع
                collected += related
            }
        }

        ratedTv.take(3).forEach { rated ->
            val rec = api.getTvRecommendations(rated.id)
            if (rec.isSuccessful) {
                val related = rec.body()?.results.orEmpty()
                    .filterNot { r -> r.id == rated.id }              // استبعاد المسلسل نفسه
                    .map { m -> m.copy(mediaType = "tv") }            // ✅ تأكيد النوع
                collected += related
            }
        }

        // 2️⃣  HISTORY داخل التطبيق (TODO لما تكمّل شغلك)
        // -----------------------------------------------------------------
        // TODO: بعد ما تكمّل الـ DAO بتاع History، اربطه هنا 👇
        // val viewedItems = localRepo.getViewHistory().firstOrNull().orEmpty()
        // viewedItems.take(5).forEach { history ->
        //     val rec = api.getMovieRecommendations(history.mediaId)
        //     if (rec.isSuccessful)
        //         collected += rec.body()?.results.orEmpty()
        //             .filterNot { r -> r.id == history.mediaId }
        // }
        // -----------------------------------------------------------------

        // 3️⃣  Search History (استعمال فعلي)
        // 3️⃣  Search History (أفضل استخدام)
        val searchHistory = localRepo.getSearchHistoryOnce()   // ✅ استخدم الدالة الجديدة المباشرة
        if (searchHistory.isNotEmpty()) {
            for (item in searchHistory.take(5)) {
                val response = api.searchMulti(item.query)
                if (response.isSuccessful) {
                    val results = response.body()?.results.orEmpty()
                        .filter { it.mediaType == "movie" || it.mediaType == "tv" }   // ✅ فلترة دقيقة
                        .take(5)
                        .map {
                            // ✅ نحدد mediaType لو ناقص
                            if (it.mediaType.isNullOrBlank()) it.copy(mediaType = "movie") else it
                        }
                    collected += results
                }
            }
        }

        // 4️⃣  في حالة فشل كل دا — fallback ذكي (Movies + TV)
        if (collected.isEmpty()) {
            Log.d("MoviesRepo", "⚠️ No user data — fallback to general smart mix.")
            getGeneralFallback()
        } else {
            collected.distinctBy { it.id }
                .sortedByDescending { it.voteAverage ?: 0.0 }
                .map { it.toMediaEntity() }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        getGeneralFallback()
    }

    /** ---------------------------------------------------------------------
     *  FALLBACK
     *  --------------------------------------------------------------------*/
    private suspend fun getGeneralFallback(): List<MediaEntity> {

        return try {
            val trendingMovies = api.getTrendingMedia("movie", "day")
                .body()?.results.orEmpty()
                .map { it.copy(mediaType = "movie") }

            val trendingTv = api.getTrendingMedia("tv", "day")
                .body()?.results.orEmpty()
                .map { it.copy(mediaType = "tv") }

            val topMovies = api.getTopRatedMovies()
                .body()?.results.orEmpty()
                .map { it.copy(mediaType = "movie") }

            val topTv = api.getPopularTvShows()
                .body()?.results.orEmpty()
                .map { it.copy(mediaType = "tv") } // ✅ خليها "tv"

            val popularMovies = api.getPopularMovies()
                .body()?.results.orEmpty()
                .map { it.copy(mediaType = "movie") }

            val allList = trendingMovies + trendingTv + topMovies + topTv + popularMovies

            val finalList = allList
                .distinctBy { it.id }
                .sortedByDescending { it.voteAverage ?: 0.0 }
                .take(25)

            finalList.map { it.toMediaEntity() }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}