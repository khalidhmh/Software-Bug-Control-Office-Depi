package com.example.mda.ui.screens.actors

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.remote.model.Actor
import com.example.mda.data.repository.ActorsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class ViewType { GRID, LIST }

class ActorViewModel(private val repository: ActorsRepository) : ViewModel() {

    private val _state = MutableStateFlow<ActorUiState>(ActorUiState.Loading)
    val state: StateFlow<ActorUiState> = _state

    private val _viewType = MutableStateFlow(ViewType.GRID)
    val viewType: StateFlow<ViewType> = _viewType

    // Pagination variables
    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false

    init {
        Log.d("ActorVM", "Initializing ActorViewModel")
        loadActors()
    }

    fun toggleViewType() {
        _viewType.value = if (_viewType.value == ViewType.GRID) ViewType.LIST else ViewType.GRID
        Log.d("ActorVM", "ViewType toggled: ${_viewType.value}")
    }

    fun loadActors(forceRefresh: Boolean = false) {
        if (isLoading) {
            Log.d("ActorVM", "loadActors ignored: already loading")
            return
        }

        Log.d("ActorVM", "loadActors called, forceRefresh=$forceRefresh")
        viewModelScope.launch {
            isLoading = true
            _state.value = ActorUiState.Loading
            try {
                currentPage = 1
                isLastPage = false

                val entities = repository.getPopularActorsWithCache(page = currentPage)
                Log.d("ActorVM", "Fetched ${entities.size} actors from repository")

                if (entities.isNotEmpty()) {
                    _state.value = ActorUiState.Success(
                        entities.map { entity ->
                            Actor(
                                id = entity.id,
                                name = entity.name,
                                profilePath = entity.profilePath,
                                biography = entity.biography,
                                birthday = entity.birthday,
                                placeOfBirth = entity.placeOfBirth,
                                knownFor = emptyList()
                            )
                        }
                    )
                    Log.d("ActorVM", "loadActors success, updated state")
                    currentPage++
                } else {
                    _state.value = ActorUiState.Error("No actors found", ErrorType.NetworkError)
                    Log.d("ActorVM", "loadActors error: No actors found")
                    isLastPage = true
                }

            } catch (e: Exception) {
                _state.value = ActorUiState.Error(e.message ?: "Unknown error", ErrorType.NetworkError)
                Log.d("ActorVM", "loadActors exception: ${e.localizedMessage}")
            } finally {
                isLoading = false
                Log.d("ActorVM", "loadActors finished, isLoading=false")
            }
        }
    }

    fun loadMoreActors() {
        if (isLoading || isLastPage) {
            Log.d("ActorVM", "loadMoreActors ignored, isLoading=$isLoading, isLastPage=$isLastPage")
            return
        }

        Log.d("ActorVM", "loadMoreActors called, currentPage=$currentPage")
        viewModelScope.launch {
            isLoading = true
            try {
                val newEntities = repository.getPopularActorsWithCache(page = currentPage)
                Log.d("ActorVM", "Fetched ${newEntities.size} more actors from repository")

                val currentList = (_state.value as? ActorUiState.Success)?.actors.orEmpty()
                val updated = (currentList + newEntities.map { entity ->
                    Actor(
                        id = entity.id,
                        name = entity.name,
                        profilePath = entity.profilePath,
                        biography = entity.biography,
                        birthday = entity.birthday,
                        placeOfBirth = entity.placeOfBirth,
                        knownFor = emptyList()
                    )
                }).distinctBy { it.id }

                _state.value = ActorUiState.Success(updated)
                Log.d("ActorVM", "loadMoreActors success, updated state with ${updated.size} actors")

                if (newEntities.isEmpty()) {
                    isLastPage = true
                    Log.d("ActorVM", "Reached last page")
                } else {
                    currentPage++
                    Log.d("ActorVM", "Incremented currentPage to $currentPage")
                }

            } catch (e: Exception) {
                _state.value = ActorUiState.Error(e.message ?: "Unknown error", ErrorType.NetworkError)
                Log.d("ActorVM", "loadMoreActors exception: ${e.localizedMessage}")
            } finally {
                isLoading = false
                Log.d("ActorVM", "loadMoreActors finished, isLoading=false")
            }
        }
    }

    fun retry() {
        Log.d("ActorVM", "retry called")
        loadActors(forceRefresh = true)
    }
}
