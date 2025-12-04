package com.example.mda.localization

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for LocalizationManager
 * Tests language switching, translation retrieval, and persistence
 */
class LocalizationManagerTest {

    private lateinit var context: Context
    private lateinit var localizationManager: LocalizationManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        localizationManager = LocalizationManager(context)
    }

    @Test
    fun testDefaultLanguageIsEnglish() = runBlocking {
        val currentLanguage = localizationManager.currentLanguage.first()
        assertEquals(LocalizationManager.Language.ENGLISH, currentLanguage)
    }

    @Test
    fun testLanguageSwitchToArabic() = runBlocking {
        localizationManager.setLanguage(LocalizationManager.Language.ARABIC)
        val currentLanguage = localizationManager.currentLanguage.first()
        assertEquals(LocalizationManager.Language.ARABIC, currentLanguage)
    }

    @Test
    fun testLanguageSwitchToGerman() = runBlocking {
        localizationManager.setLanguage(LocalizationManager.Language.GERMAN)
        val currentLanguage = localizationManager.currentLanguage.first()
        assertEquals(LocalizationManager.Language.GERMAN, currentLanguage)
    }

    @Test
    fun testLanguagePersistence() = runBlocking {
        // Set language to Arabic
        localizationManager.setLanguage(LocalizationManager.Language.ARABIC)
        
        // Create new instance to verify persistence
        val newManager = LocalizationManager(context)
        val currentLanguage = newManager.currentLanguage.first()
        assertEquals(LocalizationManager.Language.ARABIC, currentLanguage)
    }

    @Test
    fun testGetStringEnglish() {
        val result = localizationManager.getString(LocalizationKeys.SETTINGS_LANGUAGE, LocalizationManager.Language.ENGLISH)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testGetStringArabic() {
        val result = localizationManager.getString(LocalizationKeys.SETTINGS_LANGUAGE, LocalizationManager.Language.ARABIC)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testGetStringGerman() {
        val result = localizationManager.getString(LocalizationKeys.SETTINGS_LANGUAGE, LocalizationManager.Language.GERMAN)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun testPasswordSettingsKeysExist() {
        val keys = listOf(
            LocalizationKeys.SETTINGS_PASSWORD,
            LocalizationKeys.PW_SET_KIDS_PIN,
            LocalizationKeys.PW_CHANGE_KIDS_PIN,
            LocalizationKeys.PW_ENTER_PIN,
            LocalizationKeys.PW_CONFIRM_PIN,
            LocalizationKeys.PW_HINT_SET,
            LocalizationKeys.PW_HINT_CHANGE
        )
        
        keys.forEach { key ->
            val enResult = localizationManager.getString(key, LocalizationManager.Language.ENGLISH)
            val arResult = localizationManager.getString(key, LocalizationManager.Language.ARABIC)
            val deResult = localizationManager.getString(key, LocalizationManager.Language.GERMAN)
            
            assertTrue("Key $key missing in English", enResult.isNotEmpty())
            assertTrue("Key $key missing in Arabic", arResult.isNotEmpty())
            assertTrue("Key $key missing in German", deResult.isNotEmpty())
        }
    }

    @Test
    fun testLanguageSettingsKeysExist() {
        val keys = listOf(
            LocalizationKeys.SETTINGS_LANGUAGE,
            LocalizationKeys.SETTINGS_LANGUAGE_SELECT_TITLE,
            LocalizationKeys.SETTINGS_LANGUAGE_INFO_TITLE,
            LocalizationKeys.SETTINGS_LANGUAGE_INFO_BODY
        )
        
        keys.forEach { key ->
            val result = localizationManager.getString(key, LocalizationManager.Language.ENGLISH)
            assertTrue("Key $key missing", result.isNotEmpty())
        }
    }

    @Test
    fun testSecurityQuestionsKeysExist() {
        val keys = listOf(
            LocalizationKeys.SQ_TITLE,
            LocalizationKeys.SQ_HEADER_SETUP,
            LocalizationKeys.SQ_HEADER_VERIFY,
            LocalizationKeys.SQ_BTN_VERIFY
        )
        
        keys.forEach { key ->
            val result = localizationManager.getString(key, LocalizationManager.Language.ENGLISH)
            assertTrue("Key $key missing", result.isNotEmpty())
        }
    }

    @Test
    fun testLanguageCodeMapping() {
        assertEquals("en", LocalizationManager.Language.ENGLISH.code)
        assertEquals("ar", LocalizationManager.Language.ARABIC.code)
        assertEquals("de", LocalizationManager.Language.GERMAN.code)
    }

    @Test
    fun testLanguageDisplayNames() {
        assertTrue(LocalizationManager.Language.ENGLISH.displayName.isNotEmpty())
        assertTrue(LocalizationManager.Language.ARABIC.displayName.isNotEmpty())
        assertTrue(LocalizationManager.Language.GERMAN.displayName.isNotEmpty())
    }
}
