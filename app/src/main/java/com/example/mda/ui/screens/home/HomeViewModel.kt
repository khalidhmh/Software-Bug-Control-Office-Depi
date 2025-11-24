package com.example.mda.ui.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.repository.AuthRepository
import com.example.mda.data.repository.MoviesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: MoviesRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // ------------------- STATES -------------------
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

    private val _recommendedMedia = MutableStateFlow<List<MediaEntity>>(emptyList())
    val recommendedMedia: StateFlow<List<MediaEntity>> = _recommendedMedia

    var selectedTimeWindow by mutableStateOf("day")
        private set

    private var lastRecommendationUpdateTime by mutableStateOf<Long?>(null)

    // ------------------- INIT -------------------
    init {
        Log.d("HomeVM", "✅ HomeViewModel initialized")

        loadTrending("day")
        loadPopularData()
        loadTopRated()

        // ✅ راقب الجلسة وحدث التوصيات الذكية لما المستخدم يكون logged in
        viewModelScope.launch {
            authRepository.getSessionId().collect { sessionId ->
                if (!sessionId.isNullOrEmpty()) {
                    val account = authRepository.getAccountDetails().getOrNull()
                    if (account != null) {
                        Log.d("HomeVM", "🔁 Session active. Loading smart recommendations for ${account.id}")
                        loadSmartRecommendations(account.id, sessionId)
                    }
                } else {
                    Log.d("HomeVM", "🚫 No Session found, skipping recommendations.")
                }
            }
        }
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

    // ------------------- SMART RECOMMENDATIONS -------------------
    private fun loadSmartRecommendations(accountId: Int, sessionId: String) {
        viewModelScope.launch {
            try {
                val list = repository.getSmartRecommendations(accountId, sessionId)
                if (list.isNotEmpty()) {
                    _recommendedMedia.value = list
                    Log.d("HomeVM", "✅ Smart recommendations loaded (${list.size} items)")
                } else {
                    // fallback لو فاضي خالص
                    val suggestions = (_topRatedMovies.value + _trendingMedia.value)
                        .distinctBy { it.id }.take(20)
                    _recommendedMedia.value = suggestions
                }
                lastRecommendationUpdateTime = System.currentTimeMillis()
            } catch (e: Exception) {
                e.printStackTrace()
                // fallback
                _recommendedMedia.value = (_topRatedMovies.value + _popularMovies.value)
                    .distinctBy { it.id }.take(20)
            }
        }
    }

    // ------------------- ACTIVITY TRIGGER -------------------
    fun onUserActivityDetected(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val sixHours = 6 * 60 * 60 * 1000L

            if (forceRefresh || lastRecommendationUpdateTime == null || now - lastRecommendationUpdateTime!! > sixHours) {
                val sessionId = authRepository.getSessionId().firstOrNull()
                val account = authRepository.getAccountDetails().getOrNull()

                if (account != null && sessionId != null) {
                    Log.d("HomeVM", "🔄 Updating smart recommendations due to user activity.")
                    loadSmartRecommendations(account.id, sessionId)
                    lastRecommendationUpdateTime = now
                } else {
                    Log.d("HomeVM", "⚠️ Skipped activity update (no session/account).")
                }
            } else {
                Log.d("HomeVM", "🕒 Recommendations still fresh — no update needed.")
            }
        }
    }
}