package com.example.mda.data.repository

import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.remote.model.ActorFullDetails
import com.example.mda.data.remote.model.MovieResponse
import retrofit2.Response

class ActorRepository(private val api: TmdbApi, private val apiKey: String) {

    suspend fun getPopularMovies(): Response<MovieResponse> {
        return api.getPopularMovies()
    }

    suspend fun getFullActorDetails(personId: Int): Response<ActorFullDetails> {
        return api.getFullActorDetails(
            personId = personId,
            apiKey = apiKey
        )
    }
}
