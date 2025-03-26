package gg.mineral.bot.api.behaviour.node

import gg.mineral.bot.api.behaviour.BehaviourTree

abstract class CompositeNode(tree: BehaviourTree) : ChildNode(tree) {
    abstract val children: Array<ChildNode>
}