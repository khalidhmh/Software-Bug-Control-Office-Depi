package com.example.mda.viewmodel

import com.example.mda.data.SettingsDataStore
import com.example.mda.ui.screens.settings.SettingsViewModel
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: SettingsViewModel
    private val dataStore: SettingsDataStore = mockk(relaxed = true)

    private val themeFlow = MutableStateFlow(0)
    private val notificationsFlow = MutableStateFlow(true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { dataStore.themeModeFlow } returns themeFlow
        every { dataStore.notificationsFlow } returns notificationsFlow
        coJustRun { dataStore.setThemeMode(any()) }
        coJustRun { dataStore.setNotificationsEnabled(any()) }
        viewModel = SettingsViewModel(dataStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    @Test
    fun `default themeMode and notificationsEnabled are read correctly`() = runTest {
        assertEquals(0, viewModel.themeMode.value)
        assertEquals(true, viewModel.notificationsEnabled.value)
    }

    @Test
    fun `updateTheme calls datastore with correct mode`() = runTest {
        viewModel.updateTheme(2)
        coVerify { dataStore.setThemeMode(2) }
    }

    @Test
    fun `updateNotifications calls datastore with correct value`() = runTest {
        viewModel.updateNotifications(false)
        coVerify { dataStore.setNotificationsEnabled(false) }
    }

    @Test
    fun `themeMode and notificationsEnabled flows emit updated values`() = runTest {
        themeFlow.value = 1
        notificationsFlow.value = false

        assertEquals(1, viewModel.themeMode.value)
        assertEquals(false, viewModel.notificationsEnabled.value)
    }
}