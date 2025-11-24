package com.example.mda.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.settingsDataStore by preferencesDataStore(name = "settings_prefs")

class SettingsDataStore(private val context: Context) {

    companion object {
        val THEME_MODE = intPreferencesKey("theme_mode")
        val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
    }

    val themeModeFlow = context.settingsDataStore.data.map { prefs ->
        prefs[THEME_MODE] ?: 0     // 0 = System, 1 = Light, 2 = Dark
    }

    val notificationsFlow = context.settingsDataStore.data.map { prefs ->
        prefs[NOTIFICATION_ENABLED] ?: true
    }

    suspend fun setThemeMode(mode: Int) {
        context.settingsDataStore.edit { prefs ->
            prefs[THEME_MODE] = mode
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[NOTIFICATION_ENABLED] = enabled
        }
    }
}
