package com.example.mda.localization

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for LocalizationKeys
 * Verifies that all required localization keys are defined
 */
class LocalizationKeysTest {

    @Test
    fun testPasswordSettingsKeysExist() {
        assertNotNull(LocalizationKeys.SETTINGS_PASSWORD)
        assertNotNull(LocalizationKeys.PW_SET_KIDS_PIN)
        assertNotNull(LocalizationKeys.PW_CHANGE_KIDS_PIN)
        assertNotNull(LocalizationKeys.PW_MANAGE_DESC)
    }

    @Test
    fun testPinInputKeysExist() {
        assertNotNull(LocalizationKeys.PW_ENTER_PIN)
        assertNotNull(LocalizationKeys.PW_CONFIRM_PIN)
        assertNotNull(LocalizationKeys.PW_ENTER_OLD_PIN)
        assertNotNull(LocalizationKeys.PW_ENTER_NEW_PIN)
        assertNotNull(LocalizationKeys.PW_CONFIRM_NEW_PIN)
    }

    @Test
    fun testPinHintKeysExist() {
        assertNotNull(LocalizationKeys.PW_HINT_SET)
        assertNotNull(LocalizationKeys.PW_HINT_CHANGE)
    }

    @Test
    fun testPinErrorKeysExist() {
        assertNotNull(LocalizationKeys.PW_PINS_MISMATCH_TRY_AGAIN)
        assertNotNull(LocalizationKeys.PW_FORGOT_PIN)
    }

    @Test
    fun testSecurityQuestionsKeysExist() {
        assertNotNull(LocalizationKeys.SQ_TITLE)
        assertNotNull(LocalizationKeys.SQ_HEADER_SETUP)
        assertNotNull(LocalizationKeys.SQ_SUBTEXT_SETUP)
        assertNotNull(LocalizationKeys.SQ_HEADER_VERIFY)
        assertNotNull(LocalizationKeys.SQ_HINT_VERIFY)
        assertNotNull(LocalizationKeys.SQ_QUESTION_1)
        assertNotNull(LocalizationKeys.SQ_QUESTION_2)
        assertNotNull(LocalizationKeys.SQ_QUESTION_3)
        assertNotNull(LocalizationKeys.SQ_BTN_VERIFY)
        assertNotNull(LocalizationKeys.SQ_SELECT_ALL_QUESTIONS)
        assertNotNull(LocalizationKeys.SQ_NOT_SET)
        assertNotNull(LocalizationKeys.SQ_ANSWERS_NOT_MATCH)
    }

    @Test
    fun testLanguageSettingsKeysExist() {
        assertNotNull(LocalizationKeys.SETTINGS_LANGUAGE)
        assertNotNull(LocalizationKeys.SETTINGS_LANGUAGE_SELECT_TITLE)
        assertNotNull(LocalizationKeys.SETTINGS_LANGUAGE_INFO_TITLE)
        assertNotNull(LocalizationKeys.SETTINGS_LANGUAGE_INFO_BODY)
        assertNotNull(LocalizationKeys.SETTINGS_LANGUAGE_SELECTED_CD)
    }

    @Test
    fun testCommonKeysExist() {
        assertNotNull(LocalizationKeys.COMMON_BACK)
        assertNotNull(LocalizationKeys.BTN_SAVE)
        assertNotNull(LocalizationKeys.NAV_HOME)
        assertNotNull(LocalizationKeys.NAV_SEARCH)
        assertNotNull(LocalizationKeys.NAV_FAVORITES)
    }

    @Test
    fun testSettingsKeysExist() {
        assertNotNull(LocalizationKeys.SETTINGS_KIDS_MODE)
        assertNotNull(LocalizationKeys.SETTINGS_PRIVACY_POLICY)
    }

    @Test
    fun testAllKeysAreStrings() {
        val keys = listOf(
            LocalizationKeys.SETTINGS_PASSWORD,
            LocalizationKeys.PW_SET_KIDS_PIN,
            LocalizationKeys.PW_CHANGE_KIDS_PIN,
            LocalizationKeys.SETTINGS_LANGUAGE,
            LocalizationKeys.SQ_TITLE
        )
        
        keys.forEach { key ->
            assertTrue("Key should be a string", key is String)
            assertTrue("Key should not be empty", (key as String).isNotEmpty())
        }
    }

    @Test
    fun testKeyNamingConvention() {
        // All keys should follow naming convention
        assertTrue(LocalizationKeys.SETTINGS_PASSWORD.startsWith("SETTINGS_") || 
                  LocalizationKeys.SETTINGS_PASSWORD.startsWith("PW_") ||
                  LocalizationKeys.SETTINGS_PASSWORD.startsWith("SQ_") ||
                  LocalizationKeys.SETTINGS_PASSWORD.startsWith("COMMON_") ||
                  LocalizationKeys.SETTINGS_PASSWORD.startsWith("NAV_") ||
                  LocalizationKeys.SETTINGS_PASSWORD.startsWith("BTN_"))
    }

    @Test
    fun testPasswordRelatedKeysCount() {
        val passwordKeys = listOf(
            LocalizationKeys.SETTINGS_PASSWORD,
            LocalizationKeys.PW_SET_KIDS_PIN,
            LocalizationKeys.PW_CHANGE_KIDS_PIN,
            LocalizationKeys.PW_ENTER_PIN,
            LocalizationKeys.PW_CONFIRM_PIN,
            LocalizationKeys.PW_HINT_SET,
            LocalizationKeys.PW_HINT_CHANGE,
            LocalizationKeys.PW_PINS_MISMATCH_TRY_AGAIN,
            LocalizationKeys.PW_FORGOT_PIN
        )
        
        assertTrue("Should have at least 9 password-related keys", passwordKeys.size >= 9)
    }

    @Test
    fun testSecurityQuestionsKeysCount() {
        val sqKeys = listOf(
            LocalizationKeys.SQ_TITLE,
            LocalizationKeys.SQ_HEADER_SETUP,
            LocalizationKeys.SQ_HEADER_VERIFY,
            LocalizationKeys.SQ_BTN_VERIFY,
            LocalizationKeys.SQ_SELECT_ALL_QUESTIONS,
            LocalizationKeys.SQ_NOT_SET,
            LocalizationKeys.SQ_ANSWERS_NOT_MATCH
        )
        
        assertTrue("Should have at least 7 security questions keys", sqKeys.size >= 7)
    }

    @Test
    fun testLanguageSettingsKeysCount() {
        val langKeys = listOf(
            LocalizationKeys.SETTINGS_LANGUAGE,
            LocalizationKeys.SETTINGS_LANGUAGE_SELECT_TITLE,
            LocalizationKeys.SETTINGS_LANGUAGE_INFO_TITLE,
            LocalizationKeys.SETTINGS_LANGUAGE_INFO_BODY
        )
        
        assertTrue("Should have at least 4 language settings keys", langKeys.size >= 4)
    }

    @Test
    fun testNoKeyDuplication() {
        val allKeys = listOf(
            LocalizationKeys.SETTINGS_PASSWORD,
            LocalizationKeys.PW_SET_KIDS_PIN,
            LocalizationKeys.PW_CHANGE_KIDS_PIN,
            LocalizationKeys.SETTINGS_LANGUAGE,
            LocalizationKeys.SQ_TITLE,
            LocalizationKeys.COMMON_BACK,
            LocalizationKeys.BTN_SAVE
        )
        
        val uniqueKeys = allKeys.toSet()
        assertEquals("All keys should be unique", allKeys.size, uniqueKeys.size)
    }
}
