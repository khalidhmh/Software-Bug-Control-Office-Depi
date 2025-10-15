package com.example.mda.ui.actor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mda.data.remote.model.ActorFullDetails
import com.example.mda.data.repository.ActorsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActorViewModel(private val repository: ActorsRepository) : ViewModel() {

    private val _actorFullDetails = MutableStateFlow<ActorFullDetails?>(null)
    val actorFullDetails: StateFlow<ActorFullDetails?> = _actorFullDetails

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _movieCount = MutableStateFlow(0)
    val movieCount: StateFlow<Int> = _movieCount

    private val _tvShowCount = MutableStateFlow(0)
    val tvShowCount: StateFlow<Int> = _tvShowCount

    fun loadActorFullDetails(personId: Int, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            Log.d("ActorDetailsViewModel", "Loading actor details for id=$personId")
            try {
                val details = repository.getFullActorDetails(personId, forceRefresh)
                Log.d("ActorDetailsViewModel", "Actor details fetched: $details")

                if (details == null) {
                    _error.value = "No details found"
                    Log.d("ActorDetailsViewModel", "No details found for id=$personId")
                } else {
                    _actorFullDetails.value = details
                    val castList = details.combined_credits?.cast.orEmpty()
                    _movieCount.value = castList.count { it.media_type == "movie" }
                    _tvShowCount.value = castList.count { it.media_type == "tv" }
                    Log.d("ActorDetailsViewModel", "movieCount=${_movieCount.value}, tvShowCount=${_tvShowCount.value}")
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
                Log.d("ActorDetailsViewModel", "Exception fetching actor: ${e.localizedMessage}")
            } finally {
                _loading.value = false
                Log.d("ActorDetailsViewModel", "Finished loading for id=$personId")
            }
        }
    }



    // Factory
    class ActorViewModelFactory(private val repository: ActorsRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ActorViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ActorViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
