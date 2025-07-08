package gg.mineral.bot.api.entity.throwable

interface ClientPotion : ClientThrowableEntity {
    /**
     * The durability of the potion.
     *
     * @return the durability of the potion
     */
    val potionDurability: Int
}