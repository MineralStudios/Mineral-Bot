package gg.mineral.bot.api.behaviour.node

import gg.mineral.bot.api.behaviour.BTResponse
import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.event.Event
import java.util.*

abstract class BTNode {
    fun callTick(stack: Stack<BTResponse>): BTResult {
        val result = tick()
        stack.find { it.node == this }?.let { it.result = result } ?: stack.push(BTResponse(this, result))
        return result
    }

    abstract fun tick(): BTResult

    open fun <T : Event> event(event: T): Boolean {
        return false
    }

    open fun frame(): BTResult {
        return BTResult.SUCCESS
    }
}