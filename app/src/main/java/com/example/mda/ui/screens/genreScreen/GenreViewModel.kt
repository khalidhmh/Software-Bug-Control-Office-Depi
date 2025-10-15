package com.example.mda.ui.screens.genre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.remote.model.Genre
import com.example.mda.data.repository.MoviesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GenreViewModel(private val repository: MoviesRepository) : ViewModel() {

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadGenres()
    }

    fun loadGenres() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val genreList = repository.getGenres()
                _genres.value = genreList
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to load genres"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshGenres() {
        loadGenres()
    }
}
