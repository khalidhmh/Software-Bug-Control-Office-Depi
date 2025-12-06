package com.example.mda.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.auth.AuthViewModel
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.settings.SettingsScreen
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4


@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun settingsScreen_displaysAllImportantElements() {
        val fakeAuthViewModel = mockk<AuthViewModel>(relaxed = true)
        every { fakeAuthViewModel.uiState } returns MutableStateFlow(
            com.example.mda.ui.screens.auth.AuthUiState(isAuthenticated = false)
        )

        val fakeFavoritesViewModel: FavoritesViewModel = mockk(relaxed = true)

        composeRule.setContent {
            SettingsScreen(
                navController = rememberNavController(),
                onTopBarStateChange = {},
                authViewModel = fakeAuthViewModel,
                FavoritesViewModel = fakeFavoritesViewModel
            )
        }

        composeRule.onNodeWithText("Notifications").assertExists()
        composeRule.onNodeWithText("Dark Mode").assertExists()

        composeRule.onNode(hasText("Notifications") and hasClickAction()).performClick()
        composeRule.onNode(hasText("Dark Mode") and hasClickAction()).performClick()

    }

    @Test
    fun settingsScreen_canNavigateFavoritesItem() {
        val fakeAuthViewModel = mockk<AuthViewModel>(relaxed = true)
        every { fakeAuthViewModel.uiState } returns MutableStateFlow(
            com.example.mda.ui.screens.auth.AuthUiState(isAuthenticated = false)
        )
        val fakeFavoritesViewModel: FavoritesViewModel = mockk(relaxed = true)

        composeRule.setContent {
            SettingsScreen(
                navController = rememberNavController(),
                onTopBarStateChange = { _: TopBarState -> },
                authViewModel = fakeAuthViewModel,
                FavoritesViewModel = fakeFavoritesViewModel
            )
        }
        composeRule.onAllNodesWithText("Favorite Movies")
    }
}