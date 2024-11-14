package gg.mineral.bot.ai.goal;

import gg.mineral.bot.api.controls.Key;
import gg.mineral.bot.api.controls.MouseButton;

import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.goal.Goal;

import gg.mineral.bot.api.instance.ClientInstance;
import gg.mineral.bot.api.inv.item.Item;

import gg.mineral.bot.api.screen.type.ContainerScreen;

import lombok.Getter;
import lombok.val;

@Getter
public class ReplaceArmorGoal extends Goal {

    private boolean inventoryOpen = false;

    @Override
    public boolean shouldExecute() {

        // TODO: don't replace armor if eating gapple or drinking potion

        val fakePlayer = clientInstance.getFakePlayer();
        val inventory = fakePlayer.getInventory();

        if (inventory == null)
            return false;

        val missingArmorPiece = missingArmorPiece();

        return canSeeEnemy() && missingArmorPiece != Item.Type.NONE && inventory.contains(missingArmorPiece);
    }

    public ReplaceArmorGoal(ClientInstance clientInstance) {
        super(clientInstance);
    }

    private Item.Type missingArmorPiece() {
        val fakePlayer = clientInstance.getFakePlayer();
        val inventory = fakePlayer.getInventory();
        if (inventory == null)
            return Item.Type.NONE;

        val helmet = inventory.getHelmet();
        val chestplate = inventory.getChestplate();
        val leggings = inventory.getLeggings();
        val boots = inventory.getBoots();

        if (helmet == null)
            return Item.Type.HELMET;
        if (chestplate == null)
            return Item.Type.CHESTPLATE;
        if (leggings == null)
            return Item.Type.LEGGINGS;

        return boots == null ? Item.Type.BOOTS : Item.Type.NONE;
    }

    private boolean canSeeEnemy() {
        val fakePlayer = clientInstance.getFakePlayer();
        val world = fakePlayer.getWorld();
        return world == null ? false
                : world.getEntities().stream()
                        .anyMatch(entity -> !clientInstance.getConfiguration().getFriendlyUUIDs()
                                .contains(entity.getUuid()));
    }

    private void applyArmor() {
        pressButton(10, MouseButton.Type.RIGHT_CLICK);
    }

    private void switchToArmor() {
        var armorSlot = -1;
        val fakePlayer = clientInstance.getFakePlayer();
        val inventory = fakePlayer.getInventory();

        if (inventory == null)
            return;

        val missingArmorPiece = missingArmorPiece();

        // Search hotbar
        for (int i = 0; i < 36; i++) {
            val itemStack = inventory.getItemStackAt(i);
            if (itemStack == null)
                continue;
            val item = itemStack.getItem();
            if (missingArmorPiece.isType(item.getId())) {
                armorSlot = i;
                break;
            }
        }

        if (armorSlot > 8) {
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

            val slot = inventoryContainer.getSlot(inventory, armorSlot);

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
                } else {
                    // Move to end slot
                    pressKey(10, Key.Type.KEY_8);
                }

            }

            return;
        }

        if (inventoryOpen) {
            inventoryOpen = false;
            pressKey(10, Key.Type.KEY_ESCAPE);
            return;
        }

        pressKey(10, Key.Type.valueOf("KEY_" + (armorSlot + 1)));
    }

    @Override
    public void onTick() {
        val fakePlayer = clientInstance.getFakePlayer();
        val inventory = fakePlayer.getInventory();

        if (inventory == null)
            return;

        val itemStack = inventory.getHeldItemStack();

        val missingArmorPiece = missingArmorPiece();

        if (!delayedTasks.isEmpty())
            return;

        if (itemStack != null && missingArmorPiece.isType(itemStack.getItem().getId()))
            schedule(() -> applyArmor(), 100);
        else
            schedule(() -> switchToArmor(), 100);
    }

    @Override
    public boolean onEvent(Event event) {
        return false;
    }

    @Override
    public void onGameLoop() {
    }
}
