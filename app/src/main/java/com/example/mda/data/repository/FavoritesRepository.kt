package com.example.mda.data.repository

import com.example.mda.data.datastore.SessionManager
import com.example.mda.data.local.LocalRepository
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.remote.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class FavoritesRepository(
    private val localRepo: LocalRepository,
    private val api: TmdbApi,
    private val sessionManager: SessionManager
) {

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

        // 1️⃣ حدّث الـ local DB (UX أسرع حتى لو النت وقع)
        localRepo.addOrUpdate(mediaEntity)

        // 2️⃣ حاول تزامن مع TMDb account
        try {
            val sessionId = sessionManager.sessionId.first()
            val accountId = sessionManager.accountId.first()

            if (!sessionId.isNullOrEmpty() && accountId != null) {
                api.markFavorite(
                    accountId = accountId,
                    sessionId = sessionId,
                    body = mapOf(
                        "media_type" to (movie.mediaType ?: "movie"),
                        "media_id" to movie.id,
                        "favorite" to newStatus
                    )
                )
            }
        } catch (e: Exception) {
            // هنا ممكن تعمل Log بس عادي ما نكسرش الابلكيشن
            e.printStackTrace()
        }

        return newStatus
    }

    suspend fun isFavorite(id: Int): Boolean = localRepo.isFavorite(id)

    /**
     * Get remote favorites list from TMDb
     */
    suspend fun fetchFavoritesFromTmdb(): List<Int> {
        val sessionId = sessionManager.sessionId.first()
        val accountId = sessionManager.accountId.first()

        if (sessionId.isNullOrEmpty() || accountId == null) return emptyList()

        val response = api.getFavoriteMovies(accountId, sessionId)
        if (!response.isSuccessful || response.body() == null) return emptyList()

        return response.body()!!.results.map { it.id }
    }

    /**
     * Sync remote TMDb favorites → local Room DB
     */
    suspend fun syncFavoritesFromTmdb() {
        val remoteFavorites = fetchFavoritesFromTmdb()

        // Clear old local favorites
        localRepo.clearAllFavorites()

        // Add new ones
        remoteFavorites.forEach { id ->
            localRepo.setFavorite(id, true)
        }
    }
    suspend fun clearAllLocalFavorites() {
        // Remove all favorites from local Room DB
        localRepo.clearAllFavorites()
    }


}
