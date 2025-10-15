package com.example.mda.data.remote.model

data class ActorFullDetails(
    val id: Int,
    val name: String?,
    val biography: String?,
    val birthday: String?,
    val place_of_birth: String?,
    val gender: Int?,
    val profile_path: String?,
    val homepage: String?,
    val external_ids: ExternalIds?,
    val images: ActorImages?,
    val combined_credits: ActorCredits?
)

data class ExternalIds(
    val imdb_id: String? = null,
    val facebook_id: String?= null,
    val instagram_id: String?= null,
    val twitter_id: String?= null
)

data class ActorImages(
    val profiles: List<Image>
)

data class Image(
    val file_path: String
)

data class ActorCredits(
    val cast: List<Credit>,
    val crew: List<Credit>
)

data class Credit(
    val id: Int,
    val title: String?,
    val name: String?,
    val media_type: String,
    val character: String?,
    val poster_path: String?
)
