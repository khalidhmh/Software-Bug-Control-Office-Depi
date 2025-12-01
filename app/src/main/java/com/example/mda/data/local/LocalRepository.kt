package com.example.mda.data.local

import android.util.Log
import com.example.mda.data.local.dao.MediaDao
import com.example.mda.data.local.dao.MovieHistoryDao
import com.example.mda.data.local.dao.SearchHistoryDao
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.local.entities.MoviesViewedEntitty
import com.example.mda.data.local.entities.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class LocalRepository(
    private val mediaDao: MediaDao,
    private val searchHistoryDao: SearchHistoryDao,
    val movieHistoryDao: MovieHistoryDao
) {

    // ---------------- MEDIA DATA ----------------
    fun getAll(): Flow<List<MediaEntity>> = mediaDao.getAll()
    fun getFavorites(): Flow<List<MediaEntity>> = mediaDao.getFavorites()
    fun getWatchlist(): Flow<List<MediaEntity>> = mediaDao.getWatchlist()

    suspend fun getAllOnce(): List<MediaEntity> = mediaDao.getAllMediaOnce()

    suspend fun addOrUpdate(item: MediaEntity) = mediaDao.upsert(item)

    suspend fun addOrUpdateFromApi(item: MediaEntity) {
        val existing = mediaDao.getByIdOnly(item.id)
        val finalItem = if (existing != null) {
            item.copy(
                isFavorite = existing.isFavorite,
                isInWatchlist = existing.isInWatchlist
            )
        } else item
        mediaDao.upsert(finalItem)
    }

    suspend fun delete(item: MediaEntity) = mediaDao.delete(item)
    suspend fun clearNonSaved() = mediaDao.clearNonSaved()

    suspend fun addOrUpdateAllFromApi(newEntities: List<MediaEntity>) {
        if (newEntities.isEmpty()) return

        try {
            val currentCachedItems = mediaDao.getAllMediaOnce()
            val currentMap = currentCachedItems.associateBy { it.id }

            val finalEntities = newEntities.map { newItem ->
                val oldItem = currentMap[newItem.id]
                if (oldItem != null) {
                    newItem.copy(
                        isFavorite = oldItem.isFavorite,
                        isInWatchlist = oldItem.isInWatchlist
                    )
                } else {
                    newItem
                }
            }
            mediaDao.insertAll(finalEntities)
            Log.d("RepoDebug", " Successfully saved/updated ${finalEntities.size} items in DB.")

        } catch (e: Exception) {
            Log.e("RepoDebug", " Error saving to DB: ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun addOrUpdateAll(entities: List<MediaEntity>) =
        mediaDao.insertAll(entities)

    suspend fun toggleFavorite(id: Int): Boolean {
        val current = mediaDao.isFavorite(id) ?: false
        val newStatus = !current
        mediaDao.updateFavoriteStatus(id, newStatus)
        return newStatus
    }

    suspend fun addToFavorites(id: Int) = mediaDao.updateFavoriteStatus(id, true)
    suspend fun removeFromFavorites(id: Int) = mediaDao.updateFavoriteStatus(id, false)
    suspend fun isFavorite(id: Int): Boolean = mediaDao.isFavorite(id) ?: false
    suspend fun getById(id: Int): MediaEntity? = mediaDao.getByIdOnly(id)

    // ---------------- SYNC HELPERS ----------------

    suspend fun clearAllFavorites() {
        val currentFavorites = mediaDao.getFavorites().first()
        currentFavorites.forEach { entity ->
            mediaDao.updateFavoriteStatus(entity.id, false)
        }
    }

    suspend fun setFavorite(id: Int, isFavorite: Boolean) {
        mediaDao.updateFavoriteStatus(id, isFavorite)
    }

    suspend fun setFavorites(ids: List<Int>) {
        ids.forEach { id ->
            mediaDao.updateFavoriteStatus(id, true)
        }
    }

    // ---------------- SEARCH HISTORY (للـ user المحدد) ----------------

    fun getSearchHistory(userId: String?): Flow<List<SearchHistoryEntity>> =
        searchHistoryDao.getRecentHistory(userId)

    suspend fun getSearchHistoryOnce(userId: String?): List<SearchHistoryEntity> =
        searchHistoryDao.getRecentHistoryOnce(userId)

    suspend fun addSearchQuery(query: String, userId: String?) {
        searchHistoryDao.upsertSafe(SearchHistoryEntity(query = query, userId = userId))
    }

    suspend fun clearSearchHistory(userId: String?) =
        searchHistoryDao.deleteAll(userId)

    fun getMovieHistoryFlow(): Flow<List<MoviesViewedEntitty>> = movieHistoryDao.getHistory()

    suspend fun getMovieHistoryOnce(): List<MoviesViewedEntitty> = movieHistoryDao.getHistoryOnce()

    suspend fun addToViewedHistory(item: MoviesViewedEntitty) {
        movieHistoryDao.insertViewedMovie(item)
    }

    suspend fun clearViewedHistory() {
        movieHistoryDao.clearHistory()
    }
}