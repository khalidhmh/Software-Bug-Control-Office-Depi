package com.example.mda.data.repository

import android.util.Log
import com.example.mda.data.local.LocalRepository
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.remote.model.Genre
import com.example.mda.data.remote.model.MovieResponse
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
                    .map { it.toMediaEntity(typeFilter) }

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
            val localData = localRepo.getAll().first()
            localData.filter {
                (it.title ?: it.name ?: "").contains(query, ignoreCase = true)
                        && (type == "all" || it.mediaType == type)
            }
        },
        typeFilter = if (type == "all") null else type
    )
}