package com.example.mda.ui.Screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.remote.model.Movie
import com.example.mda.data.repository.MoviesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: MoviesRepository) : ViewModel() {

    // Trending (all)
    private val _trendingMovies = MutableStateFlow<List<Movie>>(emptyList())
    val trendingMovies: StateFlow<List<Movie>> = _trendingMovies

    // Popular separately
    private val _popularMovies = MutableStateFlow<List<Movie>>(emptyList())
    val popularMovies: StateFlow<List<Movie>> = _popularMovies

    private val _popularTvShows = MutableStateFlow<List<Movie>>(emptyList())
    val popularTvShows: StateFlow<List<Movie>> = _popularTvShows

    // Popular mixed (for Popular section)
    private val _popularMixed = MutableStateFlow<List<Movie>>(emptyList())
    val popularMixed: StateFlow<List<Movie>> = _popularMixed

    // Top rated
    private val _topRatedMovies = MutableStateFlow<List<Movie>>(emptyList())
    val topRatedMovies: StateFlow<List<Movie>> = _topRatedMovies

    // Time window
    var selectedTimeWindow by mutableStateOf("day")
        private set

    init {
        // Load everything once
        loadTrending("day")
        loadPopularData()
        loadTopRated()
    }

    fun loadTrending(timeWindow: String) {
        viewModelScope.launch {
            try {
                selectedTimeWindow = timeWindow
                val response = repository.getTrendingMovies(mediaType = "all", timeWindow = timeWindow)
                _trendingMovies.value = response.results
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    private fun loadPopularData() {
        viewModelScope.launch {
            try {
                val moviesResp = repository.getPopularMovies()
                val tvResp = repository.getPopularTvShows()

                _popularMovies.value = moviesResp.results
                _popularTvShows.value = tvResp.results

                _popularMixed.value = (moviesResp.results + tvResp.results)
                    .sortedByDescending { it.voteAverage }
                    .take(20)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    private fun loadTopRated() {
        viewModelScope.launch {
            try {
                _topRatedMovies.value = repository.getTopRatedMovies().results
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }
}
