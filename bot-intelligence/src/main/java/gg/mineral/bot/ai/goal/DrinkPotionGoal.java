package gg.mineral.bot.ai.goal;

import gg.mineral.bot.api.controls.Key;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.goal.Goal;
import gg.mineral.bot.api.inv.Inventory;
import gg.mineral.bot.api.inv.item.Item;
import gg.mineral.bot.api.inv.item.ItemStack;
import gg.mineral.bot.api.inv.potion.Potion;
import gg.mineral.bot.api.util.MathUtil;
import gg.mineral.bot.api.world.ClientWorld;
import lombok.Getter;

@Getter
public class DrinkPotionGoal extends Goal implements MathUtil {

    @Override
    public boolean shouldExecute() {
        return canSeeEnemy() && hasDrinkablePotion();
    }

    public DrinkPotionGoal(FakePlayer fakePlayer) {
        super(fakePlayer);
    }

    private boolean hasDrinkablePotion() {
        Inventory inventory = fakePlayer.getInventory();
        return inventory == null ? false : inventory.contains(Item.POTION);
    }

    private boolean canSeeEnemy() {
        ClientWorld world = fakePlayer.getWorld();
        return world == null ? false
                : world.getEntities().stream()
                        .anyMatch(entity -> !fakePlayer.getFriendlyEntityUUIDs().contains(entity.getUuid()));
    }

    // TODO: move from inventory to hotbar
    private void switchToDrinkablePotion() {
        int potionSlot = 0;
        Inventory inventory = fakePlayer.getInventory();

        if (inventory == null)
            return;
        for (int i = 0; i < 8; i++) {
            ItemStack itemStack = inventory.getItemStackAt(i);
            if (itemStack == null)
                continue;
            Item item = itemStack.getItem();
            if (item.getId() == Item.POTION) {
                Potion potion = itemStack.getPotion();
                if (potion.isSplash())
                    continue;

                // TODO: ensure it is not a debuff potion

                potionSlot = i;
                break;
            }
        }

        fakePlayer.getKeyboard().pressKey(10, Key.Type.valueOf("KEY_" + (potionSlot + 1)));
    }

    @Override
    public void onTick() {
        switchToDrinkablePotion();
    }

    @Override
    public boolean onEvent(Event event) {
        // Stop drinking when drank potion
        return false;
    }

    @Override
    public void onGameLoop() {
    }
}
