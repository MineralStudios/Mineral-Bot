package gg.mineral.bot.lwjgl.util.glu

import gg.mineral.bot.impl.config.BotGlobalConfig
import org.lwjgl.util.glu.GLU
import java.nio.FloatBuffer
import java.nio.IntBuffer

object GLU {
    @JvmStatic
    fun gluErrorString(error: Int): String {
        if (BotGlobalConfig.headless) return ""

        return GLU.gluErrorString(error)
    }

    @JvmStatic
    fun gluUnProject(
        winx: Float,
        winy: Float,
        winz: Float,
        modelMatrix: FloatBuffer,
        projMatrix: FloatBuffer,
        viewport: IntBuffer,
        obj_pos: FloatBuffer
    ) {
        if (BotGlobalConfig.headless) return

        GLU.gluUnProject(winx, winy, winz, modelMatrix, projMatrix, viewport, obj_pos)
    }
}