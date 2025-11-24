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

enum class MediaTypeFilter {
    ALL, MOVIE, TV
}

enum class SortType {
    NONE, AZ, ZA
}

data class ActorFilters(
    val mediaType: MediaTypeFilter = MediaTypeFilter.ALL,
    val sort: SortType = SortType.NONE,
    val minWorks: Int = 0,
    val firstWorkYear: Int? = null
)

class ActorViewModel(private val repository: ActorsRepository) : ViewModel() {

    private val _state = MutableStateFlow<ActorUiState>(ActorUiState.Loading)
    val state: StateFlow<ActorUiState> = _state.asStateFlow()

    private val _viewType = MutableStateFlow(ViewType.GRID)
    val viewType: StateFlow<ViewType> = _viewType.asStateFlow()

    private val _filters = MutableStateFlow(ActorFilters())
    val filters: StateFlow<ActorFilters> = _filters.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val gson = Gson()
    private val type = object : TypeToken<List<KnownFor>>() {}.type

    // Master list to hold all actors
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

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applySearch()
    }

    private fun applySearch() {
        val query = _searchQuery.value
        val filtered = if (query.isBlank()) {
            _allActors
        } else {
            _allActors.filter { actor ->
                actor.name.contains(query, ignoreCase = true)
            }
        }

        // Apply filters on the search results
        val filters = _filters.value
        val finalList = filtered
            .filter { actor ->
                when (filters.mediaType) {
                    MediaTypeFilter.ALL -> true
                    MediaTypeFilter.MOVIE -> actor.knownFor?.any { it.mediaType == "movie" } == true
                    MediaTypeFilter.TV -> actor.knownFor?.any { it.mediaType == "tv" } == true
                }
            }
            .filter { actor -> (actor.knownFor?.size ?: 0) >= filters.minWorks }
            .let { list ->
                when (filters.sort) {
                    SortType.NONE -> list
                    SortType.AZ -> list.sortedBy { it.name }
                    SortType.ZA -> list.sortedByDescending { it.name }
                }
            }

        _state.value = ActorUiState.Success(finalList)
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

                    // Update master list
                    _allActors = updatedList

                    // Apply search + filters
                    applySearch()

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

    // -----------------------
    //      FILTERS LOGIC
    // -----------------------

    fun updateMediaType(type: MediaTypeFilter) {
        _filters.value = _filters.value.copy(mediaType = type)
        applySearch()
    }

    fun updateSort(sort: SortType) {
        _filters.value = _filters.value.copy(sort = sort)
        applySearch()
    }

    fun updateMinWorks(min: Int) {
        _filters.value = _filters.value.copy(minWorks = min)
        applySearch()
    }
}
