package com.example.mda.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.introDataStore by preferencesDataStore("intro_prefs")

class IntroDataStore(private val context: Context) {
    companion object {
        private val KEY_INTRO_SHOWN = booleanPreferencesKey("intro_shown")
    }

    val isIntroShown: Flow<Boolean> = context.introDataStore.data.map { prefs ->
        prefs[KEY_INTRO_SHOWN] ?: false
    }

    suspend fun setIntroShown(shown: Boolean) {
        context.introDataStore.edit { prefs ->
            prefs[KEY_INTRO_SHOWN] = shown
        }
    }
}