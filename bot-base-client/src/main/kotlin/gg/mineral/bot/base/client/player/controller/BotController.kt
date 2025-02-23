package gg.mineral.bot.base.client.player.controller

import gg.mineral.bot.base.client.player.FakePlayer
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityClientPlayerMP
import net.minecraft.client.multiplayer.PlayerControllerMP
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.stats.StatFileWriter
import net.minecraft.world.World

class BotController(mc: Minecraft, netHandler: NetHandlerPlayClient) :
    PlayerControllerMP(mc, netHandler) {
    override fun createClientPlayerMP(world: World, statFileWriter: StatFileWriter): EntityClientPlayerMP {
        val mc = this.mc
        val netClientHandler = this.netClientHandler
        return FakePlayer(mc, world, mc.session, netClientHandler, statFileWriter)
    }
}
