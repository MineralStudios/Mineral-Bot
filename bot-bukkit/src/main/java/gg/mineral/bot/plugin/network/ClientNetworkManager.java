package gg.mineral.bot.plugin.network;

import javax.crypto.SecretKey;

import gg.mineral.bot.base.client.player.ClientInstance;
import gg.mineral.bot.plugin.network.packet.Client2ServerTranslator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.IChatComponent;

public class ClientNetworkManager extends NetworkManager {

    private final Client2ServerTranslator translator;
    private boolean open = true;

    public ClientNetworkManager(Client2ServerTranslator translator, Minecraft mc) {
        super(mc, true);
        this.translator = translator;
    }

    @Override
    public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {
        this.setConnectionState(EnumConnectionState.HANDSHAKING);
    }

    @Override
    public void setConnectionState(EnumConnectionState state) {
        this.connectionState = state;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, Packet p_channelRead0_2_) {
        if (p_channelRead0_2_.hasPriority())
            p_channelRead0_2_.processPacket(this.getNetHandler());
        else
            this.receivedPacketsQueue.add(p_channelRead0_2_);
    }

    @Override
    public void scheduleOutboundPacket(Packet p_150725_1_,
            @SuppressWarnings("rawtypes") GenericFutureListener... p_150725_2_) {
        this.flushOutboundQueue();
        this.dispatchPacket(p_150725_1_, p_150725_2_);
    }

    @Override
    protected void dispatchPacket(final Packet packet,
            @SuppressWarnings("rawtypes") final GenericFutureListener[] p_150732_2_) {
        val newState = EnumConnectionState.func_150752_a(packet);
        val currState = this.connectionState;

        if (newState != currState)
            this.setConnectionState(newState);

        if (mc instanceof ClientInstance instance && (!mc.isMainThread() || instance.getLatency() > 0))
            instance.scheduleTask(() -> packet.processPacket(translator), instance.getLatency());
        else
            packet.processPacket(translator);
    }

    @Override
    protected void flushOutboundQueue() {
        while (!this.outboundPacketsQueue.isEmpty()) {
            val packet = this.outboundPacketsQueue
                    .poll();
            this.dispatchPacket(packet.field_150774_a, packet.field_150773_b);
        }
    }

    @Override
    public void processReceivedPackets() {
        this.flushOutboundQueue();
        val currConnectionState = this.connectionState;

        if (this.connectionState != currConnectionState) {
            if (this.connectionState != null)
                this.netHandler.onConnectionStateTransition(this.connectionState, currConnectionState);

            this.connectionState = currConnectionState;
        }

        if (this.netHandler != null) {
            for (int iterations = 1000; !this.receivedPacketsQueue.isEmpty() && iterations >= 0; --iterations) {
                val packet = this.receivedPacketsQueue.poll();
                packet.processPacket(this.netHandler);
            }

            this.netHandler.onNetworkTick();
        }

        // this.channel.flush();
    }

    @Override
    public void closeChannel(IChatComponent p_150718_1_) {
        if (mc instanceof ClientInstance instance)
            instance.shutdown();
        this.open = false;
        this.terminationReason = p_150718_1_;
    }

    @Override
    public boolean isLocalChannel() {
        return false;
    }

    @Override
    public void enableEncryption(SecretKey p_150727_1_) {
        this.encryptionEnabled = true;
    }

    @Override
    public boolean isChannelOpen() {
        return open;
    }

    @Override
    public void disableAutoRead() {
        // TODO: simulate auto read
    }
}
