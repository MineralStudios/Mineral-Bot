package gg.mineral.bot.lwjgl.opengl

import org.lwjgl.opengl.ContextCapabilities


class ContextCapabilities(obj: ContextCapabilities? = null) {
    @JvmField
    var OpenGL11: Boolean = true

    @JvmField
    var OpenGL12: Boolean = true

    @JvmField
    var OpenGL13: Boolean = true

    @JvmField
    var OpenGL14: Boolean = true

    @JvmField
    var OpenGL15: Boolean = true

    @JvmField
    var OpenGL20: Boolean = true

    @JvmField
    var OpenGL21: Boolean = true

    @JvmField
    var OpenGL30: Boolean = true

    @JvmField
    var OpenGL31: Boolean = true

    @JvmField
    var OpenGL32: Boolean = true

    @JvmField
    var OpenGL33: Boolean = true

    @JvmField
    var OpenGL40: Boolean = true
    var OpenGL41: Boolean = true
    var OpenGL42: Boolean = true
    var OpenGL43: Boolean = true
    var OpenGL44: Boolean = true

    @JvmField
    var GL_NV_fog_distance: Boolean = false

    @JvmField
    var GL_ARB_occlusion_query: Boolean = false

    @JvmField
    var GL_ARB_multitexture: Boolean = true

    @JvmField
    var GL_EXT_blend_func_separate: Boolean = false

    @JvmField
    var GL_ARB_framebuffer_object: Boolean = true

    @JvmField
    var GL_EXT_framebuffer_object: Boolean = true

    @JvmField
    var GL_EXT_texture_filter_anisotropic: Boolean = false

    @JvmField
    var GL_ARB_shader_objects: Boolean = true

    @JvmField
    var GL_ARB_vertex_shader: Boolean = true

    @JvmField
    var GL_ARB_fragment_shader: Boolean = true

    init {
        this.OpenGL11 = obj?.OpenGL11 == true
        this.OpenGL12 = obj?.OpenGL12 == true
        this.OpenGL13 = obj?.OpenGL13 == true
        this.OpenGL14 = obj?.OpenGL14 == true
        this.OpenGL15 = obj?.OpenGL15 == true
        this.OpenGL20 = obj?.OpenGL20 == true
        this.OpenGL21 = obj?.OpenGL21 == true
        this.OpenGL30 = obj?.OpenGL30 == true
        this.OpenGL31 = obj?.OpenGL31 == true
        this.OpenGL32 = obj?.OpenGL32 == true
        this.OpenGL33 = obj?.OpenGL33 == true
        this.OpenGL40 = obj?.OpenGL40 == true
        this.OpenGL41 = obj?.OpenGL41 == true
        this.OpenGL42 = obj?.OpenGL42 == true
        this.OpenGL43 = obj?.OpenGL43 == true
        this.OpenGL44 = obj?.OpenGL44 == true
        this.GL_NV_fog_distance = obj?.GL_NV_fog_distance == true
        this.GL_ARB_occlusion_query = obj?.GL_ARB_occlusion_query == true
        this.GL_ARB_multitexture = obj?.GL_ARB_multitexture == true
        this.GL_EXT_blend_func_separate = obj?.GL_EXT_blend_func_separate == true
        this.GL_ARB_framebuffer_object = obj?.GL_ARB_framebuffer_object == true
        this.GL_EXT_framebuffer_object = obj?.GL_EXT_framebuffer_object == true
        this.GL_EXT_texture_filter_anisotropic = obj?.GL_EXT_texture_filter_anisotropic == true
        this.GL_ARB_shader_objects = obj?.GL_ARB_shader_objects == true
        this.GL_ARB_vertex_shader = obj?.GL_ARB_vertex_shader == true
        this.GL_ARB_fragment_shader = obj?.GL_ARB_fragment_shader == true
    }
}
