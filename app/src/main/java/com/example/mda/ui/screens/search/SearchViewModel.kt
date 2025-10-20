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

/**
 * SearchViewModel النهائي
 * يعالج مشكلة الفلاتر والـHistory والـdebounce كلها.
 */
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
    private val repository: MoviesRepository,
    private val historyDao: SearchHistoryDao,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val CACHE_TTL_MS = 1000L * 60 * 5
    private val cachedResults =
        mutableMapOf<Pair<String, String>, Pair<Long, List<MediaEntity>>>()

    val query = savedStateHandle.getStateFlow("query", "")
    val selectedFilter = savedStateHandle.getStateFlow("filter", "all")

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> =
        _uiState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Idle)

    // 🔹 trigger لتفعيل البحث يدويًا من الزر أو عند تغيير الفلتر
    private val searchTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    init {
        observeSearchFlow()
        observeSuggestions()
        observeHistory()
    }

    // --------------------------- SEARCH FLOW ---------------------------
    private fun observeSearchFlow() {
        combine(
            query.debounce(400).distinctUntilChanged(),
            selectedFilter,
            searchTrigger.onStart { emit(Unit) } // بدء تلقائي أول مرة
        ) { q, f, _ ->
            q.trim() to f
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
        val key = q.lowercase() to f
        val now = System.currentTimeMillis()

        val cached = cachedResults[key]
        val resultList = if (cached != null && now - cached.first < CACHE_TTL_MS) {
            cached.second
        } else {
            try {
                val results = repository.searchByType(q, f)
                cachedResults[key] = now to results
                results
            } catch (e: Exception) {
                emit(UiState.Error(e.message ?: "Network Error"))
                return@flow
            }
        }

        if (resultList.isEmpty()) emit(UiState.Empty)
        else emit(UiState.Success(resultList))
    }

    // --------------------------- USER ACTIONS ---------------------------

    /** حفظ فقط لما المستخدم يضغط Search */
    fun submitSearch() {
        val q = query.value.trim()
        if (q.isBlank()) return

        savedStateHandle["query"] = q
        searchTrigger.tryEmit(Unit)

        // 🔸 نضيفه لـHistory فقط هنا
        viewModelScope.launch {
            try {
                historyDao.upsertSafe(SearchHistoryEntity(query = q))
            } catch (_: Exception) {
            }
        }
    }

    /** تغيير النص */
    fun onQueryChange(newValue: String) {
        savedStateHandle["query"] = newValue
    }

    /** تغيير الفلتر: يفعل البحث فوريًا مع نفس الـquery */
    fun onFilterSelected(newFilter: String) {
        val lower = newFilter.lowercase()
        if (selectedFilter.value == lower) return
        savedStateHandle["filter"] = lower
        // ✅ trigger البحث مباشرة طالما فيه نص
        if (query.value.trim().isNotEmpty()) {
            searchTrigger.tryEmit(Unit)
        }
    }

    fun retryLastSearch() {
        searchTrigger.tryEmit(Unit)
    }

    // --------------------------- HISTORY + SUGGESTIONS ---------------------------

    private fun observeHistory() {
        viewModelScope.launch {
            historyDao.getRecentHistory().collect { list ->
                if (query.value.isBlank()) {
                    _uiState.value = UiState.History(list)
                }
            }
        }
    }

    private fun emitIdleHistory() {
        viewModelScope.launch {
            val list = historyDao.getRecentHistoryOnce()
            _uiState.value = UiState.History(list)
        }
    }

    fun clearHistory() = viewModelScope.launch { historyDao.deleteAll() }

    fun deleteOne(q: String) = viewModelScope.launch { historyDao.delete(q) }

    private fun observeSuggestions() {
        query
            .debounce(400)
            .distinctUntilChanged()
            .onEach { input -> updateSuggestions(input) }
            .launchIn(viewModelScope)
    }

    private suspend fun updateSuggestions(input: String) {
        if (input.isBlank()) {
            _suggestions.value = emptyList()
            return
        }

        val all = historyDao.getRecentHistoryOnce().map { it.query }
        val lowerInput = input.lowercase()
        val starts = all.filter { it.lowercase().startsWith(lowerInput) }
        val contains =
            all.filter { it.lowercase().contains(lowerInput) && !it.lowercase().startsWith(lowerInput) }
        val fuzzy = all.take(200)
            .filter { levSim(it.lowercase(), lowerInput) > 0.6 }

        _suggestions.value = (starts + contains + fuzzy).distinct().take(6)
    }

    // --------------------------- FUZZY HELPERS ---------------------------
    private fun levSim(a: String, b: String): Double {
        val longer = if (a.length > b.length) a else b
        val shorter = if (a.length > b.length) b else a
        if (longer.isEmpty()) return 1.0
        val dist = levDist(longer, shorter)
        return (longer.length - dist) / longer.length.toDouble()
    }

    private fun levDist(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in a.indices) dp[i + 1][0] = i + 1
        for (j in b.indices) dp[0][j + 1] = j + 1
        for (i in a.indices) {
            for (j in b.indices) {
                val cost = if (a[i] == b[j]) 0 else 1
                dp[i + 1][j + 1] = minOf(
                    dp[i][j + 1] + 1,
                    dp[i + 1][j] + 1,
                    dp[i][j] + cost
                )
            }
        }
        return dp[a.length][b.length]
    }
}