package com.example.mda.ui.screens.moivebygenrescreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.repository.MoviesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GenreDetailsViewModel(private val repository: MoviesRepository) : ViewModel() {

    var movies by mutableStateOf<List<MediaEntity>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    private var currentPage = 1
    private var canLoadMore = true

    fun loadMoviesByGenre(genreId: Int) {
        if (isLoading || !canLoadMore) return

        viewModelScope.launch {
            isLoading = true
            try {
                val newMovies = repository.getMoviesByGenre(genreId, currentPage)
                if (newMovies.isNotEmpty()) {
                    movies = movies + newMovies
                    currentPage++
                } else {
                    canLoadMore = false
                }
                error = null
            } catch (e: Exception) {
                error = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    fun resetAndLoad(genreId: Int) {
        movies = emptyList()
        currentPage = 1
        canLoadMore = true
        loadMoviesByGenre(genreId)
    }
}
