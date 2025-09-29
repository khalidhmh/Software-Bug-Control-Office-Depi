package com.example.mda.data.remote.api


import com.example.mda.data.remote.model.MovieResponse
import retrofit2.Response
import retrofit2.http.GET

interface TmdbApi {
    @GET("movie/popular")
    suspend fun getPopularMovies(): Response<MovieResponse>
}
