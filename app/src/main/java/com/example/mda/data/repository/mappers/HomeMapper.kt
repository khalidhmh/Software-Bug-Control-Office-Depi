package com.example.mda.data.repository.mappers

import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.model.Movie

fun MediaEntity.toMovie(): Movie {
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
        mediaType = this.mediaType ?: "movie",
        adult = this.adult,
        genreIds = this.genreIds
    )
}
