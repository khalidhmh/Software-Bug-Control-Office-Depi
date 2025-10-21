package com.example.mda.data.repository.mappers

import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.model.Movie
import com.example.mda.data.remote.model.MovieDetailsResponse

/**
 * Mapper لتحويل Movie (من API / Trending / Popular) إلى MediaEntity.
 */
fun Movie.toMediaEntity(defaultType: String? = this.mediaType ?: "movie"): MediaEntity {
    val realType = this.mediaType ?: defaultType ?:
    if (!this.name.isNullOrEmpty() && this.title.isNullOrEmpty()) "tv" else "movie"

    return MediaEntity(
        id = this.id,
        title = this.title ?: "",
        name = this.name ?: this.title ?: "",
        overview = this.overview ?: "",
        posterPath = this.posterPath,
        backdropPath = this.backdropPath,
        voteAverage = this.voteAverage ?: 0.0,
        releaseDate = this.releaseDate ?: "",
        firstAirDate = this.firstAirDate ?: "",
        mediaType = realType, // 🔥 هنا الإصلاح
        adult = this.adult ?: false,
        genreIds = this.genreIds ?: emptyList(),
        genres = emptyList()
    )
}

fun MovieDetailsResponse.toMediaEntity(type: String = "movie"): MediaEntity {
    val genreNames = this.genres?.mapNotNull { it.name } ?: emptyList()
    val genreIds = this.genres?.map { it.id } ?: emptyList()

    return MediaEntity(
        id = this.id,
        title = this.title ?: "",
        name = this.title ?: "",
        overview = this.overview ?: "",
        posterPath = this.posterPath,
        backdropPath = this.backdropPath,
        voteAverage = this.voteAverage ?: 0.0,
        releaseDate = this.releaseDate ?: "",
        firstAirDate = null,
        mediaType = type,
        adult = this.adult ?: false,
        genreIds = genreIds,
        genres = genreNames
    )
}

