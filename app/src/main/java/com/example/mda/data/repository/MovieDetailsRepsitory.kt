package com.example.mda.data.repository

import com.example.mda.data.remote.RetrofitInstance.api
import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.remote.model.MovieDetailsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn




class MovieDetailsRepository (private val apiService: TmdbApi) {

    suspend fun getMovieDetails(movieId: Int):MovieDetailsResponse {
        val response = api.getMoviesDetails(movieId = movieId)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) return body
            else throw Exception("Empty body for movie details")
        } else {
            throw Exception("Failed to load movie details: ${response.code()} ${response.message()}")
        }
    }

}
