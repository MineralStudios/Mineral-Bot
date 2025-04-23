package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import gg.mineral.bot.base.client.netty.LatencySimulatorHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.*;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;

public class NetworkManager extends SimpleChannelInboundHandler<Packet> {
    public static final Marker logMarkerNetwork = MarkerManager.getMarker("NETWORK");
    public static final Marker logMarkerPackets = MarkerManager.getMarker("NETWORK_PACKETS", logMarkerNetwork);
    public static final Marker field_152461_c = MarkerManager.getMarker("NETWORK_STAT", logMarkerNetwork);
    public static final AttributeKey<EnumConnectionState> attrKeyConnectionState = AttributeKey.valueOf("protocol");
    public static final AttributeKey<BiMap> attrKeyReceivable = AttributeKey.valueOf("receivable_packets");
    public static final AttributeKey attrKeySendable = AttributeKey.valueOf("sendable_packets");
    public static final EventLoopGroup eventLoops = Epoll.isAvailable()
            ? new EpollEventLoopGroup(0,
            (new ThreadFactoryBuilder()).setNameFormat("Netty Client Epoll #%d").setDaemon(true).build())
            : KQueue.isAvailable() ? new KQueueEventLoopGroup(0,
            (new ThreadFactoryBuilder()).setNameFormat("Netty Client KQueue #%d").setDaemon(true).build())
            : new NioEventLoopGroup(0,
            (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
    public static final NetworkStatistics field_152462_h = new NetworkStatistics();
    private static final Logger logger = LogManager.getLogger(NetworkManager.class);
    /**
     * The queue for received, unprioritized packets that will be processed at the
     * earliest opportunity
     */
    protected final Queue<Packet> receivedPacketsQueue = Queues.newConcurrentLinkedQueue();
    /**
     * The queue for packets that require transmission
     */
    protected final Queue<NetworkManager.InboundHandlerTuplePacketListener> outboundPacketsQueue = Queues
            .newConcurrentLinkedQueue();
    protected final Minecraft mc;
    /**
     * Whether this NetworkManager deals with the client or server side of the
     * connection
     */
    private final boolean isClientSide;
    /**
     * The INetHandler instance responsible for processing received packets
     */
    @Getter
    protected INetHandler netHandler;
    /**
     * The current connection state, being one of: HANDSHAKING, PLAY, STATUS, LOGIN
     */
    protected EnumConnectionState connectionState;
    /**
     * A String indicating why the network has shutdown.
     */
    protected IChatComponent terminationReason;
    protected boolean encryptionEnabled;
    /**
     * The active channel
     */
    @Getter
    private Channel channel;
    /**
     * The address of the remote party
     * -- GETTER --
     * Return the InetSocketAddress of the remote endpoint
     */
    @Getter
    private SocketAddress socketAddress;

    public NetworkManager(Minecraft mc, boolean isClientSide) {
        this.isClientSide = isClientSide;
        this.mc = mc;
    }

    /**
     * Prepares a clientside NetworkManager: establishes a connection to the address
     * and port supplied and configures
     * the channel pipeline. Returns the newly created instance.
     */
    public static NetworkManager provideLanClient(Minecraft mc, InetAddress p_150726_0_, int p_150726_1_) {
        final NetworkManager var2 = new NetworkManager(mc, true);
        (new Bootstrap()).group(eventLoops).handler(new ChannelInitializer() {

                    protected void initChannel(Channel p_initChannel_1_) {
                        try {
                            p_initChannel_1_.config().setOption(ChannelOption.IP_TOS, Integer.valueOf(24));
                        } catch (ChannelException var4) {
                        }

                        try {
                            p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, true);
                        } catch (ChannelException var3) {
                        }

                        p_initChannel_1_.pipeline().addLast("timeout", new ReadTimeoutHandler(20))
                                .addLast("latency_simulator", new LatencySimulatorHandler(mc))
                                .addLast("splitter", new MessageDeserializer2())
                                .addLast("decoder", new MessageDeserializer(NetworkManager.field_152462_h, mc))
                                .addLast("prepender", new MessageSerializer2())
                                .addLast("encoder", new MessageSerializer(NetworkManager.field_152462_h))
                                .addLast("packet_handler", var2);
                    }
                }).channel(Epoll.isAvailable() ? EpollSocketChannel.class
                        : KQueue.isAvailable() ? KQueueSocketChannel.class
                        : NioSocketChannel.class)
                .connect(p_150726_0_, p_150726_1_).syncUninterruptibly();
        return var2;
    }

    /**
     * Prepares a clientside NetworkManager: establishes a connection to the socket
     * supplied and configures the channel
     * pipeline. Returns the newly created instance.
     */
    public static NetworkManager provideLocalClient(Minecraft mc, SocketAddress p_150722_0_) {
        final NetworkManager var1 = new NetworkManager(mc, true);
        (new Bootstrap()).group(eventLoops).handler(new ChannelInitializer() {

            protected void initChannel(Channel p_initChannel_1_) {
                p_initChannel_1_.pipeline().addLast("packet_handler", var1);
            }
        }).channel(LocalChannel.class).connect(p_150722_0_).syncUninterruptibly();
        return var1;
    }

    @Override
    public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {
        super.channelActive(p_channelActive_1_);
        this.channel = p_channelActive_1_.channel();
        this.socketAddress = this.channel.remoteAddress();
        this.setConnectionState(EnumConnectionState.HANDSHAKING);
    }

    /**
     * Sets the new connection state and registers which packets this channel may
     * send and receive
     */
    public void setConnectionState(EnumConnectionState state) {
        this.connectionState = this.channel.attr(attrKeyConnectionState).getAndSet(state);
        this.channel.attr(attrKeyReceivable).set(state.func_150757_a(this.isClientSide));
        this.channel.attr(attrKeySendable).set(state.func_150754_b(this.isClientSide));
        this.channel.config().setAutoRead(true);
        logger.debug("Enabled auto read");
    }

    public void channelInactive(@NotNull ChannelHandlerContext p_channelInactive_1_) {
        this.closeChannel(new ChatComponentTranslation("disconnect.endOfStream"));
    }

    public void exceptionCaught(@NotNull ChannelHandlerContext p_exceptionCaught_1_, @NotNull Throwable p_exceptionCaught_2_) {
        ChatComponentTranslation var3;

        if (p_exceptionCaught_2_ instanceof TimeoutException) {
            var3 = new ChatComponentTranslation("disconnect.timeout");
        } else {
            var3 = new ChatComponentTranslation("disconnect.genericReason",
                    "Internal Exception: " + p_exceptionCaught_2_);
        }

        p_exceptionCaught_2_.printStackTrace();

        this.closeChannel(var3);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, Packet p_channelRead0_2_) {
        if (this.channel.isOpen()) {
            if (p_channelRead0_2_.hasPriority()) {
                p_channelRead0_2_.processPacket(this.netHandler);
            } else {
                this.receivedPacketsQueue.add(p_channelRead0_2_);
            }
        }
    }

    /**
     * Sets the NetHandler for this NetworkManager, no checks are made if this
     * handler is suitable for the particular
     * connection state (protocol)
     */
    public void setNetHandler(INetHandler p_150719_1_) {
        Validate.notNull(p_150719_1_, "packetListener");
        logger.debug("Set listener of {} to {}", new Object[]{this, p_150719_1_});
        this.netHandler = p_150719_1_;
    }

    /**
     * Will flush the outbound queue and dispatch the supplied Packet if the channel
     * is ready, otherwise it adds the
     * packet to the outbound queue and registers the GenericFutureListener to fire
     * after transmission
     */
    public void scheduleOutboundPacket(Packet p_150725_1_, GenericFutureListener... p_150725_2_) {
        if (this.channel != null && this.channel.isOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(p_150725_1_, p_150725_2_);
        } else {
            this.outboundPacketsQueue
                    .add(new NetworkManager.InboundHandlerTuplePacketListener(p_150725_1_, p_150725_2_));
        }
    }

    /**
     * Will commit the packet to the channel. If the current thread 'owns' the
     * channel it will write and flush the
     * packet, otherwise it will add a task for the channel eventloop thread to do
     * that.
     */
    protected void dispatchPacket(final Packet p_150732_1_, final GenericFutureListener[] p_150732_2_) {
        final EnumConnectionState var3 = EnumConnectionState.func_150752_a(p_150732_1_);
        final EnumConnectionState var4 = this.channel.attr(attrKeyConnectionState).get();

        if (var4 != var3) {
            logger.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }

        if (this.channel.eventLoop().inEventLoop()) {
            if (var3 != var4) {
                this.setConnectionState(var3);
            }

            this.channel.writeAndFlush(p_150732_1_).addListeners(p_150732_2_)
                    .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.channel.eventLoop().execute(() -> {
                if (var3 != var4) {
                    NetworkManager.this.setConnectionState(var3);
                }

                NetworkManager.this.channel.writeAndFlush(p_150732_1_).addListeners(p_150732_2_)
                        .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            });
        }
    }

    /**
     * Will iterate through the outboundPacketQueue and dispatch all Packets
     */
    protected void flushOutboundQueue() {
        if (this.channel != null && this.channel.isOpen()) {
            while (!this.outboundPacketsQueue.isEmpty()) {
                NetworkManager.InboundHandlerTuplePacketListener var1 = this.outboundPacketsQueue
                        .poll();
                this.dispatchPacket(var1.field_150774_a, var1.field_150773_b);
            }
        }
    }

    /**
     * Checks timeouts and processes all packets received
     */
    public void processReceivedPackets() {
        this.flushOutboundQueue();
        EnumConnectionState var1 = this.channel.attr(attrKeyConnectionState).get();

        if (this.connectionState != var1) {
            if (this.connectionState != null) {
                this.netHandler.onConnectionStateTransition(this.connectionState, var1);
            }

            this.connectionState = var1;
        }

        if (this.netHandler != null) {
            for (int var2 = 1000; !this.receivedPacketsQueue.isEmpty() && var2 >= 0; --var2) {
                Packet var3 = this.receivedPacketsQueue.poll();
                var3.processPacket(this.netHandler);
            }

            this.netHandler.onNetworkTick();
        }

        this.channel.flush();
    }

    /**
     * Closes the channel, the parameter can be used for an exit message (not
     * certain how it gets sent)
     */
    public void closeChannel(IChatComponent p_150718_1_) {
        if (this.channel.isOpen()) {
            this.channel.close();
            this.terminationReason = p_150718_1_;
        }
    }

    /**
     * True if this NetworkManager uses a memory connection (single player game).
     * False may imply both an active TCP
     * connection or simply no active connection at all
     */
    public boolean isLocalChannel() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    /**
     * Adds an encoder+decoder to the channel pipeline. The parameter is the secret
     * key used for encrypted communication
     */
    public void enableEncryption(SecretKey p_150727_1_) {
        this.channel.pipeline().addBefore("splitter", "decrypt",
                new NettyEncryptingDecoder(CryptManager.func_151229_a(2, p_150727_1_)));
        this.channel.pipeline().addBefore("prepender", "encrypt",
                new NettyEncryptingEncoder(CryptManager.func_151229_a(1, p_150727_1_)));
        this.encryptionEnabled = true;
    }

    /**
     * Returns true if this NetworkManager has an active channel, false otherwise
     */
    public boolean isChannelOpen() {
        return this.channel != null && this.channel.isOpen();
    }

    /**
     * If this channel is closed, returns the exit message, null otherwise.
     */
    public IChatComponent getExitMessage() {
        return this.terminationReason;
    }

    /**
     * Switches the channel to manual reading modus
     */
    public void disableAutoRead() {
        this.channel.config().setAutoRead(false);
    }

    public static class InboundHandlerTuplePacketListener {
        public final Packet field_150774_a;
        public final GenericFutureListener[] field_150773_b;

        public InboundHandlerTuplePacketListener(Packet p_i45146_1_, GenericFutureListener... p_i45146_2_) {
            this.field_150774_a = p_i45146_1_;
            this.field_150773_b = p_i45146_2_;
        }
    }
}
