package gg.mineral.bot.base.client.player;

import gg.mineral.bot.api.instance.ClientInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.Session;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class FakePlayer extends EntityClientPlayerMP implements gg.mineral.bot.api.entity.living.player.FakePlayer {

    public FakePlayer(Minecraft instance, World world, Session session, NetHandlerPlayClient netHandlerClient,
                      StatFileWriter statFileWriter) {
        super(instance, world, session, netHandlerClient, statFileWriter);
    }

    @Override
    @Nullable
    public ClientInstance getClientInstance() {
        return this.mc instanceof ClientInstance instance ? instance : null;
    }
}
