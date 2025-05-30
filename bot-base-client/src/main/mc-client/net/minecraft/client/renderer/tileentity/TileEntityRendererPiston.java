package net.minecraft.client.renderer.tileentity;

import gg.mineral.bot.lwjgl.opengl.GL11;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.world.World;

@RequiredArgsConstructor
public class TileEntityRendererPiston extends TileEntitySpecialRenderer {
    private RenderBlocks field_147516_b;

    private final Minecraft mc;

    public void renderTileEntityAt(TileEntityPiston p_147500_1_, double p_147500_2_, double p_147500_4_,
                                   double p_147500_6_, float p_147500_8_) {
        Block var9 = p_147500_1_.func_145861_a();

        if (var9.getMaterial() != Material.air && p_147500_1_.func_145860_a(p_147500_8_) < 1.0F) {
            Tessellator var10 = this.mc.getTessellator();
            this.bindTexture(TextureMap.locationBlocksTexture);
            RenderHelper.disableStandardItemLighting();
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_CULL_FACE);

            if (this.mc.isAmbientOcclusionEnabled()) {
                GL11.glShadeModel(GL11.GL_SMOOTH);
            } else {
                GL11.glShadeModel(GL11.GL_FLAT);
            }

            if (var10 != null) {
                var10.startDrawingQuads();
                var10.setTranslation(
                        (float) p_147500_2_ - (float) p_147500_1_.field_145851_c
                                + p_147500_1_.func_145865_b(p_147500_8_),
                        (float) p_147500_4_ - (float) p_147500_1_.field_145848_d
                                + p_147500_1_.func_145862_c(p_147500_8_),
                        (float) p_147500_6_ - (float) p_147500_1_.field_145849_e
                                + p_147500_1_.func_145859_d(p_147500_8_));
                var10.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            }

            if (var9 == Blocks.piston_head && p_147500_1_.func_145860_a(p_147500_8_) < 0.5F) {
                this.field_147516_b.renderPistonExtensionAllFaces(var9, p_147500_1_.field_145851_c,
                        p_147500_1_.field_145848_d, p_147500_1_.field_145849_e, false);
            } else if (p_147500_1_.func_145867_d() && !p_147500_1_.func_145868_b()) {
                Blocks.piston_head.func_150086_a(((BlockPistonBase) var9).func_150073_e());
                this.field_147516_b.renderPistonExtensionAllFaces(Blocks.piston_head, p_147500_1_.field_145851_c,
                        p_147500_1_.field_145848_d, p_147500_1_.field_145849_e,
                        p_147500_1_.func_145860_a(p_147500_8_) < 0.5F);
                Blocks.piston_head.func_150087_e();
                if (var10 != null)
                    var10.setTranslation((float) p_147500_2_ - (float) p_147500_1_.field_145851_c,
                            (float) p_147500_4_ - (float) p_147500_1_.field_145848_d,
                            (float) p_147500_6_ - (float) p_147500_1_.field_145849_e);
                this.field_147516_b.renderPistonBaseAllFaces(var9, p_147500_1_.field_145851_c,
                        p_147500_1_.field_145848_d, p_147500_1_.field_145849_e);
            } else {
                this.field_147516_b.renderBlockAllFaces(var9, p_147500_1_.field_145851_c, p_147500_1_.field_145848_d,
                        p_147500_1_.field_145849_e);
            }

            if (var10 != null) {
                var10.setTranslation(0.0D, 0.0D, 0.0D);
                var10.draw();
            }
            this.mc.renderHelper.enableStandardItemLighting();
        }
    }

    public void func_147496_a(World p_147496_1_) {
        this.field_147516_b = new RenderBlocks(this.mc, p_147496_1_);
    }

    public void renderTileEntityAt(TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_,
                                   float p_147500_8_) {
        this.renderTileEntityAt((TileEntityPiston) p_147500_1_, p_147500_2_, p_147500_4_, p_147500_6_, p_147500_8_);
    }
}
