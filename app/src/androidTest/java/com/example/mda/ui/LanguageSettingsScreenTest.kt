package com.example.mda.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.LocalizationManager
import com.example.mda.ui.Settings.LanguageSettingsScreen
import org.junit.Rule
import org.junit.Test

class LanguageSettingsScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun languageSettings_showsTitleOptionsAndBack() {
        val context = composeRule.activity
        val manager = LocalizationManager(context)
        val title = manager.getString(LocalizationKeys.SETTINGS_LANGUAGE_SELECT_TITLE, LocalizationManager.Language.ENGLISH)
        val back = manager.getString(LocalizationKeys.COMMON_BACK, LocalizationManager.Language.ENGLISH)

        composeRule.setContent {
            val navController = rememberNavController()
            LanguageSettingsScreen(
                navController = navController,
                onTopBarStateChange = { /* no-op in test */ }
            )
        }

        // Title visible
        composeRule.onNodeWithText(title, substring = false).assertIsDisplayed()
        // Language options visible by display names
        composeRule.onNodeWithText(LocalizationManager.Language.ENGLISH.displayName).assertIsDisplayed()
        composeRule.onNodeWithText(LocalizationManager.Language.ARABIC.displayName).assertIsDisplayed()
        composeRule.onNodeWithText(LocalizationManager.Language.GERMAN.displayName).assertIsDisplayed()
        // Back button visible
        composeRule.onNodeWithText(back).assertIsDisplayed()
    }
}
