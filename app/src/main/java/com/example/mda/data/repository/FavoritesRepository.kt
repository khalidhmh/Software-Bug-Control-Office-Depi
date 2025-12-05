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
        val existingEntity = localRepo.getById(movie.id)
        val currentStatus = existingEntity?.isFavorite ?: false
        val newStatus = !currentStatus

        val mediaEntity = if (existingEntity != null) {
            existingEntity.copy(isFavorite = newStatus)
        } else {
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

        localRepo.addOrUpdate(mediaEntity)

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
    suspend fun fetchFavoritesFromTmdb(): List<Pair<Int, String>> {
        val sessionId = sessionManager.sessionId.first()
        val accountId = sessionManager.accountId.first()

        if (sessionId.isNullOrEmpty() || accountId == null) return emptyList()

        val movieIds = try {
            val resp = api.getFavoriteMovies(accountId, sessionId)
            if (resp.isSuccessful && resp.body() != null) {
                resp.body()!!.results.map { it.id to "movie" }
            } else emptyList()
        } catch (e: Exception) { emptyList() }

        val tvIds = try {
            val resp = api.getFavoriteTvShows(accountId, sessionId)
            if (resp.isSuccessful && resp.body() != null) {
                resp.body()!!.results.map { it.id to "tv" }
            } else emptyList()
        } catch (e: Exception) { emptyList() }

        return (movieIds + tvIds)
    }


    // Sync remote TMDb favorites → local Room DB
    suspend fun syncFavoritesFromTmdb() {
        val remote = fetchFavoritesFromTmdb()
        val remoteIds = remote.map { it.first }.toSet()

        // get current local favorites
        val currentFavs = localRepo.getFavorites().first().map { it.id }.toSet()

        // remove favorites that are local but not remote
        val toRemove = currentFavs - remoteIds
        toRemove.forEach { id -> localRepo.setFavorite(id, false) }

        // add remote ones
        remoteIds.forEach { id -> localRepo.setFavorite(id, true) }
    }

    suspend fun clearAllLocalFavorites() {
        // Remove all favorites from local Room DB
        localRepo.clearAllFavorites()
    }


}
