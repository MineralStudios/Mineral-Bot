package gg.mineral.bot.lwjgl.opengl

import gg.mineral.bot.impl.config.BotGlobalConfig
import org.lwjgl.opengl.ARBShaderObjects
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

object ARBShaderObjects {
    @JvmStatic
    fun glGetObjectParameteriARB(obj: Int, pname: Int): Int {
        if (BotGlobalConfig.headless) return 0

        return ARBShaderObjects.glGetObjectParameteriARB(obj, pname)
    }

    @JvmStatic
    fun glAttachObjectARB(containerObj: Int, obj: Int) {
        if (BotGlobalConfig.headless) return

        ARBShaderObjects.glAttachObjectARB(containerObj, obj)
    }

    @JvmStatic
    fun glDeleteObjectARB(obj: Int) {
        if (BotGlobalConfig.headless) return

        ARBShaderObjects.glDeleteObjectARB(obj)
    }

    @JvmStatic
    fun glCreateShaderObjectARB(shaderType: Int): Int {
        if (BotGlobalConfig.headless) return 0

        return ARBShaderObjects.glCreateShaderObjectARB(shaderType)
    }

    @JvmStatic
    fun glShaderSourceARB(shader: Int, string: ByteBuffer) {
        if (BotGlobalConfig.headless) return

        ARBShaderObjects.glShaderSourceARB(shader, string)
    }

    @JvmStatic
    fun glCompileShaderARB(shaderObj: Int) {
        if (BotGlobalConfig.headless) return

        ARBShaderObjects.glCompileShaderARB(shaderObj)
    }

    @JvmStatic
    fun glGetInfoLogARB(obj: Int, maxLength: Int): String {
        if (BotGlobalConfig.headless) return ""

        return ARBShaderObjects.glGetInfoLogARB(obj, maxLength)
    }

    @JvmStatic
    fun glUseProgramObjectARB(programObj: Int) {
        if (BotGlobalConfig.headless) return

        ARBShaderObjects.glUseProgramObjectARB(programObj)
    }

    @JvmStatic
    fun glCreateProgramObjectARB(): Int {
        if (BotGlobalConfig.headless) return 0

        return ARBShaderObjects.glCreateProgramObjectARB()
    }

    @JvmStatic
    fun glLinkProgramARB(programObj: Int) {
        if (BotGlobalConfig.headless) return

        ARBShaderObjects.glLinkProgramARB(programObj)
    }

    @JvmStatic
    fun glGetUniformLocationARB(programObj: Int, name: CharSequence): Int {
        if (BotGlobalConfig.headless) return 0

        return ARBShaderObjects.glGetUniformLocationARB(programObj, name)
    }

    @JvmStatic
    fun glUniform1ARB(location: Int, values: IntBuffer) {
        if (BotGlobalConfig.headless) return

        ARBShaderObjects.glUniform1ARB(location, values)
    }

    @JvmStatic
    fun glUniform1ARB(location: Int, values: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        ARBShaderObjects.glUniform1ARB(location, values)
    }

    @JvmStatic
    fun glUniform1iARB(location: Int, v0: Int) {
        if (BotGlobalConfig.headless) return

        ARBShaderObjects.glUniform1iARB(location, v0)
    }

    @JvmStatic
    fun glUniform2ARB(location: Int, values: IntBuffer) {
        if (BotGlobalConfig.headless) return

        ARBShaderObjects.glUniform2ARB(location, values)
    }

    @JvmStatic
    fun glUniform2ARB(location: Int, values: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        ARBShaderObjects.glUniform2ARB(location, values)
    }

    @JvmStatic
    fun glUniform3ARB(location: Int, values: IntBuffer) {
        if (BotGlobalConfig.headless) return

        ARBShaderObjects.glUniform3ARB(location, values)
    }

    @JvmStatic
    fun glUniform3ARB(location: Int, values: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        ARBShaderObjects.glUniform3ARB(location, values)
    }

    @JvmStatic
    fun glUniform4ARB(location: Int, values: IntBuffer) {
        if (BotGlobalConfig.headless) return

        ARBShaderObjects.glUniform4ARB(location, values)
    }

    @JvmStatic
    fun glUniform4ARB(location: Int, values: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        ARBShaderObjects.glUniform4ARB(location, values)
    }

    @JvmStatic
    fun glUniformMatrix2ARB(location: Int, transpose: Boolean, matrices: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        ARBShaderObjects.glUniformMatrix2ARB(location, transpose, matrices)
    }

    @JvmStatic
    fun glUniformMatrix3ARB(location: Int, transpose: Boolean, matrices: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        ARBShaderObjects.glUniformMatrix3ARB(location, transpose, matrices)
    }

    @JvmStatic
    fun glUniformMatrix4ARB(location: Int, transpose: Boolean, matrices: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        ARBShaderObjects.glUniformMatrix4ARB(location, transpose, matrices)
    }
}