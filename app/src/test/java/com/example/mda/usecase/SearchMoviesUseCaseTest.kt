package com.example.mda.usecase

import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.domain.usecase.SearchMoviesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchMoviesUseCaseTest {

    private lateinit var useCase: SearchMoviesUseCase
    private val repository: MoviesRepository = mockk()

    @Before
    fun setup() {
        useCase = SearchMoviesUseCase(repository)
    }

    @Test
    fun `invoke returns API results when available`() = runTest {
        val apiResults = listOf(
            MediaEntity(
                id = 1,
                title = "Inception",
                name = "Inception",
                overview = "A dreaming movie",
                posterPath = null,
                backdropPath = null,
                firstAirDate = "2010-01-01",
                releaseDate = "2010-01-01",
                mediaType = "movie",
                voteAverage = 8.8
            )
        )
        coEvery { repository.searchByType("Inception", "all") } returns apiResults
        coEvery { repository.getTrendingMedia() } returns emptyList()

        val result = useCase.invoke("Inception", "all")

        assertEquals(1, result.size)
        assertEquals("Inception", result.first().title)
    }

    @Test
    fun `invoke falls back to trending when API returns empty`() = runTest {
        val trending = listOf(
            MediaEntity(
                id = 2,
                title = "The Dark Knight",
                name = "The Dark Knight",
                overview = "Batman movie",
                posterPath = null,
                backdropPath = null,
                firstAirDate = "2008-01-01",
                releaseDate = "2008-01-01",
                mediaType = "movie",
                voteAverage = 9.0
            ),
            MediaEntity(
                id = 3,
                title = "Inception",
                name = "Inception",
                overview = "Mind-bending thriller",
                posterPath = null,
                backdropPath = null,
                firstAirDate = "2010-01-01",
                releaseDate = "2010-01-01",
                mediaType = "movie",
                voteAverage = 8.8
            )
        )
        coEvery { repository.searchByType("inc", "all") } returns emptyList()
        coEvery { repository.getTrendingMedia() } returns trending

        val result = useCase.invoke("inc", "all")

        // المفروض يرجع الفيلم اللي فيه كلمة inc
        assertEquals(1, result.size)
        assertEquals("Inception", result.first().title)
    }
}