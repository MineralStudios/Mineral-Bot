package net.minecraft.client.model;

import gg.mineral.bot.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelChicken extends ModelBase {
    public final ModelRenderer head;
    public final ModelRenderer body;
    public final ModelRenderer rightLeg;
    public final ModelRenderer leftLeg;
    public final ModelRenderer rightWing;
    public final ModelRenderer leftWing;
    public final ModelRenderer bill;
    public final ModelRenderer chin;

    public ModelChicken(Minecraft mc) {
        byte var1 = 16;
        this.head = new ModelRenderer(mc, this, 0, 0);
        this.head.addBox(-2.0F, -6.0F, -2.0F, 4, 6, 3, 0.0F);
        this.head.setRotationPoint(0.0F, (float) (-1 + var1), -4.0F);
        this.bill = new ModelRenderer(mc, this, 14, 0);
        this.bill.addBox(-2.0F, -4.0F, -4.0F, 4, 2, 2, 0.0F);
        this.bill.setRotationPoint(0.0F, (float) (-1 + var1), -4.0F);
        this.chin = new ModelRenderer(mc, this, 14, 4);
        this.chin.addBox(-1.0F, -2.0F, -3.0F, 2, 2, 2, 0.0F);
        this.chin.setRotationPoint(0.0F, (float) (-1 + var1), -4.0F);
        this.body = new ModelRenderer(mc, this, 0, 9);
        this.body.addBox(-3.0F, -4.0F, -3.0F, 6, 8, 6, 0.0F);
        this.body.setRotationPoint(0.0F, (float) var1, 0.0F);
        this.rightLeg = new ModelRenderer(mc, this, 26, 0);
        this.rightLeg.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3);
        this.rightLeg.setRotationPoint(-2.0F, (float) (3 + var1), 1.0F);
        this.leftLeg = new ModelRenderer(mc, this, 26, 0);
        this.leftLeg.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3);
        this.leftLeg.setRotationPoint(1.0F, (float) (3 + var1), 1.0F);
        this.rightWing = new ModelRenderer(mc, this, 24, 13);
        this.rightWing.addBox(0.0F, 0.0F, -3.0F, 1, 4, 6);
        this.rightWing.setRotationPoint(-4.0F, (float) (-3 + var1), 0.0F);
        this.leftWing = new ModelRenderer(mc, this, 24, 13);
        this.leftWing.addBox(-1.0F, 0.0F, -3.0F, 1, 4, 6);
        this.leftWing.setRotationPoint(4.0F, (float) (-3 + var1), 0.0F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_,
                       float p_78088_6_, float p_78088_7_) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);

        if (this.isChild) {
            float var8 = 2.0F;
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 5.0F * p_78088_7_, 2.0F * p_78088_7_);
            this.head.render(p_78088_7_);
            this.bill.render(p_78088_7_);
            this.chin.render(p_78088_7_);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / var8, 1.0F / var8, 1.0F / var8);
            GL11.glTranslatef(0.0F, 24.0F * p_78088_7_, 0.0F);
            this.body.render(p_78088_7_);
            this.rightLeg.render(p_78088_7_);
            this.leftLeg.render(p_78088_7_);
            this.rightWing.render(p_78088_7_);
            this.leftWing.render(p_78088_7_);
            GL11.glPopMatrix();
        } else {
            this.head.render(p_78088_7_);
            this.bill.render(p_78088_7_);
            this.chin.render(p_78088_7_);
            this.body.render(p_78088_7_);
            this.rightLeg.render(p_78088_7_);
            this.leftLeg.render(p_78088_7_);
            this.rightWing.render(p_78088_7_);
            this.leftWing.render(p_78088_7_);
        }
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used
     * for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and
     * forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_,
                                  float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
        this.head.rotateAngleX = p_78087_5_ / (180F / (float) Math.PI);
        this.head.rotateAngleY = p_78087_4_ / (180F / (float) Math.PI);
        this.bill.rotateAngleX = this.head.rotateAngleX;
        this.bill.rotateAngleY = this.head.rotateAngleY;
        this.chin.rotateAngleX = this.head.rotateAngleX;
        this.chin.rotateAngleY = this.head.rotateAngleY;
        this.body.rotateAngleX = ((float) Math.PI / 2F);
        this.rightLeg.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_;
        this.leftLeg.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float) Math.PI) * 1.4F * p_78087_2_;
        this.rightWing.rotateAngleZ = p_78087_3_;
        this.leftWing.rotateAngleZ = -p_78087_3_;
    }
}
