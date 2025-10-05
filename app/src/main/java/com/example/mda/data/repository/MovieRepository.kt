package com.example.mda.data.repository

import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.remote.model.MovieResponse

class MoviesRepository(private val api: TmdbApi) {

    suspend fun getPopularMovies(): MovieResponse {
        return api.getPopularMovies()
    }

    suspend fun getTrendingMovies(
        mediaType: String = "all",
        timeWindow: String = "day"
    ): MovieResponse {
        return api.getTrendingMovies(
            mediaType = mediaType,
            timeWindow = timeWindow
        )
    }

    suspend fun getPopularTvShows(): MovieResponse {
        return api.getPopularTvShows()
    }

    suspend fun getTopRatedMovies(): MovieResponse {
        return api.getTopRatedMovies()
    }
}
