package com.example.mda.data.remote.api



import com.example.mda.data.remote.model.ActorFullDetails
import com.example.mda.data.remote.model.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    @GET("movie/popular")
    suspend fun getPopularMovies(): Response<MovieResponse>
    @GET("person/{person_id}")
    suspend fun getActorDetails(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") appendToResponse: String = "images,combined_credits,external_ids"
    ): Response<ActorFullDetails>
}
