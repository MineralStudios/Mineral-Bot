package gg.mineral.bot.lwjgl.opengl

import gg.mineral.bot.impl.config.BotGlobalConfig
import org.lwjgl.opengl.GL14

object GL14 {
    const val GL_GENERATE_MIPMAP: Int = 0x8191
    const val GL_GENERATE_MIPMAP_HINT: Int = 0x8192
    const val GL_DEPTH_COMPONENT16: Int = 0x81A5
    const val GL_DEPTH_COMPONENT24: Int = 0x81A6
    const val GL_DEPTH_COMPONENT32: Int = 0x81A7
    const val GL_TEXTURE_DEPTH_SIZE: Int = 0x884A
    const val GL_DEPTH_TEXTURE_MODE: Int = 0x884B
    const val GL_TEXTURE_COMPARE_MODE: Int = 0x884C
    const val GL_TEXTURE_COMPARE_FUNC: Int = 0x884D
    const val GL_COMPARE_R_TO_TEXTURE: Int = 0x884E
    const val GL_FOG_COORDINATE_SOURCE: Int = 0x8450
    const val GL_FOG_COORDINATE: Int = 0x8451
    const val GL_FRAGMENT_DEPTH: Int = 0x8452
    const val GL_CURRENT_FOG_COORDINATE: Int = 0x8453
    const val GL_FOG_COORDINATE_ARRAY_TYPE: Int = 0x8454
    const val GL_FOG_COORDINATE_ARRAY_STRIDE: Int = 0x8455
    const val GL_FOG_COORDINATE_ARRAY_POINTER: Int = 0x8456
    const val GL_FOG_COORDINATE_ARRAY: Int = 0x8457
    const val GL_POINT_SIZE_MIN: Int = 0x8126
    const val GL_POINT_SIZE_MAX: Int = 0x8127
    const val GL_POINT_FADE_THRESHOLD_SIZE: Int = 0x8128
    const val GL_POINT_DISTANCE_ATTENUATION: Int = 0x8129
    const val GL_COLOR_SUM: Int = 0x8458
    const val GL_CURRENT_SECONDARY_COLOR: Int = 0x8459
    const val GL_SECONDARY_COLOR_ARRAY_SIZE: Int = 0x845A
    const val GL_SECONDARY_COLOR_ARRAY_TYPE: Int = 0x845B
    const val GL_SECONDARY_COLOR_ARRAY_STRIDE: Int = 0x845C
    const val GL_SECONDARY_COLOR_ARRAY_POINTER: Int = 0x845D
    const val GL_SECONDARY_COLOR_ARRAY: Int = 0x845E
    const val GL_BLEND_DST_RGB: Int = 0x80C8
    const val GL_BLEND_SRC_RGB: Int = 0x80C9
    const val GL_BLEND_DST_ALPHA: Int = 0x80CA
    const val GL_BLEND_SRC_ALPHA: Int = 0x80CB
    const val GL_INCR_WRAP: Int = 0x8507
    const val GL_DECR_WRAP: Int = 0x8508
    const val GL_TEXTURE_FILTER_CONTROL: Int = 0x8500
    const val GL_TEXTURE_LOD_BIAS: Int = 0x8501
    const val GL_MAX_TEXTURE_LOD_BIAS: Int = 0x84FD
    const val GL_MIRRORED_REPEAT: Int = 0x8370
    const val GL_BLEND_COLOR: Int = 0x8005
    const val GL_BLEND_EQUATION: Int = 0x8009
    const val GL_FUNC_ADD: Int = 0x8006
    const val GL_FUNC_SUBTRACT: Int = 0x800A
    const val GL_FUNC_REVERSE_SUBTRACT: Int = 0x800B
    const val GL_MIN: Int = 0x8007
    const val GL_MAX: Int = 0x8008

    @JvmStatic
    fun glBlendFuncSeparate(sfactorRGB: Int, dfactorRGB: Int, sfactorAlpha: Int, dfactorAlpha: Int) {
        if (BotGlobalConfig.headless) return

        GL14.glBlendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha)
    }

    @JvmStatic
    fun glBlendEquation(mode: Int) {
        if (BotGlobalConfig.headless) return

        GL14.glBlendEquation(mode)
    }
}
