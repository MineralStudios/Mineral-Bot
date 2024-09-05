
package gg.mineral.bot.api.inv.potion;

import gg.mineral.bot.api.entity.effect.PotionEffect;

import java.util.List;

public interface Potion {
    /**
     * Returns whether this potion is a splash potion.
     *
     * @return true if the potion is a splash potion, false otherwise.
     */
    boolean isSplash();

    /**
     * Returns the list of potion effects.
     *
     * @return the list of potion effects
     */
    List<PotionEffect> getEffects();
}
