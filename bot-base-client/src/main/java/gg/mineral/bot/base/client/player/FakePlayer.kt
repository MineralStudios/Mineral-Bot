package gg.mineral.bot.base.client.player

import gg.mineral.bot.api.entity.living.player.FakePlayer
import gg.mineral.bot.api.instance.ClientInstance
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityClientPlayerMP
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.stats.StatFileWriter
import net.minecraft.util.Session
import net.minecraft.world.World

class FakePlayer(
    instance: Minecraft, world: World, session: Session, netHandlerClient: NetHandlerPlayClient,
    statFileWriter: StatFileWriter
) :
    EntityClientPlayerMP(instance, world, session, netHandlerClient, statFileWriter), FakePlayer {
    override val clientInstance: ClientInstance
        get() = mc as ClientInstance
}
