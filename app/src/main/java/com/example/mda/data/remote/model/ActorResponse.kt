package com.example.mda.data.remote.model

import com.google.gson.annotations.SerializedName

data class ActorResponse(
    val page: Int,
    val results: List<Actor>,
    val total_pages: Int,
    val total_results: Int
)

data class Actor(
    val id: Int,
    val name: String,

    @SerializedName("profile_path")
    val profilePath: String?,

    @SerializedName("known_for")
    val knownFor: List<KnownFor>?,

)
data class KnownFor(
    val id: Int,
    val title: String?,           // movies
    val name: String?,            //  (TV shows)
    @SerializedName("media_type")
    val mediaType:String?
)