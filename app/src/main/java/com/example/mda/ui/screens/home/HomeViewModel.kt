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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: MoviesRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // ------------------- STATES -------------------

    // 1. Trending & Popular & TopRated
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

    // ------------------- 🔥 SMART RECOMMENDATIONS -------------------

    // القائمة الأصلية "المختلطة"
    private val _recommendedMedia = MutableStateFlow<List<MediaEntity>>(emptyList())

    // ✅ 1. قائمة مفلترة للأفلام فقط (لتبويب Movies)
    val recommendedMovies: StateFlow<List<MediaEntity>> = _recommendedMedia
        .map { list -> list.filter { it.mediaType == "movie" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ✅ 2. قائمة مفلترة للمسلسلات فقط (لتبويب TV Shows)
    val recommendedTvShows: StateFlow<List<MediaEntity>> = _recommendedMedia
        .map { list -> list.filter { it.mediaType == "tv" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // ------------------- UI STATES -------------------
    var selectedTimeWindow by mutableStateOf("day")
        private set

    private var lastRecommendationUpdateTime by mutableStateOf<Long?>(null)

    // ------------------- INIT -------------------
    init {
        Log.d("HomeVM", "✅ HomeViewModel initialized")

        if (_trendingMedia.value.isEmpty()) {
            loadTrending("day")
        }

        // تحميل البيانات العامة (أفلام ومسلسلات)
        if (_popularMovies.value.isEmpty() || _popularTvShows.value.isEmpty()) {
            loadPopularData()
        }

        if (_topRatedMovies.value.isEmpty()) {
            loadTopRated()
        }

        observeSession()
    }

    /**
     * راقب الجلسة وحمّل التوصيات الذكية
     */
    private fun observeSession() {
        viewModelScope.launch {
            authRepository.getSessionId().collect { sessionId ->
                if (!sessionId.isNullOrEmpty()) {
                    val account = authRepository.getAccountDetails().getOrNull()
                    if (account != null) {
                        if (_recommendedMedia.value.isEmpty()) {
                            Log.d("HomeVM", "🔁 Session active. Loading smart recommendations for ${account.id}")
                            loadSmartRecommendations(account.id, sessionId)
                        }
                    }
                } else {
                    Log.d("HomeVM", "🚫 No Session found. Using fallback.")
                    // لو مفيش جلسة، نستخدم الـ Fallback عشان التبويبات تشتغل
                    generateFallbackRecommendations()
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
            }
        }
    }

    // ------------------- Popular -------------------
    fun loadPopularData() {
        viewModelScope.launch {
            try {
                val movies = repository.getPopularMovies()
                val tvShows = repository.getPopularTvShows() // ✅ تحميل المسلسلات مهم جداً
                Log.d("HomeVM", "📺 TV Shows Loaded: ${tvShows.size}")

                _popularMovies.value = movies
                _popularTvShows.value = tvShows
                _popularMixed.value = (movies + tvShows)
                    .sortedByDescending { it.voteAverage ?: 0.0 }
                    .take(20)

                // 🔥🔥 FIX COLD START:
                // بمجرد تحميل البيانات العامة، إذا كانت التوصيات فارغة، املأها فوراً
                if (_recommendedMedia.value.isEmpty()) {
                    generateFallbackRecommendations()
                }

            } catch (e: Exception) {
                e.printStackTrace()
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
            }
        }
    }

    // ------------------- SMART RECOMMENDATIONS LOGIC -------------------
    private fun loadSmartRecommendations(accountId: Int, sessionId: String) {
        viewModelScope.launch {
            try {
                val list = repository.getSmartRecommendations(accountId, sessionId)

                if (list.isNotEmpty()) {
                    _recommendedMedia.value = list
                    Log.d("HomeVM", "✅ Smart recommendations loaded (${list.size} items)")
                } else {
                    // لو القائمة رجعت فارغة (مستخدم جديد)، شغل الـ Fallback
                    Log.d("HomeVM", "⚠️ Empty recommendations list. Generating fallback.")
                    generateFallbackRecommendations()
                }
                lastRecommendationUpdateTime = System.currentTimeMillis()
            } catch (e: Exception) {
                e.printStackTrace()
                // لو حصل إيرور، شغل الـ Fallback
                Log.d("HomeVM", "❌ Error loading recommendations. Generating fallback.")
                generateFallbackRecommendations()
            }
        }
    }

    /**
     * 🔥 دالة لإنشاء قائمة احتياطية تحتوي على أفلام ومسلسلات
     * تضمن أن التبويبات لا تكون فارغة أبداً
     */
    private fun generateFallbackRecommendations() {
        // نأخذ أشهر 10 أفلام
        val moviesFallback = _popularMovies.value.take(10)
        // نأخذ أشهر 10 مسلسلات (مهم جداً لتبويب TV)
        val tvFallback = _popularTvShows.value.take(10)

        if (moviesFallback.isNotEmpty() || tvFallback.isNotEmpty()) {
            // نخلطهم مع بعض
            val mixed = (moviesFallback + tvFallback).shuffled()
            _recommendedMedia.value = mixed
            Log.d("HomeVM", "✅ Fallback generated: ${moviesFallback.size} Movies + ${tvFallback.size} TV Shows")
        } else {
            // حل أخير لو لسة مفيش أي داتا، هات التريند
            _recommendedMedia.value = _trendingMedia.value
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
                    // لو مفيش حساب، نتأكد ان الـ Fallback موجود
                    if (_recommendedMedia.value.isEmpty()) {
                        generateFallbackRecommendations()
                    }
                }
            }
        }
    }
}