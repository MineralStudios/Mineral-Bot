package gg.mineral.bot.lwjgl.opengl

import gg.mineral.bot.impl.config.BotGlobalConfig

import org.lwjgl.opengl.ARBMultitexture

object ARBMultitexture {
    @JvmStatic
    fun glActiveTextureARB(texture: Int) {
        if (BotGlobalConfig.headless) return

        ARBMultitexture.glActiveTextureARB(texture)
    }

    @JvmStatic
    fun glClientActiveTextureARB(texture: Int) {
        if (BotGlobalConfig.headless) return

        ARBMultitexture.glClientActiveTextureARB(texture)
    }

    @JvmStatic
    fun glMultiTexCoord2fARB(target: Int, s: Float, t: Float) {
        if (BotGlobalConfig.headless) return

        ARBMultitexture.glMultiTexCoord2fARB(target, s, t)
    }
}