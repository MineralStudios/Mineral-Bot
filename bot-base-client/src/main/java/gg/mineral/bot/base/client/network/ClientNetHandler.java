package gg.mineral.bot.base.client.network;

import com.google.common.base.Charsets;

import gg.mineral.bot.base.client.player.controller.BotController;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.val;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.world.WorldSettings;

public class ClientNetHandler extends NetHandlerPlayClient {

    public ClientNetHandler(Minecraft mc, GuiScreen guiScreen, NetworkManager netManager) {
        super(mc, guiScreen, netManager);
    }

    @Override
    public void handleJoinGame(S01PacketJoinGame packet) {
        val gameController = this.getGameController();
        gameController.playerController = new BotController(gameController, this);

        val clientWorldController = new WorldClient(gameController, this,
                new WorldSettings(0L, packet.func_149198_e(), false, packet.func_149195_d(),
                        packet.func_149196_i()),
                packet.func_149194_f(), packet.func_149192_g(), gameController.mcProfiler);

        setClientWorldController(clientWorldController);
        clientWorldController.isClient = true;
        gameController.loadWorld(clientWorldController);
        val thePlayer = gameController.thePlayer;

        if (thePlayer != null)
            thePlayer.dimension = packet.func_149194_f();
        gameController.displayGuiScreen(new GuiDownloadTerrain(gameController, this));
        if (thePlayer != null)
            thePlayer.setEntityId(packet.func_149197_c());
        this.currentServerMaxPlayers = packet.func_149193_h();
        gameController.playerController.setGameType(packet.func_149198_e());
        gameController.gameSettings.sendSettingsToServer();
        val netManager = this.getNetworkManager();
        netManager.scheduleOutboundPacket(
                new C17PacketCustomPayload("MC|Brand",
                        ClientBrandRetriever.getClientModName().getBytes(Charsets.UTF_8)),
                new GenericFutureListener[0]);
    }

}
