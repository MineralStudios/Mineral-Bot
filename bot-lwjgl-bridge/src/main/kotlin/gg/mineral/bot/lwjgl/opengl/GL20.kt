package gg.mineral.bot.lwjgl.opengl

import gg.mineral.bot.impl.config.BotGlobalConfig

import org.lwjgl.opengl.GL20
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

object GL20 {
    @JvmStatic
    fun glGetProgrami(program: Int, pname: Int): Int {
        if (BotGlobalConfig.headless) return 0

        return GL20.glGetProgrami(program, pname)
    }

    @JvmStatic
    fun glAttachShader(program: Int, shader: Int) {
        if (BotGlobalConfig.headless) return

        GL20.glAttachShader(program, shader)
    }

    @JvmStatic
    fun glDeleteShader(shader: Int) {
        if (BotGlobalConfig.headless) return

        GL20.glDeleteShader(shader)
    }

    @JvmStatic
    fun glCreateShader(type: Int): Int {
        if (BotGlobalConfig.headless) return 0

        return GL20.glCreateShader(type)
    }

    @JvmStatic
    fun glShaderSource(shader: Int, string: ByteBuffer) {
        if (BotGlobalConfig.headless) return

        GL20.glShaderSource(shader, string)
    }

    @JvmStatic
    fun glCompileShader(shader: Int) {
        if (BotGlobalConfig.headless) return

        GL20.glCompileShader(shader)
    }

    @JvmStatic
    fun glGetShaderi(shader: Int, pname: Int): Int {
        if (BotGlobalConfig.headless) return 0

        return GL20.glGetShaderi(shader, pname)
    }

    @JvmStatic
    fun glGetShaderInfoLog(shader: Int, maxLength: Int): String {
        if (BotGlobalConfig.headless) return ""

        return GL20.glGetShaderInfoLog(shader, maxLength)
    }

    @JvmStatic
    fun glGetProgramInfoLog(program: Int, maxLength: Int): String {
        if (BotGlobalConfig.headless) return ""

        return GL20.glGetProgramInfoLog(program, maxLength)
    }

    @JvmStatic
    fun glUseProgram(program: Int) {
        if (BotGlobalConfig.headless) return

        GL20.glUseProgram(program)
    }

    @JvmStatic
    fun glCreateProgram(): Int {
        if (BotGlobalConfig.headless) return 0

        return GL20.glCreateProgram()
    }

    @JvmStatic
    fun glDeleteProgram(program: Int) {
        if (BotGlobalConfig.headless) return

        GL20.glDeleteProgram(program)
    }

    @JvmStatic
    fun glLinkProgram(program: Int) {
        if (BotGlobalConfig.headless) return

        GL20.glLinkProgram(program)
    }

    @JvmStatic
    fun glGetUniformLocation(program: Int, name: CharSequence): Int {
        if (BotGlobalConfig.headless) return 0

        return GL20.glGetUniformLocation(program, name)
    }

    @JvmStatic
    fun glUniform1(location: Int, values: IntBuffer) {
        if (BotGlobalConfig.headless) return

        GL20.glUniform1(location, values)
    }

    @JvmStatic
    fun glUniform1(location: Int, values: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        GL20.glUniform1(location, values)
    }

    @JvmStatic
    fun glUniform1i(location: Int, v0: Int) {
        if (BotGlobalConfig.headless) return

        GL20.glUniform1i(location, v0)
    }

    @JvmStatic
    fun glUniform2(location: Int, values: IntBuffer) {
        if (BotGlobalConfig.headless) return

        GL20.glUniform2(location, values)
    }

    @JvmStatic
    fun glUniform2(location: Int, values: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        GL20.glUniform2(location, values)
    }

    @JvmStatic
    fun glUniform3(location: Int, values: IntBuffer) {
        if (BotGlobalConfig.headless) return

        GL20.glUniform3(location, values)
    }

    @JvmStatic
    fun glUniform3(location: Int, values: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        GL20.glUniform3(location, values)
    }

    @JvmStatic
    fun glUniform4(location: Int, values: IntBuffer) {
        if (BotGlobalConfig.headless) return

        GL20.glUniform4(location, values)
    }

    @JvmStatic
    fun glUniform4(location: Int, values: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        GL20.glUniform4(location, values)
    }

    @JvmStatic
    fun glUniformMatrix2(location: Int, transpose: Boolean, matrices: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        GL20.glUniformMatrix2(location, transpose, matrices)
    }

    @JvmStatic
    fun glUniformMatrix3(location: Int, transpose: Boolean, matrices: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        GL20.glUniformMatrix3(location, transpose, matrices)
    }

    @JvmStatic
    fun glUniformMatrix4(location: Int, transpose: Boolean, matrices: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        GL20.glUniformMatrix4(location, transpose, matrices)
    }

    @JvmStatic
    fun glGetAttribLocation(program: Int, name: CharSequence): Int {
        if (BotGlobalConfig.headless) return 0
        
        return GL20.glGetAttribLocation(program, name)
    }
}