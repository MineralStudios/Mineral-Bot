package net.minecraft.client.model;

import gg.mineral.bot.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class ModelEnderCrystal extends ModelBase {
    /**
     * The cube model for the Ender Crystal.
     */
    private final ModelRenderer cube;

    /**
     * The glass model for the Ender Crystal.
     */
    private final ModelRenderer glass;

    /**
     * The base model for the Ender Crystal.
     */
    private ModelRenderer base;

    public ModelEnderCrystal(Minecraft mc, float p_i1170_1_, boolean p_i1170_2_) {
        this.glass = new ModelRenderer(mc, this, "glass");
        this.glass.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
        this.cube = new ModelRenderer(mc, this, "cube");
        this.cube.setTextureOffset(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);

        if (p_i1170_2_) {
            this.base = new ModelRenderer(mc, this, "base");
            this.base.setTextureOffset(0, 16).addBox(-6.0F, 0.0F, -6.0F, 12, 4, 12);
        }
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_,
                       float p_78088_6_, float p_78088_7_) {
        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GL11.glTranslatef(0.0F, -0.5F, 0.0F);

        if (this.base != null)
            this.base.render(p_78088_7_);

        GL11.glRotatef(p_78088_3_, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, 0.8F + p_78088_4_, 0.0F);
        GL11.glRotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
        this.glass.render(p_78088_7_);
        float var8 = 0.875F;
        GL11.glScalef(var8, var8, var8);
        GL11.glRotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
        GL11.glRotatef(p_78088_3_, 0.0F, 1.0F, 0.0F);
        this.glass.render(p_78088_7_);
        GL11.glScalef(var8, var8, var8);
        GL11.glRotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
        GL11.glRotatef(p_78088_3_, 0.0F, 1.0F, 0.0F);
        this.cube.render(p_78088_7_);
        GL11.glPopMatrix();
    }
}
