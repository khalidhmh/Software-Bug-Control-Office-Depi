package com.example.mda.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class PersonEntity(
    @PrimaryKey val id: Int,
    val name: String?,
    val profilePath: String?,
    val knownForDepartment: String?,
    val viewedAt: Long = System.currentTimeMillis() // timestamp
)