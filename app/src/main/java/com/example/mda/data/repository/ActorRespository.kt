package com.example.mda.data.repository


import com.example.mda.data.remote.RetrofitInstance.api
import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.remote.model.ActorFullDetails
import retrofit2.Response
class ActorRepository() {

    suspend fun getFullActorDetails(personId: Int): Response<ActorFullDetails> {
        return api.getActorDetails(
            personId = personId,
            apiKey = "37cd341f439c32df40a91eabd3fec9f7",
            appendToResponse = "images,external_ids,combined_credits"
        )
    }
}

