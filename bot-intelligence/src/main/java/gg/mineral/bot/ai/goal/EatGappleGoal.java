package gg.mineral.bot.ai.goal;

import gg.mineral.bot.api.controls.Key;
import gg.mineral.bot.api.controls.MouseButton;
import gg.mineral.bot.api.entity.effect.PotionEffectType;
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
public class EatGappleGoal extends Goal {

    private boolean inventoryOpen = false, eating = false;

    @Override
    public boolean shouldExecute() {
        var hasRegen = false;
        val regenId = PotionEffectType.REGENERATION.getId();
        val fakePlayer = clientInstance.getFakePlayer();
        val activeIds = fakePlayer.getActivePotionEffectIds();

        for (int i = 0; i < activeIds.length; i++)
            if (activeIds[i] == regenId)
                hasRegen = true;

        boolean shouldExecute = eating || canSeeEnemy() && hasGapple() && !hasRegen;
        info(this, "Checking shouldExecute: " + shouldExecute);
        return shouldExecute;
    }

    public EatGappleGoal(ClientInstance clientInstance) {
        super(clientInstance);
        info(this, "EatGappleGoal initialized");
    }

    private boolean hasGapple() {
        val fakePlayer = clientInstance.getFakePlayer();
        val inventory = fakePlayer.getInventory();

        if (inventory == null) {
            warn(this, "Inventory is null");
            return false;
        }

        boolean hasGapple = inventory.contains(Item.GOLDEN_APPLE);
        info(this, "Has golden apple: " + hasGapple);
        return hasGapple;
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

    private void eatGapple() {
        this.eating = true;
        success(this, "Started eating golden apple");
    }

    private void switchToGapple() {
        eating = false;
        info(this, "Switching to golden apple");
        var gappleSlot = -1;
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
            if (item.getId() == Item.GOLDEN_APPLE) {
                gappleSlot = i;
                break;
            }
        }

        if (gappleSlot > 8) {
            if (!inventoryOpen) {
                inventoryOpen = true;
                pressKey(10, Key.Type.KEY_E);
                info(this, "Opened inventory to access golden apple");
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

            val slot = inventoryContainer.getSlot(inventory, gappleSlot);

            if (slot == null) {
                inventoryOpen = false;
                pressKey(10, Key.Type.KEY_ESCAPE);
                warn(this, "Golden apple slot is null; closing inventory");
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
                    info(this, "Moving mouse to golden apple slot at x: " + x + ", y: " + y);
                } else {
                    pressKey(10, Key.Type.KEY_8);
                    success(this, "Moved to end slot for golden apple");
                }
            }
            return;
        }

        if (inventoryOpen) {
            inventoryOpen = false;
            pressKey(10, Key.Type.KEY_ESCAPE);
            info(this, "Closing inventory after switching to golden apple");
            return;
        }

        pressKey(10, Key.Type.valueOf("KEY_" + (gappleSlot + 1)));
        success(this, "Switched to golden apple slot: " + (gappleSlot + 1));
    }

    @Override
    public void onTick() {
        val fakePlayer = clientInstance.getFakePlayer();
        val inventory = fakePlayer.getInventory();

        if (inventory == null) {
            warn(this, "Inventory is null on tick");
            return;
        }

        var hasRegen = false;
        val regenId = PotionEffectType.REGENERATION.getId();
        val activeIds = fakePlayer.getActivePotionEffectIds();

        for (int i = 0; i < activeIds.length; i++)
            if (activeIds[i] == regenId) {
                hasRegen = true;
                break;
            }

        if (eating && hasRegen) {
            eating = false;
            info(this, "Stopped eating as regeneration is active");
        }

        val rmbHeld = getButton(MouseButton.Type.RIGHT_CLICK).isPressed();

        if (!eating && rmbHeld) {
            unpressButton(MouseButton.Type.RIGHT_CLICK);
            info(this, "Unpressed RIGHT_CLICK as eating stopped");
        }

        if (hasRegen)
            return;

        if (eating && !rmbHeld) {
            pressButton(MouseButton.Type.RIGHT_CLICK);
            info(this, "Pressed RIGHT_CLICK for eating golden apple");
        }

        if (!delayedTasks.isEmpty())
            return;

        val itemStack = inventory.getHeldItemStack();

        if (itemStack != null && itemStack.getItem().getId() == Item.GOLDEN_APPLE) {
            schedule(() -> eatGapple(), 100);
            info(this, "Scheduled eatGapple task");
        } else {
            schedule(() -> switchToGapple(), 100);
            info(this, "Scheduled switchToGapple task");
        }
    }

    @Override
    public boolean onEvent(Event event) {
        if (event instanceof MouseButtonEvent mouseButtonEvent) {
            if (eating && mouseButtonEvent.getType() == MouseButton.Type.RIGHT_CLICK && !mouseButtonEvent.isPressed()) {
                info(this, "Ignoring RIGHT_CLICK release event while eating");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onGameLoop() {
    }
}
