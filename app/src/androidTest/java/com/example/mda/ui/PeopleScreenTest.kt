package com.example.mda.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.testing.TestNavHostController
import androidx.test.platform.app.InstrumentationRegistry
import com.example.mda.data.remote.model.Actor
import com.example.mda.ui.screens.actors.ActorViewModel
import com.example.mda.data.repository.ActorsRepository
import com.example.mda.data.local.dao.ActorDao
import com.example.mda.ui.screens.actors.ActorUiState
import com.example.mda.ui.screens.actors.ActorsScreen
import com.example.mda.ui.screens.actors.ViewType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class PeopleScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun peopleScreen_displaysActorsCorrectly() {
        val fakeActors = listOf(
            Actor(
                id = 1,
                name = "Tom Cruise",
                profilePath = null,
                biography = "Famous action actor",
                birthday = "1962‑07‑03",
                placeOfBirth = "USA",
                knownForDepartment = "Acting",
                knownFor = emptyList()
            ),
            Actor(
                id = 2,
                name = "Emma Stone",
                profilePath = null,
                biography = "Award winning actress",
                birthday = "1988‑11‑06",
                placeOfBirth = "USA",
                knownForDepartment = "Acting",
                knownFor = emptyList()
            )
        )

        val repo = mockk<ActorsRepository>()
        val dao = mockk<ActorDao>()
        val viewModel = mockk<ActorViewModel>()
        val fakeUiState = MutableStateFlow<ActorUiState>(ActorUiState.Success(fakeActors))
        val fakeViewType = MutableStateFlow(ViewType.LIST)
        val fakeRefreshing = MutableStateFlow(false)

        coEvery { viewModel.state } returns fakeUiState
        coEvery { viewModel.viewType } returns fakeViewType
        coEvery { viewModel.isRefreshing } returns fakeRefreshing
        coEvery { viewModel.loadActors(any()) } returns Unit
        coEvery { viewModel.loadMoreActors() } returns Unit
        coEvery { viewModel.toggleViewType() } returns Unit

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val navController = TestNavHostController(context)

        composeTestRule.setContent {
            val topState = remember { mutableStateOf(Unit) }
            ActorsScreen(
                navController = navController,
                viewModel = viewModel,
                onTopBarStateChange = {},
                actorsRepository = repo
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithTag("actor_1").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("actor_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("actor_2").assertIsDisplayed()
    }
}