package com.example.mda.ui.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.local.dao.MediaDao
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.repository.MoviesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import androidx.lifecycle.ViewModelProvider
class SearchViewModel(
    private val repository: MoviesRepository,
    private val localDao: MediaDao
) : ViewModel() {

    var query by mutableStateOf("")
        private set

    private val _results = MutableStateFlow<List<MediaEntity>>(emptyList())
    val results: StateFlow<List<MediaEntity>> = _results

    private val _history = MutableStateFlow<List<MediaEntity>>(emptyList())
    val history: StateFlow<List<MediaEntity>> = _history

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            localDao.getAll().collect { list ->
                _history.value = list.filter { it.isFavorite || it.isInWatchlist }
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        query = newQuery
    }

    fun performSearch() {
        viewModelScope.launch {
            if (query.isBlank()) {
                _results.value = emptyList()
                return@launch
            }

            try {
                // البحث في الكاش أولاً
                val cached = localDao.getAll().first().filter {
                    (it.title ?: it.name ?: "").contains(query, ignoreCase = true)
                }

                _results.value = if (cached.isNotEmpty()) cached
                else {
                    // fallback للـ API
                    repository.getTrendingMedia("all", "day").filter {
                        (it.title ?: it.name ?: "").contains(query, ignoreCase = true)
                    }
                }
            } catch (e: Exception) {
                _results.value = emptyList()
            }
        }
    }
}
 // تأكدي من أن هذا المسار يطابق مسار SearchViewModel


/**
 * مصنع لإنشاء وتزويد SearchViewModel بالاعتماديات (dependencies) التي يحتاجها.
 * هذا ضروري لأن SearchViewModel يتطلب MoviesRepository في مُنشئه (constructor).
 */
class SearchViewModelFactory(
    private val moviesRepository: MoviesRepository,
    private val localDao: MediaDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(
                repository = moviesRepository,
                localDao = localDao
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
