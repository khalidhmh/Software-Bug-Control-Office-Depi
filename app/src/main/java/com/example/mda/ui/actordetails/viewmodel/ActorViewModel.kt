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

    // ✅ Movie and TV show count
    private val _movieCount = MutableStateFlow(0)
    val movieCount: StateFlow<Int> = _movieCount

    private val _tvShowCount = MutableStateFlow(0)
    val tvShowCount: StateFlow<Int> = _tvShowCount

    fun loadActorFullDetails(personId: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = repository.getFullActorDetails(personId)
                if (response.isSuccessful) {
                    val actorDetails = response.body()
                    _actorFullDetails.value = actorDetails


                    val castList = actorDetails?.combined_credits?.cast ?: emptyList()
                    _movieCount.value = castList.count { it.media_type == "movie" }
                    _tvShowCount.value = castList.count { it.media_type == "tv" }

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

class ActorViewModelFactory : ViewModelProvider.Factory {
    private val repository = ActorRepository()
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
