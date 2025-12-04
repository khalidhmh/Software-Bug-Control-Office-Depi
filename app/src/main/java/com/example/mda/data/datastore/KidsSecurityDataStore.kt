package com.example.mda.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.kidsSecurityDataStore: DataStore<Preferences> by preferencesDataStore(name = "kids_security_prefs")

class KidsSecurityDataStore(private val context: Context) {
    companion object {
        private val KEY_PIN = stringPreferencesKey("kids_pin")
        private val KEY_LOCK_ENABLED = intPreferencesKey("kids_lock_enabled") // 1 true, 0 false
        private val KEY_Q1 = intPreferencesKey("kids_sec_q1")
        private val KEY_Q2 = intPreferencesKey("kids_sec_q2")
        private val KEY_Q3 = intPreferencesKey("kids_sec_q3")
        private val KEY_A1 = stringPreferencesKey("kids_sec_a1")
        private val KEY_A2 = stringPreferencesKey("kids_sec_a2")
        private val KEY_A3 = stringPreferencesKey("kids_sec_a3")
        // Index-based answers to be language-independent
        private val KEY_A1_INDEX = intPreferencesKey("kids_sec_a1_index")
        private val KEY_A2_INDEX = intPreferencesKey("kids_sec_a2_index")
        private val KEY_A3_INDEX = intPreferencesKey("kids_sec_a3_index")
    }

    val pinFlow: Flow<String?> = context.kidsSecurityDataStore.data.map { it[KEY_PIN] }
    val lockEnabledFlow: Flow<Boolean> = context.kidsSecurityDataStore.data.map { (it[KEY_LOCK_ENABLED] ?: 0) == 1 }

    data class SecurityQA(
        val q1: Int?, val q2: Int?, val q3: Int?,
        val a1: String?, val a2: String?, val a3: String?,
        val a1Index: Int?, val a2Index: Int?, val a3Index: Int?
    )
    val securityQAFlow: Flow<SecurityQA> = context.kidsSecurityDataStore.data.map {
        SecurityQA(
            it[KEY_Q1], it[KEY_Q2], it[KEY_Q3],
            it[KEY_A1], it[KEY_A2], it[KEY_A3],
            it[KEY_A1_INDEX], it[KEY_A2_INDEX], it[KEY_A3_INDEX]
        )
    }

    suspend fun setPin(pin: String) {
        context.kidsSecurityDataStore.edit { prefs ->
            prefs[KEY_PIN] = pin
        }
    }

    suspend fun clearPin() {
        context.kidsSecurityDataStore.edit { prefs ->
            prefs.remove(KEY_PIN)
        }
    }

    suspend fun setLockEnabled(enabled: Boolean) {
        context.kidsSecurityDataStore.edit { prefs ->
            prefs[KEY_LOCK_ENABLED] = if (enabled) 1 else 0
        }
    }

    suspend fun setSecurityQA(q1: Int, q2: Int, q3: Int, a1: String, a2: String, a3: String) {
        context.kidsSecurityDataStore.edit { prefs ->
            prefs[KEY_Q1] = q1
            prefs[KEY_Q2] = q2
            prefs[KEY_Q3] = q3
            prefs[KEY_A1] = a1.trim()
            prefs[KEY_A2] = a2.trim()
            prefs[KEY_A3] = a3.trim()
        }
    }

    suspend fun setSecurityQAWithIndices(
        q1: Int, q2: Int, q3: Int,
        a1Index: Int, a2Index: Int, a3Index: Int,
        a1Text: String, a2Text: String, a3Text: String
    ) {
        context.kidsSecurityDataStore.edit { prefs ->
            prefs[KEY_Q1] = q1
            prefs[KEY_Q2] = q2
            prefs[KEY_Q3] = q3
            // store indices
            prefs[KEY_A1_INDEX] = a1Index
            prefs[KEY_A2_INDEX] = a2Index
            prefs[KEY_A3_INDEX] = a3Index
            // store texts for backward compatibility / debug
            prefs[KEY_A1] = a1Text.trim()
            prefs[KEY_A2] = a2Text.trim()
            prefs[KEY_A3] = a3Text.trim()
        }
    }

    suspend fun verifyPin(pin: String): Boolean {
        val current = pinFlow.map { it }.let { flow ->
            var v: String? = null
            // NOTE: caller should collect, but for simplicity this method is suspend-called from UI with collect-first pattern elsewhere
            // Keep this method simple by using a one-shot read via edit hack is not good; leaving verification to caller is preferred.
            v
        }
        // This method is unused directly; verify in UI by collecting pinFlow.
        return false
    }
}
