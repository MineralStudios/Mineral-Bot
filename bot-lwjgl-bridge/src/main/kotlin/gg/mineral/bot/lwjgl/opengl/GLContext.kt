package gg.mineral.bot.lwjgl.opengl

import gg.mineral.bot.impl.config.BotGlobalConfig
import org.lwjgl.opengl.GLContext

object GLContext {
    @JvmStatic
    val capabilities: ContextCapabilities = ContextCapabilities()
        get() {
            if (BotGlobalConfig.headless) return field

            return ContextCapabilities(GLContext.getCapabilities())
        }
}
