package com.example.mda.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.mda.data.local.dao.SearchHistoryDao
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.screens.search.SearchViewModel
import com.example.mda.ui.screens.search.UiState
import io.mockk.coEvery
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
    private val repository: MoviesRepository = mockk()
    private val dao: SearchHistoryDao = mockk(relaxed = true)
    private val savedStateHandle = SavedStateHandle()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        coEvery { dao.getRecentHistory() } returns flowOf(emptyList())
        coEvery { dao.getRecentHistoryOnce() } returns emptyList()
        coEvery { dao.upsertSafe(any()) } returns Unit

        viewModel = SearchViewModel(repository, dao, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // 🟩 التست الأول – لما البحث ينجح
    @Test
    fun `when search succeeds uiState becomes Success`() = runTest(testDispatcher) {
        // بيانات مزيفة من الـ API
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

        // نخلّي الـ repository يرجع نتائج بدل ما يرمي خطأ
        coEvery { repository.searchByType("Inception", any()) } returns fakeResults
        coEvery { repository.getTrendingMedia() } returns emptyList()

        // نحفز البحث
        viewModel.onQueryChange("Inception")
        viewModel.retryLastSearch()

        // ننتظر أول UiState.Success بدل ما نتحقق فوري
        val successState = viewModel.uiState
            .filterIsInstance<UiState.Success>()
            .first()

        assertTrue(successState is UiState.Success)
    }

    // 🟥 التست التاني – لما البحث يفشل
    @Test
    fun `when search throws exception uiState becomes Error`() = runTest(testDispatcher) {
        // نخلي الـ repository يرمي Exception
        coEvery { repository.searchByType(any(), any()) } throws RuntimeException("Network Error")

        viewModel.onQueryChange("something")
        viewModel.retryLastSearch()

        // ننتظر أول UiState.Error بدل ما نتحقق فوري
        val errorState = viewModel.uiState
            .filterIsInstance<UiState.Error>()
            .first()

        assertTrue(errorState is UiState.Error)
    }
}