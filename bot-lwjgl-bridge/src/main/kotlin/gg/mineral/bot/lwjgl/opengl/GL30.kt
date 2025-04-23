package gg.mineral.bot.lwjgl.opengl

import gg.mineral.bot.impl.config.BotGlobalConfig
import org.lwjgl.opengl.GL30

object GL30 {
    @JvmStatic
    fun glBindFramebuffer(target: Int, framebuffer: Int) {
        if (BotGlobalConfig.headless) return

        GL30.glBindFramebuffer(target, framebuffer)
    }

    @JvmStatic
    fun glBindRenderbuffer(target: Int, renderbuffer: Int) {
        if (BotGlobalConfig.headless) return

        GL30.glBindRenderbuffer(target, renderbuffer)
    }

    @JvmStatic
    fun glDeleteRenderbuffers(renderbuffer: Int) {
        if (BotGlobalConfig.headless) return

        GL30.glDeleteRenderbuffers(renderbuffer)
    }

    @JvmStatic
    fun glDeleteFramebuffers(framebuffer: Int) {
        if (BotGlobalConfig.headless) return

        GL30.glDeleteFramebuffers(framebuffer)
    }

    @JvmStatic
    fun glGenFramebuffers(): Int {
        if (BotGlobalConfig.headless) return 0

        return GL30.glGenFramebuffers()
    }

    @JvmStatic
    fun glGenRenderbuffers(): Int {
        if (BotGlobalConfig.headless) return 0

        return GL30.glGenRenderbuffers()
    }

    @JvmStatic
    fun glRenderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int) {
        if (BotGlobalConfig.headless) return

        GL30.glRenderbufferStorage(target, internalformat, width, height)
    }

    @JvmStatic
    fun glFramebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: Int) {
        if (BotGlobalConfig.headless) return

        GL30.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer)
    }

    @JvmStatic
    fun glCheckFramebufferStatus(target: Int): Int {
        if (BotGlobalConfig.headless) return 36053

        return GL30.glCheckFramebufferStatus(target)
    }

    @JvmStatic
    fun glFramebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: Int, level: Int) {
        if (BotGlobalConfig.headless) return

        GL30.glFramebufferTexture2D(target, attachment, textarget, texture, level)
    }
}
