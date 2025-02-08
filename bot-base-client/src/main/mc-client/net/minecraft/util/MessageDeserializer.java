package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkStatistics;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class MessageDeserializer extends ByteToMessageDecoder {
    private static final Logger logger = LogManager.getLogger(MessageDeserializer.class);
    private static final Marker marker = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.logMarkerPackets);
    private final NetworkStatistics netStats;
    private final Minecraft mc;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> output)
            throws IOException {
        int readableBytes = byteBuf.readableBytes();

        if (readableBytes != 0) {
            val buf = new PacketBuffer(byteBuf);
            int packetId = buf.readVarIntFromBuffer();
            val packet = Packet
                    .generatePacket(ctx.channel().attr(NetworkManager.attrKeyReceivable).get(),
                            packetId);

            if (packet == null)
                throw new IOException("Bad packet id " + packetId);

            packet.readPacketData(buf, mc);

            if (buf.readableBytes() > 0)
                throw new IOException("Packet was larger than I expected, found " + buf.readableBytes()
                        + " bytes extra whilst reading packet " + packetId);

            output.add(packet);
            this.netStats.func_152469_a(packetId, readableBytes);

            if (logger.isDebugEnabled())
                logger.debug(marker, " IN: [{}:{}] {}[{}]",
                        new Object[]{ctx.channel().attr(NetworkManager.attrKeyConnectionState).get(),
                                Integer.valueOf(packetId), packet.getClass().getName(), packet.serialize()});
        }
    }
}
