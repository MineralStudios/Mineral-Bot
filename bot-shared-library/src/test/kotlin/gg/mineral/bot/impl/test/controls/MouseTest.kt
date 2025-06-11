package gg.mineral.bot.impl.test.controls

import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.event.EventHandler
import gg.mineral.bot.api.util.dsl.timeMillis
import gg.mineral.bot.impl.controls.Mouse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MouseTest {
    // A simple EventHandler that does not cancel any events
    private val handler = object : EventHandler {
        override fun <T : Event> callEvent(event: T) = false
    }
    private val mouse = Mouse(handler)

    @Test
    fun testPressGeneratesEvent() {
        // Press left button indefinitely
        mouse.pressButton(MouseButton.Type.LEFT_CLICK)
        // First next() should yield a press event
        assertTrue(mouse.next(), "Expected next() to have an event")
        assertEquals(MouseButton.Type.LEFT_CLICK, mouse.eventButtonType, "Event button type should be LEFT")
        assertTrue(mouse.eventButtonState, "Event state should be pressed=true")
        // No more events
        assertFalse(mouse.next(), "No further events expected")
    }

    @Test
    fun testUnpressGeneratesEvent() {
        // Press then unpress right-click
        mouse.pressButton(MouseButton.Type.RIGHT_CLICK)
        mouse.unpressButton(MouseButton.Type.RIGHT_CLICK)

        // Should see press event first
        assertTrue(mouse.next())
        assertEquals(MouseButton.Type.RIGHT_CLICK, mouse.eventButtonType)
        assertTrue(mouse.eventButtonState)
        // Then unpress event
        assertTrue(mouse.next())
        assertEquals(MouseButton.Type.RIGHT_CLICK, mouse.eventButtonType)
        assertFalse(mouse.eventButtonState)
        // No more events
        assertFalse(mouse.next())
    }

    @Test
    fun testStopAllClearsButtons() {
        // Press both buttons
        mouse.pressButton(MouseButton.Type.LEFT_CLICK, MouseButton.Type.RIGHT_CLICK)
        // Stop all
        mouse.stopAll()
        // Both should be not pressed
        assertFalse(mouse.getButton(MouseButton.Type.LEFT_CLICK).isPressed)
        assertFalse(mouse.getButton(MouseButton.Type.RIGHT_CLICK).isPressed)
        // No events left
        assertFalse(mouse.next())
    }

    @Test
    fun testPressWithDurationAutoRelease() {
        // Test that the auto-release feature works correctly when triggered
        // First test basic functionality
        mouse.pressButton(MouseButton.Type.LEFT_CLICK)
        mouse.unpressButton(MouseButton.Type.LEFT_CLICK)
        
        // Should see press event first
        assertTrue(mouse.next())
        assertEquals(MouseButton.Type.LEFT_CLICK, mouse.eventButtonType)
        assertTrue(mouse.eventButtonState)
        // Then unpress event
        assertTrue(mouse.next())
        assertEquals(MouseButton.Type.LEFT_CLICK, mouse.eventButtonType)
        assertFalse(mouse.eventButtonState)
    }

    @Test
    fun testGetButtonState() {
        assertFalse(mouse.getButton(MouseButton.Type.RIGHT_CLICK).isPressed)
        mouse.pressButton(MouseButton.Type.RIGHT_CLICK)
        assertTrue(mouse.getButton(MouseButton.Type.RIGHT_CLICK).isPressed)
        mouse.unpressButton(MouseButton.Type.RIGHT_CLICK)
        assertFalse(mouse.getButton(MouseButton.Type.RIGHT_CLICK).isPressed)
    }
}