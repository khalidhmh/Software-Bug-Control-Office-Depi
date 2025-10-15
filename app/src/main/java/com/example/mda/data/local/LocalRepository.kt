package com.example.mda.data.local

import com.example.mda.data.local.dao.MediaDao
import com.example.mda.data.local.entities.MediaEntity
import kotlinx.coroutines.flow.Flow

class LocalRepository(private val dao: MediaDao) {

    fun getAll(): Flow<List<MediaEntity>> = dao.getAll()
    fun getFavorites(): Flow<List<MediaEntity>> = dao.getFavorites()
    fun getWatchlist(): Flow<List<MediaEntity>> = dao.getWatchlist()

    suspend fun addOrUpdate(item: MediaEntity) = dao.upsert(item)
    suspend fun delete(item: MediaEntity) = dao.delete(item)
    suspend fun clearNonSaved() = dao.clearNonSaved()
    suspend fun addOrUpdateAll(entities: List<MediaEntity>) {
        entities.forEach { addOrUpdate(it) }
    }

}
