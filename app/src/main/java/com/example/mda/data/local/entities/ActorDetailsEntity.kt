package com.example.mda.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mda.data.local.converters.Converters

@Entity(tableName = "actor_details")
@TypeConverters(Converters::class)
data class ActorDetailsEntity(
    @PrimaryKey val id: Int,
    val name: String?,
    val biography: String?,
    val birthday: String?,
    val profilePath: String?,
    val placeOfBirth: String?,
    val movieCredits: List<Int> = emptyList(),
    val tvCredits: List<Int> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

