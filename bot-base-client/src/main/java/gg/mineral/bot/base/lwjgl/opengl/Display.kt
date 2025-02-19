package gg.mineral.bot.base.lwjgl.opengl;

import java.nio.ByteBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.PixelFormat;

import gg.mineral.bot.impl.config.BotGlobalConfig;

public class Display {

    private static final DisplayMode DEFAULT_DISPLAY_MODE = new DisplayMode(1280, 720);

    public static boolean isActive() {
        if (BotGlobalConfig.isHeadless())
            return true;

        return org.lwjgl.opengl.Display.isActive();
    }

    public static void setFullscreen(boolean fullscreen) throws LWJGLException {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.Display.setFullscreen(fullscreen);
    }

    public static DisplayMode getDisplayMode() {
        if (BotGlobalConfig.isHeadless())
            return DEFAULT_DISPLAY_MODE;

        return org.lwjgl.opengl.Display.getDisplayMode();
    }

    public static void setDisplayMode(DisplayMode displayMode) throws LWJGLException {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.Display.setDisplayMode(displayMode);
    }

    public static void setResizable(boolean resizable) {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.Display.setResizable(resizable);
    }

    public static void setTitle(String title) {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.Display.setTitle(title);
    }

    public static void setIcon(ByteBuffer[] byteBuffers) {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.Display.setIcon(byteBuffers);
    }

    public static void create(PixelFormat withDepthBits) throws LWJGLException {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.Display.create(withDepthBits);
    }

    public static void create() throws LWJGLException {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.Display.create();
    }

    public static void setVSyncEnabled(boolean enableVsync) {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.Display.setVSyncEnabled(enableVsync);
    }

    public static DisplayMode[] getAvailableDisplayModes() throws LWJGLException {
        if (BotGlobalConfig.isHeadless())
            return new DisplayMode[] { DEFAULT_DISPLAY_MODE };

        return org.lwjgl.opengl.Display.getAvailableDisplayModes();
    }

    public static DisplayMode getDesktopDisplayMode() {
        if (BotGlobalConfig.isHeadless())
            return DEFAULT_DISPLAY_MODE;

        return org.lwjgl.opengl.Display.getDesktopDisplayMode();
    }

    public static void destroy() {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.Display.destroy();
    }

    public static boolean isCreated() {
        if (BotGlobalConfig.isHeadless())
            return true;

        return org.lwjgl.opengl.Display.isCreated();
    }

    public static boolean isCloseRequested() {
        if (BotGlobalConfig.isHeadless())
            return false;

        return org.lwjgl.opengl.Display.isCloseRequested();
    }

    public static void sync(int limitFramerate) {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.Display.sync(limitFramerate);
    }

    public static void update() {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.Display.update();
    }

    public static boolean wasResized() {
        if (BotGlobalConfig.isHeadless())
            return false;

        return org.lwjgl.opengl.Display.wasResized();
    }

    public static int getWidth() {
        if (BotGlobalConfig.isHeadless())
            return DEFAULT_DISPLAY_MODE.getWidth();

        return org.lwjgl.opengl.Display.getWidth();
    }

    public static int getHeight() {
        if (BotGlobalConfig.isHeadless())
            return DEFAULT_DISPLAY_MODE.getHeight();

        return org.lwjgl.opengl.Display.getHeight();
    }

    public static Drawable getDrawable() {
        if (BotGlobalConfig.isHeadless())
            return new Drawable() {

                @Override
                public boolean isCurrent() throws LWJGLException {
                    return false;
                }

                @Override
                public void makeCurrent() throws LWJGLException {
                }

                @Override
                public void releaseContext() throws LWJGLException {
                }

                @Override
                public void destroy() {
                }

                @Override
                public void setCLSharingProperties(PointerBuffer properties) throws LWJGLException {
                }

            };

        return org.lwjgl.opengl.Display.getDrawable();
    }

}
