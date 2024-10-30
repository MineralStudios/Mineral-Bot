package gg.mineral.bot.api.controls;

public interface KeyboardState {
    /**
     * Gets a key by its type.
     *
     * @param type
     *             The type of the key to retrieve.
     * @return The key of the specified type, or null if not found.
     */
    Key getKey(Key.Type type);

    /**
     * Advances to the next log event.
     *
     * @return True if there is a next log event, false otherwise.
     */
    boolean next();

    /**
     * Gets the type of the current event key.
     *
     * @return The type of the current event key, or null if not found.
     */
    Key.Type getEventKeyType();

    /**
     * Gets the key code of the current event key.
     *
     * @return The key code of the current event key, or -1 if not found.
     */
    int getEventKey();

    /**
     * Checks if a key is currently held down.
     *
     * @param type
     *             The type of the key to check.
     * @return True if the key is held down, false otherwise.
     */
    boolean isKeyDown(Key.Type type);

    /**
     * Checks if the event key state is currently active.
     *
     * @return True if the event key is currently active, false otherwise.
     */
    boolean getEventKeyState();
}
