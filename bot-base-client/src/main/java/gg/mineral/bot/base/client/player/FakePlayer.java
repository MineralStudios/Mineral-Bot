package gg.mineral.bot.base.client.player;

import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import gg.mineral.bot.api.instance.ClientInstance;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.Session;
import net.minecraft.world.World;

public class FakePlayer extends EntityClientPlayerMP implements gg.mineral.bot.api.entity.living.player.FakePlayer {
    private final Set<UUID> friendlyEntityUUIDS = new ObjectOpenHashSet<>();

    public FakePlayer(Minecraft instance, World world, Session session, NetHandlerPlayClient netHandlerClient,
            StatFileWriter statFileWriter) {
        super(instance, world, session, netHandlerClient, statFileWriter);
    }

    @Override
    public Set<UUID> getFriendlyEntityUUIDs() {
        return friendlyEntityUUIDS;
    }

    @Override
    @Nullable
    public ClientInstance getClientInstance() {
        return this.mc instanceof ClientInstance instance ? instance : null;
    }
}
