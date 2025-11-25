package com.example.mda.data.local

import com.example.mda.data.local.dao.MediaDao
import com.example.mda.data.local.dao.SearchHistoryDao
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.local.entities.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class LocalRepository(
    private val mediaDao: MediaDao,
    private val searchHistoryDao: SearchHistoryDao, // ✅ جديد
    // TODO: لما تعمل ViewHistoryDao، ضيفه هنا كمان
) {

    // ---------------- MEDIA DATA ----------------
    fun getAll(): Flow<List<MediaEntity>> = mediaDao.getAll()
    fun getFavorites(): Flow<List<MediaEntity>> = mediaDao.getFavorites()
    fun getWatchlist(): Flow<List<MediaEntity>> = mediaDao.getWatchlist()

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

    suspend fun addOrUpdateAllFromApi(entities: List<MediaEntity>) {
        val existingMap = entities.mapNotNull { e ->
            mediaDao.getByIdOnly(e.id)?.let { e.id to it }
        }.toMap()

        val finalEntities = entities.map { e ->
            val old = existingMap[e.id]
            if (old != null) {
                e.copy(
                    isFavorite = old.isFavorite,
                    isInWatchlist = old.isInWatchlist
                )
            } else e
        }

        mediaDao.insertAll(finalEntities)
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

    // ---------------- NEW: helpers for sync (TMDb <> local) ----------------

    /**
     * Clear all favorites flag locally.
     * Implementation: read current favorites then set their flag to false
     * using existing DAO updateFavoriteStatus(id, false).
     */
    suspend fun clearAllFavorites() {
        val currentFavorites = mediaDao.getFavorites().first()
        currentFavorites.forEach { entity ->
            mediaDao.updateFavoriteStatus(entity.id, false)
        }
    }

    /**
     * Mark a single media item as favorite (or remove favorite).
     * Uses the existing DAO updateFavoriteStatus.
     */
    suspend fun setFavorite(id: Int, isFavorite: Boolean) {
        mediaDao.updateFavoriteStatus(id, isFavorite)
    }

    /**
     * Mark many ids as favorite. Items that don't exist in DB will be ignored.
     * If you want to insert missing items you can use addOrUpdate(...) before calling this.
     */
    suspend fun setFavorites(ids: List<Int>) {
        ids.forEach { id ->
            mediaDao.updateFavoriteStatus(id, true)
        }
    }

    // ---------------- SEARCH HISTORY ----------------
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>> = searchHistoryDao.getRecentHistory()
    suspend fun getSearchHistoryOnce(): List<SearchHistoryEntity> = searchHistoryDao.getRecentHistoryOnce()

    suspend fun addSearchQuery(query: String) {
        searchHistoryDao.upsertSafe(SearchHistoryEntity(query = query))
    }

    suspend fun clearSearchHistory() = searchHistoryDao.deleteAll()

    // ---------------- VIEW HISTORY (TODO) ----------------
    // TODO: لما تعمل ViewHistoryDao اضف:
    // fun getViewHistory(): Flow<List<ViewHistoryEntity>>
    // suspend fun addViewHistory(entity: ViewHistoryEntity)
}
