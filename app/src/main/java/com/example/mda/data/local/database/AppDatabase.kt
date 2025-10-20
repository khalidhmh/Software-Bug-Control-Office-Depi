package com.example.mda.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mda.data.local.converters.Converters
import com.example.mda.data.local.converters.ListConverters
import com.example.mda.data.local.dao.ActorDao
import com.example.mda.data.local.dao.ActorDetailsDao
import com.example.mda.data.local.dao.MediaDao
import com.example.mda.data.local.dao.SearchHistoryDao
import com.example.mda.data.local.entities.ActorDetailsEntity
import com.example.mda.data.local.entities.ActorEntity
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.local.entities.SearchHistoryEntity

@Database(
    entities = [
        MediaEntity::class,
        ActorEntity::class,
        ActorDetailsEntity::class,
        SearchHistoryEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
    abstract fun actorDao(): ActorDao
    abstract fun actorDetailsDao(): ActorDetailsDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}