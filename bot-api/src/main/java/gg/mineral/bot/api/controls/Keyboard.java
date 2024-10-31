package gg.mineral.bot.api.controls;

/**
 * Represents a keyboard with methods to interact with keys and manage events.
 */
public interface Keyboard {

  /**
   * Gets a key by its type.
   *
   * @param type
   *             The type of the key to retrieve.
   * @return The key of the specified type, or null if not found.
   */
  Key getKey(Key.Type type);

  /**
   * Presses a key for a specified duration.
   * 
   * @param durationMillis
   *                       The duration in milliseconds the key is to be pressed.
   * @param type
   *                       The type of the key to press.
   */
  void pressKey(int durationMillis, Key.Type... type);

  /**
   * Presses a key indefinitely.
   * 
   * @param type
   *             The type of the key to press.
   */
  default void pressKey(Key.Type... type) {
    pressKey(Integer.MAX_VALUE, type);
  }

  /**
   * Unpresses a key.
   * 
   * @param type
   *                       The type of the key to unpress.
   * @param durationMillis
   *                       The duration in milliseconds the key is to be
   *                       unpressed.
   */
  void unpressKey(int durationMillis, Key.Type... type);

  /**
   * Unpresses a key indefinitely.
   * 
   * @param type
   *             The type of the key to unpress.
   */
  default void unpressKey(Key.Type... type) {
    unpressKey(Integer.MAX_VALUE, type);
  }

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

  /**
   * Stops all keyboard actions.
   */
  void stopAll();
}