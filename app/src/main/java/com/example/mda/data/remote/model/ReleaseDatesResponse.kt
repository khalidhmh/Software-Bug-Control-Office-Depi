package com.example.mda.data.remote.model

import com.google.gson.annotations.SerializedName

// TMDB /movie/{movie_id}/release_dates

data class ReleaseDatesResponse(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("results") val results: List<CountryRelease> = emptyList()
) {
    data class CountryRelease(
        @SerializedName("iso_3166_1") val iso31661: String? = null,
        @SerializedName("release_dates") val releaseDates: List<ReleaseItem> = emptyList()
    )

    data class ReleaseItem(
        @SerializedName("certification") val certification: String? = null,
        @SerializedName("iso_639_1") val iso6391: String? = null,
        @SerializedName("release_date") val releaseDate: String? = null,
        @SerializedName("type") val type: Int? = null,
        @SerializedName("note") val note: String? = null
    )
}
