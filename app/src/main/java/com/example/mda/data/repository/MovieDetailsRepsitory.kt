package com.example.mda.data.repository

import com.example.mda.data.local.dao.MediaDao
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.remote.model.MovieDetailsResponse
import com.example.mda.data.repository.mappers.toMediaEntity
import com.example.mda.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
        val response = apiService.getMovieDetails(movieId = id, apiKey = Constants.API_KEY)
        if (response.isSuccessful) {
            val body: MovieDetailsResponse? = response.body()
            val entity = body?.toMediaEntity("movie") ?: return@withContext null
            mediaDao.upsert(entity)
            entity
        } else throw Exception("Failed to load movie details: ${response.code()}")
    }

    suspend fun getTvById(id: Int): MediaEntity? = withContext(Dispatchers.IO) {
        val response = apiService.getTvDetails(tvId = id, apiKey = Constants.API_KEY)
        if (response.isSuccessful) {
            val body: MovieDetailsResponse? = response.body()
            val entity = body?.toMediaEntity("tv") ?: return@withContext null
            mediaDao.upsert(entity)
            entity
        } else throw Exception("Failed to load tv details: ${response.code()}")
    }
}


// 🔹 Mapper لتحويل MovieDetailsResponse لـ MediaEntity

