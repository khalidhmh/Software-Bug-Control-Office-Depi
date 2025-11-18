package com.example.mda.data.remote.model.auth

import com.google.gson.annotations.SerializedName

data class AccountDetails(
    @SerializedName("id")
    val id: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("include_adult")
    val includeAdult: Boolean,
    @SerializedName("iso_639_1")
    val iso6391: String,
    @SerializedName("iso_3166_1")
    val iso31661: String,
    @SerializedName("avatar")
    val avatar: Avatar?
)

data class Avatar(
    @SerializedName("gravatar")
    val gravatar: Gravatar?,
    @SerializedName("tmdb")
    val tmdb: Tmdb?
)

data class Gravatar(
    @SerializedName("hash")
    val hash: String?
)

data class Tmdb(
    @SerializedName("avatar_path")
    val avatarPath: String?
)
