package gg.mineral.bot.lwjgl.opengl

import gg.mineral.bot.impl.config.BotGlobalConfig

import org.lwjgl.opengl.ARBFramebufferObject

object ARBFramebufferObject {
    @JvmStatic
    fun glBindFramebuffer(target: Int, framebuffer: Int) {
        if (BotGlobalConfig.headless) return

        ARBFramebufferObject.glBindFramebuffer(target, framebuffer)
    }

    @JvmStatic
    fun glBindRenderbuffer(target: Int, renderbuffer: Int) {
        if (BotGlobalConfig.headless) return

        ARBFramebufferObject.glBindRenderbuffer(target, renderbuffer)
    }

    @JvmStatic
    fun glDeleteRenderbuffers(renderbuffer: Int) {
        if (BotGlobalConfig.headless) return

        ARBFramebufferObject.glDeleteRenderbuffers(renderbuffer)
    }

    @JvmStatic
    fun glDeleteFramebuffers(framebuffer: Int) {
        if (BotGlobalConfig.headless) return

        ARBFramebufferObject.glDeleteFramebuffers(framebuffer)
    }

    @JvmStatic
    fun glGenFramebuffers(): Int {
        if (BotGlobalConfig.headless) return 0

        return ARBFramebufferObject.glGenFramebuffers()
    }

    @JvmStatic
    fun glGenRenderbuffers(): Int {
        if (BotGlobalConfig.headless) return 0

        return ARBFramebufferObject.glGenRenderbuffers()
    }

    @JvmStatic
    fun glRenderbufferStorage(target: Int, internalFormat: Int, width: Int, height: Int) {
        if (BotGlobalConfig.headless) return

        ARBFramebufferObject.glRenderbufferStorage(target, internalFormat, width, height)
    }

    @JvmStatic
    fun glFramebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: Int) {
        if (BotGlobalConfig.headless) return

        ARBFramebufferObject.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer)
    }

    @JvmStatic
    fun glCheckFramebufferStatus(target: Int): Int {
        if (BotGlobalConfig.headless) return 0

        return ARBFramebufferObject.glCheckFramebufferStatus(target)
    }

    @JvmStatic
    fun glFramebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: Int, level: Int) {
        if (BotGlobalConfig.headless) return

        ARBFramebufferObject.glFramebufferTexture2D(target, attachment, textarget, texture, level)
    }
}