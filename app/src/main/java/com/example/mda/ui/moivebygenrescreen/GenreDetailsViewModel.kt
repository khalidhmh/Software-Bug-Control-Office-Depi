package com.example.mda.ui.moivebygenrescreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.remote.model.Movie
import com.example.mda.data.repository.MoviesRepository
import kotlinx.coroutines.launch

class GenreDetailsViewModel(private val repository: MoviesRepository) : ViewModel() {

    var movies by mutableStateOf<List<Movie>>(emptyList())
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
                val response = repository.getMoviesByGenre(genreId, currentPage)

                if (response.results.isNotEmpty()) {
                    movies = movies + response.results
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

    // to make the Screen load the first page if you back
    fun resetAndLoad(genreId: Int) {
        movies = emptyList()
        currentPage = 1
        canLoadMore = true
        loadMoviesByGenre(genreId)
    }
}
