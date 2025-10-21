package com.example.mda.data.local.dao

import androidx.room.*
import com.example.mda.data.local.entities.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * SearchHistoryDao
 * - يمنع التكرار عبر UNIQUE index.
 * - يستخدم COLLATE NOCASE لجعل البحث غير حساس لحالة الحروف.
 * - يحتوي على upsertSafe (Insert IGNORE + Update timestamp).
 * - يدعم استرجاع التاريخ كسريان Flow أو دفعة واحدة.
 */
@Dao
interface SearchHistoryDao {

    /** آخر 10 عمليات بحث - لحظيًا عبر Flow */
    @Query(
        "SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10"
    )
    fun getRecentHistory(): Flow<List<SearchHistoryEntity>>

    /** آخر 10 عمليات بحث مرة واحدة للاقتراحات */
    @Query(
        "SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10"
    )
    suspend fun getRecentHistoryOnce(): List<SearchHistoryEntity>

    /** إدراج بدون تكرار */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: SearchHistoryEntity): Long

    /** تحديث timestamp لعناصر موجودة */
    @Update
    suspend fun update(entity: SearchHistoryEntity)

    /**
     * Upsert يدوي لمنع التكرار:
     * في حالة وجود الاستعلام بالفعل، يتم تحديث timestamp.
     */
    @Transaction
    suspend fun upsertSafe(entity: SearchHistoryEntity) {
        val id = insert(entity)
        if (id == -1L) {
            update(entity.copy(timestamp = System.currentTimeMillis()))
        }
    }

    /** حذف عنصر معين مع تجاهل حالة الأحرف */
    @Query("DELETE FROM search_history WHERE query = :query COLLATE NOCASE")
    suspend fun delete(query: String)

    /** حذف جميع السجلات */
    @Query("DELETE FROM search_history")
    suspend fun deleteAll()
}