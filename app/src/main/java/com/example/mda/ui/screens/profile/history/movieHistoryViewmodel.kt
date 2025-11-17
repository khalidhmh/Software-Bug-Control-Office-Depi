package com.example.mda.ui.screens.profile.history

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mda.data.local.entities.MoviesViewedEntitty
import com.example.mda.data.repository.MoviesHistoryRepository
import kotlinx.coroutines.launch

class MoviesHistoryViewModel(
    private val repository: MoviesHistoryRepository
) : ViewModel() {

    val history = repository.history.asLiveData()

    fun saveViewedMovie(movie: MoviesViewedEntitty) {
        viewModelScope.launch {
            repository.addMovie(movie)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}


class MoviesHistoryViewModelFactory(
    private val repository: MoviesHistoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoviesHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MoviesHistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
