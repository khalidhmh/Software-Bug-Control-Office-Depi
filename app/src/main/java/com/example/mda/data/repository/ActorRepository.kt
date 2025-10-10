package com.example.mda.data.remote.repository

import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.remote.model.ActorResponse
import retrofit2.Response

class ActorRepository(
    private val api: TmdbApi
) {
    suspend fun getPopularActors(page: Int ): Response<ActorResponse> {
        return api.getPopularActors(page)
    }
}