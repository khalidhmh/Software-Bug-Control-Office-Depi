package com.example.mda.data.repository.mappers


import com.example.mda.data.local.entities.ActorDetailsEntity
import com.example.mda.data.remote.model.*

fun ActorDetailsEntity.toRemote(): ActorFullDetails {
    return ActorFullDetails(
        id = id,
        name = name ?: "",                // ✅ استخدم قيمة افتراضية فاضية لو null
        biography = biography ?: "",
        birthday = birthday ?: "",
        profile_path = profilePath ?: "",
        place_of_birth = placeOfBirth ?: "",
        gender = 0,
        homepage = null,
        external_ids = ExternalIds(
            imdb_id = null,
            facebook_id = null,
            instagram_id = null,
            twitter_id = null
        ),
        images = ActorImages(emptyList()),
        combined_credits = ActorCredits(emptyList(), emptyList())
    )
}