package gg.mineral.bot.api.controls;

/**
 * Represents a keyboard with methods to interact with keys and manage events.
 */
public interface Keyboard extends KeyboardState {

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
   * Gets the current keyboard state.
   * 
   * @return The current keyboard state.
   */
  KeyboardState getState();

  /**
   * Sets the current keyboard state.
   * 
   * @param state
   *              The new keyboard state.
   */
  void setState(KeyboardState state);

  /**
   * Stops all keyboard actions.
   */
  void stopAll();
}
