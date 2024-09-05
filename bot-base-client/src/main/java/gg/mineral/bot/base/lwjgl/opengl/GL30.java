package gg.mineral.bot.base.lwjgl.opengl;

import gg.mineral.bot.impl.config.BotGlobalConfig;

public class GL30 {

    public static void glBindFramebuffer(int target, int framebuffer) {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.GL30.glBindFramebuffer(target, framebuffer);
    }

    public static void glBindRenderbuffer(int target, int renderbuffer) {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.GL30.glBindRenderbuffer(target, renderbuffer);
    }

    public static void glDeleteRenderbuffers(int renderbuffer) {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.GL30.glDeleteRenderbuffers(renderbuffer);
    }

    public static void glDeleteFramebuffers(int framebuffer) {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.GL30.glDeleteFramebuffers(framebuffer);
    }

    public static int glGenFramebuffers() {
        if (BotGlobalConfig.isHeadless())
            return 0;

        return org.lwjgl.opengl.GL30.glGenFramebuffers();
    }

    public static int glGenRenderbuffers() {
        if (BotGlobalConfig.isHeadless())
            return 0;

        return org.lwjgl.opengl.GL30.glGenRenderbuffers();
    }

    public static void glRenderbufferStorage(int target, int internalformat, int width, int height) {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.GL30.glRenderbufferStorage(target, internalformat, width, height);
    }

    public static void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.GL30.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
    }

    public static int glCheckFramebufferStatus(int target) {
        if (BotGlobalConfig.isHeadless())
            return 36053;

        return org.lwjgl.opengl.GL30.glCheckFramebufferStatus(target);
    }

    public static void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.GL30.glFramebufferTexture2D(target, attachment, textarget, texture, level);
    }

}
