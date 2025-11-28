package com.example.mda.ui.screens.genreDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.filteration.FilterType
import kotlinx.coroutines.launch

enum class MediaTypeFilter {
    MOVIES, TV_SHOWS
}

class GenreDetailsViewModel(private val repository: MoviesRepository) : ViewModel() {

    var movies by mutableStateOf<List<MediaEntity>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var currentFilter by mutableStateOf(FilterType.ALL)
        private set

    var mediaTypeFilter by mutableStateOf(MediaTypeFilter.MOVIES)
        private set

    private var currentPage = 1
    private var canLoadMore = true
    private var allMovies = mutableListOf<MediaEntity>()
    private var currentGenreId: Int = 0

    fun loadMoviesByGenre(genreId: Int) {
        if (isLoading || !canLoadMore) return
        currentGenreId = genreId

        viewModelScope.launch {
            isLoading = true
            try {
                val newMovies = when (mediaTypeFilter) {
                    MediaTypeFilter.MOVIES -> repository.getMoviesByGenre(genreId, currentPage)
                    MediaTypeFilter.TV_SHOWS -> repository.getTvShowsByGenre(genreId, currentPage)
                }

                if (newMovies.isNotEmpty()) {
                    val uniqueMovies = newMovies.filter { newMovie ->
                        allMovies.none { it.id == newMovie.id }
                    }
                    allMovies.addAll(uniqueMovies)
                    applyFilter(currentFilter)
                    currentPage++
                } else {
                    canLoadMore = false
                }
                error = null
            } catch (e: Exception) {
                error = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    fun resetAndLoad(genreId: Int) {
        movies = emptyList()
        allMovies.clear()
        currentPage = 1
        canLoadMore = true
        currentGenreId = genreId
        loadMoviesByGenre(genreId)
    }

    fun setMediaTypeFilter(filter: MediaTypeFilter, genreId: Int) {
        if (mediaTypeFilter != filter) {
            mediaTypeFilter = filter
            resetAndLoad(genreId)
        }
    }

    fun setFilterType(filterType: FilterType) {
        currentFilter = filterType
        applyFilter(filterType)
    }

    fun applyFilter(filterType: FilterType) {
        val filteredMovies = when (filterType) {
            FilterType.TOP_RATED ->
                allMovies.sortedByDescending { it.voteAverage ?: 0.0 }

            FilterType.NEWEST ->
                allMovies.sortedByDescending { it.releaseDate ?: it.firstAirDate ?: "" }

            FilterType.POPULAR ->
                allMovies.sortedByDescending { it.voteCount ?: 0L }

            FilterType.FAMILY_FRIENDLY ->
                allMovies.filter { it.adult == false }

            FilterType.ALL ->
                allMovies
        }

        movies = filteredMovies
    }
}