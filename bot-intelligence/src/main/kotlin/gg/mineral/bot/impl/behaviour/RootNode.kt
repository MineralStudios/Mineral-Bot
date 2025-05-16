package gg.mineral.bot.impl.behaviour

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.leaf
import gg.mineral.bot.api.behaviour.node.ChildNode
import gg.mineral.bot.api.behaviour.node.composite.SelectorNode
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.impl.behaviour.branch.DrinkPotionBranch
import gg.mineral.bot.impl.behaviour.branch.EatGappleBranch
import gg.mineral.bot.impl.behaviour.branch.MeleeCombatBranch

class RootNode(tree: BehaviourTree) : SelectorNode(tree) {
    private var started = false

    override val children: Array<ChildNode> = arrayOf(

        // Start sprinting.
        leaf(tree) {
            if (started) return@leaf BTResult.SKIP
            started = true
            pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
            BTResult.SKIP
        },
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

        // If already consuming, finish consuming
        leaf(tree) {
            return@leaf if (clientInstance.fakePlayer.usingItem) BTResult.RUNNING
            else if (getButton(MouseButton.Type.RIGHT_CLICK).isPressed) {
                val isPressed = getButton(MouseButton.Type.RIGHT_CLICK).isPressed
                println("Right click pressed: $isPressed")
                unpressButton(MouseButton.Type.RIGHT_CLICK)
                BTResult.SKIP
            } else BTResult.FAILURE
        },

        // Drink potion
        DrinkPotionBranch(tree),

        // Eat gapple
        EatGappleBranch(tree),

        // TODO: Eat food
        //EatFoodBranch(tree),

        // TODO: Throw debuff pot
        //ThrowDebuffPotBranch(tree),

        // TODO: Throw pearl
        //ThrowPearlBranch(tree),

        // TODO: Drop empty bowl
        //DropEmptyBowlBranch(tree),

        // TODO: Melee combat
        MeleeCombatBranch(tree)
    )
}