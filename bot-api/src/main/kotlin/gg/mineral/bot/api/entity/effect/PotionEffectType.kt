package gg.mineral.bot.api.entity.effect

/**
 * Represents a type of potion and its effect on an entity.
 */
enum class PotionEffectType(val id: Int, val effectName: String, val instant: Boolean) {
    SPEED(1, "Speed", false), SLOW(2, "Slow", false), FAST_DIGGING(3, "Fast Digging", false), SLOW_DIGGING(
        4,
        "Slow Digging", false
    ),
    INCREASE_DAMAGE(5, "Increase Damage", false), INSTANT_HEAL(6, "Heal", true), HARM(
        7,
        "Harm", true
    ),
    JUMP(8, "Jump", false), CONFUSION(9, "Confusion", false), REGENERATION(
        10,
        "Regeneration", false
    ),
    DAMAGE_RESISTANCE(11, "Damage Resistance", false), FIRE_RESISTANCE(
        12, "Fire Resistance",
        false
    ),
    WATER_BREATHING(13, "Water Breathing", false), INVISIBILITY(
        14,
        "Invisibility", false
    ),
    BLINDNESS(15, "Blindness", false), NIGHT_VISION(
        16,
        "Night Vision", false
    ),
    HUNGER(17, "Hunger", false), WEAKNESS(
        18,
        "Weakness", false
    ),
    POISON(19, "Poison", false), WITHER(
        20,
        "Wither", false
    ),
    REGEN(
        21, "Health Boost",
        false
    ),
    ABSORPTION(
        22, "Absorption",
        false
    ),
    SATURATION(
        23, "Saturation",
        true
    );

    companion object {
        @JvmStatic
        fun getById(id: Int): PotionEffectType {
            return entries[id - 1]
        }

        @JvmStatic
        fun getByName(name: String): PotionEffectType? {
            for (effect in entries) if (effect.name.equals(name, ignoreCase = true)) return effect

            return null
        }
    }
}
