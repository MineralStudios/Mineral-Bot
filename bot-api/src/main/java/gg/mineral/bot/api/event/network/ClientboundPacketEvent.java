package gg.mineral.bot.api.event.network;

import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.packet.ClientboundPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ClientboundPacketEvent implements Event {
    private final ClientboundPacket packet;
}
