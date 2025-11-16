package com.example.mda.ui.screens.actors

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.remote.model.Actor
import com.example.mda.data.repository.ActorsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.mda.data.remote.model.KnownFor
import kotlin.collections.emptyList
import kotlin.collections.map


enum class ViewType { GRID, LIST }

class ActorViewModel(private val repository: ActorsRepository) : ViewModel() {

    private val _state = MutableStateFlow<ActorUiState>(ActorUiState.Loading)
    val state: StateFlow<ActorUiState> = _state

    private val _viewType = MutableStateFlow(ViewType.GRID)
    val viewType: StateFlow<ViewType> = _viewType

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // --- Master list to hold all actors ---
    private var _allActors: List<Actor> = emptyList()

    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false

    val gson = Gson()
    val type = object : com.google.gson.reflect.TypeToken<List<KnownFor>>() {}.type

    init {
        loadActors()
    }

    fun toggleViewType() {
        _viewType.value = if (_viewType.value == ViewType.GRID) ViewType.LIST else ViewType.GRID
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applySearch()
    }

    private fun applySearch() {
        val query = _searchQuery.value
        val filtered = if (query.isBlank()) {
            _allActors // reset to full list if search is empty
        } else {
            _allActors.filter { actor ->
                actor.name.contains(query, ignoreCase = true)
            }
        }
        _state.value = ActorUiState.Success(filtered)
    }

    fun loadActors(forceRefresh: Boolean = false) {
        if (isLoading && !forceRefresh) return
        if (forceRefresh) {
            currentPage = 1
            isLastPage = false
        }

        viewModelScope.launch {
            isLoading = true
            if (forceRefresh) _isRefreshing.value = true
            if (currentPage == 1 && !forceRefresh) _state.value = ActorUiState.Loading

            try {
                val newEntities = repository.getPopularActorsWithCache(page = currentPage)

                val currentList = if (currentPage == 1) emptyList() else _allActors
                val updatedList = (currentList + newEntities.map { entity ->
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
                        knownFor = knownForList
                    )
                }).distinctBy { it.id }

                // --- Update master list ---
                _allActors = updatedList

                // Apply search on the master list
                applySearch()

                currentPage++
                if (newEntities.isEmpty()) isLastPage = true

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




