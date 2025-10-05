package com.example.mda.ui.moivebygenrescreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.remote.model.Movie
import com.example.mda.data.repository.MoviesRepository
import kotlinx.coroutines.launch

class GenreDetailsViewModel(private val repository : MoviesRepository) : ViewModel() {

    var movies by mutableStateOf<List<Movie>>(emptyList())
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun loadMoviesByGenre(genreId: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getMoviesByGenre(genreId)
                movies = response.results
                error = null
            } catch (e: Exception) {
                error = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }
}