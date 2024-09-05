package gg.mineral.bot.base.lwjgl.opengl;

import gg.mineral.bot.impl.config.BotGlobalConfig;

public class GLContext {

    private static final ContextCapabilities capabilities = new ContextCapabilities();

    public static ContextCapabilities getCapabilities() {
        if (BotGlobalConfig.isHeadless())
            return capabilities;

        return new ContextCapabilities(org.lwjgl.opengl.GLContext.getCapabilities());
    }

}
