package gg.mineral.bot.api.entity.effect;

import org.eclipse.jdt.annotation.Nullable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Represents a type of potion and its effect on an entity.
 */
@Getter
@RequiredArgsConstructor
@ToString(includeFieldNames = true)
public enum PotionEffectType {
    SPEED(1, "Speed", false), SLOW(2, "Slow", false), FAST_DIGGING(3, "Fast Digging", false), SLOW_DIGGING(4,
            "Slow Digging", false), INCREASE_DAMAGE(5, "Increase Damage", false), INSTANT_HEAL(6, "Heal", true), HARM(7,
                    "Harm", true), JUMP(8, "Jump", false), CONFUSION(9, "Confusion", false), REGENERATION(10,
                            "Regeneration", false), DAMAGE_RESISTANCE(11, "Damage Resistance", false), FIRE_RESISTANCE(
                                    12, "Fire Resistance",
                                    false), WATER_BREATHING(13, "Water Breathing", false), INVISIBILITY(14,
                                            "Invisibility", false), BLINDNESS(15, "Blindness", false), NIGHT_VISION(16,
                                                    "Night Vision", false), HUNGER(17, "Hunger", false), WEAKNESS(18,
                                                            "Weakness", false), POISON(19, "Poison", false), WITHER(20,
                                                                    "Wither", false), REGEN(21, "Health Boost",
                                                                            false), ABSORPTION(22, "Absorption",
                                                                                    false), SATURATION(23, "Saturation",
                                                                                            true);

    private final int id;
    private final String name;
    private final boolean instant;

    @Nullable
    public static PotionEffectType getById(int id) {
        return values()[id - 1];
    }

    @Nullable
    public static PotionEffectType getByName(String name) {
        for (PotionEffectType effect : values())
            if (effect.name.equalsIgnoreCase(name))
                return effect;

        return null;
    }
}
