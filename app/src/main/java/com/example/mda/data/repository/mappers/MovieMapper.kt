package com.example.mda.data.repository.mappers

import com.example.mda.data.local.entities.Cast
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.local.entities.Video
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
        mediaType = realType,
        adult = this.adult ?: false,
        genreIds = this.genreIds ?: emptyList(),
        genres = emptyList()
    )
}


/**
 * 🆕 Mapper محدث لتحويل MovieDetailsResponse إلى MediaEntity مع كل التفاصيل
 */
fun MovieDetailsResponse.toMediaEntity(type: String = "movie"): MediaEntity {
    val genreNames = this.genres?.mapNotNull { it.name } ?: emptyList()
    val genreIds = this.genres?.map { it.id } ?: emptyList()
    
    // تحويل Cast من API إلى Cast Entity
    val castList = this.credits?.cast?.take(20)?.map { castItem ->
        Cast(
            id = castItem.id,
            name = castItem.name ?: "Unknown",
            character = castItem.character ?: "",
            profilePath = castItem.profilePath
        )
    }
    
    // تحويل Videos من API إلى Video Entity
    val videosList = this.videos?.results?.filter { 
        it.site?.equals("YouTube", ignoreCase = true) == true 
    }?.take(10)?.map { videoItem ->
        Video(
            key = videoItem.key ?: "",
            name = videoItem.name ?: "Video",
            site = videoItem.site ?: "YouTube",
            type = videoItem.type ?: "Clip"
        )
    }
    
    // استخراج أسماء اللغات
    val languages = this.spokenLanguages?.mapNotNull { it.name }
    
    // استخراج أسماء شركات الإنتاج
    val companies = this.productionCompanies?.mapNotNull { it.name }
    
    // استخراج أسماء الدول المنتجة
    val countries = this.productionCountries?.mapNotNull { it.name }

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
        genres = genreNames,
        
        // ========== 🆕 الحقول الجديدة ==========
        runtime = this.runtime,
        tagline = this.tagline,
        status = this.status,
        voteCount = this.voteCount,
        budget = this.budget,
        revenue = this.revenue,
        imdbId = this.imdbId,
        homepage = this.homepage,
        spokenLanguages = languages,
        productionCompanies = companies,
        productionCountries = countries,
        cast = castList,
        videos = videosList
    )
}
