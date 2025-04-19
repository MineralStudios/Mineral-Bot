package gg.mineral.bot.base.client.netty

import gg.mineral.bot.base.client.instance.ClientInstance
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise
import net.minecraft.client.Minecraft

class LatencySimulatorHandler(private val mc: Minecraft) : ChannelOutboundHandlerAdapter() {

    @Throws(Exception::class)
    override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
        if (mc is ClientInstance) {
            mc.scheduleTask(Runnable {
                try {
                    super.write(ctx, msg, promise)
                } catch (e: Exception) {
                    promise.setFailure(e)
                }
            }, mc.latency.toLong())
        } else super.write(ctx, msg, promise)
    }
}
