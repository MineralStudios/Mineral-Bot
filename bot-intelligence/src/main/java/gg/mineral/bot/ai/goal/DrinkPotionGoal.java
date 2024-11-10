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
        boolean shouldExecute = drinking || canSeeEnemy() && hasDrinkablePotion();
        info(this, "Checking shouldExecute: " + shouldExecute);
        return shouldExecute;
    }

    public DrinkPotionGoal(ClientInstance clientInstance) {
        super(clientInstance);
        info(this, "DrinkPotionGoal initialized");
    }

    private boolean hasDrinkablePotion() {
        val fakePlayer = clientInstance.getFakePlayer();
        val inventory = fakePlayer.getInventory();

        if (inventory == null) {
            warn(this, "Inventory is null");
            return false;
        }

        boolean hasDrinkablePotion = inventory.containsPotion(potion -> !potion.isSplash());
        info(this, "Has drinkable potion: " + hasDrinkablePotion);
        return hasDrinkablePotion;
    }

    private boolean canSeeEnemy() {
        val fakePlayer = clientInstance.getFakePlayer();
        val world = fakePlayer.getWorld();

        if (world == null) {
            warn(this, "World is null");
            return false;
        }

        boolean canSeeEnemy = world.getEntities().stream()
                .anyMatch(entity -> entity instanceof ClientPlayer
                        && !clientInstance.getConfiguration().getFriendlyUUIDs().contains(entity.getUuid()));
        info(this, "Checking canSeeEnemy: " + canSeeEnemy);
        return canSeeEnemy;
    }

    private void drinkPotion() {
        drinking = true;
        success(this, "Started drinking potion");
    }

    private void switchToDrinkablePotion() {
        info(this, "Switching to a drinkable potion");
        var potionSlot = -1;
        val fakePlayer = clientInstance.getFakePlayer();
        val inventory = fakePlayer.getInventory();

        if (inventory == null) {
            warn(this, "Inventory is null");
            return;
        }

        for (int i = 0; i < 36; i++) {
            val itemStack = inventory.getItemStackAt(i);
            if (itemStack == null)
                continue;
            val item = itemStack.getItem();
            if (item.getId() == Item.POTION) {
                val potion = itemStack.getPotion();
                if (potion.isSplash())
                    continue;

                potionSlot = i;
                break;
            }
        }

        if (potionSlot > 8) {
            if (!inventoryOpen) {
                inventoryOpen = true;
                pressKey(10, Key.Type.KEY_E);
                info(this, "Opened inventory");
                return;
            }

            val screen = clientInstance.getCurrentScreen();

            if (screen == null) {
                inventoryOpen = false;
                warn(this, "Screen is null; closing inventory");
                return;
            }

            val inventoryContainer = fakePlayer.getInventoryContainer();

            if (inventoryContainer == null) {
                inventoryOpen = false;
                warn(this, "Inventory container is null; closing inventory");
                return;
            }

            val slot = inventoryContainer.getSlot(inventory, potionSlot);

            if (slot == null) {
                inventoryOpen = false;
                pressKey(10, Key.Type.KEY_ESCAPE);
                warn(this, "Potion slot is null; closing inventory");
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
                    info(this, "Moving mouse to potion slot at x: " + x + ", y: " + y);
                } else {
                    pressKey(10, Key.Type.KEY_8);
                    success(this, "Moved to end slot");
                }
            }
            return;
        }

        if (inventoryOpen) {
            inventoryOpen = false;
            pressKey(10, Key.Type.KEY_ESCAPE);
            info(this, "Closing inventory after switching potion");
            return;
        }

        pressKey(10, Key.Type.valueOf("KEY_" + (potionSlot + 1)));
        success(this, "Switched to potion slot: " + (potionSlot + 1));
    }

    @Override
    public void onTick() {
        val fakePlayer = clientInstance.getFakePlayer();
        val inventory = fakePlayer.getInventory();

        if (inventory == null) {
            warn(this, "Inventory is null on tick");
            return;
        }

        val itemStack = inventory.getHeldItemStack();

        if (drinking && (itemStack == null || itemStack.getItem().getId() != Item.POTION)) {
            drinking = false;
            info(this, "Stopped drinking as no potion is held");
        }

        val rmbHeld = getButton(MouseButton.Type.RIGHT_CLICK).isPressed();

        if (drinking && !rmbHeld) {
            pressButton(MouseButton.Type.RIGHT_CLICK);
            info(this, "Pressed RIGHT_CLICK for drinking");
        }

        if (!drinking && rmbHeld) {
            unpressButton(MouseButton.Type.RIGHT_CLICK);
            info(this, "Unpressed RIGHT_CLICK as drinking stopped");
        }

        if (drinking || !delayedTasks.isEmpty()) {
            return;
        }

        if (itemStack != null && itemStack.getItem().getId() == Item.POTION) {
            schedule(() -> drinkPotion(), 100);
            info(this, "Scheduled drinkPotion task");
        } else {
            schedule(() -> switchToDrinkablePotion(), 100);
            info(this, "Scheduled switchToDrinkablePotion task");
        }
    }

    @Override
    public boolean onEvent(Event event) {
        if (event instanceof MouseButtonEvent mouseButtonEvent) {
            if (drinking && mouseButtonEvent.getType() == MouseButton.Type.RIGHT_CLICK
                    && !mouseButtonEvent.isPressed()) {
                info(this, "Ignoring RIGHT_CLICK release event while drinking");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onGameLoop() {
    }
}
