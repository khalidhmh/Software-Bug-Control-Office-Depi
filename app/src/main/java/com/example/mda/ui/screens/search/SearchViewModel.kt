package com.example.mda.ui.screens.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.local.dao.SearchHistoryDao
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.local.entities.SearchHistoryEntity
import com.example.mda.data.repository.MoviesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ---------------- UI STATE ----------------
sealed interface UiState {
    data object Idle : UiState
    data class History(val items: List<SearchHistoryEntity>) : UiState
    data object Loading : UiState
    data class Success(val results: List<MediaEntity>) : UiState
    data object Empty : UiState
    data class Error(val message: String) : UiState
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(
    val repository: MoviesRepository,
    private val historyDao: SearchHistoryDao,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val query = savedStateHandle.getStateFlow("query", "")
    val selectedFilter = savedStateHandle.getStateFlow("filter", "all")

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> =
        _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Idle)

    private val searchTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    var currentUserId: String? = null

    init {
        observeSearchFlow()
    }

    // ---------------- MAIN SEARCH FLOW ----------------
    private fun observeSearchFlow() {
        combine(
            query.debounce(400).distinctUntilChanged(),
            selectedFilter,
            searchTrigger.onStart { emit(Unit) }
        ) { q, f, _ ->
            q.trim() to f.lowercase()
        }.flatMapLatest { (q, f) ->
            if (q.isBlank()) {
                emitIdleHistory()
                flowOf(UiState.History(emptyList()))
            } else {
                performSearchFlow(q, f)
            }
        }.onEach { _uiState.value = it }
            .launchIn(viewModelScope)
    }

    private fun performSearchFlow(q: String, f: String): Flow<UiState> = flow {
        emit(UiState.Loading)
        try {
            val apiResults = repository.searchByType(q, f)
            val results = if (apiResults.isEmpty()) {
                val localData = repository.getTrendingMedia()
                localData.filter {
                    val title = (it.title ?: it.name ?: "").lowercase()
                    title.contains(q.lowercase())
                }
            } else apiResults

            if (results.isEmpty()) emit(UiState.Empty) else emit(UiState.Success(results))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Network Error"))
        }
    }

    // ---------------- USER ACTIONS ----------------
    fun submitSearch() {
        val q = query.value.trim()
        if (q.isBlank() || currentUserId == null) return

        viewModelScope.launch {
            searchTrigger.tryEmit(Unit)
            historyDao.upsertSafe(
                SearchHistoryEntity(query = q, userId = currentUserId)
            )
        }
    }
    fun onQueryChange(newValue: String) {
        savedStateHandle["query"] = newValue
    }

    fun onFilterSelected(newFilter: String) {
        val lower = newFilter.lowercase()
        savedStateHandle["filter"] = lower
        searchTrigger.tryEmit(Unit)
    }

    // ---------------- HISTORY ----------------
    fun observeHistory() {
        viewModelScope.launch {
            val uid = currentUserId
            if (uid != null) {
                historyDao.getRecentHistory(uid).collect { list ->
                    _uiState.value = UiState.History(list)
                }
            } else {
                _uiState.value = UiState.History(emptyList())
            }
        }
    }

    fun emitIdleHistory() {
        viewModelScope.launch {
            val uid = currentUserId
            if (uid != null) {
                val list = historyDao.getRecentHistoryOnce(uid)
                _uiState.value = UiState.History(list)
            } else {
                _uiState.value = UiState.History(emptyList())
            }
        }
    }

    fun clearHistory() = viewModelScope.launch {
        currentUserId?.let { uid -> historyDao.deleteAll(uid) }
    }

    fun deleteOne(q: String) = viewModelScope.launch {
        currentUserId?.let { uid -> historyDao.delete(q, uid) }
    }
}