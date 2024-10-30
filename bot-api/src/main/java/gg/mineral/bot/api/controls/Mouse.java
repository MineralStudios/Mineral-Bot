package gg.mineral.bot.api.controls;

/**
 * Represents a mouse with methods to interact with buttons and manage events.
 */
public interface Mouse extends MouseState {

  /**
   * Presses a button for a specified duration.
   * 
   * @param durationMillis
   *                       The duration in milliseconds the button is to be
   *                       pressed.
   * @param type
   *                       The type of the button to press.
   */
  void pressButton(int durationMillis, MouseButton.Type... type);

  /**
   * Presses a button indefinitely.
   * 
   * @param type
   *             The type of the button to press.
   */
  default void pressButton(MouseButton.Type... type) {
    pressButton(Integer.MAX_VALUE, type);
  }

  /**
   * Unpresses a button.
   * 
   * @param type
   *                       The type of the button to unpress.
   * @param durationMillis
   *                       The duration in milliseconds the button is to be
   *                       unpressed.
   */
  void unpressButton(int durationMillis, MouseButton.Type... type);

  /**
   * Unpresses a button indefinitely.
   * 
   * @param type
   *             The type of the button to unpress.
   */
  default void unpressButton(MouseButton.Type... type) {
    unpressButton(Integer.MAX_VALUE, type);
  }

  /**
   * Gets the current mouse state.
   * 
   * @return the current mouse state
   */
  MouseState getState();

  /**
   * Sets the current mouse state.
   * 
   * @param state
   *              The state to set.
   */
  void setState(MouseState state);

  /**
   * Updates the mouse position to change the yaw by a specified amount.
   * 
   * @param dYaw
   */
  void changeYaw(float dYaw);

  /**
   * Updates the mouse position to change the pitch by a specified amount.
   * 
   * @param dPitch
   */
  void changePitch(float dPitch);

  /**
   * Stops all mouse actions.
   */
  void stopAll();
}
