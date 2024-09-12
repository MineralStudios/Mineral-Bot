package gg.mineral.bot.ai.goal;

import gg.mineral.bot.api.controls.Key;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.goal.Goal;
import gg.mineral.bot.api.inv.Inventory;
import gg.mineral.bot.api.inv.InventoryContainer;
import gg.mineral.bot.api.inv.Slot;
import gg.mineral.bot.api.inv.item.Item;
import gg.mineral.bot.api.inv.item.ItemStack;
import gg.mineral.bot.api.inv.potion.Potion;
import gg.mineral.bot.api.screen.Screen;
import gg.mineral.bot.api.screen.type.ContainerScreen;
import gg.mineral.bot.api.util.MathUtil;
import gg.mineral.bot.api.world.ClientWorld;
import lombok.Getter;

@Getter
public class DrinkPotionGoal extends Goal implements MathUtil {

    private boolean inventoryOpen = false;

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

    private void switchToDrinkablePotion() {
        int potionSlot = -1;
        Inventory inventory = fakePlayer.getInventory();

        if (inventory == null)
            return;

        // Search hotbar
        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = inventory.getItemStackAt(i);
            if (itemStack == null)
                continue;
            Item item = itemStack.getItem();
            if (item.getId() == Item.POTION) {
                Potion potion = itemStack.getPotion();
                if (potion.isSplash())
                    continue;

                // TODO: ensure it is not a debuff potion and doesn't already have the effect

                potionSlot = i;
                break;
            }
        }

        if (potionSlot > 8) {
            if (!inventoryOpen) {
                inventoryOpen = true;
                fakePlayer.getKeyboard().pressKey(10, Key.Type.KEY_E);
                return;
            }

            Screen screen = fakePlayer.getCurrentScreen();

            if (screen == null) {
                inventoryOpen = false;
                return;
            }
            // Move mouse
            InventoryContainer inventoryContainer = fakePlayer.getInventoryContainer();

            if (inventoryContainer == null) {
                inventoryOpen = false;
                return;
            }

            Slot slot = inventoryContainer.getSlot(inventory, potionSlot);

            if (slot == null) {
                inventoryOpen = false;
                fakePlayer.getKeyboard().pressKey(10, Key.Type.KEY_ESCAPE);
                return;
            }

            if (screen instanceof ContainerScreen containerScreen) {
                int x = containerScreen.getSlotX(slot);
                int y = containerScreen.getSlotY(slot);

                int currX = fakePlayer.getMouse().getX();
                int currY = fakePlayer.getMouse().getY();

                if (currX != x || currY != y) {
                    fakePlayer.getMouse().setX(x);
                    fakePlayer.getMouse().setY(y);
                } else {
                    // Move to end slot
                    fakePlayer.getKeyboard().pressKey(10, Key.Type.KEY_8);
                }

            }

            return;
        }

        if (inventoryOpen) {
            inventoryOpen = false;
            fakePlayer.getKeyboard().pressKey(10, Key.Type.KEY_ESCAPE);
            return;
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
