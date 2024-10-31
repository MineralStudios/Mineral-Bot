package gg.mineral.bot.base.client.netty;

import gg.mineral.bot.base.client.instance.ClientInstance;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;

@RequiredArgsConstructor
public class LatencySimulatorHandler extends ChannelOutboundHandlerAdapter {
    private final Minecraft mc;

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise)
            throws Exception {
        if (this.mc instanceof ClientInstance instance) {
            instance.scheduleTask(() -> {
                try {
                    super.write(ctx, msg, promise);
                } catch (Exception e) {
                    promise.setFailure(e);
                }
            }, instance.getLatency());
        } else {
            super.write(ctx, msg, promise);
        }
    }
}
