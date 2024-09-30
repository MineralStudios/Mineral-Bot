package net.minecraft.util;

import gg.mineral.bot.base.lwjgl.opengl.Display;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;

@RequiredArgsConstructor
public class MouseHelper {
    /** Mouse delta X this frame */
    public int deltaX;

    /** Mouse delta Y this frame */
    public int deltaY;
    private final Minecraft mc;

    /**
     * Grabs the mouse cursor it doesn't move and isn't seen.
     */
    public void grabMouseCursor() {
        this.mc.getMouse().setGrabbed(true);
        this.deltaX = 0;
        this.deltaY = 0;
    }

    /**
     * Ungrabs the mouse cursor so it can be moved and set it to the center of the
     * screen
     */
    public void ungrabMouseCursor() {
        this.mc.getMouse().setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
        this.mc.getMouse().setGrabbed(false);
    }

    public void mouseXYChange() {
        this.deltaX = this.mc.getMouse().getDX();
        this.deltaY = this.mc.getMouse().getDY();
    }
}
