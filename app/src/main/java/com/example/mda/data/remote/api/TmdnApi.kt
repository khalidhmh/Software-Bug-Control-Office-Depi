package com.example.mda.data.remote.api

import com.example.mda.data.remote.model.GenreResponse
import com.example.mda.data.remote.model.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    // Popular Movies
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Response<MovieResponse>

    // Movie Genres
    @GET("genre/movie/list")
    suspend fun getGenres(): Response<GenreResponse>

    // Movies by Genre
    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("with_genres") genreId: Int
    ): Response<MovieResponse>

    // Trending Movies (Day / Week)
    @GET("trending/{media_type}/{time_window}")
    suspend fun getTrendingMovies(
        @Path("media_type") mediaType: String = "all", // all / movie / tv
        @Path("time_window") timeWindow: String = "day", // day or week
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Response<MovieResponse>

    // Popular TV Shows
    @GET("tv/popular")
    suspend fun getPopularTvShows(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Response<MovieResponse>

    // Top Rated Movies
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Response<MovieResponse>
}
