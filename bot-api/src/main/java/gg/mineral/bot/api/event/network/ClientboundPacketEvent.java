package gg.mineral.bot.api.event.network;

import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.packet.ClientboundPacket;
import lombok.Value;

@Value
public class ClientboundPacketEvent implements Event {
    ClientboundPacket packet;
}
