package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import gg.mineral.bot.api.entity.effect.PotionEffect;
import gg.mineral.bot.api.inv.potion.Potion;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

public final class ItemStack implements gg.mineral.bot.api.inv.item.ItemStack, Potion {
    public static final DecimalFormat field_111284_a = new DecimalFormat("#.###");

    /**
     * Size of the stack.
     */
    public int stackSize;

    /**
     * Number of animation frames to go when receiving an item (by walking into it,
     * for example).
     */
    public int animationsToGo;
    /**
     * A NBTTagMap containing data about an ItemStack. Can only be used for non
     * stackable items
     */
    public NBTTagCompound stackTagCompound;
    private Item item;
    /**
     * Damage dealt to the item or number of use. Raise when using items.
     */
    private int itemDamage;

    /**
     * Item frame this stack is on, or null if not on an item frame.
     */
    private EntityItemFrame itemFrame;

    public ItemStack(Block block) {
        this(block, 1);
    }

    public ItemStack(Block block, int p_i1877_2_) {
        this(block, p_i1877_2_, 0);
    }

    public ItemStack(Block p_i1878_1_, int p_i1878_2_, int p_i1878_3_) {
        this(Item.getItemFromBlock(p_i1878_1_), p_i1878_2_, p_i1878_3_);
    }

    public ItemStack(Item p_i1879_1_) {
        this(p_i1879_1_, 1);
    }

    public ItemStack(Item p_i1880_1_, int p_i1880_2_) {
        this(p_i1880_1_, p_i1880_2_, 0);
    }

    public ItemStack(Item p_i1881_1_, int p_i1881_2_, int p_i1881_3_) {
        this.item = p_i1881_1_;
        this.stackSize = p_i1881_2_;
        this.itemDamage = p_i1881_3_;

        if (this.itemDamage < 0)
            this.itemDamage = 0;
    }

    private ItemStack() {
    }

    public static ItemStack loadItemStackFromNBT(NBTTagCompound p_77949_0_) {
        ItemStack var1 = new ItemStack();
        var1.readFromNBT(p_77949_0_);
        return var1.getItem() != null ? var1 : null;
    }

    public static boolean areItemStackTagsEqual(ItemStack p_77970_0_, ItemStack p_77970_1_) {
        return p_77970_0_ == null
                && p_77970_1_ == null || (p_77970_0_ != null && p_77970_1_ != null && ((p_77970_0_.stackTagCompound != null || p_77970_1_.stackTagCompound == null) && (p_77970_0_.stackTagCompound == null
                || p_77970_0_.stackTagCompound.equals(p_77970_1_.stackTagCompound))));
    }

    /**
     * compares ItemStack argument1 with ItemStack argument2; returns true if both
     * ItemStacks are equal
     */
    public static boolean areItemStacksEqual(ItemStack p_77989_0_, ItemStack p_77989_1_) {
        return p_77989_0_ == null && p_77989_1_ == null || (p_77989_0_ != null && p_77989_1_ != null && p_77989_0_.isItemStackEqual(p_77989_1_));
    }

    /**
     * Creates a copy of a ItemStack, a null parameters will return a null.
     */
    public static ItemStack copyItemStack(ItemStack p_77944_0_) {
        return p_77944_0_ == null ? null : p_77944_0_.copy();
    }

    /**
     * Remove the argument from the stack size. Return a new stack object with
     * argument size.
     */
    public ItemStack splitStack(int p_77979_1_) {
        ItemStack var2 = new ItemStack(this.item, p_77979_1_, this.itemDamage);

        if (this.stackTagCompound != null) {
            var2.stackTagCompound = (NBTTagCompound) this.stackTagCompound.copy();
        }

        this.stackSize -= p_77979_1_;
        return var2;
    }

    /**
     * Returns the object corresponding to the stack.
     */
    public Item getItem() {
        return this.item;
    }

    /**
     * Returns the icon index of the current stack.
     */
    public IIcon getIconIndex() {
        return this.getItem().getIconIndex(this);
    }

    public int getItemSpriteNumber() {
        return this.getItem().getSpriteNumber();
    }

    public boolean tryPlaceItemIntoWorld(EntityPlayer p_77943_1_, World p_77943_2_, int p_77943_3_, int p_77943_4_,
                                         int p_77943_5_, int p_77943_6_, float p_77943_7_, float p_77943_8_, float p_77943_9_) {
        boolean var10 = this.getItem().onItemUse(this, p_77943_1_, p_77943_2_, p_77943_3_, p_77943_4_, p_77943_5_,
                p_77943_6_, p_77943_7_, p_77943_8_, p_77943_9_);

        if (var10) {
            p_77943_1_.addStat(StatList.objectUseStats[Item.getIdFromItem(this.item)], 1);
        }

        return var10;
    }

    public float func_150997_a(Block p_150997_1_) {
        return this.getItem().func_150893_a(this, p_150997_1_);
    }

    /**
     * Called whenever this item stack is equipped and right clicked. Returns the
     * new item stack to put in the position
     * where this item is. Args: world, player
     */
    public ItemStack useItemRightClick(World p_77957_1_, EntityPlayer p_77957_2_) {
        return this.getItem().onItemRightClick(this, p_77957_1_, p_77957_2_);
    }

    public ItemStack onFoodEaten(World p_77950_1_, EntityPlayer p_77950_2_) {
        return this.getItem().onEaten(this, p_77950_1_, p_77950_2_);
    }

    /**
     * Write the stack fields to a NBT object. Return the new NBT object.
     */
    public NBTTagCompound writeToNBT(NBTTagCompound p_77955_1_) {
        p_77955_1_.setShort("id", (short) Item.getIdFromItem(this.item));
        p_77955_1_.setByte("Count", (byte) this.stackSize);
        p_77955_1_.setShort("Damage", (short) this.itemDamage);

        if (this.stackTagCompound != null) {
            p_77955_1_.setTag("tag", this.stackTagCompound);
        }

        return p_77955_1_;
    }

    /**
     * Read the stack fields from a NBT object.
     */
    public void readFromNBT(NBTTagCompound p_77963_1_) {
        this.item = Item.getItemById(p_77963_1_.getShort("id"));
        this.stackSize = p_77963_1_.getByte("Count");
        this.itemDamage = p_77963_1_.getShort("Damage");

        if (this.itemDamage < 0) {
            this.itemDamage = 0;
        }

        if (p_77963_1_.func_150297_b("tag", 10)) {
            this.stackTagCompound = p_77963_1_.getCompoundTag("tag");
        }
    }

    /**
     * Returns maximum size of the stack.
     */
    public int getMaxStackSize() {
        return this.getItem().getItemStackLimit();
    }

    /**
     * Returns true if the ItemStack can hold 2 or more units of the item.
     */
    public boolean isStackable() {
        return this.getMaxStackSize() > 1 && (!this.isItemStackDamageable() || !this.isItemDamaged());
    }

    /**
     * true if this itemStack is damageable
     */
    public boolean isItemStackDamageable() {
        return this.item.getMaxDamage() > 0 && (!this.hasTagCompound() || !this.getTagCompound().getBoolean("Unbreakable"));
    }

    public boolean getHasSubtypes() {
        return this.item.getHasSubtypes();
    }

    /**
     * returns true when a damageable item is damaged
     */
    public boolean isItemDamaged() {
        return this.isItemStackDamageable() && this.itemDamage > 0;
    }

    /**
     * gets the damage of an itemstack, for displaying purposes
     */
    public int getItemDamageForDisplay() {
        return this.itemDamage;
    }

    /**
     * gets the damage of an itemstack
     */
    public int getItemDamage() {
        return this.itemDamage;
    }

    /**
     * Sets the item damage of the ItemStack.
     */
    public void setItemDamage(int p_77964_1_) {
        this.itemDamage = p_77964_1_;

        if (this.itemDamage < 0) {
            this.itemDamage = 0;
        }
    }

    /**
     * Returns the max damage an item in the stack can take.
     */
    public int getMaxDamage() {
        return this.item.getMaxDamage();
    }

    /**
     * Attempts to damage the ItemStack with par1 amount of damage, If the ItemStack
     * has the Unbreaking enchantment
     * there is a chance for each point of damage to be negated. Returns true if it
     * takes more damage than
     * getMaxDamage(). Returns false otherwise or if the ItemStack can't be damaged
     * or if all points of damage are
     * negated.
     */
    public boolean attemptDamageItem(int p_96631_1_, Random p_96631_2_) {
        if (!this.isItemStackDamageable()) {
            return false;
        } else {
            if (p_96631_1_ > 0) {
                int var3 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, this);
                int var4 = 0;

                for (int var5 = 0; var3 > 0 && var5 < p_96631_1_; ++var5) {
                    if (EnchantmentDurability.negateDamage(this, var3, p_96631_2_)) {
                        ++var4;
                    }
                }

                p_96631_1_ -= var4;

                if (p_96631_1_ <= 0) {
                    return false;
                }
            }

            this.itemDamage += p_96631_1_;
            return this.itemDamage > this.getMaxDamage();
        }
    }

    /**
     * Damages the item in the ItemStack
     */
    public void damageItem(int p_77972_1_, EntityLivingBase p_77972_2_) {
        if (!(p_77972_2_ instanceof EntityPlayer) || !((EntityPlayer) p_77972_2_).capabilities.isCreativeMode) {
            if (this.isItemStackDamageable()) {
                if (this.attemptDamageItem(p_77972_1_, p_77972_2_.getRNG())) {
                    p_77972_2_.renderBrokenItemStack(this);
                    --this.stackSize;

                    if (p_77972_2_ instanceof EntityPlayer var3) {
                        var3.addStat(StatList.objectBreakStats[Item.getIdFromItem(this.item)], 1);

                        if (this.stackSize == 0 && this.getItem() instanceof ItemBow) {
                            var3.destroyCurrentEquippedItem();
                        }
                    }

                    if (this.stackSize < 0) {
                        this.stackSize = 0;
                    }

                    this.itemDamage = 0;
                }
            }
        }
    }

    /**
     * Calls the corresponding fct in di
     */
    public void hitEntity(EntityLivingBase p_77961_1_, EntityPlayer p_77961_2_) {
        boolean var3 = this.item.hitEntity(this, p_77961_1_, p_77961_2_);

        if (var3) {
            p_77961_2_.addStat(StatList.objectUseStats[Item.getIdFromItem(this.item)], 1);
        }
    }

    public void func_150999_a(World p_150999_1_, Block p_150999_2_, int p_150999_3_, int p_150999_4_, int p_150999_5_,
                              EntityPlayer p_150999_6_) {
        boolean var7 = this.item.onBlockDestroyed(this, p_150999_1_, p_150999_2_, p_150999_3_, p_150999_4_,
                p_150999_5_, p_150999_6_);

        if (var7) {
            p_150999_6_.addStat(StatList.objectUseStats[Item.getIdFromItem(this.item)], 1);
        }
    }

    public boolean func_150998_b(Block p_150998_1_) {
        return this.item.func_150897_b(p_150998_1_);
    }

    public boolean interactWithEntity(EntityPlayer p_111282_1_, EntityLivingBase p_111282_2_) {
        return this.item.itemInteractionForEntity(this, p_111282_1_, p_111282_2_);
    }

    /**
     * Returns a new stack with the same properties.
     */
    public ItemStack copy() {
        ItemStack var1 = new ItemStack(this.item, this.stackSize, this.itemDamage);

        if (this.stackTagCompound != null) {
            var1.stackTagCompound = (NBTTagCompound) this.stackTagCompound.copy();
        }

        return var1;
    }

    /**
     * compares ItemStack argument to the instance ItemStack; returns true if both
     * ItemStacks are equal
     */
    private boolean isItemStackEqual(ItemStack p_77959_1_) {
        return this.stackSize == p_77959_1_.stackSize && (this.item == p_77959_1_.item && (this.itemDamage == p_77959_1_.itemDamage && ((this.stackTagCompound != null || p_77959_1_.stackTagCompound == null) && (this.stackTagCompound == null
                || this.stackTagCompound.equals(p_77959_1_.stackTagCompound)))));
    }

    /**
     * compares ItemStack argument to the instance ItemStack; returns true if the
     * Items contained in both ItemStacks are
     * equal
     */
    public boolean isItemEqual(ItemStack p_77969_1_) {
        return this.item == p_77969_1_.item && this.itemDamage == p_77969_1_.itemDamage;
    }

    public String getUnlocalizedName() {
        return this.item.getUnlocalizedName(this);
    }

    public String toString() {
        return this.stackSize + "x" + this.item.getUnlocalizedName() + "@" + this.itemDamage;
    }

    /**
     * Called each tick as long the ItemStack in on player inventory. Used to
     * progress the pickup animation and update
     * maps.
     */
    public void updateAnimation(World p_77945_1_, Entity p_77945_2_, int p_77945_3_, boolean p_77945_4_) {
        if (this.animationsToGo > 0) {
            --this.animationsToGo;
        }

        this.item.onUpdate(this, p_77945_1_, p_77945_2_, p_77945_3_, p_77945_4_);
    }

    public void onCrafting(World p_77980_1_, EntityPlayer p_77980_2_, int p_77980_3_) {
        p_77980_2_.addStat(StatList.objectCraftStats[Item.getIdFromItem(this.item)], p_77980_3_);
        this.item.onCreated(this, p_77980_1_, p_77980_2_);
    }

    public int getMaxItemUseDuration() {
        return this.getItem().getMaxItemUseDuration(this);
    }

    public EnumAction getItemUseAction() {
        return this.getItem().getItemUseAction(this);
    }

    /**
     * Called when the player releases the use item button. Args: world,
     * entityplayer, itemInUseCount
     */
    public void onPlayerStoppedUsing(World p_77974_1_, EntityPlayer p_77974_2_, int p_77974_3_) {
        this.getItem().onPlayerStoppedUsing(this, p_77974_1_, p_77974_2_, p_77974_3_);
    }

    /**
     * Returns true if the ItemStack has an NBTTagCompound. Currently used to store
     * enchantments.
     */
    public boolean hasTagCompound() {
        return this.stackTagCompound != null;
    }

    /**
     * Returns the NBTTagCompound of the ItemStack.
     */
    public NBTTagCompound getTagCompound() {
        return this.stackTagCompound;
    }

    /**
     * Assigns a NBTTagCompound to the ItemStack, minecraft validates that only
     * non-stackable items can have it.
     */
    public void setTagCompound(NBTTagCompound p_77982_1_) {
        this.stackTagCompound = p_77982_1_;
    }

    public NBTTagList getEnchantmentTagList() {
        return this.stackTagCompound == null ? null : this.stackTagCompound.getTagList("ench", 10);
    }

    /**
     * returns the display name of the itemstack
     */
    public String getDisplayName() {
        String var1 = this.getItem().getItemStackDisplayName(this);

        if (this.stackTagCompound != null && this.stackTagCompound.func_150297_b("display", 10)) {
            NBTTagCompound var2 = this.stackTagCompound.getCompoundTag("display");

            if (var2.func_150297_b("Name", 8)) {
                var1 = var2.getString("Name");
            }
        }

        return var1;
    }

    public ItemStack setStackDisplayName(String p_151001_1_) {
        if (this.stackTagCompound == null) {
            this.stackTagCompound = new NBTTagCompound();
        }

        if (!this.stackTagCompound.func_150297_b("display", 10)) {
            this.stackTagCompound.setTag("display", new NBTTagCompound());
        }

        this.stackTagCompound.getCompoundTag("display").setString("Name", p_151001_1_);
        return this;
    }

    public void func_135074_t() {
        if (this.stackTagCompound != null) {
            if (this.stackTagCompound.func_150297_b("display", 10)) {
                NBTTagCompound var1 = this.stackTagCompound.getCompoundTag("display");
                var1.removeTag("Name");

                if (var1.hasNoTags()) {
                    this.stackTagCompound.removeTag("display");

                    if (this.stackTagCompound.hasNoTags()) {
                        this.setTagCompound(null);
                    }
                }
            }
        }
    }

    /**
     * Returns true if the itemstack has a display name
     */
    public boolean hasDisplayName() {
        return this.stackTagCompound != null && (this.stackTagCompound.func_150297_b("display", 10) && this.stackTagCompound.getCompoundTag("display").func_150297_b("Name", 8));
    }

    /**
     * Return a list of strings containing information about the item
     */
    public List<String> getTooltip(EntityPlayer p_82840_1_, boolean p_82840_2_) {
        ArrayList<String> var3 = new ArrayList<>();
        String var4 = this.getDisplayName();

        if (this.hasDisplayName()) {
            var4 = EnumChatFormatting.ITALIC + var4 + EnumChatFormatting.RESET;
        }

        int var6;

        if (p_82840_2_) {
            String var5 = "";

            if (var4.length() > 0) {
                var4 = var4 + " (";
                var5 = ")";
            }

            var6 = Item.getIdFromItem(this.item);

            if (this.getHasSubtypes()) {
                var4 = var4 + String.format("#%04d/%d%s",
                        Integer.valueOf(var6), Integer.valueOf(this.itemDamage), var5);
            } else {
                var4 = var4 + String.format("#%04d%s", Integer.valueOf(var6), var5);
            }
        } else if (!this.hasDisplayName() && this.item == Items.filled_map) {
            var4 = var4 + " #" + this.itemDamage;
        }

        var3.add(var4);
        this.item.addInformation(this, p_82840_1_, var3, p_82840_2_);

        if (this.hasTagCompound()) {
            NBTTagList var13 = this.getEnchantmentTagList();

            if (var13 != null) {
                for (var6 = 0; var6 < var13.tagCount(); ++var6) {
                    short var7 = var13.getCompoundTagAt(var6).getShort("id");
                    short var8 = var13.getCompoundTagAt(var6).getShort("lvl");

                    if (Enchantment.enchantmentsList[var7] != null) {
                        var3.add(Enchantment.enchantmentsList[var7].getTranslatedName(var8));
                    }
                }
            }

            if (this.stackTagCompound.func_150297_b("display", 10)) {
                NBTTagCompound var15 = this.stackTagCompound.getCompoundTag("display");

                if (var15.func_150297_b("color", 3)) {
                    if (p_82840_2_) {
                        var3.add("Color: #" + Integer.toHexString(var15.getInteger("color")).toUpperCase());
                    } else {
                        var3.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("item.dyed"));
                    }
                }

                if (var15.func_150299_b("Lore") == 9) {
                    NBTTagList var17 = var15.getTagList("Lore", 8);

                    if (var17.tagCount() > 0) {
                        for (int var19 = 0; var19 < var17.tagCount(); ++var19) {
                            var3.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC
                                    + var17.getStringTagAt(var19));
                        }
                    }
                }
            }
        }

        Multimap var14 = this.getAttributeModifiers();

        if (!var14.isEmpty()) {
            var3.add("");
            Iterator var16 = var14.entries().iterator();

            while (var16.hasNext()) {
                Entry var18 = (Entry) var16.next();
                AttributeModifier var20 = (AttributeModifier) var18.getValue();
                double var9 = var20.getAmount();

                if (var20.getID() == Item.field_111210_e) {
                    var9 += EnchantmentHelper.func_152377_a(this, EnumCreatureAttribute.UNDEFINED);
                }

                double var11;

                if (var20.getOperation() != 1 && var20.getOperation() != 2) {
                    var11 = var9;
                } else {
                    var11 = var9 * 100.0D;
                }

                if (var9 > 0.0D) {
                    var3.add(EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted(
                            "attribute.modifier.plus." + var20.getOperation(),
                            new Object[]{field_111284_a.format(var11),
                                    StatCollector.translateToLocal("attribute.name." + var18.getKey())}));
                } else if (var9 < 0.0D) {
                    var11 *= -1.0D;
                    var3.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted(
                            "attribute.modifier.take." + var20.getOperation(),
                            new Object[]{field_111284_a.format(var11),
                                    StatCollector.translateToLocal("attribute.name." + var18.getKey())}));
                }
            }
        }

        if (this.hasTagCompound() && this.getTagCompound().getBoolean("Unbreakable")) {
            var3.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("item.unbreakable"));
        }

        if (p_82840_2_ && this.isItemDamaged()) {
            var3.add("Durability: " + (this.getMaxDamage() - this.getItemDamageForDisplay()) + " / "
                    + this.getMaxDamage());
        }

        return var3;
    }

    public boolean hasEffect() {
        return this.getItem().hasEffect(this);
    }

    public EnumRarity getRarity() {
        return this.getItem().getRarity(this);
    }

    /**
     * True if it is a tool and has no enchantments to begin with
     */
    public boolean isItemEnchantable() {
        return this.getItem().isItemTool(this) && !this.isItemEnchanted();
    }

    /**
     * Adds an enchantment with a desired level on the ItemStack.
     */
    public void addEnchantment(Enchantment p_77966_1_, int p_77966_2_) {
        if (this.stackTagCompound == null) {
            this.setTagCompound(new NBTTagCompound());
        }

        if (!this.stackTagCompound.func_150297_b("ench", 9)) {
            this.stackTagCompound.setTag("ench", new NBTTagList());
        }

        NBTTagList var3 = this.stackTagCompound.getTagList("ench", 10);
        NBTTagCompound var4 = new NBTTagCompound();
        var4.setShort("id", (short) p_77966_1_.effectId);
        var4.setShort("lvl", (byte) p_77966_2_);
        var3.appendTag(var4);
    }

    /**
     * True if the item has enchantment data
     */
    public boolean isItemEnchanted() {
        return this.stackTagCompound != null && this.stackTagCompound.func_150297_b("ench", 9);
    }

    public void setTagInfo(String p_77983_1_, NBTBase p_77983_2_) {
        if (this.stackTagCompound == null) {
            this.setTagCompound(new NBTTagCompound());
        }

        this.stackTagCompound.setTag(p_77983_1_, p_77983_2_);
    }

    public boolean canEditBlocks() {
        return this.getItem().canItemEditBlocks();
    }

    /**
     * Return whether this stack is on an item frame.
     */
    public boolean isOnItemFrame() {
        return this.itemFrame != null;
    }

    /**
     * Return the item frame this stack is on. Returns null if not on an item frame.
     */
    public EntityItemFrame getItemFrame() {
        return this.itemFrame;
    }

    /**
     * Set the item frame this stack is on.
     */
    public void setItemFrame(EntityItemFrame p_82842_1_) {
        this.itemFrame = p_82842_1_;
    }

    /**
     * Get this stack's repair cost, or 0 if no repair cost is defined.
     */
    public int getRepairCost() {
        return this.hasTagCompound() && this.stackTagCompound.func_150297_b("RepairCost", 3)
                ? this.stackTagCompound.getInteger("RepairCost")
                : 0;
    }

    /**
     * Set this stack's repair cost.
     */
    public void setRepairCost(int p_82841_1_) {
        if (!this.hasTagCompound()) {
            this.stackTagCompound = new NBTTagCompound();
        }

        this.stackTagCompound.setInteger("RepairCost", p_82841_1_);
    }

    /**
     * Gets the attribute modifiers for this ItemStack.\nWill check for an NBT tag
     * list containing modifiers for the
     * stack.
     */
    public Multimap getAttributeModifiers() {
        Multimap var1;

        if (this.hasTagCompound() && this.stackTagCompound.func_150297_b("AttributeModifiers", 9)) {
            var1 = HashMultimap.create();
            NBTTagList var2 = this.stackTagCompound.getTagList("AttributeModifiers", 10);

            for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
                NBTTagCompound var4 = var2.getCompoundTagAt(var3);
                AttributeModifier var5 = SharedMonsterAttributes.readAttributeModifierFromNBT(var4);

                if (var5.getID().getLeastSignificantBits() != 0L && var5.getID().getMostSignificantBits() != 0L) {
                    var1.put(var4.getString("AttributeName"), var5);
                }
            }
        } else {
            var1 = this.getItem().getItemAttributeModifiers();
        }

        return var1;
    }

    @Override
    public double getAttackDamage() {
        double baseAmount = 0;
        double multiplierBase = 0;
        double multiplier = 1;

        // Loop through the attribute modifiers and apply the operations
        for (AttributeModifier var1 : ((Set<AttributeModifier>) this.getAttributeModifiers()
                .get("generic.attackDamage"))) {
            switch (var1.getOperation()) {
                case 0: // Add operation
                    baseAmount += var1.getAmount();
                    break;
                case 1: // Multiply base operation
                    multiplierBase += var1.getAmount();
                    break;
                case 2: // Multiply operation
                    multiplier *= (1 + var1.getAmount());
                    break;
                default:
                    break;
            }
        }

        // Apply the multiply_base operation
        double amount = baseAmount * (1 + multiplierBase);
        // Apply the multiply operation
        amount *= multiplier;

        // Apply any additional bonuses from enchantments
        amount += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, this) * 1.25D;

        return amount;
    }

    public void func_150996_a(Item p_150996_1_) {
        this.item = p_150996_1_;
    }

    public IChatComponent func_151000_E() {
        IChatComponent var1 = (new ChatComponentText("[")).appendText(this.getDisplayName()).appendText("]");

        if (this.item != null) {
            NBTTagCompound var2 = new NBTTagCompound();
            this.writeToNBT(var2);
            var1.getChatStyle().setChatHoverEvent(
                    new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ChatComponentText(var2.toString())));
            var1.getChatStyle().setColor(this.getRarity().rarityColor);
        }

        return var1;
    }

    @Override
    public int getDurability() {
        return this.getItemDamage();
    }

    @Override
    public Potion getPotion() {
        if (getItem().getId() != Item.POTION)
            return null;
        return this;
    }

    @Override
    public boolean isSplash() {
        if (getItem().getId() != Item.POTION)
            throw new IllegalStateException("Item is not a potion");

        return ItemPotion.isSplash(this.getItemDamage());
    }

    @Override
    public List<PotionEffect> getEffects() {
        if (getItem() instanceof ItemPotion potion)
            return Collections.unmodifiableList(potion.getEffects(this));

        throw new IllegalStateException("Item is not a potion");
    }
}
