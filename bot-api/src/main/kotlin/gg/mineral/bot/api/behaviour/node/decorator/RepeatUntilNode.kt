package gg.mineral.bot.api.behaviour.node.decorator

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.node.DecoratorNode
import gg.mineral.bot.api.event.Event

abstract class RepeatUntilNode(
    tree: BehaviourTree,
    private val finishState: BTResult = BTResult.FAILURE
) :
    DecoratorNode(tree) {
    // TODO:  tick until
    final override fun tick(): BTResult {
        var result = child.callTick()
        while (result != finishState) result = child.callTick()
        return result
    }

    final override fun frame(): BTResult {
        var result = child.callFrame()
        while (result != finishState) result = child.callFrame()
        return result
    }

    final override fun <T : Event> event(event: T): BTResult {
        var result = child.callEvent(event)
        while (result != finishState) result = child.callEvent(event)
        return result
    }
}