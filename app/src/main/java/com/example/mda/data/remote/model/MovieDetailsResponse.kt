package com.example.mda.data.remote.model

import com.google.gson.annotations.SerializedName

data class MovieDetailsResponse(
    @SerializedName("genres") val genres: List<GenreResponse>? = emptyList(),
    @SerializedName("credits") val credits: CreditsResponse? = null,
    @SerializedName("videos") val videos: VideosResponse? = null,
    @SerializedName("adult") val adult: Boolean = false,
    @SerializedName("backdrop_path") val backdropPath: String? = null,
    @SerializedName("belongs_to_collection") val belongsToCollection: BelongsToCollection? = null,
    @SerializedName("budget") val budget: Long = 0L,
    @SerializedName("homepage") val homepage: String? = null,
    @SerializedName("id") val id: Int = 0,
    @SerializedName("imdb_id") val imdbId: String? = null,
    @SerializedName("original_language") val originalLanguage: String? = null,
    @SerializedName("original_title") val originalTitle: String? = null,
    @SerializedName("overview") val overview: String? = null,
    @SerializedName("popularity") val popularity: Double = 0.0,
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("production_companies") val productionCompanies: List<ProductionCompany>? = emptyList(),
    @SerializedName("production_countries") val productionCountries: List<ProductionCountry>? = emptyList(),
    @SerializedName("release_date") val releaseDate: String? = null,
    @SerializedName("revenue") val revenue: Long = 0L,
    @SerializedName("runtime") val runtime: Int? = null,
    @SerializedName("spoken_languages") val spokenLanguages: List<SpokenLanguage>? = emptyList(),
    @SerializedName("status") val status: String? = null,
    @SerializedName("tagline") val tagline: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("video") val video: Boolean = false,
    @SerializedName("vote_average") val voteAverage: Double = 0.0,
    @SerializedName("vote_count") val voteCount: Long = 0L,
) {

    data class GenreResponse(
        val id: Int = 0,
        val name: String? = null
    )

    data class CreditsResponse(
        val cast: List<CastResponse>? = emptyList()
    )

    data class CastResponse(
        val id: Int = 0,
        val name: String? = null,
        @SerializedName("profile_path") val profilePath: String? = null,
        val character: String? = null
    )

    data class VideosResponse(
        val results: List<VideoResponse>? = emptyList()
    )

    data class VideoResponse(
        val key: String? = null,
        val name: String? = null,
        val site: String? = null,
        val type: String? = null
    )

    data class BelongsToCollection(
        @SerializedName("backdrop_path") val backdropPath: String? = null,
        @SerializedName("id") val id: Int = 0,
        @SerializedName("name") val name: String? = null,
        @SerializedName("poster_path") val posterPath: String? = null
    )

    data class ProductionCompany(
        @SerializedName("id") val id: Int = 0,
        @SerializedName("logo_path") val logoPath: String? = null,
        @SerializedName("name") val name: String? = null,
        @SerializedName("origin_country") val originCountry: String? = null
    )

    data class ProductionCountry(
        @SerializedName("iso_3166_1") val iso31661: String? = null,
        @SerializedName("name") val name: String? = null
    )

    data class SpokenLanguage(
        @SerializedName("iso_639_1") val iso6391: String? = null,
        @SerializedName("name") val name: String? = null
    )
}
