package com.example.mda.ui.screens.movieDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.repository.MovieDetailsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    private val repository: MovieDetailsRepository
) : ViewModel() {

    private val _details = MutableStateFlow<MediaEntity?>(null)
    val details: StateFlow<MediaEntity?> = _details

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /** Load Movie details, fallback to cache if needed */
    fun loadMovieDetails(id: Int, fromNetwork: Boolean = false) {
        load(id, isTv = false, fromNetwork = fromNetwork)
    }

    /** Load TV details, fallback to cache if needed */
    fun loadTvDetails(id: Int, fromNetwork: Boolean = false) {
        load(id, isTv = true, fromNetwork = fromNetwork)
    }

    private fun load(id: Int, isTv: Boolean, fromNetwork: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val media: MediaEntity? = if (fromNetwork) {
                    if (isTv) repository.getTvById(id)
                    else repository.getMovieById(id)
                } else {
                    if (isTv) repository.getCachedTv(id) ?: repository.getTvById(id)
                    else repository.getCachedMovie(id) ?: repository.getMovieById(id)
                }

                if (media != null) _details.value = media
                else _error.value = "No data available"

            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
