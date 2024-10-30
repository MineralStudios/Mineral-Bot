package gg.mineral.bot.ai.goal;

import gg.mineral.bot.api.controls.Key;
import gg.mineral.bot.api.controls.MouseButton;
import gg.mineral.bot.api.entity.effect.PotionEffectType;
import gg.mineral.bot.api.entity.living.player.ClientPlayer;

import gg.mineral.bot.api.event.Event;

import gg.mineral.bot.api.goal.Goal;
import gg.mineral.bot.api.instance.ClientInstance;
import gg.mineral.bot.api.inv.item.Item;

import gg.mineral.bot.api.screen.type.ContainerScreen;
import gg.mineral.bot.api.util.MathUtil;

import lombok.Getter;
import lombok.val;

@Getter
public class EatGappleGoal extends Goal implements MathUtil {

    private boolean inventoryOpen = false, eating = false;

    @Override
    public boolean shouldExecute() {
        val fakePlayer = clientInstance.getFakePlayer();

        if (fakePlayer == null)
            return false;
        var hasRegen = false;
        val regenId = PotionEffectType.REGENERATION.getId();
        val activeIds = fakePlayer.getActivePotionEffectIds();

        for (int i = 0; i < activeIds.length; i++)
            if (activeIds[i] == regenId)
                hasRegen = true;

        return eating || canSeeEnemy() && hasGapple() && !hasRegen;
    }

    public EatGappleGoal(ClientInstance clientInstance) {
        super(clientInstance);
    }

    private boolean hasGapple() {
        val fakePlayer = clientInstance.getFakePlayer();

        if (fakePlayer == null)
            return false;
        val inventory = fakePlayer.getInventory();
        return inventory == null ? false : inventory.contains(Item.GOLDEN_APPLE);
    }

    private boolean canSeeEnemy() {
        val fakePlayer = clientInstance.getFakePlayer();

        if (fakePlayer == null)
            return false;
        val world = fakePlayer.getWorld();
        return world == null ? false
                : world.getEntities().stream()
                        .anyMatch(entity -> entity instanceof ClientPlayer
                                && !fakePlayer.getFriendlyEntityUUIDs().contains(entity.getUuid()));
    }

    private void eatGapple() {
        this.eating = true;
    }

    private void switchToGapple() {
        val fakePlayer = clientInstance.getFakePlayer();

        if (fakePlayer == null)
            return;
        var gappleSlot = -1;
        val inventory = fakePlayer.getInventory();

        if (inventory == null)
            return;

        // Search hotbar
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

            val slot = inventoryContainer.getSlot(inventory, gappleSlot);

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

        pressKey(10, Key.Type.valueOf("KEY_" + (gappleSlot + 1)));
    }

    @Override
    public void onTick() {
        val fakePlayer = clientInstance.getFakePlayer();

        if (fakePlayer == null)
            return;

        val inventory = fakePlayer.getInventory();

        if (inventory == null)
            return;

        var hasRegen = false;
        val regenId = PotionEffectType.REGENERATION.getId();
        val activeIds = fakePlayer.getActivePotionEffectIds();

        for (int i = 0; i < activeIds.length; i++)
            if (activeIds[i] == regenId) {
                hasRegen = true;
                break;
            }

        if (eating && hasRegen)
            eating = false;

        val rmbHeld = getButton(MouseButton.Type.RIGHT_CLICK).isPressed();

        if (eating && !rmbHeld)
            pressButton(MouseButton.Type.RIGHT_CLICK);

        if (!eating && rmbHeld)
            unpressButton(MouseButton.Type.RIGHT_CLICK);

        if (eating || hasRegen)
            return;
        // TODO: lookaway

        val itemStack = inventory.getHeldItemStack();

        if (itemStack != null && itemStack.getItem().getId() == Item.GOLDEN_APPLE)
            eatGapple();
        else
            switchToGapple();
    }

    @Override
    public boolean onEvent(Event event) {
        return false;
    }

    @Override
    public void onGameLoop() {
    }
}
