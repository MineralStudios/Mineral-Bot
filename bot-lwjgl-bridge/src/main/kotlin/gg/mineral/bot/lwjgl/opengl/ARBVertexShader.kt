package gg.mineral.bot.lwjgl.opengl

import gg.mineral.bot.impl.config.BotGlobalConfig

import org.lwjgl.opengl.ARBVertexShader

object ARBVertexShader {
    @JvmStatic
    fun glGetAttribLocationARB(programObj: Int, name: CharSequence): Int {
        if (BotGlobalConfig.headless) return 0

        return ARBVertexShader.glGetAttribLocationARB(programObj, name)
    }
}