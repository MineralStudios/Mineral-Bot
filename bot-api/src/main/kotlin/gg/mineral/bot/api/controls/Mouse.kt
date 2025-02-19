package gg.mineral.bot.api.controls

/**
 * Represents a mouse with methods to interact with buttons and manage events.
 */
interface Mouse {
    /**
     * Gets a mouse button by its type.
     *
     * @param type The type of the mouse button to retrieve.
     * @return The mouse button of the specified type, or null if not found.
     */
    fun getButton(type: MouseButton.Type): MouseButton?

    /**
     * Presses a button for a specified duration.
     *
     * @param durationMillis The duration in milliseconds the button is to be
     * pressed.
     * @param type           The type of the button to press.
     */
    fun pressButton(durationMillis: Int, vararg type: MouseButton.Type)

    /**
     * Presses a button indefinitely.
     *
     * @param type The type of the button to press.
     */
    fun pressButton(vararg type: MouseButton.Type) {
        pressButton(Int.MAX_VALUE, *type)
    }

    /**
     * Unpresses a button.
     *
     * @param type           The type of the button to unpress.
     * @param durationMillis The duration in milliseconds the button is to be
     * unpressed.
     */
    fun unpressButton(durationMillis: Int, vararg type: MouseButton.Type)

    /**
     * Unpresses a button indefinitely.
     *
     * @param type The type of the button to unpress.
     */
    fun unpressButton(vararg type: MouseButton.Type) {
        unpressButton(Int.MAX_VALUE, *type)
    }

    /**
     * Advances to the next log event.
     *
     * @return True if there is a next log event, false otherwise.
     */
    fun next(): Boolean

    /**
     * Gets the type of the current event button.
     *
     * @return The type of the current event button, or null if not found.
     */
    val eventButtonType: MouseButton.Type?

    /**
     * Gets the button code of the current event button.
     *
     * @return The button code of the current event button, or -1 if not
     * found.
     */
    val eventButton: Int

    /**
     * Gets the delta wheel of the current event.
     *
     * @return The delta wheel of the current event.
     */
    /**
     * Sets the delta wheel of the current event.
     *
     * @param dWheel The delta wheel of the current event.
     */
    var dWheel: Int

    /**
     * @return The x position of the mouse.
     */
    /**
     * Sets the x position of the mouse.
     *
     * @param x The x position of the mouse.
     */
    var x: Int

    /**
     * @return The y position of the mouse.
     */
    /**
     * Sets the y position of the mouse.
     *
     * @param y The y position of the mouse.
     */
    var y: Int

    /**
     * Sets the position of the mouse.
     *
     * @param x The x position of the mouse.
     * @param y The y position of the mouse.
     */
    fun setCursorPosition(x: Int, y: Int)

    /**
     * Updates the mouse position to change the yaw by a specified amount.
     *
     * @param dYaw
     */
    fun changeYaw(dYaw: Float)

    /**
     * Updates the mouse position to change the pitch by a specified amount.
     *
     * @param dPitch
     */
    fun changePitch(dPitch: Float)

    /**
     * @return The delta x position of the mouse.
     */
    /**
     * Sets the delta x position of the mouse.
     *
     * @param dx The delta x position of the mouse.
     */
    var dX: Int

    /**
     * @return The delta y position of the mouse.
     */
    /**
     * Sets the delta y position of the mouse.
     *
     * @param dy The delta y position of the mouse.
     */
    var dY: Int

    /**
     * Stops all mouse actions.
     */
    fun stopAll()

    /**
     * @return True if the mouse is grabbed, false otherwise.
     */
    /**
     * Sets if the mouse is grabbed.
     *
     * @param grabbed
     */
    var isGrabbed: Boolean
}