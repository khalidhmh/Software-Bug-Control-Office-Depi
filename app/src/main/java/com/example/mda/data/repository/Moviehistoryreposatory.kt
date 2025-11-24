package com.example.mda.data.repository


import com.example.mda.data.local.dao.MovieHistoryDao
import com.example.mda.data.local.entities.MoviesViewedEntitty
import kotlinx.coroutines.flow.Flow

class MoviesHistoryRepository(private val dao: MovieHistoryDao) {

    val history: Flow<List<MoviesViewedEntitty>> = dao.getHistory()

    suspend fun addMovie(movie: MoviesViewedEntitty) {
        dao.insertViewedMovie(movie)
    }

    suspend fun clearHistory() {
        dao.clearHistory()
    }
}
