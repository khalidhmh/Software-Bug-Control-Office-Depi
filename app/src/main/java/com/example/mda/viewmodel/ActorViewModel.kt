package com.example.mda.ui.actor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.remote.model.ActorFullDetails
import com.example.mda.data.remote.model.MovieResponse
import com.example.mda.data.repository.ActorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider

class ActorViewModel(private val repository: ActorRepository) : ViewModel() {

    private val _popularMovies = MutableStateFlow<MovieResponse?>(null)
    val popularMovies: StateFlow<MovieResponse?> = _popularMovies

    private val _actorFullDetails = MutableStateFlow<ActorFullDetails?>(null)
    val actorFullDetails: StateFlow<ActorFullDetails?> = _actorFullDetails

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadPopularMovies() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = repository.getPopularMovies()
                if (response.isSuccessful) {
                    _popularMovies.value = response.body()
                } else {
                    _error.value = "Failed to load popular movies"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadActorFullDetails(personId: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = repository.getFullActorDetails(personId)
                if (response.isSuccessful) {
                    _actorFullDetails.value = response.body()
                } else {
                    _error.value = "Failed to load actor details"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }
}





class ActorViewModelFactory(private val repository: ActorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
