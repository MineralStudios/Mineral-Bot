package gg.mineral.bot.base.client.netty

import gg.mineral.bot.base.client.instance.ClientInstance
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise
import net.minecraft.client.Minecraft
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.NetworkManager
import java.util.concurrent.TimeUnit

class LatencySimulatorHandler(private val mc: Minecraft) : ChannelOutboundHandlerAdapter() {
    private val latencyNano: Long
        get() = if (mc is ClientInstance) mc.latency.toLong() * 1_000_000 else 0L

    @Throws(Exception::class)
    override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
        val delay = latencyNano
        val channel = ctx.channel()
        if (delay > 0 && mc is ClientInstance && channel.attr(NetworkManager.attrKeyConnectionState)
                ?.get() == EnumConnectionState.PLAY
        ) {
            ctx.executor().schedule({
                try {
                    if (!channel.isOpen || !channel.isActive) return@schedule
                    if (mc.configuration.instantFlush)
                        ctx.writeAndFlush(msg, promise)
                    else
                        super.write(ctx, msg, promise)
                } catch (e: Exception) {
                    promise.setFailure(e)
                }
            }, delay, TimeUnit.NANOSECONDS)
        } else super.write(ctx, msg, promise)
    }

    @Throws(Exception::class)
    override fun flush(ctx: ChannelHandlerContext) {
        val delay = latencyNano
        val channel = ctx.channel()
        if (delay > 0 && mc is ClientInstance && !mc.configuration.instantFlush && channel.attr(NetworkManager.attrKeyConnectionState)
                ?.get() == EnumConnectionState.PLAY
        ) {
            ctx.executor().schedule({
                if (!channel.isOpen || !channel.isActive) return@schedule
                super.flush(ctx)
            }, delay, TimeUnit.NANOSECONDS)
        } else super.flush(ctx)
    }
}
