package com.example.mda.data.remote.api


import com.example.mda.data.remote.model.ActorResponse
import com.example.mda.data.remote.model.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {
    @GET("movie/popular")
    suspend fun getPopularMovies(): Response<MovieResponse>

    @GET("person/popular")
    suspend fun getPopularActors(
        @Query("page") page: Int,
    ): Response<ActorResponse>
}

