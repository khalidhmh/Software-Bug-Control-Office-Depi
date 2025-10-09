package com.example.mda.data.repository

import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.remote.model.GenreResponse
import com.example.mda.data.remote.model.MovieResponse
import retrofit2.Response

class MoviesRepository(private val api: TmdbApi) {

    // Genres
    suspend fun getGenres(): GenreResponse {
        val response = api.getGenres()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Failed to load genres: ${response.code()} ${response.message()}")
        }
    }

    // Movies by Genre
    suspend fun getMoviesByGenre(genreId: Int, page: Int): MovieResponse {
        val response = api.getMoviesByGenre(genreId, page)
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Failed to load movies by genre: ${response.code()} ${response.message()}")
        }
    }


    // Popular Movies
    suspend fun getPopularMovies(): MovieResponse {
        val response = api.getPopularMovies()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Failed to load popular movies: ${response.code()} ${response.message()}")
        }
    }

    // Trending Movies
    suspend fun getTrendingMovies(
        mediaType: String = "all",
        timeWindow: String = "day"
    ): MovieResponse {
        val response = api.getTrendingMovies(mediaType, timeWindow)
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Failed to load trending movies: ${response.code()} ${response.message()}")
        }
    }

    // Popular TV Shows
    suspend fun getPopularTvShows(): MovieResponse {
        val response = api.getPopularTvShows()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Failed to load popular TV shows: ${response.code()} ${response.message()}")
        }
    }

    // Top Rated Movies
    suspend fun getTopRatedMovies(): MovieResponse {
        val response = api.getTopRatedMovies()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Failed to load top rated movies: ${response.code()} ${response.message()}")
        }
    }
}
