package com.example.mda.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
        private val ACCOUNT_ID_KEY = intPreferencesKey("account_id")   // 👈 موجود تمام
        private val ACCOUNT_NAME_KEY = stringPreferencesKey("account_name")
        private val ACCOUNT_USERNAME_KEY = stringPreferencesKey("account_username")
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

    // 👇 حفظ الـ account_id
    suspend fun saveAccountId(accountId: Int) {
        context.dataStore.edit { preferences ->
            preferences[ACCOUNT_ID_KEY] = accountId
        }
    }
    suspend fun saveAccountInfo(name: String?, username: String) {
        context.dataStore.edit { preferences ->
            if (!name.isNullOrEmpty()) {
                preferences[ACCOUNT_NAME_KEY] = name
            }
            preferences[ACCOUNT_USERNAME_KEY] = username
        }
    }

    // 👇 قراءة الـ sessionId
    val sessionId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[SESSION_ID_KEY]
    }

    // 👇 قراءة الـ requestToken
    val requestToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[REQUEST_TOKEN_KEY]
    }

    // 👈 دي اللي كانت ناقصة: قراءة الـ accountId
    val accountId: Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[ACCOUNT_ID_KEY]
    }
    val accountName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ACCOUNT_NAME_KEY]
    }

    val accountUsername: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ACCOUNT_USERNAME_KEY]
    }
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(SESSION_ID_KEY)
            preferences.remove(REQUEST_TOKEN_KEY)
            preferences.remove(ACCOUNT_ID_KEY)
            preferences.remove(ACCOUNT_NAME_KEY)
            preferences.remove(ACCOUNT_USERNAME_KEY)
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[SESSION_ID_KEY] != null
        }.first()
    }
}
