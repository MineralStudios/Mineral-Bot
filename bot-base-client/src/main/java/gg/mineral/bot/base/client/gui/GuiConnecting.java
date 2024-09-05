package gg.mineral.bot.base.client.gui;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gg.mineral.bot.impl.thread.ThreadManager;
import net.minecraft.client.Minecraft;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Setter;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class GuiConnecting extends GuiScreen {
    private static final Logger logger = LogManager.getLogger(GuiConnecting.class);
    public NetworkManager networkManager;
    protected boolean cancelled;
    public final GuiScreen previousScreen;
    private final String ip;
    private final int port;
    @Setter
    private ConnectFunction connectFunction;
    private ServerData serverData;

    public GuiConnecting(GuiScreen previousScreen, Minecraft mc, ServerData serverData) {
        super(mc);
        ServerAddress socketAddress = ServerAddress.fromServerIp(serverData.serverIP);
        this.serverData = serverData;
        this.previousScreen = previousScreen;
        this.ip = socketAddress.getIP();
        this.port = socketAddress.getPort();

        this.connectFunction = (ip, port) -> {
            logger.info("Connecting to " + ip + ", " + port);
            ThreadManager.getAsyncExecutor().execute(() -> {
                InetAddress iNetAddress = null;

                try {
                    if (GuiConnecting.this.cancelled)
                        return;

                    iNetAddress = InetAddress.getByName(ip);
                    GuiConnecting.this.networkManager = NetworkManager.provideLanClient(mc, iNetAddress, port);
                    GuiConnecting.this.networkManager
                            .setNetHandler(new NetHandlerLoginClient(GuiConnecting.this.networkManager,
                                    GuiConnecting.this.mc, GuiConnecting.this.previousScreen));
                    GuiConnecting.this.networkManager.scheduleOutboundPacket(
                            new C00Handshake(5, ip, port, EnumConnectionState.LOGIN),
                            new GenericFutureListener[0]);
                    GuiConnecting.this.networkManager.scheduleOutboundPacket(
                            new C00PacketLoginStart(GuiConnecting.this.mc.getSession().getGameProfile()),
                            new GenericFutureListener[0]);
                } catch (UnknownHostException var5) {
                    if (GuiConnecting.this.cancelled)
                        return;

                    GuiConnecting.logger.error("Couldn\'t connect to server", var5);
                    GuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnecting.this.mc,
                            GuiConnecting.this.previousScreen,
                            "connect.failed",
                            new ChatComponentTranslation("disconnect.genericReason", new Object[] { "Unknown host" })));
                } catch (Exception e) {
                    if (GuiConnecting.this.cancelled)
                        return;

                    GuiConnecting.logger.error("Couldn\'t connect to server", e);
                    String errorMessage = e.toString();

                    if (iNetAddress != null)
                        errorMessage = errorMessage.replaceAll(iNetAddress.toString() + ":" + port, "");

                    GuiConnecting.this.mc
                            .displayGuiScreen(new GuiDisconnected(GuiConnecting.this.mc,
                                    GuiConnecting.this.previousScreen, "connect.failed",
                                    new ChatComponentTranslation("disconnect.genericReason",
                                            new Object[] { errorMessage })));
                }
            });
        };

    }

    @FunctionalInterface
    public interface ConnectFunction {
        void connect(String ip, int port);
    }

    public GuiConnecting(GuiScreen previousScreen, Minecraft mc, String ipArg, int portArg) {
        super(mc);
        this.previousScreen = previousScreen;
        this.ip = ipArg;
        this.port = portArg;

        this.connectFunction = (ip, port) -> {
            logger.info("Connecting to " + ip + ", " + port);
            ThreadManager.getAsyncExecutor().execute(() -> {
                InetAddress iNetAddress = null;

                try {
                    if (GuiConnecting.this.cancelled)
                        return;

                    iNetAddress = InetAddress.getByName(ip);
                    GuiConnecting.this.networkManager = NetworkManager.provideLanClient(mc, iNetAddress, port);
                    GuiConnecting.this.networkManager
                            .setNetHandler(new NetHandlerLoginClient(GuiConnecting.this.networkManager,
                                    GuiConnecting.this.mc, GuiConnecting.this.previousScreen));
                    GuiConnecting.this.networkManager.scheduleOutboundPacket(
                            new C00Handshake(5, ip, port, EnumConnectionState.LOGIN),
                            new GenericFutureListener[0]);
                    GuiConnecting.this.networkManager.scheduleOutboundPacket(
                            new C00PacketLoginStart(GuiConnecting.this.mc.getSession().getGameProfile()),
                            new GenericFutureListener[0]);
                } catch (UnknownHostException var5) {
                    if (GuiConnecting.this.cancelled)
                        return;

                    GuiConnecting.logger.error("Couldn\'t connect to server", var5);
                    GuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnecting.this.mc,
                            GuiConnecting.this.previousScreen,
                            "connect.failed",
                            new ChatComponentTranslation("disconnect.genericReason", new Object[] { "Unknown host" })));
                } catch (Exception e) {
                    if (GuiConnecting.this.cancelled)
                        return;

                    GuiConnecting.logger.error("Couldn\'t connect to server", e);
                    String errorMessage = e.toString();

                    if (iNetAddress != null)
                        errorMessage = errorMessage.replaceAll(iNetAddress.toString() + ":" + port, "");

                    GuiConnecting.this.mc
                            .displayGuiScreen(new GuiDisconnected(GuiConnecting.this.mc,
                                    GuiConnecting.this.previousScreen, "connect.failed",
                                    new ChatComponentTranslation("disconnect.genericReason",
                                            new Object[] { errorMessage })));
                }
            });
        };
    }

    public void initConnectingGui() {
        if (serverData != null)
            mc.setServerData(serverData);

        mc.loadWorld((WorldClient) null);
        this.connect(ip, port);
    }

    protected void connect(final String ip, final int port) {
        this.connectFunction.connect(ip, port);
    }

    @Override
    public void updateScreen() {
        if (this.networkManager == null)
            return;

        if (this.networkManager.isChannelOpen())
            this.networkManager.processReceivedPackets();
        else if (this.networkManager.getExitMessage() != null)
            this.networkManager.getNetHandler().onDisconnect(this.networkManager.getExitMessage());
    }

    @Override
    protected void keyTyped(char character, int p_73869_2_) {
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(
                new GuiButton(this.mc, 0, this.width / 2 - 100, this.height / 2 + 50,
                        I18n.format("gui.cancel", new Object[0])));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.cancelled = true;

            if (this.networkManager != null) {
                this.networkManager.closeChannel(new ChatComponentText("Aborted"));
            }

            this.mc.displayGuiScreen(this.previousScreen);
        }
    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        this.drawDefaultBackground();

        if (this.networkManager == null)
            this.drawCenteredString(this.fontRendererObj, I18n.format("connect.connecting", new Object[0]),
                    this.width / 2, this.height / 2 - 50, 16777215);
        else
            this.drawCenteredString(this.fontRendererObj, I18n.format("connect.authorizing", new Object[0]),
                    this.width / 2, this.height / 2 - 50, 16777215);

        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }
}
