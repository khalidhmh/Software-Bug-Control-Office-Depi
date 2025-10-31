package com.example.mda.data.repository

import com.example.mda.data.local.LocalRepository
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.model.Movie
import kotlinx.coroutines.flow.Flow

class FavoritesRepository(private val localRepo: LocalRepository) {

    fun getFavorites(): Flow<List<MediaEntity>> = localRepo.getFavorites()

    suspend fun toggleFavorite(movie: Movie): Boolean {
        // التأكد أولاً إن الفيلم موجود في القاعدة
        val existingEntity = localRepo.getById(movie.id)
        val currentStatus = existingEntity?.isFavorite ?: false
        val newStatus = !currentStatus
        
        // إذا كان الفيلم موجود، نحدث الـ isFavorite فقط مع الحفاظ على كل البيانات
        val mediaEntity = if (existingEntity != null) {
            existingEntity.copy(isFavorite = newStatus)
        } else {
            // إذا كان جديد، نضيفه بالكامل
            MediaEntity(
                id = movie.id,
                title = movie.title,
                name = movie.name,
                overview = movie.overview,
                posterPath = movie.posterPath,
                backdropPath = movie.backdropPath,
                voteAverage = movie.voteAverage,
                releaseDate = movie.releaseDate,
                firstAirDate = movie.firstAirDate,
                mediaType = movie.mediaType ?: "movie",
                adult = movie.adult,
                isFavorite = newStatus,
                genreIds = movie.genreIds,
            )
        }
        
        // حفظ الـ entity
        localRepo.addOrUpdate(mediaEntity)
        
        return newStatus
    }

    suspend fun isFavorite(id: Int): Boolean = localRepo.isFavorite(id)
}
