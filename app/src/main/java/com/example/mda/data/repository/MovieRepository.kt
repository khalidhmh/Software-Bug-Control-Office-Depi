package com.example.mda.data.repository

import android.util.Log
import com.example.mda.data.local.LocalRepository
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.remote.model.Genre
import com.example.mda.data.remote.model.MovieResponse
import com.example.mda.data.remote.model.getKnownForTitles
import com.example.mda.data.repository.mappers.toMediaEntity
import kotlinx.coroutines.flow.first
import com.example.mda.ui.kids.KidsFilter

class MoviesRepository(
    private val api: TmdbApi,
    private val localRepo: LocalRepository
) {

    companion object {
        private const val TAG = "RepoDebug"
    }

    /** ---------------------------------------------------------------------
     *  SAFE API CALL
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

                Log.d(TAG, "✅ API Success: Fetched ${response.results.size} items. Processing...")

                // 🔥 التعديل: فلترة النتائج لإزالة العناصر السيئة (بدون صور أو أسماء)
                val rawResults = response.results
                    .filter { it.adult != true } // استبعاد المحتوى غير اللائق
                    .filter { !it.posterPath.isNullOrBlank() } // ✅ استبعاد العناصر اللي من غير صورة (أهم خطوة)
                    .filter { !it.title.isNullOrBlank() || !it.name.isNullOrBlank() } // ✅ استبعاد العناصر اللي من غير اسم

                // تم إزالة sortedByDescending { it.popularity } لأن المتغير غير موجود في الموديل عندك
                // الفلترة بالأعلى كافية جداً لتنظيف البحث

                var entities = rawResults.map { it.toMediaEntity(typeFilter) }

                // إجبار الـ mediaType لو ناقص
                entities = entities.map {
                    if (it.mediaType.isNullOrBlank() && typeFilter != null)
                        it.copy(mediaType = typeFilter)
                    else it
                }

                // فلترة إضافية عند الحاجة
                if (typeFilter != null) entities = entities.filter { it.mediaType == typeFilter }
                if (genreId != null) entities = entities.filter { it.genreIds?.contains(genreId) == true }

                // 🔥 حفظ في الكاش المحلي
                if (entities.isNotEmpty()) {
                    localRepo.addOrUpdateAllFromApi(entities)
                }

                entities
            } else {
                Log.w(TAG, "⚠️ API returned null or empty. Using Fallback.")
                fallback()
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ API Call Failed: ${e.message}. Using Fallback.")
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

    suspend fun getTvShowsByGenre(genreId: Int, page: Int = 1): List<MediaEntity> = safeApiCall(
        apiCall = {
            val res = api.getTvShowsByGenre(genreId, page)
            if (res.isSuccessful) res.body() else null
        },
        fallback = {
            localRepo.getAll().first()
                .filter { it.mediaType == "tv" && it.genreIds?.contains(genreId) == true }
        },
        typeFilter = "tv",
        genreId = genreId
    )

    // ---------------------- TV Shows ----------------------
    suspend fun getPopularTvShows(): List<MediaEntity> = safeApiCall(
        apiCall = {
            val res = api.getPopularTvShows()
            if (res.isSuccessful) res.body() else null
        },
        fallback = { localRepo.getAll().first().filter { it.mediaType == "tv" } },
        typeFilter = "tv"
    )

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
        val rawResults: List<MediaEntity> = when (type.lowercase()) {
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
                        body?.results
                            ?.filter { !it.profilePath.isNullOrBlank() }
                            ?.map {
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

        // 🔎 فلترة خاصة بالأطفال
        // ✅ إصلاح: تخطي فلترة الأطفال عند البحث عن ممثلين "people"
        val filteredResults = if (type.lowercase() == "people") {
            rawResults // نرجّعهم زي ما هم بدون فلترة genres/adult
        } else {
            KidsFilter.filterKids(
                rawResults.filterNot {
                    it.title.isNullOrBlank() ||
                            (it.adult == true) ||
                            ((it.genres?.isEmpty() == true) && (it.genreIds?.isEmpty() == true))
                }
            )
        }


        return filteredResults
    }

    // ---------------------- Smart Recommendations ----------------------
    suspend fun getSmartRecommendations(accountId: Int, sessionId: String): List<MediaEntity> = try {

        val collected = mutableListOf<MediaEntity>()

        // =================================================
        // 1️⃣ Viewed History & Similar (سجل المشاهدة)
        // =================================================
        // ✅ تصحيح: استخدام الدالة المساعدة في LocalRepo
        val historyList = localRepo.getMovieHistoryOnce()

        if (historyList.isNotEmpty()) {
            // أ) إضافة آخر 5 أفلام شاهدها المستخدم
            val mappedHistory = historyList.take(5).map { it.toMediaEntity() }
            collected.addAll(mappedHistory)

            // ب) جلب توصيات لآخر فيلم تمت مشاهدته
            val lastViewed = historyList.first()
            val isTv = lastViewed.mediaType == "tv" || lastViewed.mediaType.isNullOrBlank() // تحوط للنوع

            val recResponse = if (isTv) {
                api.getSimilarTvShows(lastViewed.id)
            } else {
                api.getSimilarMovies(lastViewed.id)
            }

            if (recResponse.isSuccessful) {
                val similarItems = recResponse.body()?.results.orEmpty()
                    .filterNot { it.id == lastViewed.id }
                    .take(5)
                    .map {
                        // استخدام الـ mapper الموجود لديك
                        it.toMediaEntity(defaultType = lastViewed.mediaType)
                    }
                collected.addAll(similarItems)
            }
        }

        // =================================================
        // 2️⃣ Rated Movies & TV (التقييمات)
        // =================================================
        val ratedMoviesRes = api.getRatedMovies(accountId, sessionId)
        val ratedTvRes = api.getRatedTvShows(accountId, sessionId)

        val ratedMovies = ratedMoviesRes.body()?.results.orEmpty().take(3)
        val ratedTv = ratedTvRes.body()?.results.orEmpty().take(3)

        ratedMovies.forEach { rated ->
            val rec = api.getMovieRecommendations(rated.id)
            if (rec.isSuccessful) {
                val related = rec.body()?.results.orEmpty()
                    .take(3)
                    .map { it.toMediaEntity(defaultType = "movie") }
                collected.addAll(related)
            }
        }

        ratedTv.forEach { rated ->
            val rec = api.getTvRecommendations(rated.id)
            if (rec.isSuccessful) {
                val related = rec.body()?.results.orEmpty()
                    .take(3)
                    .map { it.toMediaEntity(defaultType = "tv") }
                collected.addAll(related)
            }
        }

        // =================================================
        // 3️⃣ Search History (سجل البحث)
        // =================================================
        // ✅ تصحيح: استخدام الدالة المساعدة بدلاً من الوصول المباشر للـ DAO
        val searchHistory = localRepo.getSearchHistoryOnce(accountId.toString())
        if (searchHistory.isNotEmpty()) {
            searchHistory.take(3).forEach { item ->
                val response = api.searchMulti(item.query)
                if (response.isSuccessful) {
                    val results = response.body()?.results.orEmpty()
                        .filter { it.mediaType == "movie" || it.mediaType == "tv" }
                        .take(3)
                        .map { it.toMediaEntity() }
                    collected.addAll(results)
                }
            }
        }

        // =================================================
        // 4️⃣ Final Processing
        // =================================================
        val finalList = if (collected.isEmpty()) {
            getGeneralFallback()
        } else {
            val distinctList = collected
                .distinctBy { it.id }
                .shuffled()

            // ✅ تصحيح: استخدام دالة الحفظ الذكية للحفاظ على المفضلة
            localRepo.addOrUpdateAllFromApi(distinctList)

            distinctList
        }

        finalList

    } catch (e: Exception) {
        e.printStackTrace()
        // ✅ تصحيح: استخدام الدالة المساعدة للجلب من الكاش
        val cached = localRepo.getAllOnce()
        if (cached.isNotEmpty()) cached.shuffled().take(20) else getGeneralFallback()
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

            val finalEntities = allList.distinctBy { it.id }
                .sortedByDescending { it.voteAverage ?: 0.0 }
                .take(25)
                .map { it.toMediaEntity() }

            // 🔥 Save Fallback to DB
            Log.d(TAG, "💾 Saving Fallback data to DB (${finalEntities.size} items)")
            localRepo.addOrUpdateAllFromApi(finalEntities)

            finalEntities

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}