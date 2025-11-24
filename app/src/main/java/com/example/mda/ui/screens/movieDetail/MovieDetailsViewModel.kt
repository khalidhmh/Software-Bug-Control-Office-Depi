package com.example.mda.ui.screens.movieDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.repository.MovieDetailsRepository
import com.example.mda.data.repository.MovieDetailsRepository.ReleaseSummary
import com.example.mda.data.repository.MovieDetailsRepository.ProvidersGrouped
import com.example.mda.data.remote.model.ReviewsResponse
import com.example.mda.data.remote.model.KeywordsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    private val repository: MovieDetailsRepository
) : ViewModel() {

    private val _details = MutableStateFlow<MediaEntity?>(null)
    val details: StateFlow<MediaEntity?> = _details

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _similar = MutableStateFlow<List<MediaEntity>>(emptyList())
    val similar: StateFlow<List<MediaEntity>> = _similar

    private val _recommendations = MutableStateFlow<List<MediaEntity>>(emptyList())
    val recommendations: StateFlow<List<MediaEntity>> = _recommendations

    private val _providers = MutableStateFlow<ProvidersGrouped?>(null)
    val providers: StateFlow<ProvidersGrouped?> = _providers

    private val _reviews = MutableStateFlow<ReviewsResponse?>(null)
    val reviews: StateFlow<ReviewsResponse?> = _reviews

    private val _keywords = MutableStateFlow<KeywordsResponse?>(null)
    val keywords: StateFlow<KeywordsResponse?> = _keywords

    private val _releaseSummary = MutableStateFlow<ReleaseSummary?>(null)
    val releaseSummary: StateFlow<ReleaseSummary?> = _releaseSummary

    /** Load Movie details, fallback to cache if needed */
    fun loadMovieDetails(id: Int, fromNetwork: Boolean = false) {
        load(id, isTv = false, fromNetwork = fromNetwork)
    }

    /** Load TV details, fallback to cache if needed */
    fun loadTvDetails(id: Int, fromNetwork: Boolean = false) {
        load(id, isTv = true, fromNetwork = fromNetwork)
    }

    private fun load(id: Int, isTv: Boolean, fromNetwork: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // ✅ جيب من الـ cache أول عشان نعرض بيانات سريعة
                val cached = if (isTv) repository.getCachedTv(id) else repository.getCachedMovie(id)

                // لو فيه cache، اعرضه أول (للسرعة)
                if (cached != null && !fromNetwork) {
                    _details.value = cached
                }

                // ✅ بعد كده روح للـ API عشان تجيب البيانات الكاملة (cast, videos, etc.)
                val fresh = if (isTv) repository.getTvById(id) else repository.getMovieById(id)

                if (fresh != null) {
                    _details.value = fresh
                    val similarItems = if (isTv) repository.getSimilarTvShows(id) else repository.getSimilarMovies(id)
                    _similar.value = similarItems.filter { !it.posterPath.isNullOrBlank() }

                    val recItems = if (isTv) repository.getRecommendedTvShows(id) else repository.getRecommendedMovies(id)
                    _recommendations.value = recItems.filter { !it.posterPath.isNullOrBlank() }

                    _providers.value = if (isTv) repository.getTvProviders(id) else repository.getMovieProviders(id)

                    _reviews.value = if (isTv) repository.getTvReviews(id) else repository.getMovieReviews(id)
                    _keywords.value = if (isTv) repository.getTvKeywords(id) else repository.getMovieKeywords(id)
                    _releaseSummary.value = if (isTv) null else repository.getMovieReleaseSummary(id)
                } else if (cached == null) {
                    _error.value = "No data available"
                }

            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
