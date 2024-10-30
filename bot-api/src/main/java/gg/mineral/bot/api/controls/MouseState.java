package gg.mineral.bot.api.controls;

public interface MouseState {

    /**
     * Gets a mouse button by its type.
     *
     * @param type
     *             The type of the mouse button to retrieve.
     * @return The mouse button of the specified type, or null if not found.
     */
    MouseButton getButton(MouseButton.Type type);

    /**
     * Advances to the next log event.
     *
     * @return True if there is a next log event, false otherwise.
     */
    boolean next();

    /**
     * Gets the type of the current event button.
     *
     * @return The type of the current event button, or null if not found.
     */
    MouseButton.Type getEventButtonType();

    /**
     * Gets the button code of the current event button.
     *
     * @return The button code of the current event button, or -1 if not
     *         found.
     */
    int getEventButton();

    /**
     * Gets the delta wheel of the current event.
     * 
     * @return The delta wheel of the current event.
     */
    int getDWheel();

    /**
     * Sets the delta wheel of the current event.
     * 
     * @param dWheel
     *               The delta wheel of the current event.
     */
    void setDWheel(int dWheel);

    /**
     * @return The x position of the mouse.
     */
    int getX();

    /**
     * @return The y position of the mouse.
     */
    int getY();

    /**
     * Sets the x position of the mouse.
     * 
     * @param x
     *          The x position of the mouse.
     */
    void setX(int x);

    /**
     * Sets the y position of the mouse.
     * 
     * @param y
     *          The y position of the mouse.
     */
    void setY(int y);

    /**
     * @return The delta x position of the mouse.
     */
    int getDX();

    /**
     * @return The delta y position of the mouse.
     */
    int getDY();

    /**
     * Sets the delta x position of the mouse.
     * 
     * @param dx
     *           The delta x position of the mouse.
     */
    void setDX(int dx);

    /**
     * Sets the delta y position of the mouse.
     * 
     * @param dy
     *           The delta y position of the mouse.
     */
    void setDY(int dy);
}
