// Khaled Edit: Final full TmdbApi version - Movies + TV Shows separated + Adult filtering.

// Khaled Edit: Final full TmdbApi version - Movies + TV Shows separated + Adult filtering.

package com.example.mda.data.remote.api

import com.example.mda.BuildConfig
import com.example.mda.data.remote.model.*
import com.example.mda.util.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.*

interface TmdbApi {

    // ------------------ Movies ------------------

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): Response<MovieResponse>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): Response<MovieResponse>

    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): Response<MovieResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("append_to_response") appendToResponse: String = "videos,credits",
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): Response<MovieDetailsResponse>

    // ------------------ TV Shows ------------------

    @GET("tv/popular")
    suspend fun getPopularTvShows(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): Response<MovieResponse>

    @GET("tv/{tv_id}")
    suspend fun getTvDetails(
        @Path("tv_id") tvId: Int,
        @Query("append_to_response") appendToResponse: String = "videos,credits",
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): Response<MovieDetailsResponse>

    // ------------------ Trending ------------------

    @GET("trending/{media_type}/{time_window}")
    suspend fun getTrendingMedia(
        @Path("media_type") mediaType: String = "all",
        @Path("time_window") timeWindow: String = "day",
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): Response<MovieResponse>

    // ------------------ Actors / People ------------------
    // ✅ Khaled Edit: Added endpoint for popular people list (used in ActorsScreen)
    @GET("person/popular")
    suspend fun getPopularPeople(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
    ): Response<ActorResponse>

    // ✅ Khaled Edit: Search people by name (optional, used for future search feature)
    @GET("search/person")
    suspend fun searchPeople(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
    ): Response<ActorResponse>

    @GET("person/{person_id}")
    suspend fun getActorDetails(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("append_to_response") appendToResponse: String
    ): Response<ActorFullDetails>

    // ------------------ Genres ------------------

    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): Response<GenreResponse>
    // ------------------ Search ------------------
    @GET("search/multi")
    suspend fun searchMulti(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): Response<MovieResponse>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): Response<MovieResponse>

    @GET("search/tv")
    suspend fun searchTvShows(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): Response<MovieResponse>
}















































//// Khaled Edit: Updated HomeViewModel to use offline cached data via MediaEntity
//
//package com.example.mda.ui.screens.home
//
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.mda.data.local.entities.MediaEntity            // 🧩 Khaled Edit: استخدام الكيان الجديد
//import com.example.mda.data.repository.MoviesRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//class HomeViewModel(private val repository: MoviesRepository) : ViewModel() {
//
//    // 🔹 Khaled Edit: استخدام MediaEntity بدل Movie لعرض البيانات سواء online أو offline.
//    private val _trendingMedia = MutableStateFlow<List<MediaEntity>>(emptyList())
//    val trendingMedia: StateFlow<List<MediaEntity>> = _trendingMedia
//
//    private val _popularMovies = MutableStateFlow<List<MediaEntity>>(emptyList())
//    val popularMovies: StateFlow<List<MediaEntity>> = _popularMovies
//
//    private val _popularTvShows = MutableStateFlow<List<MediaEntity>>(emptyList())
//    val popularTvShows: StateFlow<List<MediaEntity>> = _popularTvShows
//
//    private val _popularMixed = MutableStateFlow<List<MediaEntity>>(emptyList())
//    val popularMixed: StateFlow<List<MediaEntity>> = _popularMixed
//
//    private val _topRatedMovies = MutableStateFlow<List<MediaEntity>>(emptyList())
//    val topRatedMovies: StateFlow<List<MediaEntity>> = _topRatedMovies
//
//    // 🔹 اختيار المدى الزمني (يوم / أسبوع)
//    var selectedTimeWindow by mutableStateOf("day")
//        private set
//
//    init {
//        // 📡 Khaled Edit: تحميل البيانات سواء Online أو Offline
//        loadTrending("day")
//        loadPopularData()
//        loadTopRated()
//    }
//
//    // 🔹 تحميل الـ Trending (Movies + TV)
//    fun loadTrending(timeWindow: String) {
//        viewModelScope.launch {
//            try {
//                selectedTimeWindow = timeWindow
//                val list = repository.getTrendingMedia(mediaType = "all", timeWindow = timeWindow)
//                _trendingMedia.value = list
//            } catch (t: Throwable) {
//                t.printStackTrace()
//            }
//        }
//    }
//
//    // 🔹 تحميل الـ Popular Movies & TV Shows
//    private fun loadPopularData() {
//        viewModelScope.launch {
//            try {
//                val movies = repository.getPopularMovies()
//                val tv = repository.getPopularTvShows()
//
//                _popularMovies.value = movies
//                _popularTvShows.value = tv
//                _popularMixed.value = (movies + tv)
//                    .sortedByDescending { it.voteAverage ?: 0.0 }
//                    .take(20)
//            } catch (t: Throwable) {
//                t.printStackTrace()
//            }
//        }
//    }
//
//    // 🔹 تحميل Top Rated Movies
//    private fun loadTopRated() {
//        viewModelScope.launch {
//            try {
//                val topRated = repository.getTopRatedMovies()
//                _topRatedMovies.value = topRated
//            } catch (t: Throwable) {
//                t.printStackTrace()
//            }
//        }
//    }
//}