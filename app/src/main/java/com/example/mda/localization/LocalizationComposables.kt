package com.example.mda.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext

/**
 * Composable helper to get localized strings
 * Usage: val text = localizedString(LocalizationKeys.HOME_TITLE)
 */
@Composable
fun localizedString(key: String): String {
    val context = LocalContext.current
    val manager = LocalizationManager(context)
    val language = manager.currentLanguage.collectAsState(initial = LocalizationManager.Language.ENGLISH).value
    return manager.getString(key, language)
}

/**
 * Get localized string with placeholder replacement
 * Usage: localizedString(LocalizationKeys.SEARCH_ERROR, mapOf("error" to "Network error"))
 */
@Composable
fun localizedString(key: String, replacements: Map<String, String>): String {
    var text = localizedString(key)
    replacements.forEach { (placeholder, value) ->
        text = text.replace("{$placeholder}", value)
    }
    return text
}

/**
 * Get localized string with single placeholder
 * Usage: localizedString(LocalizationKeys.SEARCH_ERROR, "error", "Network error")
 */
@Composable
fun localizedString(key: String, placeholder: String, value: String): String {
    return localizedString(key, mapOf(placeholder to value))
}
