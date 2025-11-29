package com.example.mda.domain.usecase

import com.example.mda.data.local.entities.ActorEntity
import com.example.mda.data.repository.ActorsRepository

class GetPopularActorsUseCase(private val repository: ActorsRepository) {
    suspend operator fun invoke(page: Int = 1): List<ActorEntity> {
        return repository.getPopularActorsWithCache(page)
    }
}