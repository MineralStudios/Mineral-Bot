package gg.mineral.bot.api.controls

/**
 * Represents a keyboard with methods to interact with keys and manage events.
 */
interface Keyboard {
    /**
     * Gets a key by its type.
     *
     * @param type
     * The type of the key to retrieve.
     * @return The key of the specified type, or null if not found.
     */
    fun getKey(type: Key.Type): Key?

    /**
     * Presses a key for a specified duration.
     *
     * @param durationMillis
     * The duration in milliseconds the key is to be pressed.
     * @param type
     * The type of the key to press.
     */
    fun pressKey(durationMillis: Int, vararg types: Key.Type)

    /**
     * Presses a key indefinitely.
     *
     * @param type
     * The type of the key to press.
     */
    fun pressKey(vararg type: Key.Type) {
        pressKey(Int.MAX_VALUE, *type)
    }

    /**
     * Unpresses a key.
     *
     * @param type
     * The type of the key to unpress.
     * @param durationMillis
     * The duration in milliseconds the key is to be
     * unpressed.
     */
    fun unpressKey(durationMillis: Int, vararg types: Key.Type)

    /**
     * Unpresses a key indefinitely.
     *
     * @param type
     * The type of the key to unpress.
     */
    fun unpressKey(vararg type: Key.Type) {
        unpressKey(Int.MAX_VALUE, *type)
    }

    /**
     * Advances to the next log event.
     *
     * @return True if there is a next log event, false otherwise.
     */
    fun next(): Boolean

    /**
     * Gets the type of the current event key.
     *
     * @return The type of the current event key, or null if not found.
     */
    val eventKeyType: Key.Type?

    /**
     * Gets the key code of the current event key.
     *
     * @return The key code of the current event key, or -1 if not found.
     */
    val eventKey: Int

    /**
     * Checks if a key is currently held down.
     *
     * @param type
     * The type of the key to check.
     * @return True if the key is held down, false otherwise.
     */
    fun isKeyDown(type: Key.Type): Boolean

    /**
     * Checks if the event key state is currently active.
     *
     * @return True if the event key is currently active, false otherwise.
     */
    val eventKeyState: Boolean

    /**
     * Returns a snapshot list of all key state changes that have been recorded so far.
     *
     * This method does not modify the internal state of the Keyboard, so any pending log entries
     * (like [currentLog]) are left intact and will be processed by subsequent calls to [next].
     *
     * @return A list of all key state changes that have been recorded so far.
     */
    fun getKeyStateChanges(): List<Log>

    /**
     * Stops all keyboard actions.
     */
    fun stopAll()

    /**
     * Sets the state of the specified keys.
     *
     * @param types
     * The types of the keys to set the state of.
     */
    fun setState(vararg types: Key.Type)

    @JvmRecord
    data class Log(val type: Key.Type, val pressed: Boolean)
}