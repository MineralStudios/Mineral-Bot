package gg.mineral.bot.ai.goal;

import gg.mineral.bot.api.controls.Key;
import gg.mineral.bot.api.controls.MouseButton;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.event.network.ClientboundPacketEvent;
import gg.mineral.bot.api.goal.Goal;
import gg.mineral.bot.api.inv.Inventory;
import gg.mineral.bot.api.inv.InventoryContainer;
import gg.mineral.bot.api.inv.Slot;
import gg.mineral.bot.api.inv.item.Item;
import gg.mineral.bot.api.inv.item.ItemStack;
import gg.mineral.bot.api.inv.potion.Potion;
import gg.mineral.bot.api.packet.play.clientbound.EntityStatusPacket;
import gg.mineral.bot.api.screen.Screen;
import gg.mineral.bot.api.screen.type.ContainerScreen;
import gg.mineral.bot.api.util.MathUtil;
import gg.mineral.bot.api.world.ClientWorld;
import lombok.Getter;

@Getter
public class DrinkPotionGoal extends Goal implements MathUtil {

    private boolean inventoryOpen = false, drinking = false;

    @Override
    public boolean shouldExecute() {
        return drinking || canSeeEnemy() && hasDrinkablePotion();
    }

    public DrinkPotionGoal(FakePlayer fakePlayer) {
        super(fakePlayer);
    }

    private boolean hasDrinkablePotion() {
        Inventory inventory = fakePlayer.getInventory();
        // TODO: is drinkable
        return inventory == null ? false : inventory.contains(Item.POTION);
    }

    private boolean canSeeEnemy() {
        ClientWorld world = fakePlayer.getWorld();
        return world == null ? false
                : world.getEntities().stream()
                        .anyMatch(entity -> !fakePlayer.getFriendlyEntityUUIDs().contains(entity.getUuid()));
    }

    private void drinkPotion() {
        drinking = true;
        getMouse().pressButton(MouseButton.Type.RIGHT_CLICK);
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

            Slot slot = inventoryContainer.getSlot(inventory, potionSlot);

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

        getKeyboard().pressKey(10, Key.Type.valueOf("KEY_" + (potionSlot + 1)));
    }

    @Override
    public void onTick() {

        if (drinking)
            return;

        Inventory inventory = fakePlayer.getInventory();

        if (inventory == null)
            return;

        ItemStack itemStack = inventory.getHeldItemStack();
        // TODO: lookaway
        if (itemStack != null && itemStack.getItem().getId() == Item.POTION) // TODO: is drinkable and needs to consume
            drinkPotion();
        else
            switchToDrinkablePotion();
    }

    @Override
    public boolean onEvent(Event event) {
        if (event instanceof ClientboundPacketEvent packetEvent) {

            if (packetEvent.getPacket() instanceof EntityStatusPacket entityStatusPacket) {
                if (entityStatusPacket.getEntityId() == fakePlayer.getEntityId()) {
                    getMouse().unpressButton(MouseButton.Type.RIGHT_CLICK);
                    drinking = false;
                }
            }

        }
        // Stop drinking when drank potion
        return false;
    }

    @Override
    public void onGameLoop() {
    }
}
