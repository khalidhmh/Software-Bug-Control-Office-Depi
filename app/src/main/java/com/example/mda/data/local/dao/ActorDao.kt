package com.example.mda.data.local.dao

import androidx.room.*
import com.example.mda.data.local.entities.ActorEntity

@Dao
interface ActorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(actor: ActorEntity)

    @Query("SELECT * FROM actors WHERE id = :id")
    suspend fun getDetails(id: Int): ActorEntity?

    @Query("SELECT * FROM actors")
    suspend fun getAllActors(): List<ActorEntity>


    // Khalid: DAO for caching actor info and retrieving details
}
