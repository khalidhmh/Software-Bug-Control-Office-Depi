package com.example.mda.data.repository

import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.remote.model.MovieResponse

class MoviesRepository(private val api: TmdbApi) {
    suspend fun getPopularMovies() = api.getPopularMovies().body() ?: MovieResponse(0, emptyList(),0,0)
}

