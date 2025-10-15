package com.example.mda.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actors")
data class ActorEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val profilePath: String?,
    val biography: String?,
    val birthday: String?,
    val placeOfBirth: String?
)

// Khalid: Entity for Room database to store actor info
