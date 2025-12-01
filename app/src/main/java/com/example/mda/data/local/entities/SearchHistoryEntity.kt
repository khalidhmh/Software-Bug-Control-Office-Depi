package com.example.mda.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * كيان سجل البحث مع فهرسة وتحكم بالتكرار
 */
@Entity(
    tableName = "search_history",
    indices = [
        Index(value = ["query"], unique = true),
        Index(value = ["timestamp"])
    ]
)
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val query: String,
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String? = null
)