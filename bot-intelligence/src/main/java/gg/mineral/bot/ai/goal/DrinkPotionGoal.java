package gg.mineral.bot.ai.goal;

import gg.mineral.bot.api.controls.Key;
import gg.mineral.bot.api.controls.MouseButton;
import gg.mineral.bot.api.entity.living.player.ClientPlayer;

import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.event.peripherals.MouseButtonEvent;
import gg.mineral.bot.api.goal.Goal;
import gg.mineral.bot.api.instance.ClientInstance;
import gg.mineral.bot.api.inv.item.Item;

import gg.mineral.bot.api.screen.type.ContainerScreen;
import lombok.Getter;
import lombok.val;

@Getter
public class DrinkPotionGoal extends Goal {

    private boolean inventoryOpen = false, drinking = false;

    @Override
    public boolean shouldExecute() {
        return drinking || canSeeEnemy() && hasDrinkablePotion();
    }

    public DrinkPotionGoal(ClientInstance clientInstance) {
        super(clientInstance);
    }

    private boolean hasDrinkablePotion() {
        val inventory = fakePlayer.getInventory();
        return inventory == null ? false : inventory.containsPotion(potion -> !potion.isSplash());
    }

    private boolean canSeeEnemy() {
        val world = fakePlayer.getWorld();
        return world == null ? false
                : world.getEntities().stream()
                        .anyMatch(entity -> entity instanceof ClientPlayer
                                && !fakePlayer.getFriendlyEntityUUIDs().contains(entity.getUuid()));
    }

    private void drinkPotion() {
        drinking = true;
    }

    private void switchToDrinkablePotion() {
        var potionSlot = -1;
        val inventory = fakePlayer.getInventory();

        if (inventory == null)
            return;

        // Search hotbar
        for (int i = 0; i < 36; i++) {
            val itemStack = inventory.getItemStackAt(i);
            if (itemStack == null)
                continue;
            val item = itemStack.getItem();
            if (item.getId() == Item.POTION) {
                val potion = itemStack.getPotion();
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
                pressKey(10, Key.Type.KEY_E);
                return;
            }

            val screen = clientInstance.getCurrentScreen();

            if (screen == null) {
                inventoryOpen = false;
                return;
            }
            // Move mouse
            val inventoryContainer = fakePlayer.getInventoryContainer();

            if (inventoryContainer == null) {
                inventoryOpen = false;
                return;
            }

            val slot = inventoryContainer.getSlot(inventory, potionSlot);

            if (slot == null) {
                inventoryOpen = false;
                pressKey(10, Key.Type.KEY_ESCAPE);
                return;
            }

            if (screen instanceof ContainerScreen containerScreen) {
                val x = containerScreen.getSlotX(slot);
                val y = containerScreen.getSlotY(slot);

                val currX = getMouseX();
                val currY = getMouseY();

                if (currX != x || currY != y) {
                    setMouseX(x);
                    setMouseY(y);
                } else
                    // Move to end slot
                    pressKey(10, Key.Type.KEY_8);

            }

            return;
        }

        if (inventoryOpen) {
            inventoryOpen = false;
            pressKey(10, Key.Type.KEY_ESCAPE);
            return;
        }

        pressKey(10, Key.Type.valueOf("KEY_" + (potionSlot + 1)));
    }

    @Override
    public void onTick() {

        val inventory = fakePlayer.getInventory();

        if (inventory == null)
            return;

        val itemStack = inventory.getHeldItemStack();

        if (drinking && (itemStack == null || itemStack.getItem().getId() != Item.POTION))
            drinking = false;

        val rmbHeld = getButton(MouseButton.Type.RIGHT_CLICK).isPressed();

        if (drinking && !rmbHeld)
            pressButton(MouseButton.Type.RIGHT_CLICK);

        if (!drinking && rmbHeld)
            unpressButton(MouseButton.Type.RIGHT_CLICK);

        if (drinking)
            return;

        // TODO: lookaway
        if (itemStack != null && itemStack.getItem().getId() == Item.POTION)
            drinkPotion();
        else
            switchToDrinkablePotion();

    }

    @Override
    public boolean onEvent(Event event) {
        if (event instanceof MouseButtonEvent mouseButtonEvent)
            if (drinking && mouseButtonEvent.getType() == MouseButton.Type.RIGHT_CLICK && !mouseButtonEvent.isPressed())
                return true;
        return false;
    }

    @Override
    public void onGameLoop() {
    }
}
