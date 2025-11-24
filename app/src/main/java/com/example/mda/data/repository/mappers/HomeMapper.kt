package com.example.mda.data.repository.mappers

import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.model.Movie

fun MediaEntity.toMovie(): Movie {
    val inferredType = when {
        !this.mediaType.isNullOrBlank() -> this.mediaType
        this.firstAirDate != null || (this.title == null && this.name != null) -> "tv"
        else -> "movie"
    }
    return Movie(
        id = this.id,
        title = this.title,
        name = this.name,
        overview = this.overview,
        posterPath = this.posterPath,
        backdropPath = this.backdropPath,
        voteAverage = this.voteAverage ?: 0.0,
        releaseDate = this.releaseDate,
        firstAirDate = this.firstAirDate,
        mediaType = inferredType,
        adult = this.adult,
        genreIds = this.genreIds
    )
}
