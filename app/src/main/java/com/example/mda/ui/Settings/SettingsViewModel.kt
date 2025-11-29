package com.example.mda.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mda.data.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val dataStore: SettingsDataStore) : ViewModel() {

    // ---------------------- Flows ----------------------
    // Theme mode: 0 = System, 1 = Light, 2 = Dark
    val themeMode = dataStore.themeModeFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Notifications enabled flow
    val notificationsEnabled = dataStore.notificationsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    // ---------------------- Functions ----------------------
    fun updateTheme(mode: Int) {
        viewModelScope.launch {
            dataStore.setThemeMode(mode)
        }
    }

    fun updateNotifications(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.setNotificationsEnabled(enabled)
        }
    }
}

// ---------------------- ViewModel Factory ----------------------
@Suppress("UNCHECKED_CAST")
class SettingsViewModelFactory(
    private val dataStore: SettingsDataStore
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(dataStore) as T
    }
}
