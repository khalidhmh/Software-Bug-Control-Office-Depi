package com.example.mda.data.repository

import android.util.Log
import com.example.mda.data.local.LocalRepository
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.remote.model.Genre
import com.example.mda.data.remote.model.Movie
import com.example.mda.data.remote.model.MovieResponse
import com.example.mda.data.remote.model.getKnownForTitles
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
                    .map { it.toMediaEntity(typeFilter) }

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

    // ---------------------- Movies ----------------------
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

    // ---------------------- TV Shows ----------------------
    suspend fun getPopularTvShows(): List<MediaEntity> = safeApiCall(
        apiCall = {
            val res = api.getPopularTvShows()
            if (res.isSuccessful) res.body() else null
        },
        fallback = { localRepo.getAll().first().filter { it.mediaType == "tv" } },
        typeFilter = "tv"
    ).also { Log.d("MoviesRepository", "✅ TV Shows fetched: ${it.size}") }

    // ---------------------- Trending ----------------------
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

    // ---------------------- Search ----------------------
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

    /** 🔹 بحث بنوع محدد (Movie / TV / Person) */
    suspend fun searchByType(query: String, type: String): List<MediaEntity> {
        return when (type.lowercase()) {
            "movie" -> safeApiCall(
                apiCall = {
                    val res = api.searchMovies(query)
                    if (res.isSuccessful) res.body() else null
                },
                fallback = {
                    val local = localRepo.getAll().first()
                    local.filter {
                        (it.title ?: it.name ?: "").contains(query, true) && it.mediaType == "movie"
                    }
                },
                typeFilter = "movie"
            )

            "tv" -> safeApiCall(
                apiCall = {
                    val res = api.searchTvShows(query)
                    if (res.isSuccessful) res.body() else null
                },
                fallback = {
                    val local = localRepo.getAll().first()
                    local.filter {
                        (it.title ?: it.name ?: "").contains(query, true) && it.mediaType == "tv"
                    }
                },
                typeFilter = "tv"
            )

            "people" -> {
                try {
                    val res = api.searchPeople(query)
                    if (res.isSuccessful) {
                        val body = res.body()
                        body?.results?.map {
                            MediaEntity(
                                id = it.id,
                                name = it.name,
                                title = it.name,
                                overview = it.getKnownForTitles(),
                                posterPath = it.profilePath,
                                backdropPath = null,
                                voteAverage = null,
                                releaseDate = null,
                                firstAirDate = null,
                                mediaType = "person",
                                adult = false,
                                genreIds = emptyList(),
                                isFavorite = false,
                                isInWatchlist = false
                            )
                        } ?: emptyList()
                    } else emptyList()
                } catch (e: Exception) {
                    emptyList()
                }
            }

            else -> safeApiCall(
                apiCall = {
                    val res = api.searchMulti(query)
                    if (res.isSuccessful) res.body() else null
                },
                fallback = {
                    localRepo.getAll().first().filter {
                        (it.title ?: it.name ?: "").contains(query, true)
                    }
                }
            )
        }
    }

    // ---------------------- Smart Recommendations ----------------------
    suspend fun getSmartRecommendations(accountId: Int, sessionId: String): List<MediaEntity> = try {

        val collected = mutableListOf<Movie>()

        // 1️⃣ Rated Movies & TV Shows
        val ratedMoviesRes = api.getRatedMovies(accountId, sessionId)
        val ratedTvRes = api.getRatedTvShows(accountId, sessionId)

        val ratedMovies = ratedMoviesRes.body()?.results.orEmpty()
        val ratedTv = ratedTvRes.body()?.results.orEmpty()

        ratedMovies.take(3).forEach { rated ->
            val rec = api.getMovieRecommendations(rated.id)
            if (rec.isSuccessful) {
                val related = rec.body()?.results.orEmpty()
                    .filterNot { r -> r.id == rated.id }
                    .map { m -> m.copy(mediaType = "movie") }
                collected += related
            }
        }

        ratedTv.take(3).forEach { rated ->
            val rec = api.getTvRecommendations(rated.id)
            if (rec.isSuccessful) {
                val related = rec.body()?.results.orEmpty()
                    .filterNot { r -> r.id == rated.id }
                    .map { m -> m.copy(mediaType = "tv") }
                collected += related
            }
        }

        // 2️⃣ Search History
        val searchHistory = localRepo.getSearchHistoryOnce()
        if (searchHistory.isNotEmpty()) {
            for (item in searchHistory.take(5)) {
                val response = api.searchMulti(item.query)
                if (response.isSuccessful) {
                    val results = response.body()?.results.orEmpty()
                        .filter { it.mediaType == "movie" || it.mediaType == "tv" }
                        .take(5)
                        .map {
                            if (it.mediaType.isNullOrBlank()) it.copy(mediaType = "movie") else it
                        }
                    collected += results
                }
            }
        }

        // 3️⃣ Fallback
        if (collected.isEmpty()) getGeneralFallback()
        else collected.distinctBy { it.id }
            .sortedByDescending { it.voteAverage ?: 0.0 }
            .map { it.toMediaEntity() }

    } catch (e: Exception) {
        e.printStackTrace()
        getGeneralFallback()
    }

    // ---------------------- Fallback ----------------------
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
                .map { it.copy(mediaType = "tv") }

            val popularMovies = api.getPopularMovies()
                .body()?.results.orEmpty()
                .map { it.copy(mediaType = "movie") }

            val allList = trendingMovies + trendingTv + topMovies + topTv + popularMovies

            allList.distinctBy { it.id }
                .sortedByDescending { it.voteAverage ?: 0.0 }
                .take(25)
                .map { it.toMediaEntity() }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
