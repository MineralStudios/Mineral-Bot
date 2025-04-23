package gg.mineral.bot.lwjgl.opengl

import gg.mineral.bot.impl.config.BotGlobalConfig

import org.lwjgl.opengl.EXTBlendFuncSeparate

object EXTBlendFuncSeparate {
    @JvmStatic
    fun glBlendFuncSeparateEXT(sfactorRGB: Int, dfactorRGB: Int, sfactorAlpha: Int, dfactorAlpha: Int) {
        if (BotGlobalConfig.headless) return

        EXTBlendFuncSeparate.glBlendFuncSeparateEXT(
            sfactorRGB,
            dfactorRGB,
            sfactorAlpha,
            dfactorAlpha
        )
    }
}