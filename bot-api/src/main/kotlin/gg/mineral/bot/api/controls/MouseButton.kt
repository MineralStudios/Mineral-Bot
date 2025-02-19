package gg.mineral.bot.api.controls

/**
 * Represents a mouse button with actions and state checking methods.
 */
interface MouseButton {
    /**
     * Checks if the mouse button is currently held.
     *
     * @return True if the mouse button is held, false otherwise.
     */
    /**
     * Presses the mouse button.
     *
     * @param pressed
     * True if the mouse button is pressed, false otherwise.
     */
    val isPressed: Boolean

    /**
     * Gets the type of the mouse button.
     *
     * @return The type of the mouse button as a Type enum.
     */
    val type: Type

    /**
     * Enum representing the possible mouse button types.
     */
    enum class Type(val keyCode: Int) {
        UNKNOWN(-1), LEFT_CLICK(0), RIGHT_CLICK(1), MIDDLE_CLICK(2);

        companion object {
            @JvmStatic
            fun fromKeyCode(i: Int): Type {
                for (type in entries) if (type.keyCode == i) return type

                return UNKNOWN
            }
        }
    }
}
