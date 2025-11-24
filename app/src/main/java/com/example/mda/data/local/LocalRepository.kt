package com.example.mda.data.local

import com.example.mda.data.local.dao.MediaDao
import com.example.mda.data.local.entities.MediaEntity
import kotlinx.coroutines.flow.Flow

class LocalRepository(private val dao: MediaDao) {

    fun getAll(): Flow<List<MediaEntity>> = dao.getAll()
    fun getFavorites(): Flow<List<MediaEntity>> = dao.getFavorites()
    fun getWatchlist(): Flow<List<MediaEntity>> = dao.getWatchlist()

    // تحديث مباشر بدون التحقق (يُستخدم عند تحديث الـ favorites يدوياً)
    suspend fun addOrUpdate(item: MediaEntity) = dao.upsert(item)
    
    // تحديث مع الحفاظ على حالة المفضلة والـ Watchlist (يُستخدم عند جلب البيانات من الـ API)
    suspend fun addOrUpdateFromApi(item: MediaEntity) {
        val existing = dao.getByIdOnly(item.id)
        val finalItem = if (existing != null) {
            item.copy(
                isFavorite = existing.isFavorite,
                isInWatchlist = existing.isInWatchlist
            )
        } else {
            item
        }
        dao.upsert(finalItem)
    }
    
    suspend fun delete(item: MediaEntity) = dao.delete(item)
    suspend fun clearNonSaved() = dao.clearNonSaved()
    
    // حفظ عدة entities مع الحفاظ على حالة المفضلة والـ Watchlist
    suspend fun addOrUpdateAllFromApi(entities: List<MediaEntity>) {
        // جلب كل الـ entities الموجودة مرة واحدة لتحسين الأداء
        val existingMap = entities.mapNotNull { entity ->
            dao.getByIdOnly(entity.id)?.let { entity.id to it }
        }.toMap()
        
        // تحديث كل entity مع الحفاظ على الـ flags
        val finalEntities = entities.map { entity ->
            val existing = existingMap[entity.id]
            if (existing != null) {
                entity.copy(
                    isFavorite = existing.isFavorite,
                    isInWatchlist = existing.isInWatchlist
                )
            } else {
                entity
            }
        }
        
        dao.insertAll(finalEntities)
    }
    
    suspend fun addOrUpdateAll(entities: List<MediaEntity>) {
        dao.insertAll(entities)
    }

    // Favorites Management
    suspend fun toggleFavorite(id: Int): Boolean {
        val currentStatus = dao.isFavorite(id) ?: false
        val newStatus = !currentStatus
        dao.updateFavoriteStatus(id, newStatus)
        return newStatus
    }

    suspend fun addToFavorites(id: Int) = dao.updateFavoriteStatus(id, true)
    suspend fun removeFromFavorites(id: Int) = dao.updateFavoriteStatus(id, false)
    suspend fun isFavorite(id: Int): Boolean = dao.isFavorite(id) ?: false
    suspend fun getById(id: Int): MediaEntity? = dao.getByIdOnly(id)

}
