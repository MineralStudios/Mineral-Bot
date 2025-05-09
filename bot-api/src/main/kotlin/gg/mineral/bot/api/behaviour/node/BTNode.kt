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

    fun callFrame(stack: Stack<BTResponse>): BTResult {
        val result = frame()
        stack.find { it.node == this }?.let { it.result = result } ?: stack.push(BTResponse(this, result))
        return result
    }

    fun <T : Event> callEvent(stack: Stack<BTResponse>, event: T): BTResult {
        val result = event(event)
        stack.find { it.node == this }?.let { it.result = result } ?: stack.push(BTResponse(this, result))
        return result
    }

    abstract fun tick(): BTResult

    abstract fun <T : Event> event(event: T): BTResult

    abstract fun frame(): BTResult
}