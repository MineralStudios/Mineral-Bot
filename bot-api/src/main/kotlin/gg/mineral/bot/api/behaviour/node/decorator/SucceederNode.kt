package gg.mineral.bot.api.behaviour.node.decorator

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.node.DecoratorNode
import gg.mineral.bot.api.event.Event

abstract class SucceederNode(tree: BehaviourTree) : DecoratorNode(tree) {
    final override fun tick() = BTResult.SUCCESS.apply { child.callTick() }

    final override fun frame() = BTResult.SUCCESS.apply { child.callFrame() }

    final override fun <T : Event> event(event: T) = BTResult.SUCCESS.apply { child.callEvent(event) }
}