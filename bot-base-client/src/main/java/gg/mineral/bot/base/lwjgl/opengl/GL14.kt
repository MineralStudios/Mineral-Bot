package gg.mineral.bot.base.lwjgl.opengl;

import gg.mineral.bot.impl.config.BotGlobalConfig;

public class GL14 {

    public static final int GL_GENERATE_MIPMAP = 0x8191,
            GL_GENERATE_MIPMAP_HINT = 0x8192,
            GL_DEPTH_COMPONENT16 = 0x81A5,
            GL_DEPTH_COMPONENT24 = 0x81A6,
            GL_DEPTH_COMPONENT32 = 0x81A7,
            GL_TEXTURE_DEPTH_SIZE = 0x884A,
            GL_DEPTH_TEXTURE_MODE = 0x884B,
            GL_TEXTURE_COMPARE_MODE = 0x884C,
            GL_TEXTURE_COMPARE_FUNC = 0x884D,
            GL_COMPARE_R_TO_TEXTURE = 0x884E,
            GL_FOG_COORDINATE_SOURCE = 0x8450,
            GL_FOG_COORDINATE = 0x8451,
            GL_FRAGMENT_DEPTH = 0x8452,
            GL_CURRENT_FOG_COORDINATE = 0x8453,
            GL_FOG_COORDINATE_ARRAY_TYPE = 0x8454,
            GL_FOG_COORDINATE_ARRAY_STRIDE = 0x8455,
            GL_FOG_COORDINATE_ARRAY_POINTER = 0x8456,
            GL_FOG_COORDINATE_ARRAY = 0x8457,
            GL_POINT_SIZE_MIN = 0x8126,
            GL_POINT_SIZE_MAX = 0x8127,
            GL_POINT_FADE_THRESHOLD_SIZE = 0x8128,
            GL_POINT_DISTANCE_ATTENUATION = 0x8129,
            GL_COLOR_SUM = 0x8458,
            GL_CURRENT_SECONDARY_COLOR = 0x8459,
            GL_SECONDARY_COLOR_ARRAY_SIZE = 0x845A,
            GL_SECONDARY_COLOR_ARRAY_TYPE = 0x845B,
            GL_SECONDARY_COLOR_ARRAY_STRIDE = 0x845C,
            GL_SECONDARY_COLOR_ARRAY_POINTER = 0x845D,
            GL_SECONDARY_COLOR_ARRAY = 0x845E,
            GL_BLEND_DST_RGB = 0x80C8,
            GL_BLEND_SRC_RGB = 0x80C9,
            GL_BLEND_DST_ALPHA = 0x80CA,
            GL_BLEND_SRC_ALPHA = 0x80CB,
            GL_INCR_WRAP = 0x8507,
            GL_DECR_WRAP = 0x8508,
            GL_TEXTURE_FILTER_CONTROL = 0x8500,
            GL_TEXTURE_LOD_BIAS = 0x8501,
            GL_MAX_TEXTURE_LOD_BIAS = 0x84FD,
            GL_MIRRORED_REPEAT = 0x8370,
            GL_BLEND_COLOR = 0x8005,
            GL_BLEND_EQUATION = 0x8009,
            GL_FUNC_ADD = 0x8006,
            GL_FUNC_SUBTRACT = 0x800A,
            GL_FUNC_REVERSE_SUBTRACT = 0x800B,
            GL_MIN = 0x8007,
            GL_MAX = 0x8008;

    public static void glBlendFuncSeparate(int sfactorRGB, int dfactorRGB, int sfactorAlpha, int dfactorAlpha) {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.GL14.glBlendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha);
    }

    public static void glBlendEquation(int mode) {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.opengl.GL14.glBlendEquation(mode);
    }

}
