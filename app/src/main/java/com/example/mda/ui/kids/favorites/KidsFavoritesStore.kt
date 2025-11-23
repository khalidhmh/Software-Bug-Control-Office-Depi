package com.example.mda.ui.kids.favorites

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.kidsDataStore by preferencesDataStore(name = "kids_prefs")

object KidsFavoritesStore {
    private val KEY_IDS = stringSetPreferencesKey("kids_fav_ids")

    fun favoritesIdsFlow(context: Context): Flow<Set<Int>> =
        context.kidsDataStore.data.map { prefs ->
            (prefs[KEY_IDS] ?: emptySet()).mapNotNull { it.toIntOrNull() }.toSet()
        }

    fun isFavoriteFlow(context: Context, id: Int): Flow<Boolean> =
        favoritesIdsFlow(context).map { it.contains(id) }

    suspend fun toggle(context: Context, id: Int) {
        context.kidsDataStore.edit { prefs ->
            val current = prefs[KEY_IDS] ?: emptySet()
            prefs[KEY_IDS] = if (current.contains(id.toString())) {
                current - id.toString()
            } else {
                current + id.toString()
            }
        }
    }
}
