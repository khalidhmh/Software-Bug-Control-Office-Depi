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
     * - فيها فلترة حسب النوع أو الـ genre
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
                    .filter { it.adult != true } // استبعاد المحتوى للكبار
                    .map { it.toMediaEntity() }

                // فلترة حسب النوع (movie / tv)
                if (typeFilter != null) {
                    entities = entities.filter { it.mediaType == typeFilter }
                }

                // فلترة حسب النوع Genre ID
                if (genreId != null) {
                    entities = entities.filter { it.genreIds?.contains(genreId) == true }
                }

                // حفظ البيانات في قاعدة البيانات المحلية
                localRepo.addOrUpdateAll(entities)

                Log.d("MoviesRepository", "✅ API success: ${entities.size} items loaded")
                entities
            } else {
                Log.w("MoviesRepository", "⚠️ API returned empty, using fallback")
                fallback()
            }

        } catch (e: Exception) {
            Log.e("MoviesRepository", "❌ API failed: ${e.message}", e)
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
}
