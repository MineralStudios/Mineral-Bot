package gg.mineral.bot.lwjgl.util.glu

import gg.mineral.bot.impl.config.BotGlobalConfig
import org.lwjgl.util.glu.Project

object Project {
    @JvmStatic
    fun gluPerspective(
        fovy: Float,
        aspect: Float,
        zNear: Float,
        zFar: Float
    ) {
        if (BotGlobalConfig.headless) return

        Project.gluPerspective(fovy, aspect, zNear, zFar)
    }
}
