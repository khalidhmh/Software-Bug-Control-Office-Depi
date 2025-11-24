package com.example.mda.ui.kids.search

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.kidsSearchDataStore by preferencesDataStore(name = "kids_search_prefs")

object KidsSearchStore {
    private val KEY_HISTORY = stringSetPreferencesKey("kids_search_history")
    private val KEY_LAST_QUERY = stringPreferencesKey("kids_search_last_query")

    fun historyFlow(context: Context): Flow<List<String>> =
        context.kidsSearchDataStore.data.map { prefs ->
            (prefs[KEY_HISTORY] ?: emptySet()).toList().sortedBy { it.lowercase() }
        }

    fun lastQueryFlow(context: Context): Flow<String> =
        context.kidsSearchDataStore.data.map { it[KEY_LAST_QUERY] ?: "" }

    suspend fun saveQuery(context: Context, query: String) {
        if (query.isBlank()) return
        context.kidsSearchDataStore.edit { prefs ->
            val current = prefs[KEY_HISTORY] ?: emptySet()
            prefs[KEY_HISTORY] = (current + query).take(50).toSet() // cap size
            prefs[KEY_LAST_QUERY] = query
        }
    }

    suspend fun clearHistory(context: Context) {
        context.kidsSearchDataStore.edit { prefs ->
            prefs[KEY_HISTORY] = emptySet()
        }
    }

    suspend fun deleteOne(context: Context, query: String) {
        context.kidsSearchDataStore.edit { prefs ->
            val current = prefs[KEY_HISTORY] ?: emptySet()
            prefs[KEY_HISTORY] = (current - query)
        }
    }
}
