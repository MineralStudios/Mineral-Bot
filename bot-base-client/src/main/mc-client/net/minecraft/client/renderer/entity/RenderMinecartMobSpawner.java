package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityMobSpawnerRenderer;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;

public class RenderMinecartMobSpawner extends RenderMinecart {
    public RenderMinecartMobSpawner(Minecraft mc) {
        super(mc);
    }

    protected void func_147910_a(EntityMinecartMobSpawner p_147910_1_, float p_147910_2_, Block p_147910_3_,
            int p_147910_4_) {
        super.func_147910_a(p_147910_1_, p_147910_2_, p_147910_3_, p_147910_4_);

        if (p_147910_3_ == Blocks.mob_spawner) {
            TileEntityMobSpawnerRenderer.func_147517_a(this.mc, p_147910_1_.func_98039_d(), p_147910_1_.posX,
                    p_147910_1_.posY,
                    p_147910_1_.posZ, p_147910_2_);
        }
    }

    protected void func_147910_a(EntityMinecart p_147910_1_, float p_147910_2_, Block p_147910_3_, int p_147910_4_) {
        this.func_147910_a((EntityMinecartMobSpawner) p_147910_1_, p_147910_2_, p_147910_3_, p_147910_4_);
    }
}
