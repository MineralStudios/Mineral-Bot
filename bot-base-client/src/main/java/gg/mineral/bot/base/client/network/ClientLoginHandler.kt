package gg.mineral.bot.base.client.network;

import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerLoginClient;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;

public class ClientLoginHandler extends NetHandlerLoginClient {

    public ClientLoginHandler(NetworkManager netManager, Minecraft mc, GuiScreen guiScreen) {
        super(netManager, mc, guiScreen);
    }

    @Override
    public void onConnectionStateTransition(EnumConnectionState prevState, EnumConnectionState newState) {
        getLogger().debug("Switching protocol from " + prevState + " to " + newState);

        val networkManager = this.getNetworkManager();

        if (newState == EnumConnectionState.PLAY)
            networkManager.setNetHandler(
                    new ClientNetHandler(this.getMc(), this.getGuiScreen(), networkManager));
    }

}
