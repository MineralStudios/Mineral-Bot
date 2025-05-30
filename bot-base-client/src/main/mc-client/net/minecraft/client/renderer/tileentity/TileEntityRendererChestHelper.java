package net.minecraft.client.renderer.tileentity;

import lombok.RequiredArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;

@RequiredArgsConstructor
public class TileEntityRendererChestHelper {
    private TileEntityChest field_147717_b = new TileEntityChest(0);
    private TileEntityChest field_147718_c = new TileEntityChest(1);
    private TileEntityEnderChest field_147716_d = new TileEntityEnderChest();
    private final Minecraft mc;

    public void func_147715_a(Block p_147715_1_, int p_147715_2_, float p_147715_3_) {
        TileEntityRendererDispatcher tileEntityRendererDispatcher = this.mc.tileEntityRendererDispatcher;

        if (tileEntityRendererDispatcher == null)
            return;

        if (p_147715_1_ == Blocks.ender_chest)
            tileEntityRendererDispatcher.func_147549_a(this.field_147716_d, 0.0D, 0.0D, 0.0D, 0.0F);
        else if (p_147715_1_ == Blocks.trapped_chest)
            tileEntityRendererDispatcher.func_147549_a(this.field_147718_c, 0.0D, 0.0D, 0.0D, 0.0F);
        else
            tileEntityRendererDispatcher.func_147549_a(this.field_147717_b, 0.0D, 0.0D, 0.0D, 0.0F);
    }
}
