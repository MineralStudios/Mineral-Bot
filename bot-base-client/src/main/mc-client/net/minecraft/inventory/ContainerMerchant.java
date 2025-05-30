package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerMerchant extends Container {
    /** Instance of Merchant. */
    private IMerchant theMerchant;
    private InventoryMerchant merchantInventory;

    /** Instance of World. */
    private final World theWorld;

    public ContainerMerchant(InventoryPlayer p_i1821_1_, IMerchant p_i1821_2_, World p_i1821_3_) {
        this.theMerchant = p_i1821_2_;
        this.theWorld = p_i1821_3_;
        this.merchantInventory = new InventoryMerchant(p_i1821_1_.player, p_i1821_2_);
        this.addSlotToContainer(new Slot(this.merchantInventory, 0, 36, 53));
        this.addSlotToContainer(new Slot(this.merchantInventory, 1, 62, 53));
        this.addSlotToContainer(
                new SlotMerchantResult(p_i1821_1_.player, p_i1821_2_, this.merchantInventory, 2, 120, 53));
        int var4;

        for (var4 = 0; var4 < 3; ++var4) {
            for (int var5 = 0; var5 < 9; ++var5) {
                this.addSlotToContainer(new Slot(p_i1821_1_, var5 + var4 * 9 + 9, 8 + var5 * 18, 84 + var4 * 18));
            }
        }

        for (var4 = 0; var4 < 9; ++var4) {
            this.addSlotToContainer(new Slot(p_i1821_1_, var4, 8 + var4 * 18, 142));
        }
    }

    public InventoryMerchant getMerchantInventory() {
        return this.merchantInventory;
    }

    public void addCraftingToCrafters(ICrafting p_75132_1_) {
        super.addCraftingToCrafters(p_75132_1_);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory p_75130_1_) {
        this.merchantInventory.resetRecipeAndSlots();
        super.onCraftMatrixChanged(p_75130_1_);
    }

    public void setCurrentRecipeIndex(int p_75175_1_) {
        this.merchantInventory.setCurrentRecipeIndex(p_75175_1_);
    }

    public void updateProgressBar(int p_75137_1_, int p_75137_2_) {
    }

    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        return this.theMerchant.getCustomer() == p_75145_1_;
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you
     * will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_) {
        ItemStack var3 = null;
        Slot var4 = (Slot) this.inventorySlots.get(p_82846_2_);

        if (var4 != null && var4.getHasStack()) {
            ItemStack var5 = var4.getStack();
            var3 = var5.copy();

            if (p_82846_2_ == 2) {
                if (!this.mergeItemStack(var5, 3, 39, true)) {
                    return null;
                }

                var4.onSlotChange(var5, var3);
            } else if (p_82846_2_ != 0 && p_82846_2_ != 1) {
                if (p_82846_2_ >= 3 && p_82846_2_ < 30) {
                    if (!this.mergeItemStack(var5, 30, 39, false)) {
                        return null;
                    }
                } else if (p_82846_2_ >= 30 && p_82846_2_ < 39 && !this.mergeItemStack(var5, 3, 30, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(var5, 3, 39, false)) {
                return null;
            }

            if (var5.stackSize == 0) {
                var4.putStack((ItemStack) null);
            } else {
                var4.onSlotChanged();
            }

            if (var5.stackSize == var3.stackSize) {
                return null;
            }

            var4.onPickupFromSlot(p_82846_1_, var5);
        }

        return var3;
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer p_75134_1_) {
        super.onContainerClosed(p_75134_1_);
        this.theMerchant.setCustomer((EntityPlayer) null);
        super.onContainerClosed(p_75134_1_);

        if (!this.theWorld.isClient) {
            ItemStack var2 = this.merchantInventory.getStackInSlotOnClosing(0);

            if (var2 != null) {
                p_75134_1_.dropPlayerItemWithRandomChoice(var2, false);
            }

            var2 = this.merchantInventory.getStackInSlotOnClosing(1);

            if (var2 != null) {
                p_75134_1_.dropPlayerItemWithRandomChoice(var2, false);
            }
        }
    }
}
