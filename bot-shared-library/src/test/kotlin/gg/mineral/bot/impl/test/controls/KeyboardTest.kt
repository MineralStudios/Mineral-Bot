package gg.mineral.bot.impl.test.controls

import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.event.EventHandler
import gg.mineral.bot.impl.controls.Keyboard
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KeyboardTest {
    // A simple EventHandler that does not cancel any events
    private val handler = object : EventHandler {
        override fun <T : Event> callEvent(event: T): Boolean = false
    }
    private val keyboard = Keyboard(handler)

    @Test
    fun testPressGeneratesEvent() {
        // Press KEY_W indefinitely
        keyboard.pressKey(Key.Type.KEY_W)
        // First next() should yield a press event
        assertTrue(keyboard.next(), "Expected next() to have an event")
        assertEquals(Key.Type.KEY_W, keyboard.eventKeyType, "Event key type should be KEY_W")
        assertTrue(keyboard.eventKeyState, "Event state should be pressed=true")
        // No more events
        assertFalse(keyboard.next(), "No further events expected")
    }

    @Test
    fun testUnpressGeneratesEvent() {
        // Press then unpress KEY_E
        keyboard.pressKey(Key.Type.KEY_E)
        keyboard.unpressKey(Key.Type.KEY_E)

        // Should see press event first
        assertTrue(keyboard.next())
        assertEquals(Key.Type.KEY_E, keyboard.eventKeyType)
        assertTrue(keyboard.eventKeyState)
        // Then unpress event
        assertTrue(keyboard.next())
        assertEquals(Key.Type.KEY_E, keyboard.eventKeyType)
        assertFalse(keyboard.eventKeyState)
        // No more events
        assertFalse(keyboard.next())
    }

    @Test
    fun testGetKeyStateChanges() {
        // Press several keys
        keyboard.pressKey(Key.Type.KEY_A)
        keyboard.pressKey(Key.Type.KEY_S)
        // Retrieve state changes
        val changes = keyboard.getKeyStateChanges()
        // Should contain both press logs
        val types = changes.map { it.type }
        assertTrue(types.containsAll(listOf(Key.Type.KEY_A, Key.Type.KEY_S)))
    }

    @Test
    fun testStopAllClearsKeys() {
        // Press multiple keys
        keyboard.pressKey(Key.Type.KEY_D, Key.Type.KEY_F)
        // Stop all
        keyboard.stopAll()
        // Both should be not pressed
        assertFalse(keyboard.isKeyDown(Key.Type.KEY_D))
        assertFalse(keyboard.isKeyDown(Key.Type.KEY_F))
        // No events left
        assertFalse(keyboard.next())
    }

    @Test
    fun testPressWithDurationAutoRelease() {
        keyboard.pressKey(10, Key.Type.KEY_Z)
        // press event
        assertTrue(keyboard.next())
        assertEquals(Key.Type.KEY_Z, keyboard.eventKeyType)
        assertTrue(keyboard.eventKeyState)
        // simulate time passage
        keyboard.onGameLoop(System.nanoTime() / 1_000_000 + 20)
        // auto-release event
        assertTrue(keyboard.next())
        assertEquals(Key.Type.KEY_Z, keyboard.eventKeyType)
        assertFalse(keyboard.eventKeyState)
    }

    @Test
    fun testSetState() {
        keyboard.setState(Key.Type.KEY_X, Key.Type.KEY_Y)
        assertTrue(keyboard.isKeyDown(Key.Type.KEY_X))
        assertTrue(keyboard.isKeyDown(Key.Type.KEY_Y))
        val types = keyboard.getKeyStateChanges().map { it.type }
        assertTrue(types.containsAll(listOf(Key.Type.KEY_X, Key.Type.KEY_Y)))
    }
}
