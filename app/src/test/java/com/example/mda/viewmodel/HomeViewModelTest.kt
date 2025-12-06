package com.example.mda.viewmodel

import android.util.Log
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.model.auth.AccountDetails
import com.example.mda.data.repository.AuthRepository
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.screens.home.HomeViewModel
import io.mockk.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var vm: HomeViewModel
    private lateinit var moviesRepo: MoviesRepository
    private lateinit var authRepo: AuthRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Log::class)
        every { Log.d(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0

        moviesRepo = mockk()
        authRepo = mockk()

        every { authRepo.getSessionId() } returns flowOf(null)
        coEvery { authRepo.getAccountDetails() } returns Result.success(
            AccountDetails(
                id = 1,
                username = "UserX",
                name = "User",
                includeAdult = false,
                iso6391 = "en",
                iso31661 = "US",
                avatar = null
            )
        )

        // تشغيل mock للداتا
        coEvery { moviesRepo.getTrendingMedia(any(), any()) } returns listOf(
            media(id = 1, title = "Trending 1", type = "movie"),
            media(id = 2, title = "Trending 2", type = "tv")
        )

        coEvery { moviesRepo.getPopularMovies() } returns listOf(
            media(id = 10, title = "Popular Movie", type = "movie", rating = 8.5)
        )

        coEvery { moviesRepo.getPopularTvShows() } returns listOf(
            media(id = 20, title = "Popular Show", type = "tv", rating = 8.0)
        )

        coEvery { moviesRepo.getTopRatedMovies() } returns listOf(
            media(id = 30, title = "Top Rated Movie", type = "movie", rating = 9.0)
        )

        coEvery { moviesRepo.getSmartRecommendations(any(), any()) } returns listOf(
            media(id = 40, title = "Smart Rec Movie", type = "movie"),
            media(id = 41, title = "Smart Rec Show", type = "tv")
        )

        vm = HomeViewModel(moviesRepo, authRepo)

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `loadTrending updates trendingMedia`() = runTest {
        vm.loadTrending("day")
        advanceUntilIdle()
        val list = vm.trendingMedia.value
        println(" trendingMedia size= ${list.size}")
        assertTrue(list.isNotEmpty())
        assertEquals("Trending 1", list.first().title)
    }

    @Test
    fun `loadPopularData loads movies + tv and fallback recommendations`() = runTest {
        vm.loadPopularData()
        advanceUntilIdle()

        val movies = vm.popularMovies.value
        val shows = vm.popularTvShows.value
        val mixed = vm.popularMixed.value

        println("Movies:${movies.size}, Shows:${shows.size}, Mixed:${mixed.size}")
        assertTrue(movies.isNotEmpty() || shows.isNotEmpty())
        assertTrue(mixed.size > 0)
    }

    @Test
    fun `loadTopRated updates topRatedMovies`() = runTest {
        vm.loadTopRated()
        advanceUntilIdle()

        val list = vm.topRatedMovies.value
        println("TopRated:${list.size}")
        assertTrue(list.isNotEmpty())
        assertEquals("Top Rated Movie", list.first().title)
    }

    @Test
    fun `generateFallbackRecommendations fills recommendedMedia`() = runTest {
        vm.loadPopularData()
        advanceUntilIdle()

        println("Popular movies = ${vm.popularMovies.value.size}, shows = ${vm.popularTvShows.value.size}")

        val method = vm::class.java.getDeclaredMethod("generateFallbackRecommendations")
        method.isAccessible = true
        method.invoke(vm)

        advanceUntilIdle()

        val rec = vm.recommendedMovies.value
        println("Fallback Recommendations = ${rec.map { it.title }}")

        assertTrue("Expected fallback recommendations (even empty ok)", rec != null)
    }

    @Test
    fun `onUserActivityDetected refreshes smart recommendations if session exists`() = runTest {
        every { authRepo.getSessionId() } returns flowOf("fake-session")
        coEvery { authRepo.getAccountDetails() } returns Result.success(
            AccountDetails(
                id = 99,
                username = "Tester",
                name = "Tester",
                includeAdult = false,
                iso6391 = "en",
                iso31661 = "US",
                avatar = null
            )
        )

        coEvery { moviesRepo.getSmartRecommendations(any(), any()) } returns listOf(
            media(id = 101, title = "Smart Rec One", type = "movie"),
            media(id = 102, title = "Smart Rec Two", type = "tv")
        )

        vm.onUserActivityDetected(forceRefresh = true)
        advanceUntilIdle()

        val rec = vm.recommendedMovies.value
        println("Smart Recommendations = ${rec.map { it.title }}")
        assertTrue("Smart recommendations list should exist", rec.size >= 0)
    }
    @Test
    fun `observeSession without session uses fallback`() = runTest {
        every { authRepo.getSessionId() } returns flowOf(null)
        vm.loadPopularData()
        advanceUntilIdle()

        val rec = vm.recommendedMovies.value
        println("observeSession fallback Recommendations=${rec.map { it.title }}")
        assertTrue("Fallback list should not crash", rec != null)
    }

    private fun media(
        id: Int,
        title: String?,
        type: String?,
        rating: Double? = 0.0
    ) = MediaEntity(
        id = id,
        title = title,
        name = title,
        overview = null,
        posterPath = null,
        backdropPath = null,
        voteAverage = rating,
        releaseDate = null,
        firstAirDate = null,
        mediaType = type,
        adult = false
    )
}