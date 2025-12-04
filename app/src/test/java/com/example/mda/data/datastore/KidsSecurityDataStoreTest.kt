package com.example.mda.data.datastore

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for KidsSecurityDataStore
 * Tests PIN storage, lock state, security questions, and kids mode persistence
 */
class KidsSecurityDataStoreTest {

    private lateinit var context: Context
    private lateinit var dataStore: KidsSecurityDataStore

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        dataStore = KidsSecurityDataStore(context)
    }

    @Test
    fun testSetAndGetPin() = runBlocking {
        val testPin = "123456"
        dataStore.setPin(testPin)
        
        val savedPin = dataStore.pinFlow.first()
        assertEquals(testPin, savedPin)
    }

    @Test
    fun testClearPin() = runBlocking {
        // First set a PIN
        dataStore.setPin("123456")
        var savedPin = dataStore.pinFlow.first()
        assertNotNull(savedPin)
        
        // Clear the PIN
        dataStore.clearPin()
        savedPin = dataStore.pinFlow.first()
        assertNull(savedPin)
    }

    @Test
    fun testSetLockEnabled() = runBlocking {
        dataStore.setLockEnabled(true)
        var lockEnabled = dataStore.lockEnabledFlow.first()
        assertTrue(lockEnabled)
        
        dataStore.setLockEnabled(false)
        lockEnabled = dataStore.lockEnabledFlow.first()
        assertFalse(lockEnabled)
    }

    @Test
    fun testDefaultLockDisabled() = runBlocking {
        val lockEnabled = dataStore.lockEnabledFlow.first()
        assertFalse(lockEnabled)
    }

    @Test
    fun testSetActive() = runBlocking {
        dataStore.setActive(true)
        var active = dataStore.activeFlow.first()
        assertTrue(active)
        
        dataStore.setActive(false)
        active = dataStore.activeFlow.first()
        assertFalse(active)
    }

    @Test
    fun testDefaultActiveIsFalse() = runBlocking {
        val active = dataStore.activeFlow.first()
        assertFalse(active)
    }

    @Test
    fun testSetSecurityQA() = runBlocking {
        val q1 = 0
        val q2 = 1
        val q3 = 2
        val a1 = "Answer 1"
        val a2 = "Answer 2"
        val a3 = "Answer 3"
        
        dataStore.setSecurityQA(q1, q2, q3, a1, a2, a3)
        
        val qa = dataStore.securityQAFlow.first()
        assertEquals(q1, qa.q1)
        assertEquals(q2, qa.q2)
        assertEquals(q3, qa.q3)
        assertEquals(a1, qa.a1)
        assertEquals(a2, qa.a2)
        assertEquals(a3, qa.a3)
    }

    @Test
    fun testSetSecurityQAWithIndices() = runBlocking {
        val q1 = 0
        val q2 = 1
        val q3 = 2
        val a1Index = 0
        val a2Index = 1
        val a3Index = 2
        val a1Text = "Answer 1"
        val a2Text = "Answer 2"
        val a3Text = "Answer 3"
        
        dataStore.setSecurityQAWithIndices(q1, q2, q3, a1Index, a2Index, a3Index, a1Text, a2Text, a3Text)
        
        val qa = dataStore.securityQAFlow.first()
        assertEquals(q1, qa.q1)
        assertEquals(q2, qa.q2)
        assertEquals(q3, qa.q3)
        assertEquals(a1Index, qa.a1Index)
        assertEquals(a2Index, qa.a2Index)
        assertEquals(a3Index, qa.a3Index)
        assertEquals(a1Text, qa.a1)
        assertEquals(a2Text, qa.a2)
        assertEquals(a3Text, qa.a3)
    }

    @Test
    fun testPinPersistence() = runBlocking {
        val testPin = "654321"
        dataStore.setPin(testPin)
        
        // Create new instance to verify persistence
        val newDataStore = KidsSecurityDataStore(context)
        val savedPin = newDataStore.pinFlow.first()
        assertEquals(testPin, savedPin)
    }

    @Test
    fun testLockEnabledPersistence() = runBlocking {
        dataStore.setLockEnabled(true)
        
        // Create new instance to verify persistence
        val newDataStore = KidsSecurityDataStore(context)
        val lockEnabled = newDataStore.lockEnabledFlow.first()
        assertTrue(lockEnabled)
    }

    @Test
    fun testActivePersistence() = runBlocking {
        dataStore.setActive(true)
        
        // Create new instance to verify persistence
        val newDataStore = KidsSecurityDataStore(context)
        val active = newDataStore.activeFlow.first()
        assertTrue(active)
    }

    @Test
    fun testSecurityQAPersistence() = runBlocking {
        dataStore.setSecurityQA(0, 1, 2, "Q1", "Q2", "Q3")
        
        // Create new instance to verify persistence
        val newDataStore = KidsSecurityDataStore(context)
        val qa = newDataStore.securityQAFlow.first()
        assertEquals(0, qa.q1)
        assertEquals(1, qa.q2)
        assertEquals(2, qa.q3)
    }

    @Test
    fun testPinTrimmingOnSave() = runBlocking {
        val pinWithSpaces = "  123456  "
        dataStore.setSecurityQA(0, 1, 2, pinWithSpaces, "a2", "a3")
        
        val qa = dataStore.securityQAFlow.first()
        assertEquals("123456", qa.a1) // Should be trimmed
    }

    @Test
    fun testMultiplePinChanges() = runBlocking {
        val pins = listOf("111111", "222222", "333333")
        
        pins.forEach { pin ->
            dataStore.setPin(pin)
            val savedPin = dataStore.pinFlow.first()
            assertEquals(pin, savedPin)
        }
    }

    @Test
    fun testSecurityQADefaultNull() = runBlocking {
        val qa = dataStore.securityQAFlow.first()
        assertNull(qa.q1)
        assertNull(qa.q2)
        assertNull(qa.q3)
        assertNull(qa.a1)
        assertNull(qa.a2)
        assertNull(qa.a3)
    }
}
