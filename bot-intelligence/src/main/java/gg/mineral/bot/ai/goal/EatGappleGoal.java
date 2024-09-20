package gg.mineral.bot.ai.goal;

import gg.mineral.bot.api.controls.Key;
import gg.mineral.bot.api.controls.MouseButton;
import gg.mineral.bot.api.entity.effect.PotionEffectType;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.goal.Goal;
import gg.mineral.bot.api.inv.Inventory;
import gg.mineral.bot.api.inv.InventoryContainer;
import gg.mineral.bot.api.inv.Slot;
import gg.mineral.bot.api.inv.item.Item;
import gg.mineral.bot.api.inv.item.ItemStack;
import gg.mineral.bot.api.screen.Screen;
import gg.mineral.bot.api.screen.type.ContainerScreen;
import gg.mineral.bot.api.util.MathUtil;
import gg.mineral.bot.api.world.ClientWorld;
import lombok.Getter;

@Getter
public class EatGappleGoal extends Goal implements MathUtil {

    private boolean inventoryOpen = false, eating = false;

    @Override
    public boolean shouldExecute() {
        boolean hasRegen = false;
        int regenId = PotionEffectType.REGENERATION.getId();
        int[] activeIds = fakePlayer.getActivePotionEffectIds();

        for (int i = 0; i < activeIds.length; i++)
            if (activeIds[i] == regenId)
                hasRegen = true;

        return eating || canSeeEnemy() && hasGapple() && !hasRegen;
    }

    public EatGappleGoal(FakePlayer fakePlayer) {
        super(fakePlayer);
    }

    private boolean hasGapple() {
        Inventory inventory = fakePlayer.getInventory();
        return inventory == null ? false : inventory.contains(Item.GOLDEN_APPLE);
    }

    private boolean canSeeEnemy() {
        ClientWorld world = fakePlayer.getWorld();
        return world == null ? false
                : world.getEntities().stream()
                        .anyMatch(entity -> !fakePlayer.getFriendlyEntityUUIDs().contains(entity.getUuid()));
    }

    private void eatGapple() {
        eating = true;
    }

    private void switchToGapple() {
        int gappleSlot = -1;
        Inventory inventory = fakePlayer.getInventory();

        if (inventory == null)
            return;

        // Search hotbar
        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = inventory.getItemStackAt(i);
            if (itemStack == null)
                continue;
            Item item = itemStack.getItem();
            if (item.getId() == Item.GOLDEN_APPLE) {
                gappleSlot = i;
                break;
            }
        }

        if (gappleSlot > 8) {
            if (!inventoryOpen) {
                inventoryOpen = true;
                getKeyboard().pressKey(10, Key.Type.KEY_E);
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

            Slot slot = inventoryContainer.getSlot(inventory, gappleSlot);

            if (slot == null) {
                inventoryOpen = false;
                getKeyboard().pressKey(10, Key.Type.KEY_ESCAPE);
                return;
            }

            if (screen instanceof ContainerScreen containerScreen) {
                int x = containerScreen.getSlotX(slot);
                int y = containerScreen.getSlotY(slot);

                int currX = getMouse().getX();
                int currY = getMouse().getY();

                if (currX != x || currY != y) {
                    getMouse().setX(x);
                    getMouse().setY(y);
                } else {
                    // Move to end slot
                    getKeyboard().pressKey(10, Key.Type.KEY_8);
                }

            }

            return;
        }

        if (inventoryOpen) {
            inventoryOpen = false;
            getKeyboard().pressKey(10, Key.Type.KEY_ESCAPE);
            return;
        }

        getKeyboard().pressKey(10, Key.Type.valueOf("KEY_" + (gappleSlot + 1)));
    }

    @Override
    public void onTick() {

        Inventory inventory = fakePlayer.getInventory();

        if (inventory == null)
            return;

        boolean hasRegen = false;
        int regenId = PotionEffectType.REGENERATION.getId();
        int[] activeIds = fakePlayer.getActivePotionEffectIds();

        for (int i = 0; i < activeIds.length; i++)
            if (activeIds[i] == regenId) {
                hasRegen = true;
                break;
            }

        if (eating && hasRegen)
            eating = false;

        boolean rmbHeld = getMouse().getButton(MouseButton.Type.RIGHT_CLICK).isPressed();

        if (eating && !rmbHeld)
            getMouse().pressButton(MouseButton.Type.RIGHT_CLICK);

        if (!eating && rmbHeld)
            getMouse().unpressButton(MouseButton.Type.RIGHT_CLICK);

        if (eating || hasRegen)
            return;
        // TODO: lookaway

        ItemStack itemStack = inventory.getHeldItemStack();

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
