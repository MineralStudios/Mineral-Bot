package io.netty.channel.local

import io.netty.channel.Channel

object LocalChannelUtil {
    fun get(address: LocalAddress): Channel? {
        return LocalChannelRegistry.get(address)
    }
}