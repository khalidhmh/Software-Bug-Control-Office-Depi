package com.example.mda.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mda.data.local.entities.MoviesViewedEntitty
import com.example.mda.data.local.entities.PersonEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface HistoryDao {

    @Query("SELECT * FROM history ORDER BY viewedAt DESC")
    fun getHistory(): Flow<List<PersonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViewedPerson(person: PersonEntity)

    @Query("DELETE FROM history")
    suspend fun clearHistory()
}
@Dao
interface MovieHistoryDao {

    @Query("SELECT * FROM Movies_history ORDER BY viewedAt DESC")
    fun getHistory(): Flow<List<MoviesViewedEntitty>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViewedMovie(movie: MoviesViewedEntitty)

    @Query("DELETE FROM Movies_history")
    suspend fun clearHistory()
}
