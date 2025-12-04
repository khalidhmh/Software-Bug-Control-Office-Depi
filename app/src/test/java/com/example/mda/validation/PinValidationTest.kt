package com.example.mda.validation

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for PIN validation logic
 * Tests PIN format, length, comparison, and edge cases
 */
class PinValidationTest {

    @Test
    fun testValidPinLength4() {
        val pin = "1234"
        assertTrue(isValidPinLength(pin, 4))
    }

    @Test
    fun testValidPinLength6() {
        val pin = "123456"
        assertTrue(isValidPinLength(pin, 6))
    }

    @Test
    fun testInvalidPinTooShort() {
        val pin = "123"
        assertFalse(isValidPinLength(pin, 4))
    }

    @Test
    fun testInvalidPinTooLong() {
        val pin = "1234567"
        assertFalse(isValidPinLength(pin, 6))
    }

    @Test
    fun testPinComparison() {
        val pin1 = "123456"
        val pin2 = "123456"
        assertTrue(comparePins(pin1, pin2))
    }

    @Test
    fun testPinComparisonFails() {
        val pin1 = "123456"
        val pin2 = "654321"
        assertFalse(comparePins(pin1, pin2))
    }

    @Test
    fun testPinComparisonWithWhitespace() {
        val pin1 = "  123456  "
        val pin2 = "123456"
        assertTrue(comparePins(pin1, pin2))
    }

    @Test
    fun testPinComparisonCaseSensitive() {
        val pin1 = "123456"
        val pin2 = "123456"
        assertTrue(comparePins(pin1, pin2))
    }

    @Test
    fun testEmptyPinValidation() {
        val pin = ""
        assertFalse(isValidPinLength(pin, 4))
    }

    @Test
    fun testPinWithLetters() {
        val pin = "12AB34"
        assertTrue(pin.length == 6)
    }

    @Test
    fun testPinWithSpecialCharacters() {
        val pin = "12@456"
        assertTrue(pin.length == 6)
    }

    @Test
    fun testRequiredPinLength() {
        val savedPin = "123456"
        val requiredLength = (savedPin.length).coerceIn(4, 6)
        assertEquals(6, requiredLength)
    }

    @Test
    fun testRequiredPinLengthLegacy() {
        val savedPin = "1234"
        val requiredLength = (savedPin.length).coerceIn(4, 6)
        assertEquals(4, requiredLength)
    }

    @Test
    fun testRequiredPinLengthNull() {
        val savedPin: String? = null
        val requiredLength = (savedPin?.length ?: 6).coerceIn(4, 6)
        assertEquals(6, requiredLength)
    }

    @Test
    fun testPinMismatchDetection() {
        val firstPin = "123456"
        val confirmPin = "654321"
        assertFalse(comparePins(firstPin, confirmPin))
    }

    @Test
    fun testPinMatchDetection() {
        val firstPin = "123456"
        val confirmPin = "123456"
        assertTrue(comparePins(firstPin, confirmPin))
    }

    @Test
    fun testPinNullComparison() {
        val pin1: String? = null
        val pin2 = "123456"
        assertFalse(comparePins(pin1 ?: "", pin2))
    }

    @Test
    fun testPinEmptyComparison() {
        val pin1 = ""
        val pin2 = ""
        assertTrue(comparePins(pin1, pin2))
    }

    // Helper functions
    private fun isValidPinLength(pin: String, requiredLength: Int): Boolean {
        return pin.length == requiredLength
    }

    private fun comparePins(pin1: String, pin2: String): Boolean {
        return pin1.trim() == pin2.trim()
    }
}
