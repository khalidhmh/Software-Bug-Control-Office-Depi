package com.example.mda.data.remote.api

import com.example.mda.data.remote.model.ActorFullDetails
import com.example.mda.data.remote.model.ActorResponse
import com.example.mda.data.remote.model.GenreResponse
import com.example.mda.data.remote.model.MovieDetailsResponse
import com.example.mda.data.remote.model.MovieResponse
import com.example.mda.util.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {




    @GET("person/popular")
    suspend fun getPopularActors(
        @Query("page") page: Int,
    ): Response<ActorResponse>
//    suspend fun getPopularMovies(
//        @Query("language") language: String = "en-US",
//        @Query("page") page: Int = 1
//    ): Response<MovieResponse>

    // Movie Genres
    @GET("genre/movie/list")
    suspend fun getGenres(): Response<GenreResponse>

    // Movies by Genre
    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
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

    // Movie Details (with videos & credits)
    @GET("movie/{movie_id}")
    suspend fun getMoviesDetailss(
        @Path("movie_id") movieId: Int,
        @Query("append_to_response") appendToResponse: String = "videos,credits",
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en",
    ): MovieDetailsResponse
    @GET("movie/{movie_id}")
    suspend fun getMoviesDetails(
        @Path("movie_id") movieId: Int,
        @Query("append_to_response") appendToResponse: String = "videos,credits",
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = API_KEY,
    ): Response<MovieDetailsResponse>
    // Popular Movies
    @GET("movie/popular")
    suspend fun getPopularMovies(): Response<MovieResponse>
    @GET("person/{person_id}")
    suspend fun getActorDetails(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") appendToResponse: String = "images,combined_credits,external_ids"
    ): Response<ActorFullDetails>
}

