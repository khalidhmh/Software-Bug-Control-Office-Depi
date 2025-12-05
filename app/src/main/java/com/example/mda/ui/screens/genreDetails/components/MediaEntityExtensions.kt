package com.example.mda.ui.screens.genreDetails

import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.model.Movie

fun MediaEntity.mediaTypeFilterIsTv(): Boolean =
    this.mediaType?.lowercase() == "tv" || this.firstAirDate != null

fun MediaEntity.toMovie(): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        name = this.name,
        overview = this.overview,
        posterPath = this.posterPath,
        backdropPath = this.backdropPath,
        releaseDate = this.releaseDate,
        firstAirDate = this.firstAirDate,
        voteAverage = this.voteAverage ?: 0.0,
        mediaType = this.mediaType,
        adult = this.adult,
        genreIds = this.genreIds
    )
}