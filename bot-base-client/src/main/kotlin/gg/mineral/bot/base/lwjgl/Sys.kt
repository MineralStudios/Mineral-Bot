package gg.mineral.bot.base.lwjgl

import gg.mineral.bot.impl.config.BotGlobalConfig
import org.lwjgl.Sys

object Sys {
    @JvmStatic
    fun openURL(string: String) {
        if (BotGlobalConfig.headless) return

        Sys.openURL(string)
    }

    @JvmStatic
    val version: String
        get() {
            if (BotGlobalConfig.headless) return "Mineral"

            return Sys.getVersion()
        }

    @JvmStatic
    val time: Long
        get() {
            if (BotGlobalConfig.headless) return System.nanoTime() / 1000000

            return Sys.getTime()
        }

    @JvmStatic
    val timerResolution: Long
        get() {
            if (BotGlobalConfig.headless) return 1000

            return Sys.getTimerResolution()
        }
}
