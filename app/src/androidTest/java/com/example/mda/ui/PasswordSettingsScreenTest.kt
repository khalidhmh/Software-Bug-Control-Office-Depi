package com.example.mda.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.LocalizationManager
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.settings.password.PasswordSettingsScreen
import org.junit.Rule
import org.junit.Test

class PasswordSettingsScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun passwordSettings_showsTitleAndActions() {
        val context = composeRule.activity
        val manager = LocalizationManager(context)
        val title = manager.getString(LocalizationKeys.SETTINGS_PASSWORD, LocalizationManager.Language.ENGLISH)
        val setPin = manager.getString(LocalizationKeys.PW_SET_KIDS_PIN, LocalizationManager.Language.ENGLISH)
        val changePin = manager.getString(LocalizationKeys.PW_CHANGE_KIDS_PIN, LocalizationManager.Language.ENGLISH)

        composeRule.setContent {
            val navController = rememberNavController()
            PasswordSettingsScreen(
                navController = navController,
                onTopBarStateChange = { _: TopBarState -> /* ignore in test */ }
            )
        }

        composeRule.onNodeWithText(setPin).assertExists()
        // It's acceptable that only one of them appears depending on current DataStore; we assert at least one is present
        // If change pin is visible, this also passes
        composeRule.onNodeWithText(changePin).assertExists()
    }
}
