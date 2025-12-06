package com.example.mda.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.mda.data.local.dao.SearchHistoryDao
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.local.entities.SearchHistoryEntity
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.screens.search.SearchViewModel
import com.example.mda.ui.screens.search.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel
    private val repository: MoviesRepository = mockk(relaxed = true)
    private val dao: SearchHistoryDao = mockk(relaxed = true)
    private val savedStateHandle = SavedStateHandle()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { dao.getRecentHistory(any()) } returns flowOf(emptyList())
        coEvery { dao.getRecentHistoryOnce(any()) } returns emptyList()
        coEvery { dao.upsertSafe(any()) } returns Unit
        coEvery { dao.deleteAll(any()) } returns Unit
        coEvery { dao.delete(any(), any()) } returns Unit

        viewModel = SearchViewModel(repository, dao, savedStateHandle)
        viewModel.currentUserId = "user_123"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when search succeeds uiState becomes Success`() = runTest(testDispatcher) {
        val fakeResults = listOf(
            MediaEntity(
                id = 1,
                title = "Inception",
                name = "Inception",
                overview = "A movie about dreams",
                posterPath = null,
                backdropPath = null,
                firstAirDate = "2010",
                releaseDate = "2010",
                mediaType = "movie",
                voteAverage = 8.8
            )
        )

        coEvery { repository.searchByType("Inception", any()) } returns fakeResults
        coEvery { repository.getTrendingMedia() } returns emptyList()

        viewModel.onQueryChange("Inception")
        viewModel.submitSearch()

        val successState = viewModel.uiState
            .filterIsInstance<UiState.Success>()
            .first()

        assertTrue(successState is UiState.Success)
    }

    @Test
    fun `when search throws exception uiState becomes Error`() = runTest(testDispatcher) {
        coEvery { repository.searchByType(any(), any()) } throws RuntimeException("Network Error")

        viewModel.onQueryChange("something")
        viewModel.submitSearch()

        val errorState = viewModel.uiState
            .filterIsInstance<UiState.Error>()
            .first()

        assertTrue(errorState is UiState.Error)
    }

    @Test
    fun `when emitIdleHistory called and user set history is returned`() = runTest(testDispatcher) {
        val fakeHistory = listOf(SearchHistoryEntity(query = "Inception", userId = "user_123"))
        coEvery { dao.getRecentHistoryOnce("user_123") } returns fakeHistory

        viewModel.emitIdleHistory()

        val state = viewModel.uiState.filterIsInstance<UiState.History>().first()

        assertTrue(state.items.isNotEmpty())
        assertTrue(state.items.first().query == "Inception")
    }

    @Test
    fun `when clearHistory called dao deleteAll is invoked with userId`() = runTest(testDispatcher) {
        viewModel.clearHistory()
        coVerify { dao.deleteAll("user_123") }
    }

    @Test
    fun `when deleteOne called dao delete is invoked with query and userId`() = runTest(testDispatcher) {
        viewModel.deleteOne("Matrix")
        coVerify { dao.delete("Matrix", "user_123") }
    }
}