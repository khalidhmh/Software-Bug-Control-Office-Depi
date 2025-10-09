package com.example.mda.data.repository

import com.example.mda.data.remote.model.Movie

// ----- Repository interface (provided by your data layer) -----
// The real implementations live in your data layer. ViewModel uses this interface.
interface MovieRepository {
    suspend fun getBannerMovies(): List<Movie>
    suspend fun getTrendingMovies(): List<Movie>
}