package gg.mineral.bot.lwjgl

import org.lwjgl.BufferUtils
import java.nio.ByteBuffer
import java.nio.IntBuffer

object BufferUtils {
    @JvmStatic
    fun createIntBuffer(size: Int): IntBuffer {
        return BufferUtils.createIntBuffer(size)
    }

    @JvmStatic
    fun createByteBuffer(size: Int): ByteBuffer {
        return BufferUtils.createByteBuffer(size)
    }
}