package com.example.mda.data.remote.model

data class FavoriteMoviesResponse(
    val page: Int,
    val results: List<Movie>,
    val total_pages: Int,
    val total_results: Int
)