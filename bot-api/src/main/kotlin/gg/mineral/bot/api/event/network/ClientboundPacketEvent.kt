package gg.mineral.bot.api.event.network

import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.packet.ClientboundPacket

@JvmRecord
data class ClientboundPacketEvent(val packet: ClientboundPacket) : Event
