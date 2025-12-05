package com.example.mda.data.repository

import com.example.mda.data.local.dao.MediaDao
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.repository.mappers.toMediaEntity
import kotlinx.coroutines.flow.first

class GenreDetailsRepository(
    private val api: TmdbApi,
    private val dao: MediaDao
) {

    // Khaled Edit: جلب Movies حسب Genre مع الكاش
    suspend fun getMoviesByGenre(genreId: Int): List<MediaEntity> {
        // 🔹 جلب من الكاش أولاً
        val cached: List<MediaEntity> = dao.getAll().first()
            .filter { it.genreIds?.contains(genreId) == true } // فلترة حسب الـ Genre

        if (cached.isNotEmpty()) return cached

        // 🔹 لو مفيش كاش، جلب من الـ API
        val response = api.getMoviesByGenre(genreId)
        if (response.isSuccessful) {
            val movies: List<MediaEntity> = response.body()?.results
                ?.map { it.toMediaEntity("movie") } ?: emptyList()

            // 🔹 حفظ في الكاش
            movies.forEach { dao.upsert(it) }

            return movies
        } else throw Exception("Failed to load movies by genre")
    }

}
