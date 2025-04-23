package gg.mineral.bot.lwjgl.opengl

import gg.mineral.bot.impl.config.BotGlobalConfig

import org.lwjgl.opengl.EXTFramebufferObject

object EXTFramebufferObject {
    @JvmStatic
    fun glBindFramebufferEXT(target: Int, framebuffer: Int) {
        if (BotGlobalConfig.headless) return

        EXTFramebufferObject.glBindFramebufferEXT(target, framebuffer)
    }

    @JvmStatic
    fun glBindRenderbufferEXT(target: Int, renderbuffer: Int) {
        if (BotGlobalConfig.headless) return

        EXTFramebufferObject.glBindRenderbufferEXT(target, renderbuffer)
    }

    @JvmStatic
    fun glDeleteRenderbuffersEXT(renderbuffer: Int) {
        if (BotGlobalConfig.headless) return

        EXTFramebufferObject.glDeleteRenderbuffersEXT(renderbuffer)
    }

    @JvmStatic
    fun glDeleteFramebuffersEXT(framebuffer: Int) {
        if (BotGlobalConfig.headless) return

        EXTFramebufferObject.glDeleteFramebuffersEXT(framebuffer)
    }

    @JvmStatic
    fun glGenFramebuffersEXT(): Int {
        if (BotGlobalConfig.headless) return 0

        return EXTFramebufferObject.glGenFramebuffersEXT()
    }

    @JvmStatic
    fun glGenRenderbuffersEXT(): Int {
        if (BotGlobalConfig.headless) return 0

        return EXTFramebufferObject.glGenRenderbuffersEXT()
    }

    @JvmStatic
    fun glRenderbufferStorageEXT(target: Int, internalFormat: Int, width: Int, height: Int) {
        if (BotGlobalConfig.headless) return

        EXTFramebufferObject.glRenderbufferStorageEXT(target, internalFormat, width, height)
    }

    @JvmStatic
    fun glFramebufferRenderbufferEXT(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: Int) {
        if (BotGlobalConfig.headless) return

        EXTFramebufferObject.glFramebufferRenderbufferEXT(target, attachment, renderbuffertarget, renderbuffer)
    }

    @JvmStatic
    fun glCheckFramebufferStatusEXT(target: Int): Int {
        if (BotGlobalConfig.headless) return 0

        return EXTFramebufferObject.glCheckFramebufferStatusEXT(target)
    }

    @JvmStatic
    fun glFramebufferTexture2DEXT(target: Int, attachment: Int, textarget: Int, texture: Int, level: Int) {
        if (BotGlobalConfig.headless) return

        EXTFramebufferObject.glFramebufferTexture2DEXT(target, attachment, textarget, texture, level)
    }
}