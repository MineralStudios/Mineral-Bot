package gg.mineral.bot.api.goal.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ItemId(val itemId: Int, val durability: Int = 1, val searchEntireInventory: Boolean = true)
