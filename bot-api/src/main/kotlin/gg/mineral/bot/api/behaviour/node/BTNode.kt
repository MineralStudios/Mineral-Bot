package gg.mineral.bot.api.behaviour.node

import gg.mineral.bot.api.behaviour.BTResponse
import gg.mineral.bot.api.behaviour.BTResult
import java.util.*

abstract class BTNode {
    fun callTick(stack: Stack<BTResponse>): BTResult {
        val result = tick()
        stack.find { it.node == this }?.let { it.result = result } ?: stack.push(BTResponse(this, result))
        return result
    }

    abstract fun tick(): BTResult
}