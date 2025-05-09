package gg.mineral.bot.impl.behaviour

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.inverter
import gg.mineral.bot.api.behaviour.leaf
import gg.mineral.bot.api.behaviour.node.ChildNode
import gg.mineral.bot.api.behaviour.node.composite.SelectorNode
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.impl.behaviour.branch.DrinkPotionBranch

class RootNode(tree: BehaviourTree) : SelectorNode(tree) {
    private var started = false

    override val children: Array<ChildNode> = arrayOf(

        // Start sprinting.
        inverter(tree, leaf(tree, onTick = {
            if (started) return@leaf BTResult.SUCCESS
            started = true
            pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
            BTResult.SUCCESS
        })),
        // TODO: Safe walk (don't walk into danger)

        // Replace armor
        //ReplaceArmorBranch(tree, Item.Type.HELMET) { helmet },
        //ReplaceArmorBranch(tree, Item.Type.BOOTS) { boots },
        //ReplaceArmorBranch(tree, Item.Type.LEGGINGS) { leggings },
        //ReplaceArmorBranch(tree, Item.Type.CHESTPLATE) { chestplate },

        // Heal soup
        //HealSoupBranch(tree),

        // Throw health pot
        //ThrowHealthPotBranch(tree),

        // Drink potion
        DrinkPotionBranch(tree),

        // Eat gapple
        //EatGappleBranch(tree),

        // TODO: Eat food
        //EatFoodBranch(tree),

        // TODO: Throw debuff pot
        //ThrowDebuffPotBranch(tree),

        // TODO: Throw pearl
        //ThrowPearlBranch(tree),

        // TODO: Drop empty bowl
        //DropEmptyBowlBranch(tree),

        // TODO: Melee combat
        //MeleeCombatBranch(tree)
    )
}