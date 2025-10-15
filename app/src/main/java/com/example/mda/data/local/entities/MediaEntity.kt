// Khaled Edit: Unified Entity for Movies & TV Shows caching and offline usage

package com.example.mda.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_items")
data class MediaEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,

    // Titles
    val title: String?,         // For movies
    val name: String?,          // For TV shows

    // Common Info
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,

    // Ratings & Dates
    val voteAverage: Double?,
    val releaseDate: String?,
    val firstAirDate: String?,

    // Type
    val mediaType: String?,     // "movie" or "tv"

    // TV specific
    val numberOfSeasons: Int? = null,
    val numberOfEpisodes: Int? = null,

    // Adult content flag
    val adult: Boolean? = false,

    // Local flags
    val isFavorite: Boolean = false,
    val isInWatchlist: Boolean = false,

    val genreIds: List<Int>? = null, // <-- ضيف ده

    val genres: List<String>? = null,   // ✅ أضفنا genres


    // For history & analytics
    val timestamp: Long = System.currentTimeMillis()
) {
    val displayTitle: String
        get() = title ?: name ?: "Unknown"
}