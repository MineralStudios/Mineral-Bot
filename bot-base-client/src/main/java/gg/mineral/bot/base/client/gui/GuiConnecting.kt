package gg.mineral.bot.base.client.gui

import gg.mineral.bot.base.client.gui.GuiConnecting.ConnectFunction
import gg.mineral.bot.impl.thread.ThreadManager.asyncExecutor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiDisconnected
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.multiplayer.ServerAddress
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.network.NetHandlerLoginClient
import net.minecraft.client.resources.I18n
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.NetworkManager
import net.minecraft.network.handshake.client.C00Handshake
import net.minecraft.network.login.client.C00PacketLoginStart
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatComponentTranslation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.net.InetAddress
import java.net.UnknownHostException

open class GuiConnecting : GuiScreen {
    var networkManager: NetworkManager? = null

    var cancelled: Boolean = false
    private val previousScreen: GuiScreen
    private val ip: String
    private val port: Int

    var connectFunction: ConnectFunction
    private var serverData: ServerData? = null

    constructor(previousScreen: GuiScreen, mc: Minecraft?, serverData: ServerData) : super(mc) {
        val socketAddress = ServerAddress.fromServerIp(serverData.serverIP)
        this.serverData = serverData
        this.previousScreen = previousScreen
        this.ip = socketAddress.ip
        this.port = socketAddress.port

        this.connectFunction = ConnectFunction { ip: String, port: Int ->
            logger.info("Connecting to $ip, $port")
            asyncExecutor.execute {
                var iNetAddress: InetAddress? = null
                try {
                    if (this@GuiConnecting.cancelled) return@execute

                    iNetAddress = InetAddress.getByName(ip)

                    NetworkManager.provideLanClient(mc, iNetAddress, port).let {
                        this@GuiConnecting.networkManager = it
                        it
                            .setNetHandler(
                                NetHandlerLoginClient(
                                    it,
                                    this@GuiConnecting.mc, this@GuiConnecting.previousScreen
                                )
                            )
                        it.scheduleOutboundPacket(
                            C00Handshake(
                                5,
                                ip,
                                port,
                                EnumConnectionState.LOGIN
                            ),
                            *arrayOfNulls(0)
                        )
                        it.scheduleOutboundPacket(
                            C00PacketLoginStart(this@GuiConnecting.mc.session.gameProfile),
                            *arrayOfNulls(0)
                        )

                    }

                } catch (var5: UnknownHostException) {
                    if (this@GuiConnecting.cancelled) return@execute

                    logger.error("Couldn\'t connect to server", var5)
                    this@GuiConnecting.mc.displayGuiScreen(
                        GuiDisconnected(
                            this@GuiConnecting.mc,
                            this@GuiConnecting.previousScreen,
                            "connect.failed",
                            ChatComponentTranslation(
                                "disconnect.genericReason",
                                "Unknown host"
                            )
                        )
                    )
                } catch (e: Exception) {
                    if (this@GuiConnecting.cancelled) return@execute

                    logger.error("Couldn\'t connect to server", e)
                    var errorMessage = e.toString()

                    if (iNetAddress != null) errorMessage = errorMessage.replace(("$iNetAddress:$port").toRegex(), "")

                    this@GuiConnecting.mc
                        .displayGuiScreen(
                            GuiDisconnected(
                                this@GuiConnecting.mc,
                                this@GuiConnecting.previousScreen, "connect.failed",
                                ChatComponentTranslation(
                                    "disconnect.genericReason",
                                    errorMessage
                                )
                            )
                        )
                }
            }
        }
    }

    fun interface ConnectFunction {
        fun connect(ip: String, port: Int)
    }

    constructor(previousScreen: GuiScreen, mc: Minecraft?, ipArg: String, portArg: Int) : super(mc) {
        this.previousScreen = previousScreen
        this.ip = ipArg
        this.port = portArg

        this.connectFunction = ConnectFunction { ip: String, port: Int ->
            logger.info("Connecting to $ip, $port")
            asyncExecutor.execute {
                var iNetAddress: InetAddress? = null
                try {
                    if (this@GuiConnecting.cancelled) return@execute

                    iNetAddress = InetAddress.getByName(ip)
                    NetworkManager.provideLanClient(mc, iNetAddress, port).let {
                        this@GuiConnecting.networkManager = it
                        it
                            .setNetHandler(
                                NetHandlerLoginClient(
                                    it,
                                    this@GuiConnecting.mc, this@GuiConnecting.previousScreen
                                )
                            )
                        it.scheduleOutboundPacket(
                            C00Handshake(
                                5,
                                ip,
                                port,
                                EnumConnectionState.LOGIN
                            ),
                            *arrayOfNulls(0)
                        )
                        it.scheduleOutboundPacket(
                            C00PacketLoginStart(this@GuiConnecting.mc.session.gameProfile),
                            *arrayOfNulls(0)
                        )

                    }
                } catch (var5: UnknownHostException) {
                    if (this@GuiConnecting.cancelled) return@execute

                    logger.error("Couldn\'t connect to server", var5)
                    this@GuiConnecting.mc.displayGuiScreen(
                        GuiDisconnected(
                            this@GuiConnecting.mc,
                            this@GuiConnecting.previousScreen,
                            "connect.failed",
                            ChatComponentTranslation(
                                "disconnect.genericReason",
                                "Unknown host"
                            )
                        )
                    )
                } catch (e: Exception) {
                    if (this@GuiConnecting.cancelled) return@execute

                    logger.error("Couldn\'t connect to server", e)
                    var errorMessage = e.toString()

                    if (iNetAddress != null) errorMessage = errorMessage.replace(("$iNetAddress:$port").toRegex(), "")

                    this@GuiConnecting.mc
                        .displayGuiScreen(
                            GuiDisconnected(
                                this@GuiConnecting.mc,
                                this@GuiConnecting.previousScreen, "connect.failed",
                                ChatComponentTranslation(
                                    "disconnect.genericReason",
                                    errorMessage
                                )
                            )
                        )
                }
            }
        }
    }

    fun initConnectingGui() {
        if (serverData != null) mc.setServerData(serverData)

        mc.loadWorld(null as WorldClient?)
        this.connect(ip, port)
    }

    protected fun connect(ip: String, port: Int) {
        connectFunction.connect(ip, port)
    }

    override fun updateScreen() {
        if (this.networkManager == null) return

        if (networkManager!!.isChannelOpen) networkManager!!.processReceivedPackets()
        else if (networkManager!!.exitMessage != null) networkManager!!.netHandler.onDisconnect(
            networkManager!!.exitMessage
        )
    }

    override fun keyTyped(character: Char, p_73869_2_: Int) {
    }

    override fun initGui() {
        buttonList.clear()
        buttonList.add(
            GuiButton(
                this.mc, 0, this.width / 2 - 100, this.height / 2 + 50,
                I18n.format("gui.cancel", *arrayOfNulls(0))
            )
        )
    }

    override fun actionPerformed(button: GuiButton) {
        if (button.id == 0) {
            this.cancelled = true

            if (this.networkManager != null) networkManager!!.closeChannel(ChatComponentText("Aborted"))

            mc.displayGuiScreen(this.previousScreen)
        }
    }

    override fun drawScreen(p_73863_1_: Int, p_73863_2_: Int, p_73863_3_: Float) {
        this.drawDefaultBackground()

        if (this.networkManager == null) this.drawCenteredString(
            this.fontRendererObj, I18n.format("connect.connecting", *arrayOfNulls(0)),
            this.width / 2, this.height / 2 - 50, 16777215
        )
        else this.drawCenteredString(
            this.fontRendererObj, I18n.format("connect.authorizing", *arrayOfNulls(0)),
            this.width / 2, this.height / 2 - 50, 16777215
        )

        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_)
    }

    companion object {
        private val logger: Logger = LogManager.getLogger(
            GuiConnecting::class.java
        )
    }
}
