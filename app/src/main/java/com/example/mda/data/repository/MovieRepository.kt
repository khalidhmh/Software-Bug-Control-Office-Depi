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

class MoviesRepository(
    private val api: TmdbApi,
    private val localRepo: LocalRepository
) {

    /**
     * 🔹 دالة عامة للتعامل مع الـ API والكاش
     * - تحاول تجيب البيانات من السيرفر
     * - لو فشلت ترجع بيانات من قاعدة البيانات المحلية
     */
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

                // ✅ Make sure mediaType is always set (important for filtering)
                entities = entities.map {
                    if (it.mediaType.isNullOrBlank() && typeFilter != null)
                        it.copy(mediaType = typeFilter)
                    else it
                }

                if (typeFilter != null) {
                    entities = entities.filter { it.mediaType == typeFilter }
                }

                if (genreId != null) {
                    entities = entities.filter { it.genreIds?.contains(genreId) == true }
                }

                localRepo.addOrUpdateAllFromApi(entities)
                entities
            } else {
                fallback()
            }

        } catch (e: Exception) {
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
            if (res.isSuccessful) {
                res.body()?.genres ?: emptyList()
            } else {
                Log.e("MoviesRepository", "⚠️ Genres API failed with code ${res.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("MoviesRepository", "❌ Failed to fetch genres: ${e.message}")
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
        typeFilter = null // ❌ شيل الفلتر مؤقتًا عشان مايحذفش الداتا
    ).map { entity ->
        // ✅ بعد ما ترجع البيانات، لو مفيش mediaType خليها "tv"
        if (entity.mediaType.isNullOrBlank()) entity.copy(mediaType = "tv") else entity
    }.also { Log.d("MoviesRepository", "✅ TV Shows fetched: ${it.size}") }

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

    // ---------------------- 🔍 Search ----------------------

    /** 🔹 بحث شامل (Movies + TV + People) */
    suspend fun searchMulti(query: String): List<MediaEntity> = safeApiCall(
        apiCall = {
            val res = api.searchMulti(query = query)
            if (res.isSuccessful) res.body() else null
        },
        fallback = {
            val localData = localRepo.getAll().first()
            localData.filter {
                (it.title ?: it.name ?: "").contains(query, ignoreCase = true)
            }
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

            "people" -> { // 🔹 [EDIT] معالجة خاصة لأن API بيرجع ActorResponse مش MovieResponse
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
                                // 🔹 القيم اللي ناقصه نمررها null هنا
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
                    val local = localRepo.getAll().first().filter {
                        (it.title ?: it.name ?: "").contains(query, true)
                    }
                    local
                }
            )
        }
    }
}
