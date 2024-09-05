package gg.mineral.bot.base.lwjgl.opengl;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ContextCapabilities {

    public boolean OpenGL11 = true, OpenGL12 = true, OpenGL13 = true, OpenGL14 = true, OpenGL15 = true, OpenGL20 = true,
            OpenGL21 = true, OpenGL30 = true, OpenGL31 = true, OpenGL32 = true, OpenGL33 = true, OpenGL40 = true,
            OpenGL41 = true, OpenGL42 = true, OpenGL43 = true, OpenGL44 = true, GL_NV_fog_distance = false,
            GL_ARB_occlusion_query = false, GL_ARB_multitexture = true, GL_EXT_blend_func_separate = false,
            GL_ARB_framebuffer_object = true, GL_EXT_framebuffer_object = true,
            GL_EXT_texture_filter_anisotropic = false, GL_ARB_shader_objects = true, GL_ARB_vertex_shader = true,
            GL_ARB_fragment_shader = true;

    public ContextCapabilities(org.lwjgl.opengl.ContextCapabilities obj) {
        this.OpenGL11 = obj.OpenGL11;
        this.OpenGL12 = obj.OpenGL12;
        this.OpenGL13 = obj.OpenGL13;
        this.OpenGL14 = obj.OpenGL14;
        this.OpenGL15 = obj.OpenGL15;
        this.OpenGL20 = obj.OpenGL20;
        this.OpenGL21 = obj.OpenGL21;
        this.OpenGL30 = obj.OpenGL30;
        this.OpenGL31 = obj.OpenGL31;
        this.OpenGL32 = obj.OpenGL32;
        this.OpenGL33 = obj.OpenGL33;
        this.OpenGL40 = obj.OpenGL40;
        this.OpenGL41 = obj.OpenGL41;
        this.OpenGL42 = obj.OpenGL42;
        this.OpenGL43 = obj.OpenGL43;
        this.OpenGL44 = obj.OpenGL44;
        this.GL_NV_fog_distance = obj.GL_NV_fog_distance;
        this.GL_ARB_occlusion_query = obj.GL_ARB_occlusion_query;
        this.GL_ARB_multitexture = obj.GL_ARB_multitexture;
        this.GL_EXT_blend_func_separate = obj.GL_EXT_blend_func_separate;
        this.GL_ARB_framebuffer_object = obj.GL_ARB_framebuffer_object;
        this.GL_EXT_framebuffer_object = obj.GL_EXT_framebuffer_object;
        this.GL_EXT_texture_filter_anisotropic = obj.GL_EXT_texture_filter_anisotropic;
        this.GL_ARB_shader_objects = obj.GL_ARB_shader_objects;
        this.GL_ARB_vertex_shader = obj.GL_ARB_vertex_shader;
        this.GL_ARB_fragment_shader = obj.GL_ARB_fragment_shader;
    }
}
