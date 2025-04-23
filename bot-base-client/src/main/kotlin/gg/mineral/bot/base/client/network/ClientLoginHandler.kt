package gg.mineral.bot.base.client.network

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.network.NetHandlerLoginClient
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.NetworkManager

class ClientLoginHandler(netManager: NetworkManager, mc: Minecraft, guiScreen: GuiScreen? = null) :
    NetHandlerLoginClient(netManager, mc, guiScreen) {
    override fun onConnectionStateTransition(prevState: EnumConnectionState, newState: EnumConnectionState) {
        val networkManager = this.networkManager

        if (newState === EnumConnectionState.PLAY) networkManager.netHandler =
            ClientNetHandler(
                this.mc,
                guiScreen,
                networkManager
            )
    }
}
