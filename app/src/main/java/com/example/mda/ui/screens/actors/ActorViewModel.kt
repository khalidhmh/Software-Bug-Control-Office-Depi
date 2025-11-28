package com.example.mda.ui.screens.actors

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.remote.model.Actor
import com.example.mda.data.remote.model.KnownFor
import com.example.mda.data.repository.ActorsRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ViewType { GRID, LIST }

class ActorViewModel(private val repository: ActorsRepository) : ViewModel() {

    private val _state = MutableStateFlow<ActorUiState>(ActorUiState.Loading)
    val state: StateFlow<ActorUiState> = _stat.asStateFlow()

    private val _viewType = MutableStateFlow(ViewType.GRID)
    val viewType: StateFlow<ViewType> = _viewType.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val gson = Gson()
    private val type = object : TypeToken<List<KnownFor>>() {}.type

    private var _allActors: List<Actor> = emptyList()
    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false

    init {
        loadActors()
    }

    fun toggleViewType() {
        _viewType.value = if (_viewType.value == ViewType.GRID) ViewType.LIST else ViewType.GRID
    }

    fun loadActors(forceRefresh: Boolean = false) {
        if (isLoading && !forceRefresh) return
        if (forceRefresh) {
            currentPage = 1
            isLastPage = false
        }

        Log.d("ActorVM", "loadActors called, forceRefresh=$forceRefresh, currentPage=$currentPage")
        viewModelScope.launch {
            isLoading = true
            if (forceRefresh) _isRefreshing.value = true

            if (currentPage == 1 && !forceRefresh) {
                _state.value = ActorUiState.Loading
            }

            try {
                val newEntities = repository.getPopularActorsWithCache(page = currentPage)
                Log.d("ActorVM", "Fetched ${newEntities.size} actors from repository")

                if (newEntities.isEmpty() && currentPage == 1) {
                    _state.value = ActorUiState.Error("No actors found", ErrorType.NetworkError)
                    isLastPage = true
                } else {
                    val currentList =
                        if (currentPage == 1) emptyList()
                        else (_state.value as? ActorUiState.Success)?.actors.orEmpty()

                    val updatedList =
                        (currentList + newEntities.map { entity ->
                            val knownForList = try {
                                val field = entity::class.java.getDeclaredField("knownFor")
                                field.isAccessible = true
                                val jsonValue = field.get(entity) as? String
                                jsonValue?.let { gson.fromJson<List<KnownFor>>(it, type) } ?: emptyList()
                            } catch (e: Exception) {
                                emptyList()
                            }

                            Actor(
                                id = entity.id,
                                name = entity.name,
                                profilePath = entity.profilePath,
                                biography = entity.biography,
                                birthday = entity.birthday,
                                placeOfBirth = entity.placeOfBirth,
                                knownForDepartment = entity.knownForDepartment,
                                knownFor = knownForList
                            )
                        }).distinctBy { it.id }

                    _allActors = updatedList
                    _state.value = ActorUiState.Success(_allActors)

                    currentPage++
                    if (newEntities.isEmpty()) isLastPage = true
                }

            } catch (e: Exception) {
                if (_allActors.isEmpty()) {
                    _state.value = ActorUiState.Error(e.message ?: "Unknown error", ErrorType.NetworkError)
                }
            } finally {
                isLoading = false
                _isRefreshing.value = false
            }
        }
    }

    fun loadMoreActors() {
        if (isLoading || isLastPage) return
        loadActors(forceRefresh = false)
    }

    fun retry() {
        loadActors(forceRefresh = true)
    }
}