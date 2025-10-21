package com.example.mda.ui.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.repository.MoviesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: MoviesRepository
) : ViewModel() {

    private val _trendingMedia = MutableStateFlow<List<MediaEntity>>(emptyList())
    val trendingMedia: StateFlow<List<MediaEntity>> = _trendingMedia

    private val _popularMovies = MutableStateFlow<List<MediaEntity>>(emptyList())
    val popularMovies: StateFlow<List<MediaEntity>> = _popularMovies

    private val _popularTvShows = MutableStateFlow<List<MediaEntity>>(emptyList())
    val popularTvShows: StateFlow<List<MediaEntity>> = _popularTvShows

    private val _popularMixed = MutableStateFlow<List<MediaEntity>>(emptyList())
    val popularMixed: StateFlow<List<MediaEntity>> = _popularMixed

    private val _topRatedMovies = MutableStateFlow<List<MediaEntity>>(emptyList())
    val topRatedMovies: StateFlow<List<MediaEntity>> = _topRatedMovies

    var selectedTimeWindow by mutableStateOf("day")
        private set

    init {
        Log.d("HomeVM", "✅ HomeViewModel initialized")
        loadTrending("day")
        loadPopularData()
        loadTopRated()
    }

    // ------------------- Trending -------------------
    fun loadTrending(timeWindow: String) {
        viewModelScope.launch {
            selectedTimeWindow = timeWindow
            try {
                val trending = repository.getTrendingMedia("all", timeWindow)
                _trendingMedia.value = trending
            } catch (e: Exception) {
                e.printStackTrace()
                _trendingMedia.value = emptyList()
            }
        }
    }

    // ------------------- Popular -------------------
    fun loadPopularData() {
        viewModelScope.launch {
            try {
                val movies = repository.getPopularMovies()
                val tvShows = repository.getPopularTvShows()
                Log.d("HomeVM", "📺 TV Shows Loaded: ${tvShows.size}")

                _popularMovies.value = movies
                _popularTvShows.value = tvShows

                _popularMixed.value = (movies + tvShows)
                    .sortedByDescending { it.voteAverage ?: 0.0 }
                    .take(20)

            } catch (e: Exception) {
                e.printStackTrace()
                _popularMovies.value = emptyList()
                _popularTvShows.value = emptyList()
                _popularMixed.value = emptyList()
            }
        }
    }

    // ------------------- Top Rated -------------------
    fun loadTopRated() {
        viewModelScope.launch {
            try {
                val topRated = repository.getTopRatedMovies()
                _topRatedMovies.value = topRated
            } catch (e: Exception) {
                e.printStackTrace()
                _topRatedMovies.value = emptyList()
            }
        }
    }
}
