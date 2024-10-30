package gg.mineral.bot.base.client.player.controller;

import gg.mineral.bot.base.client.player.FakePlayer;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.world.World;

public class BotController extends PlayerControllerMP {

    public BotController(Minecraft mc, NetHandlerPlayClient netHandler) {
        super(mc, netHandler);
    }

    @Override
    public EntityClientPlayerMP createClientPlayerMP(World world, StatFileWriter statFileWriter) {
        val mc = this.getMc();
        val netClientHandler = this.getNetClientHandler();
        return new FakePlayer(mc, world, mc.getSession(), netClientHandler, statFileWriter);
    }

}
