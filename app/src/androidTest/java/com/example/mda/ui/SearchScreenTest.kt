package com.example.mda.ui

import androidx.navigation.testing.TestNavHostController
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.SavedStateHandle
import androidx.test.platform.app.InstrumentationRegistry
import com.example.mda.ui.screens.search.SearchScreen
import com.example.mda.ui.screens.search.SearchViewModel
import com.example.mda.data.local.dao.SearchHistoryDao
import com.example.mda.data.repository.MoviesRepository
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.auth.AuthViewModel
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class SearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchScreen_displaysSuccessResults() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val navController = TestNavHostController(context)
        val moviesRepo: MoviesRepository = mockk(relaxed = true)
        val dao: SearchHistoryDao = mockk(relaxed = true)
        val searchViewModel = SearchViewModel(moviesRepo, dao, SavedStateHandle())

        val favoritesViewModel = mockk<FavoritesViewModel>(relaxed = true)
        val authViewModel = mockk<AuthViewModel>(relaxed = true)

        composeTestRule.setContent {
            val topBarState = remember { mutableStateOf(TopBarState()) }

            SearchScreen(
                navController = navController,
                viewModel = searchViewModel,
                onTopBarStateChange = { topBarState.value = it },
                favoritesViewModel = favoritesViewModel,
                authViewModel = authViewModel
            )
        }
        composeTestRule.onNodeWithText("Search movies, shows, people...").assertIsDisplayed()    }
}