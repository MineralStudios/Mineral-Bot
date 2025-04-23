package gg.mineral.bot.base.client.netty

import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.handler.timeout.ReadTimeoutHandler
import net.minecraft.client.Minecraft
import net.minecraft.network.NetworkManager
import net.minecraft.util.MessageDeserializer
import net.minecraft.util.MessageDeserializer2
import net.minecraft.util.MessageSerializer
import net.minecraft.util.MessageSerializer2

class ClientChannelInitializer(
    private val mc: Minecraft,
    private val packetHandler: NetworkManager
) : ChannelInitializer<Channel>() {
    override fun initChannel(channel: Channel) {
        channel.pipeline().addLast("timeout", ReadTimeoutHandler(20))
            .addLast("latency_simulator", LatencySimulatorHandler(mc))
            .addLast("splitter", MessageDeserializer2())
            .addLast("decoder", MessageDeserializer(NetworkManager.field_152462_h, mc))
            .addLast("prepender", MessageSerializer2())
            .addLast("encoder", MessageSerializer(NetworkManager.field_152462_h))
            .addLast("packet_handler", packetHandler)
    }
}