package net.minecraft.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerHandshakeMemory;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerHandshakeTCP;
import net.minecraft.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class NetworkSystem {
    private static final Logger logger = LogManager.getLogger(NetworkSystem.class);
    private static final EventLoopGroup eventLoops = Epoll.isAvailable()
            ? new EpollEventLoopGroup(0,
            (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll #%d").setDaemon(true).build())
            : KQueue.isAvailable() ? new KQueueEventLoopGroup(0,
            (new ThreadFactoryBuilder()).setNameFormat("Netty KQueue #%d").setDaemon(true).build())
            : new NioEventLoopGroup(0,
            (new ThreadFactoryBuilder()).setNameFormat("Netty IO #%d").setDaemon(true).build());

    /**
     * Reference to the MinecraftServer object.
     */
    private final MinecraftServer mcServer;

    /**
     * True if this NetworkSystem has never had his endpoints terminated
     */
    public volatile boolean isAlive;

    /**
     * Contains all endpoints added to this NetworkSystem
     */
    private final List endpoints = Collections.synchronizedList(new ArrayList());

    /**
     * A list containing all NetworkManager instances of all endpoints
     */
    private final List networkManagers = Collections.synchronizedList(new ArrayList());

    private final Minecraft mc;

    public NetworkSystem(Minecraft mc, MinecraftServer p_i45292_1_) {
        this.mcServer = p_i45292_1_;
        this.mc = mc;
        this.isAlive = true;
    }

    /**
     * Adds a channel that listens on publicly accessible network ports
     */
    public void addLanEndpoint(InetAddress p_151265_1_, int p_151265_2_) throws IOException {
        List var3 = this.endpoints;

        synchronized (this.endpoints) {
            this.endpoints.add(
                    ((ServerBootstrap) ((ServerBootstrap) (new ServerBootstrap()).channel(
                            Epoll.isAvailable() ? EpollServerSocketChannel.class
                                    : KQueue.isAvailable() ? KQueueServerSocketChannel.class
                                    : NioServerSocketChannel.class))
                            .childHandler(new ChannelInitializer() {

                                protected void initChannel(Channel p_initChannel_1_) {
                                    try {
                                        p_initChannel_1_.config().setOption(
                                                ChannelOption.IP_TOS,
                                                Integer.valueOf(24));
                                    } catch (ChannelException var4) {
                                        ;
                                    }

                                    try {
                                        p_initChannel_1_.config()
                                                .setOption(ChannelOption.TCP_NODELAY, true);
                                    } catch (ChannelException var3) {
                                        ;
                                    }

                                    p_initChannel_1_.pipeline()
                                            .addLast("timeout", new ReadTimeoutHandler(30))
                                            .addLast("legacy_query",
                                                    new PingResponseHandler(NetworkSystem.this))
                                            .addLast("splitter", new MessageDeserializer2())
                                            .addLast("decoder",
                                                    new MessageDeserializer(
                                                            NetworkManager.field_152462_h, mc))
                                            .addLast("prepender", new MessageSerializer2())
                                            .addLast("encoder",
                                                    new MessageSerializer(
                                                            NetworkManager.field_152462_h));
                                    NetworkManager var2 = new NetworkManager(
                                            NetworkSystem.this.mc, false);
                                    NetworkSystem.this.networkManagers.add(var2);
                                    p_initChannel_1_.pipeline().addLast("packet_handler", var2);
                                    var2.setNetHandler(
                                            new NetHandlerHandshakeTCP(
                                                    NetworkSystem.this.mcServer, var2));
                                }
                            }).group(eventLoops).localAddress(p_151265_1_, p_151265_2_)).bind()
                            .syncUninterruptibly());
        }
    }

    /**
     * Adds a channel that listens locally
     */
    public SocketAddress addLocalEndpoint() {
        List var2 = this.endpoints;
        ChannelFuture var1;

        synchronized (this.endpoints) {
            var1 = ((ServerBootstrap) ((ServerBootstrap) (new ServerBootstrap()).channel(LocalServerChannel.class))
                    .childHandler(new ChannelInitializer() {

                        protected void initChannel(Channel p_initChannel_1_) {
                            NetworkManager var2 = new NetworkManager(NetworkSystem.this.mc, false);
                            var2.setNetHandler(new NetHandlerHandshakeMemory(NetworkSystem.this.mcServer, var2));
                            NetworkSystem.this.networkManagers.add(var2);
                            p_initChannel_1_.pipeline().addLast("packet_handler", var2);
                        }
                    }).group(eventLoops).localAddress(LocalAddress.ANY)).bind().syncUninterruptibly();
            this.endpoints.add(var1);
        }

        return var1.channel().localAddress();
    }

    /**
     * Shuts down all open endpoints (with immediate effect?)
     */
    public void terminateEndpoints() {
        this.isAlive = false;
        Iterator var1 = this.endpoints.iterator();

        while (var1.hasNext()) {
            ChannelFuture var2 = (ChannelFuture) var1.next();
            var2.channel().close().syncUninterruptibly();
        }
    }

    /**
     * Will try to process the packets received by each NetworkManager, gracefully
     * manage processing failures and cleans
     * up dead connections
     */
    public void networkTick() {
        List var1 = this.networkManagers;

        synchronized (this.networkManagers) {
            Iterator var2 = this.networkManagers.iterator();

            while (var2.hasNext()) {
                final NetworkManager var3 = (NetworkManager) var2.next();

                if (!var3.isChannelOpen()) {
                    var2.remove();

                    if (var3.getExitMessage() != null) {
                        var3.getNetHandler().onDisconnect(var3.getExitMessage());
                    } else if (var3.getNetHandler() != null) {
                        var3.getNetHandler().onDisconnect(new ChatComponentText("Disconnected"));
                    }
                } else {
                    try {
                        var3.processReceivedPackets();
                    } catch (Exception var8) {
                        if (var3.isLocalChannel()) {
                            CrashReport var10 = CrashReport.makeCrashReport(var8, "Ticking memory connection");
                            CrashReportCategory var6 = var10.makeCategory("Ticking connection");
                            var6.addCrashSectionCallable("Connection", () -> var3.toString());
                            throw new ReportedException(var10);
                        }

                        logger.warn("Failed to handle packet for " + var3.getSocketAddress(), var8);
                        final ChatComponentText var5 = new ChatComponentText("Internal server error");
                        var3.scheduleOutboundPacket(new S40PacketDisconnect(var5),
                                new GenericFutureListener[]{new GenericFutureListener() {

                                    public void operationComplete(Future p_operationComplete_1_) {
                                        var3.closeChannel(var5);
                                    }
                                }
                                });
                        var3.disableAutoRead();
                    }
                }
            }
        }
    }

    public MinecraftServer func_151267_d() {
        return this.mcServer;
    }
}
