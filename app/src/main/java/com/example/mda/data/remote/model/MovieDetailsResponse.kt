package com.example.mda.data.remote.model

import com.google.gson.annotations.SerializedName


data class MovieDetailsResponse(
    @SerializedName("genres") val genres: List<GenreResponse>?,
    @SerializedName("credits") val credits: CreditsResponse?,
    @SerializedName("videos") val videos: VideosResponse?,
    @SerializedName("adult") val adult: Boolean,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("belongs_to_collection") val belongsToCollection: BelongsToCollection?,
    @SerializedName("budget") val budget: Long,
    @SerializedName("homepage") val homepage: String,
    @SerializedName("id") val id: Int,
    @SerializedName("imdb_id") val imdbId: String,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("original_title") val originalTitle: String,
    @SerializedName("overview") val overview: String?,
    @SerializedName("popularity") val popularity: Double,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("production_companies") val productionCompanies: List<ProductionCompany>,
    @SerializedName("production_countries") val productionCountries: List<ProductionCountry>,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("revenue") val revenue: Long,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("spoken_languages") val spokenLanguages: List<SpokenLanguage>,
    @SerializedName("status") val status: String,
    @SerializedName("tagline") val tagline: String?,
    @SerializedName("title") val title: String,
    @SerializedName("video") val video: Boolean,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Long,
) {
    data class GenreResponse(
        val id: Int,
        val name: String
    )

    data class CreditsResponse(
        val cast: List<CastResponse>?
    )

    data class CastResponse(
        val id: Int,
        val name: String,
        @SerializedName("profile_path") val profilePath: String?,
        val character: String?
    )

    data class VideosResponse(
        val results: List<VideoResponse>?
    )

    data class VideoResponse(
        val key: String,
        val name: String,
        val site: String,
        val type: String
    )

    data class BelongsToCollection(
        @SerializedName("backdrop_path") val backdropPath: String,
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("poster_path") val posterPath: String
    )


    data class ProductionCompany(
        @SerializedName("id") val id: Int,
        @SerializedName("logo_path") val logoPath: String?,
        @SerializedName("name") val name: String,
        @SerializedName("origin_country") val originCountry: String
    )


    data class ProductionCountry(
        @SerializedName("iso_3166_1") val iso31661: String,
        @SerializedName("name") val name: String
    )


    data class SpokenLanguage(
        @SerializedName("iso_639_1") val iso6391: String,
        @SerializedName("name") val name: String
    )
}