package gg.mineral.bot.api.behaviour.node.decorator

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.node.DecoratorNode
import gg.mineral.bot.api.event.Event

abstract class InverterNode(tree: BehaviourTree) : DecoratorNode(tree) {
    final override fun tick() = when (child.callTick()) {
        BTResult.SUCCESS -> BTResult.FAILURE
        BTResult.FAILURE -> BTResult.SUCCESS
        BTResult.RUNNING -> BTResult.RUNNING
        BTResult.SKIP -> BTResult.SKIP
    }

    final override fun frame() = when (child.callFrame()) {
        BTResult.SUCCESS -> BTResult.FAILURE
        BTResult.FAILURE -> BTResult.SUCCESS
        BTResult.RUNNING -> BTResult.RUNNING
        BTResult.SKIP -> BTResult.SKIP
    }

    final override fun <T : Event> event(event: T) = when (child.callEvent(event)) {
        BTResult.SUCCESS -> BTResult.FAILURE
        BTResult.FAILURE -> BTResult.SUCCESS
        BTResult.RUNNING -> BTResult.RUNNING
        BTResult.SKIP -> BTResult.SKIP
    }
}