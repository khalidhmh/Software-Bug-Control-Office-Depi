package com.example.mda.data.local

import android.util.Log
import com.example.mda.data.local.dao.MediaDao
import com.example.mda.data.local.dao.SearchHistoryDao
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.local.entities.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class LocalRepository(
    private val mediaDao: MediaDao,
    private val searchHistoryDao: SearchHistoryDao
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

    // 🔥🔥 دالة معدلة بالكامل لتحسين الأداء والحفاظ على المفضلة
    suspend fun addOrUpdateAllFromApi(newEntities: List<MediaEntity>) {
        if (newEntities.isEmpty()) return

        try {
            // 1. جلب كل الداتا الموجودة حالياً مرة واحدة (استعلام واحد سريع)
            val currentCachedItems = mediaDao.getAllMediaOnce()

            // 2. تحويلها لـ Map للبحث السريع (ID -> Entity)
            val currentMap = currentCachedItems.associateBy { it.id }

            // 3. دمج الداتا الجديدة مع القديمة
            val finalEntities = newEntities.map { newItem ->
                val oldItem = currentMap[newItem.id]
                if (oldItem != null) {
                    // لو الفيلم موجود، حافظ على حالة الـ Favorite والـ Watchlist
                    newItem.copy(
                        isFavorite = oldItem.isFavorite,
                        isInWatchlist = oldItem.isInWatchlist
                    )
                } else {
                    newItem
                }
            }

            // 4. حفظ الكل مرة واحدة
            mediaDao.insertAll(finalEntities)
            Log.d("RepoDebug", "💾 Successfully saved/updated ${finalEntities.size} items in DB.")

        } catch (e: Exception) {
            Log.e("RepoDebug", "❌ Error saving to DB: ${e.message}")
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

    // ---------------- NEW: helpers for sync (TMDb <> local) ----------------

    /**
     * Clear all favorites flag locally.
     */
    suspend fun clearAllFavorites() {
        val currentFavorites = mediaDao.getFavorites().first()
        currentFavorites.forEach { entity ->
            mediaDao.updateFavoriteStatus(entity.id, false)
        }
    }

    /**
     * Mark a single media item as favorite (or remove favorite).
     */
    suspend fun setFavorite(id: Int, isFavorite: Boolean) {
        mediaDao.updateFavoriteStatus(id, isFavorite)
    }

    /**
     * Mark many ids as favorite. Items that don't exist in DB will be ignored.
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
}