package optifine;

import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.ISaveHandler;

public class WorldServerMultiOF extends WorldServerOF {
    public WorldServerMultiOF(Minecraft mc, MinecraftServer p_i45283_1_, ISaveHandler p_i45283_2_, String p_i45283_3_,
            int p_i45283_4_, WorldSettings p_i45283_5_, WorldServer p_i45283_6_, Profiler p_i45283_7_) {
        super(mc, p_i45283_1_, p_i45283_2_, p_i45283_3_, p_i45283_4_, p_i45283_5_, p_i45283_7_);
        this.mapStorage = p_i45283_6_.mapStorage;
        this.worldScoreboard = p_i45283_6_.getScoreboard();
        this.worldInfo = new DerivedWorldInfo(p_i45283_6_.getWorldInfo());
    }

    /**
     * Saves the chunks to disk.
     */
    protected void saveLevel() throws MinecraftException {
    }
}
