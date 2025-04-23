package gg.mineral.bot.lwjgl.opengl

import gg.mineral.bot.impl.config.BotGlobalConfig
import org.lwjgl.opengl.GL13

object GL13 {
    const val GL_TEXTURE0: Int = 0x84C0
    const val GL_TEXTURE1: Int = 0x84C1
    const val GL_TEXTURE2: Int = 0x84C2
    const val GL_TEXTURE3: Int = 0x84C3
    const val GL_TEXTURE4: Int = 0x84C4
    const val GL_TEXTURE5: Int = 0x84C5
    const val GL_TEXTURE6: Int = 0x84C6
    const val GL_TEXTURE7: Int = 0x84C7
    const val GL_TEXTURE8: Int = 0x84C8
    const val GL_TEXTURE9: Int = 0x84C9
    const val GL_TEXTURE10: Int = 0x84CA
    const val GL_TEXTURE11: Int = 0x84CB
    const val GL_TEXTURE12: Int = 0x84CC
    const val GL_TEXTURE13: Int = 0x84CD
    const val GL_TEXTURE14: Int = 0x84CE
    const val GL_TEXTURE15: Int = 0x84CF
    const val GL_TEXTURE16: Int = 0x84D0
    const val GL_TEXTURE17: Int = 0x84D1
    const val GL_TEXTURE18: Int = 0x84D2
    const val GL_TEXTURE19: Int = 0x84D3
    const val GL_TEXTURE20: Int = 0x84D4
    const val GL_TEXTURE21: Int = 0x84D5
    const val GL_TEXTURE22: Int = 0x84D6
    const val GL_TEXTURE23: Int = 0x84D7
    const val GL_TEXTURE24: Int = 0x84D8
    const val GL_TEXTURE25: Int = 0x84D9
    const val GL_TEXTURE26: Int = 0x84DA
    const val GL_TEXTURE27: Int = 0x84DB
    const val GL_TEXTURE28: Int = 0x84DC
    const val GL_TEXTURE29: Int = 0x84DD
    const val GL_TEXTURE30: Int = 0x84DE
    const val GL_TEXTURE31: Int = 0x84DF
    const val GL_ACTIVE_TEXTURE: Int = 0x84E0
    const val GL_CLIENT_ACTIVE_TEXTURE: Int = 0x84E1
    const val GL_MAX_TEXTURE_UNITS: Int = 0x84E2
    const val GL_NORMAL_MAP: Int = 0x8511
    const val GL_REFLECTION_MAP: Int = 0x8512
    const val GL_TEXTURE_CUBE_MAP: Int = 0x8513
    const val GL_TEXTURE_BINDING_CUBE_MAP: Int = 0x8514
    const val GL_TEXTURE_CUBE_MAP_POSITIVE_X: Int = 0x8515
    const val GL_TEXTURE_CUBE_MAP_NEGATIVE_X: Int = 0x8516
    const val GL_TEXTURE_CUBE_MAP_POSITIVE_Y: Int = 0x8517
    const val GL_TEXTURE_CUBE_MAP_NEGATIVE_Y: Int = 0x8518
    const val GL_TEXTURE_CUBE_MAP_POSITIVE_Z: Int = 0x8519
    const val GL_TEXTURE_CUBE_MAP_NEGATIVE_Z: Int = 0x851A
    const val GL_PROXY_TEXTURE_CUBE_MAP: Int = 0x851B
    const val GL_MAX_CUBE_MAP_TEXTURE_SIZE: Int = 0x851C
    const val GL_COMPRESSED_ALPHA: Int = 0x84E9
    const val GL_COMPRESSED_LUMINANCE: Int = 0x84EA
    const val GL_COMPRESSED_LUMINANCE_ALPHA: Int = 0x84EB
    const val GL_COMPRESSED_INTENSITY: Int = 0x84EC
    const val GL_COMPRESSED_RGB: Int = 0x84ED
    const val GL_COMPRESSED_RGBA: Int = 0x84EE
    const val GL_TEXTURE_COMPRESSION_HINT: Int = 0x84EF
    const val GL_TEXTURE_COMPRESSED_IMAGE_SIZE: Int = 0x86A0
    const val GL_TEXTURE_COMPRESSED: Int = 0x86A1
    const val GL_NUM_COMPRESSED_TEXTURE_FORMATS: Int = 0x86A2
    const val GL_COMPRESSED_TEXTURE_FORMATS: Int = 0x86A3
    const val GL_MULTISAMPLE: Int = 0x809D
    const val GL_SAMPLE_ALPHA_TO_COVERAGE: Int = 0x809E
    const val GL_SAMPLE_ALPHA_TO_ONE: Int = 0x809F
    const val GL_SAMPLE_COVERAGE: Int = 0x80A0
    const val GL_SAMPLE_BUFFERS: Int = 0x80A8
    const val GL_SAMPLES: Int = 0x80A9
    const val GL_SAMPLE_COVERAGE_VALUE: Int = 0x80AA
    const val GL_SAMPLE_COVERAGE_INVERT: Int = 0x80AB
    const val GL_MULTISAMPLE_BIT: Int = 0x20000000
    const val GL_TRANSPOSE_MODELVIEW_MATRIX: Int = 0x84E3
    const val GL_TRANSPOSE_PROJECTION_MATRIX: Int = 0x84E4
    const val GL_TRANSPOSE_TEXTURE_MATRIX: Int = 0x84E5
    const val GL_TRANSPOSE_COLOR_MATRIX: Int = 0x84E6
    const val GL_COMBINE: Int = 0x8570
    const val GL_COMBINE_RGB: Int = 0x8571
    const val GL_COMBINE_ALPHA: Int = 0x8572
    const val GL_SOURCE0_RGB: Int = 0x8580
    const val GL_SOURCE1_RGB: Int = 0x8581
    const val GL_SOURCE2_RGB: Int = 0x8582
    const val GL_SOURCE0_ALPHA: Int = 0x8588
    const val GL_SOURCE1_ALPHA: Int = 0x8589
    const val GL_SOURCE2_ALPHA: Int = 0x858A
    const val GL_OPERAND0_RGB: Int = 0x8590
    const val GL_OPERAND1_RGB: Int = 0x8591
    const val GL_OPERAND2_RGB: Int = 0x8592
    const val GL_OPERAND0_ALPHA: Int = 0x8598
    const val GL_OPERAND1_ALPHA: Int = 0x8599
    const val GL_OPERAND2_ALPHA: Int = 0x859A
    const val GL_RGB_SCALE: Int = 0x8573
    const val GL_ADD_SIGNED: Int = 0x8574
    const val GL_INTERPOLATE: Int = 0x8575
    const val GL_SUBTRACT: Int = 0x84E7
    const val GL_CONSTANT: Int = 0x8576
    const val GL_PRIMARY_COLOR: Int = 0x8577
    const val GL_PREVIOUS: Int = 0x8578
    const val GL_DOT3_RGB: Int = 0x86AE
    const val GL_DOT3_RGBA: Int = 0x86AF
    const val GL_CLAMP_TO_BORDER: Int = 0x812D

    @JvmStatic
    fun glActiveTexture(texture: Int) {
        if (BotGlobalConfig.headless) return

        GL13.glActiveTexture(texture)
    }

    @JvmStatic
    fun glClientActiveTexture(texture: Int) {
        if (BotGlobalConfig.headless) return

        GL13.glClientActiveTexture(texture)
    }

    @JvmStatic
    fun glMultiTexCoord2f(target: Int, s: Float, t: Float) {
        if (BotGlobalConfig.headless) return

        GL13.glMultiTexCoord2f(target, s, t)
    }
}
