package com.example.mda.data.repository

import android.util.Log
import com.example.mda.data.local.dao.MediaDao
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.remote.model.MovieDetailsResponse
import com.example.mda.data.repository.mappers.toMediaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "MovieDetailsRepository"
private const val TMDB_API_KEY = "53b57a02de510f8c255d80f88e705cf2"

class MovieDetailsRepository(
    private val apiService: TmdbApi,
    private val mediaDao: MediaDao
) {

    suspend fun getCachedMovie(id: Int): MediaEntity? = withContext(Dispatchers.IO) {
        mediaDao.getById(id, type = "movie")
    }

    suspend fun getCachedTv(id: Int): MediaEntity? = withContext(Dispatchers.IO) {
        mediaDao.getById(id, type = "tv")
    }

    suspend fun getMovieById(id: Int): MediaEntity? = withContext(Dispatchers.IO) {
        Log.d(TAG, "🎬 Fetching movie details for ID: $id")
        try {
            val response = apiService.getMovieDetails(
                movieId = id, 
                appendToResponse = "videos,credits",
                apiKey = TMDB_API_KEY
            )
            
            if (response.isSuccessful) {
                val body: MovieDetailsResponse? = response.body()
                Log.d(TAG, "✅ Movie API response: ${body?.title}")
                Log.d(TAG, "📊 Credits: ${body?.credits?.cast?.size ?: 0} cast members")
                Log.d(TAG, "🎥 Videos: ${body?.videos?.results?.size ?: 0} videos")
                
                val entity = body?.toMediaEntity("movie") ?: return@withContext null
                
                Log.d(TAG, "💾 Saving to database: Cast=${entity.cast?.size}, Videos=${entity.videos?.size}")
                mediaDao.upsert(entity)
                entity
            } else {
                Log.e(TAG, "❌ API Error: ${response.code()} - ${response.message()}")
                throw Exception("Failed to load movie details: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception in getMovieById: ${e.message}", e)
            throw e
        }
    }

    suspend fun getTvById(id: Int): MediaEntity? = withContext(Dispatchers.IO) {
        Log.d(TAG, "📺 Fetching TV details for ID: $id")
        try {
            val response = apiService.getTvDetails(
                tvId = id,
                appendToResponse = "videos,credits",
                apiKey = TMDB_API_KEY
            )
            
            if (response.isSuccessful) {
                val body: MovieDetailsResponse? = response.body()
                Log.d(TAG, "✅ TV API response: ${body?.title}")
                Log.d(TAG, "📊 Credits: ${body?.credits?.cast?.size ?: 0} cast members")
                Log.d(TAG, "🎥 Videos: ${body?.videos?.results?.size ?: 0} videos")
                
                val entity = body?.toMediaEntity("tv") ?: return@withContext null
                
                Log.d(TAG, "💾 Saving to database: Cast=${entity.cast?.size}, Videos=${entity.videos?.size}")
                mediaDao.upsert(entity)
                entity
            } else {
                Log.e(TAG, "❌ API Error: ${response.code()} - ${response.message()}")
                throw Exception("Failed to load tv details: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception in getTvById: ${e.message}", e)
            throw e
        }
    }
}


// 🔹 Mapper لتحويل MovieDetailsResponse لـ MediaEntity

