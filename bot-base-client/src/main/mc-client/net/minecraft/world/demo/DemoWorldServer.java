package net.minecraft.world.demo;

import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveHandler;

public class DemoWorldServer extends WorldServer {
    private static final long demoWorldSeed = (long) "North Carolina".hashCode();
    public static final WorldSettings demoWorldSettings = (new WorldSettings(demoWorldSeed,
            WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT)).enableBonusChest();

    public DemoWorldServer(Minecraft mc, MinecraftServer p_i45282_1_, ISaveHandler p_i45282_2_, String p_i45282_3_,
            int p_i45282_4_, Profiler p_i45282_5_) {
        super(mc, p_i45282_1_, p_i45282_2_, p_i45282_3_, p_i45282_4_, demoWorldSettings, p_i45282_5_);
    }
}
