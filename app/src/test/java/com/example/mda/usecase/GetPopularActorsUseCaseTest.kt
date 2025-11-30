package com.example.mda.usecase

import com.example.mda.data.local.entities.ActorEntity
import com.example.mda.data.repository.ActorsRepository
import com.example.mda.domain.usecase.GetPopularActorsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetPopularActorsUseCaseTest {

    private lateinit var useCase: GetPopularActorsUseCase
    private val repository: ActorsRepository = mockk()

    @Before
    fun setup() {
        useCase = GetPopularActorsUseCase(repository)
    }

    @Test
    fun `invoke returns list of actors from repository`() = runTest {
        val fakeActors = listOf(
            ActorEntity(id = 1, name = "Tom Cruise", profilePath = null, biography = "", birthday = "", placeOfBirth = "USA", knownForDepartment = "Acting", knownFor = "[]"),
            ActorEntity(id = 2, name = "Emma Stone", profilePath = null, biography = "", birthday = "", placeOfBirth = "USA", knownForDepartment = "Acting", knownFor = "[]")
        )
        coEvery { repository.getPopularActorsWithCache(page = 1) } returns fakeActors

        val result = useCase.invoke(1)

        assertEquals(2, result.size)
        assertEquals("Tom Cruise", result.first().name)
    }
}