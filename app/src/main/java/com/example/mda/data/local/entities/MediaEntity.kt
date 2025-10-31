package com.example.mda.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mda.data.local.converters.Converters


@Entity(tableName = "media_items")
@TypeConverters(Converters::class)
data class MediaEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,

    val title: String?,
    val name: String?,

    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,

    val voteAverage: Double?,
    val releaseDate: String?,
    val firstAirDate: String?,
    val mediaType: String?,

    val numberOfSeasons: Int? = null,
    val numberOfEpisodes: Int? = null,

    val adult: Boolean? = false,

    val isFavorite: Boolean = false,
    val isInWatchlist: Boolean = false,

    val genreIds: List<Int>? = null,
    val genres: List<String>? = null,

    val runtime: Int? = null,
    val tagline: String? = null,
    val status: String? = null,
    val voteCount: Long? = null,
    val budget: Long? = null,
    val revenue: Long? = null,
    val imdbId: String? = null,
    val homepage: String? = null,

    val spokenLanguages: List<String>? = null,
    val productionCompanies: List<String>? = null,
    val productionCountries: List<String>? = null,

    val cast: List<Cast>? = null,
    val videos: List<Video>? = null,

    val timestamp: Long = System.currentTimeMillis()
) {
    val displayTitle: String
        get() = title ?: name ?: "Unknown"

    val year: String?
        get() = releaseDate?.take(4) ?: firstAirDate?.take(4)

    fun getFullPosterUrl(): String? {
        return posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
    }

    fun getFullBackdropUrl(): String? {
        return backdropPath?.let { "https://image.tmdb.org/t/p/original$it" }
    }
}
