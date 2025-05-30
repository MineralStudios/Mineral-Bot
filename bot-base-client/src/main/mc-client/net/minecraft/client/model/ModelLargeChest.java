package net.minecraft.client.model;

import net.minecraft.client.Minecraft;

public class ModelLargeChest extends ModelChest {

    public ModelLargeChest(Minecraft mc) {
        super(mc);
        this.chestLid = (new ModelRenderer(mc, this, 0, 0)).setTextureSize(128, 64);
        this.chestLid.addBox(0.0F, -5.0F, -14.0F, 30, 5, 14, 0.0F);
        this.chestLid.rotationPointX = 1.0F;
        this.chestLid.rotationPointY = 7.0F;
        this.chestLid.rotationPointZ = 15.0F;
        this.chestKnob = (new ModelRenderer(mc, this, 0, 0)).setTextureSize(128, 64);
        this.chestKnob.addBox(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F);
        this.chestKnob.rotationPointX = 16.0F;
        this.chestKnob.rotationPointY = 7.0F;
        this.chestKnob.rotationPointZ = 15.0F;
        this.chestBelow = (new ModelRenderer(mc, this, 0, 19)).setTextureSize(128, 64);
        this.chestBelow.addBox(0.0F, 0.0F, 0.0F, 30, 10, 14, 0.0F);
        this.chestBelow.rotationPointX = 1.0F;
        this.chestBelow.rotationPointY = 6.0F;
        this.chestBelow.rotationPointZ = 1.0F;
    }
}
