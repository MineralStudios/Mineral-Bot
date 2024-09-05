package gg.mineral.bot.plugin.network;

import javax.crypto.SecretKey;

import gg.mineral.bot.api.BotAPI;
import gg.mineral.bot.base.client.player.FakePlayerInstance;
import gg.mineral.bot.plugin.network.packet.Client2ServerTranslator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.GenericFutureListener;
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

    @SuppressWarnings("unchecked")
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
    protected void dispatchPacket(final Packet p_150732_1_,
            @SuppressWarnings("rawtypes") final GenericFutureListener[] p_150732_2_) {
        final EnumConnectionState var3 = EnumConnectionState.func_150752_a(p_150732_1_);
        final EnumConnectionState var4 = this.connectionState;

        if (var3 != var4)
            this.setConnectionState(var3);

        if (mc instanceof FakePlayerInstance instance && (!mc.isMainThread() || instance.getLatency() > 0))
            instance.schedulePacket(() -> p_150732_1_.processPacket(translator), instance.getLatency());
        else
            p_150732_1_.processPacket(translator);
    }

    @Override
    protected void flushOutboundQueue() {
        while (!this.outboundPacketsQueue.isEmpty()) {
            NetworkManager.InboundHandlerTuplePacketListener var1 = (NetworkManager.InboundHandlerTuplePacketListener) this.outboundPacketsQueue
                    .poll();
            this.dispatchPacket(var1.field_150774_a, var1.field_150773_b);
        }
    }

    @Override
    public void processReceivedPackets() {
        this.flushOutboundQueue();
        EnumConnectionState var1 = this.connectionState;

        if (this.connectionState != var1) {
            if (this.connectionState != null)
                this.netHandler.onConnectionStateTransition(this.connectionState, var1);

            this.connectionState = var1;
        }

        if (this.netHandler != null) {
            for (int var2 = 1000; !this.receivedPacketsQueue.isEmpty() && var2 >= 0; --var2) {
                Packet var3 = (Packet) this.receivedPacketsQueue.poll();
                var3.processPacket(this.netHandler);
            }

            this.netHandler.onNetworkTick();
        }

        // this.channel.flush();
    }

    @Override
    public void closeChannel(IChatComponent p_150718_1_) {
        if (mc instanceof FakePlayerInstance instance)
            BotAPI.INSTANCE.despawn(instance);
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
