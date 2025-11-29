package com.example.mda.domain.usecase

import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.repository.MoviesRepository

/**
 * UseCase مسؤول عن منطق البحث وتصفية النتائج محليًا لو النت فاضي
 */
class SearchMoviesUseCase(
    private val repository: MoviesRepository
) {
    suspend operator fun invoke(query: String, filter: String): List<MediaEntity> {
        val apiResults = repository.searchByType(query.trim(), filter.lowercase())

        // لو الـ API رجع صفر، استعن بالـ trending وخد اللي فيه الكلمة
        if (apiResults.isEmpty()) {
            val trending = repository.getTrendingMedia()
            return trending.filter {
                val title = (it.title ?: it.name ?: "").lowercase()
                title.contains(query.lowercase())
            }
        }

        return apiResults
    }
}