package com.example.mda.viewmodel


import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import com.example.mda.data.remote.model.Genre
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.screens.genreScreen.GenreViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCoroutinesApi::class)
class GenreViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: MoviesRepository
    private lateinit var viewModel: GenreViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadGenres success updates genres and isLoading`() = runTest {
        val sampleGenres = listOf(Genre(id = 1, name = "Action"), Genre(id = 2, name = "Comedy"))
        coEvery { repository.getGenres() } returns sampleGenres

        viewModel = GenreViewModel(repository)

        // advance coroutine scheduler so init/loadGenres completes
        advanceUntilIdle()

        val loaded = viewModel.genres.value
        assertEquals(2, loaded.size)
        assertEquals("Action", loaded.first().name)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `loadGenres failure sets error`() = runTest {
        coEvery { repository.getGenres() } throws RuntimeException("network failed")

        viewModel = GenreViewModel(repository)
        advanceUntilIdle()

        assertNotNull(viewModel.error.value)
        assertTrue(viewModel.error.value!!.contains("network failed"))
    }
}
