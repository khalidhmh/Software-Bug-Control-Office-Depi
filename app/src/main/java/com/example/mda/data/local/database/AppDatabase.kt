package com.example.mda.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mda.data.local.converters.Converters
import com.example.mda.data.local.dao.ActorDao
import com.example.mda.data.local.dao.ActorDetailsDao
import com.example.mda.data.local.dao.HistoryDao
import com.example.mda.data.local.dao.MediaDao
import com.example.mda.data.local.dao.MovieHistoryDao
import com.example.mda.data.local.dao.SearchHistoryDao
import com.example.mda.data.local.entities.ActorDetailsEntity
import com.example.mda.data.local.entities.ActorEntity
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.local.entities.MoviesViewedEntitty
import com.example.mda.data.local.entities.PersonEntity
import com.example.mda.data.local.entities.SearchHistoryEntity

@Database(
    entities = [
        MediaEntity::class,
        ActorEntity::class,
        ActorDetailsEntity::class,
        SearchHistoryEntity::class,
        PersonEntity::class,
        MoviesViewedEntitty::class
    ],
    version = 12,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mediaDao(): MediaDao
    abstract fun actorDao(): ActorDao
    abstract fun actorDetailsDao(): ActorDetailsDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun historyDao(): HistoryDao
    abstract fun MoviehistoryDao(): MovieHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mda_database"
                ).fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}
