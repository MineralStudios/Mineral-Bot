package gg.mineral.bot.base.client.network

import com.google.common.base.Charsets
import gg.mineral.bot.base.client.player.controller.BotController
import net.minecraft.client.ClientBrandRetriever
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiDownloadTerrain
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.client.C17PacketCustomPayload
import net.minecraft.network.play.server.S01PacketJoinGame
import net.minecraft.world.WorldSettings

class ClientNetHandler(mc: Minecraft, guiScreen: GuiScreen?, netManager: NetworkManager) :
    NetHandlerPlayClient(mc, guiScreen, netManager) {
    override fun handleJoinGame(packet: S01PacketJoinGame) {
        val gameController = this.gameController
        gameController.playerController = BotController(
            gameController,
            this
        )

        val clientWorldController = WorldClient(
            gameController, this,
            WorldSettings(
                0L, packet.func_149198_e(), false, packet.func_149195_d(),
                packet.func_149196_i()
            ),
            packet.func_149194_f(), packet.func_149192_g(), gameController.mcProfiler
        )

        setClientWorldController(clientWorldController)
        clientWorldController.isClient = true
        gameController.loadWorld(clientWorldController)
        val thePlayer = gameController.thePlayer

        if (thePlayer != null) thePlayer.dimension = packet.func_149194_f()
        gameController.displayGuiScreen(GuiDownloadTerrain(gameController, this))
        thePlayer?.setEntityId(packet.func_149197_c())
        this.currentServerMaxPlayers = packet.func_149193_h()
        gameController.playerController.setGameType(packet.func_149198_e())
        gameController.gameSettings.sendSettingsToServer()
        val netManager = this.networkManager
        netManager.scheduleOutboundPacket(
            C17PacketCustomPayload(
                "MC|Brand",
                ClientBrandRetriever.getClientModName().toByteArray(Charsets.UTF_8)
            )
        )
    }
}
