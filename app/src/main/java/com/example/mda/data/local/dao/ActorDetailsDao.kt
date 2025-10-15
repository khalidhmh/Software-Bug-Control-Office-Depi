package com.example.mda.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert // ✅ Khaled Edit: استخدام Upsert
// ❌ REMOVED (commented out): كنا بنستخدم Insert مع REPLACE
// import androidx.room.Insert
// import androidx.room.OnConflictStrategy
import com.example.mda.data.local.entities.ActorDetailsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActorDetailsDao {

// ❌ OLD:
// @Query("SELECT * FROM actor_details WHERE id = :id")
// fun getDetails(id: Int): Flow<ActorDetailsEntity?>

    // ✅ NEW: LIMIT 1 عشان نضمن صف واحد (مع إن id PrimaryKey بس ده أوضح)
    @Query("SELECT * FROM actor_details WHERE id = :id LIMIT 1")
    fun getDetails(id: Int): Flow<ActorDetailsEntity?>

// ❌ OLD:
// @Insert(onConflict = OnConflictStrategy.REPLACE)
// suspend fun upsert(details: ActorDetailsEntity)

    // ✅ NEW: Upsert بيسهّل Insert/Update تلقائيًا
    @Upsert
    suspend fun upsert(details: ActorDetailsEntity)
}