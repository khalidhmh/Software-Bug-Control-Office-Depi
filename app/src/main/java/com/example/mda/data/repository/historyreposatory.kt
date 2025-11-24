package com.example.mda.data.repository

import com.example.mda.data.local.dao.HistoryDao
import com.example.mda.data.local.entities.PersonEntity

class HistoryRepository(private val dao: HistoryDao) {

    val history = dao.getHistory()

    suspend fun addPerson(person: PersonEntity) {
        dao.insertViewedPerson(person)
    }

    suspend fun clearHistory() {
        dao.clearHistory()
    }
}
