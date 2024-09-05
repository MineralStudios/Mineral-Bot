package gg.mineral.bot.api.controls;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a mouse button with actions and state checking methods.
 */
public interface MouseButton {

  /**
   * Presses the mouse button.
   * 
   * @param pressed
   *          True if the mouse button is pressed, false otherwise.
   */
  void setPressed(boolean pressed);

  /**
   * Checks if the mouse button is currently held.
   *
   * @return True if the mouse button is held, false otherwise.
   */
  boolean isPressed();

  /**
   * Gets the type of the mouse button.
   *
   * @return The type of the mouse button as a Type enum.
   */
  Type getType();

  /**
   * Enum representing the possible mouse button types.
   */
  @RequiredArgsConstructor
  @Getter
  static enum Type {
    UNKNOWN(-1), LEFT_CLICK(0), RIGHT_CLICK(1), MIDDLE_CLICK(2);

    final int keyCode;

    public static Type fromKeyCode(int i) {
      for (Type type : values())
        if (type.keyCode == i)
          return type;

      return UNKNOWN;
    }
  }
}
