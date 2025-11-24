package com.example.mda.data.local.entities


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Movies_history")
data class MoviesViewedEntitty(
    @PrimaryKey val id: Int,
    val name: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val mediaType: String?,
    val viewedAt: Long = System.currentTimeMillis() // timestamp
)