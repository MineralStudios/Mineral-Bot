package net.minecraft.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelCreeper extends ModelBase {
    public ModelRenderer head;
    public ModelRenderer field_78133_b;
    public ModelRenderer body;
    public ModelRenderer leg1;
    public ModelRenderer leg2;
    public ModelRenderer leg3;
    public ModelRenderer leg4;

    public ModelCreeper(Minecraft mc) {
        this(mc, 0.0F);
    }

    public ModelCreeper(Minecraft mc, float p_i46366_1_) {
        byte var2 = 4;
        this.head = new ModelRenderer(mc, this, 0, 0);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, p_i46366_1_);
        this.head.setRotationPoint(0.0F, (float) var2, 0.0F);
        this.field_78133_b = new ModelRenderer(mc, this, 32, 0);
        this.field_78133_b.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, p_i46366_1_ + 0.5F);
        this.field_78133_b.setRotationPoint(0.0F, (float) var2, 0.0F);
        this.body = new ModelRenderer(mc, this, 16, 16);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, p_i46366_1_);
        this.body.setRotationPoint(0.0F, (float) var2, 0.0F);
        this.leg1 = new ModelRenderer(mc, this, 0, 16);
        this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, p_i46366_1_);
        this.leg1.setRotationPoint(-2.0F, (float) (12 + var2), 4.0F);
        this.leg2 = new ModelRenderer(mc, this, 0, 16);
        this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, p_i46366_1_);
        this.leg2.setRotationPoint(2.0F, (float) (12 + var2), 4.0F);
        this.leg3 = new ModelRenderer(mc, this, 0, 16);
        this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, p_i46366_1_);
        this.leg3.setRotationPoint(-2.0F, (float) (12 + var2), -4.0F);
        this.leg4 = new ModelRenderer(mc, this, 0, 16);
        this.leg4.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, p_i46366_1_);
        this.leg4.setRotationPoint(2.0F, (float) (12 + var2), -4.0F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_,
            float p_78088_6_, float p_78088_7_) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
        this.head.render(p_78088_7_);
        this.body.render(p_78088_7_);
        this.leg1.render(p_78088_7_);
        this.leg2.render(p_78088_7_);
        this.leg3.render(p_78088_7_);
        this.leg4.render(p_78088_7_);
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
        this.head.rotateAngleY = p_78087_4_ / (180F / (float) Math.PI);
        this.head.rotateAngleX = p_78087_5_ / (180F / (float) Math.PI);
        this.leg1.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_;
        this.leg2.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float) Math.PI) * 1.4F * p_78087_2_;
        this.leg3.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float) Math.PI) * 1.4F * p_78087_2_;
        this.leg4.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_;
    }
}
