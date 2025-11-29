package com.example.mda.repository

import android.util.Log
import com.example.mda.data.local.dao.ActorDao
import com.example.mda.data.local.entities.ActorEntity
import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.remote.model.Actor
import com.example.mda.data.remote.model.ActorResponse
import com.example.mda.data.repository.ActorsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ActorsRepositoryTest {

    private val api: TmdbApi = mockk()
    private val actorDao: ActorDao = mockk(relaxed = true)
    private lateinit var repository: ActorsRepository

    @Before
    fun setup() {
        // ✅ حل نهائي لمشكلة اللوجز: نعمل Mock لكل أنواع Log
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0

        repository = ActorsRepository(api, actorDao)
    }

    @Test
    fun `getPopularActorsWithCache returns data from API when successful`() = runTest {
        // Arrange
        val fakeActor = Actor(
            id = 1,
            name = "Tom Cruise",
            profilePath = "path.jpg",
            biography = "Some bio",
            birthday = "1962-07-03",
            placeOfBirth = "USA",
            knownForDepartment = "Acting",
            knownFor = emptyList()
        )

        val response = Response.success(
            ActorResponse(page = 1, results = listOf(fakeActor), total_pages = 1, total_results = 1)
        )

        coEvery { api.getPopularPeople(page = 1) } returns response

        // Act
        val result = repository.getPopularActorsWithCache(1)

        // Assert
        assertEquals(1, result.size)
        assertEquals("Tom Cruise", result.first().name)
        coVerify(exactly = 1) { api.getPopularPeople(1) }
    }

    @Test
    fun `getPopularActorsWithCache returns data from cache when API throws exception`() = runTest {
        // Arrange
        coEvery { api.getPopularPeople(page = 1) } throws RuntimeException("Network error")
        val cachedActors = listOf(
            ActorEntity(
                id = 2,
                name = "Emma Stone",
                profilePath = "profile.jpg",
                biography = "Actress",
                birthday = "1988-11-06",
                placeOfBirth = "USA",
                knownForDepartment = "Acting",
                knownFor = "[]"
            )
        )
        coEvery { actorDao.getAllActors() } returns cachedActors

        // Act
        val result = repository.getPopularActorsWithCache(1)

        // Assert
        assertFalse(result.isEmpty())
        assertEquals("Emma Stone", result.first().name)
    }

    @Test
    fun `getCachedActors returns actors from DAO`() = runTest {
        // Arrange
        val cached = listOf(
            ActorEntity(
                id = 3,
                name = "Johnny Depp",
                profilePath = null,
                biography = "Actor",
                birthday = null,
                placeOfBirth = null,
                knownForDepartment = "Acting",
                knownFor = null
            )
        )
        coEvery { actorDao.getAllActors() } returns cached

        // Act
        val result = repository.getCachedActors()

        // Assert
        assertEquals(1, result.size)
        assertEquals("Johnny Depp", result.first().name)
        coVerify { actorDao.getAllActors() }
    }
}