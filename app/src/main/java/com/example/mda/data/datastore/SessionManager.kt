package com.example.mda.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class SessionManager(private val context: Context) {
    
    companion object {
        private val SESSION_ID_KEY = stringPreferencesKey("session_id")
        private val REQUEST_TOKEN_KEY = stringPreferencesKey("request_token")
    }

    suspend fun saveSessionId(sessionId: String) {
        context.dataStore.edit { preferences ->
            preferences[SESSION_ID_KEY] = sessionId
        }
    }

    suspend fun saveRequestToken(requestToken: String) {
        context.dataStore.edit { preferences ->
            preferences[REQUEST_TOKEN_KEY] = requestToken
        }
    }

    val sessionId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[SESSION_ID_KEY]
    }

    val requestToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[REQUEST_TOKEN_KEY]
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(SESSION_ID_KEY)
            preferences.remove(REQUEST_TOKEN_KEY)
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[SESSION_ID_KEY] != null
        }.first()
    }
}
