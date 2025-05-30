package optifine;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;

public class ClearWater {
    public static void updateWaterOpacity(Minecraft mc, GameSettings settings, World world) {
        if (settings != null) {
            byte cp = 3;

            if (settings.ofClearWater) {
                cp = 1;
            }

            BlockUtils.setLightOpacity(Blocks.water, cp);
            BlockUtils.setLightOpacity(Blocks.flowing_water, cp);
        }

        if (world != null) {
            IChunkProvider var23 = world.getChunkProvider();

            if (var23 != null) {
                EntityLivingBase rve = mc.renderViewEntity;

                if (rve != null) {
                    int cViewX = (int) rve.posX / 16;
                    int cViewZ = (int) rve.posZ / 16;
                    int cXMin = cViewX - 512;
                    int cXMax = cViewX + 512;
                    int cZMin = cViewZ - 512;
                    int cZMax = cViewZ + 512;
                    int countUpdated = 0;

                    for (int threadName = cXMin; threadName < cXMax; ++threadName) {
                        for (int cz = cZMin; cz < cZMax; ++cz) {
                            if (var23.chunkExists(threadName, cz)) {
                                Chunk c = var23.provideChunk(threadName, cz);

                                if (c != null && !(c instanceof EmptyChunk)) {
                                    int x0 = threadName << 4;
                                    int z0 = cz << 4;
                                    int x1 = x0 + 16;
                                    int z1 = z0 + 16;

                                    for (int x = x0; x < x1; ++x) {
                                        int z = z0;

                                        while (z < z1) {
                                            int posH = world.getPrecipitationHeight(x, z);
                                            int y = 0;

                                            while (true) {
                                                if (y < posH) {
                                                    Block block = world.getBlock(x, y, z);

                                                    if (block.getMaterial() != Material.water) {
                                                        ++y;
                                                        continue;
                                                    }

                                                    world.markBlocksDirtyVertical(x, z, y, posH);
                                                    ++countUpdated;
                                                }

                                                ++z;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (countUpdated > 0) {
                        String var24 = "server";

                        if (Config.isMinecraftThread()) {
                            var24 = "client";
                        }

                        Config.dbg("ClearWater (" + var24 + ") relighted " + countUpdated + " chunks");
                    }
                }
            }
        }
    }
}
