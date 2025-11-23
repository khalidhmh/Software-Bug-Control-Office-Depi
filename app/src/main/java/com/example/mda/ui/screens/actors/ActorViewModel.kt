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
    val minWorks: Int = 0 ,// الفلتر الجديد: أقل عدد أعمال
    val firstWorkYear: Int? = null
)

class ActorViewModel(private val repository: ActorsRepository) : ViewModel() {

    private val _state = MutableStateFlow<ActorUiState>(ActorUiState.Loading)
    val state: StateFlow<ActorUiState> = _state

    private val _viewType = MutableStateFlow(ViewType.GRID)
    val viewType: StateFlow<ViewType> = _viewType

    private val _filters = MutableStateFlow(ActorFilters())
    val filters: StateFlow<ActorFilters> = _filters.asStateFlow()

    val gson = Gson()
    val type = object : TypeToken<List<KnownFor>>() {}.type

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    // Pagination variables
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

        viewModelScope.launch {
            isLoading = true
            if (forceRefresh) _isRefreshing.value = true

            if (currentPage == 1 && !forceRefresh) {
                _state.value = ActorUiState.Loading
            }

            try {
                val newEntities = repository.getPopularActorsWithCache(page = currentPage)

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

                    _state.value = ActorUiState.Success(updatedList)

                    // ⭐ تطبيق الفلاتر بعد تحميل البيانات ⭐
                    applyFilters()

                    currentPage++
                    if (newEntities.isEmpty()) isLastPage = true
                }
            } catch (e: Exception) {
                if ((_state.value as? ActorUiState.Success)?.actors.orEmpty().isEmpty()) {
                    _state.value = ActorUiState.Error(e.message ?: "Unknown error", ErrorType.NetworkError)
                }
            } finally {
                isLoading = false
                _isRefreshing.value = false
            }
        }
    }

    fun loadMoreActors() {
        if (!isLoading && !isLastPage) loadActors()
    }

    fun retry() {
        loadActors(forceRefresh = true)
    }

    // -----------------------
    //      FILTERS LOGIC
    // -----------------------

    fun updateMediaType(type: MediaTypeFilter) {
        _filters.value = _filters.value.copy(mediaType = type)
        applyFilters()
    }

    fun updateSort(sort: SortType) {
        _filters.value = _filters.value.copy(sort = sort)
        applyFilters()
    }

    fun updateMinWorks(min: Int) {
        _filters.value = _filters.value.copy(minWorks = min)
        applyFilters()
    }

    private fun applyFilters() {
        val currentState = _state.value
        if (currentState !is ActorUiState.Success) return

        val original = currentState.actors
        val filters = _filters.value

        val filtered = original
            .filter { actor ->
                // Filter by media type
                when (filters.mediaType) {
                    MediaTypeFilter.ALL -> true
                    MediaTypeFilter.MOVIE -> actor.knownFor?.any { it.mediaType == "movie" } == true
                    MediaTypeFilter.TV -> actor.knownFor?.any { it.mediaType == "tv" } == true
                }
            }
            // Filter by minimum number of works
            .filter { actor -> (actor.knownFor?.size ?: 0) >= filters.minWorks }
            .let { list ->
                when (filters.sort) {
                    SortType.NONE -> list
                    SortType.AZ -> list.sortedBy { it.name }
                    SortType.ZA -> list.sortedByDescending { it.name }
                }
            }

        _state.value = ActorUiState.Success(filtered)
    }
}
