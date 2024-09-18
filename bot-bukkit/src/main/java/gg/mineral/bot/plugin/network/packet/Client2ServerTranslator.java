package gg.mineral.bot.plugin.network.packet;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;

import io.netty.buffer.Unpooled;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.status.INetHandlerStatusServer;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayInAbilities;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInChat;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInCloseWindow;
import net.minecraft.server.v1_8_R3.PacketPlayInCustomPayload;
import net.minecraft.server.v1_8_R3.PacketPlayInEnchantItem;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInHeldItemSlot;
import net.minecraft.server.v1_8_R3.PacketPlayInKeepAlive;
import net.minecraft.server.v1_8_R3.PacketPlayInSetCreativeSlot;
import net.minecraft.server.v1_8_R3.PacketPlayInSettings;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_8_R3.PacketPlayInTabComplete;
import net.minecraft.server.v1_8_R3.PacketPlayInTransaction;
import net.minecraft.server.v1_8_R3.PacketPlayInUpdateSign;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayInWindowClick;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.util.IChatComponent;

public class Client2ServerTranslator
        implements INetHandlerPlayServer, INetHandlerStatusServer, INetHandlerLoginServer, INetHandlerHandshakeServer {

    @Setter
    private PlayerConnection playerConnection;

    @Override
    public void onDisconnect(IChatComponent p_147231_1_) {
        String text = IChatComponent.Serializer.toJson(p_147231_1_);
        IChatBaseComponent chatComponent = IChatBaseComponent.ChatSerializer.a(text);
        playerConnection.a(chatComponent);
    }

    @Override
    public void onConnectionStateTransition(EnumConnectionState p_147232_1_, EnumConnectionState p_147232_2_) {
        // TODO: Implement
    }

    @Override
    public void onNetworkTick() {
        // TODO: Implement
    }

    @Override
    public void processAnimation(C0APacketAnimation p_147350_1_) {
        PacketPlayInArmAnimation packet = new PacketPlayInArmAnimation();
        packet.timestamp = System.currentTimeMillis();
        playerConnection.a(packet);
    }

    @Override
    public void processChatMessage(C01PacketChatMessage p_147354_1_) {
        PacketPlayInChat packet = new PacketPlayInChat(p_147354_1_.getMessage());
        playerConnection.a(packet);
    }

    @Override
    public void processTabComplete(C14PacketTabComplete p_147341_1_) {
        PacketPlayInTabComplete packet = new PacketPlayInTabComplete(p_147341_1_.getMessage());
        playerConnection.a(packet);
    }

    @Override
    public void processClientStatus(C16PacketClientStatus p_147342_1_) {
        PacketPlayInClientCommand packet = new PacketPlayInClientCommand(
                net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand.values()[p_147342_1_.getState()
                        .ordinal()]);
        playerConnection.a(packet);
    }

    @Override
    public void processClientSettings(C15PacketClientSettings p_147352_1_) {
        String locale = p_147352_1_.getLocale();
        int viewDistance = p_147352_1_.getViewDistance();
        EntityPlayer.EnumChatVisibility chatFlags = p_147352_1_.getChatFlags();
        boolean chatColors = p_147352_1_.isChatColors();
        boolean showCape = p_147352_1_.isShowCape();

        net.minecraft.server.v1_8_R3.EntityHuman.EnumChatVisibility chatFlagsObj = net.minecraft.server.v1_8_R3.EntityHuman.EnumChatVisibility
                .a(chatFlags.getChatVisibility());
        byte clothingByte = 0;

        if (showCape)
            clothingByte |= 0x01;

        PacketPlayInSettings packet = new PacketPlayInSettings(locale, viewDistance, chatFlagsObj, chatColors,
                clothingByte);
        playerConnection.a(packet);
    }

    @Override
    public void processConfirmTransaction(C0FPacketConfirmTransaction p_147339_1_) {
        PacketPlayInTransaction packet = new PacketPlayInTransaction(p_147339_1_.getWindowId(),
                p_147339_1_.getActionNumber(), p_147339_1_.isAccepted());
        playerConnection.a(packet);
    }

    @Override
    public void processEnchantItem(C11PacketEnchantItem p_147338_1_) {
        PacketPlayInEnchantItem packet = new PacketPlayInEnchantItem(p_147338_1_.getWindowId(),
                p_147338_1_.getEnchantment());
        playerConnection.a(packet);
    }

    public static Material getMaterial(Item item) {
        @SuppressWarnings("deprecation")
        Material material = Material.getMaterial(Item.getIdFromItem(item));
        return material == null ? Material.AIR : material;
    }

    public static org.bukkit.inventory.ItemStack asBukkitCopy(ItemStack original) {
        if (original == null)
            return new org.bukkit.inventory.ItemStack(Material.AIR);

        org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(getMaterial(original.getItem()),
                original.stackSize,
                (short) original.getDurability());
        // TODO: if (hasItemMeta(original))
        // stack.setItemMeta(getItemMeta(original));

        return stack;
    }

    @Nullable
    private net.minecraft.server.v1_8_R3.ItemStack fromNMC(ItemStack itemNMC) {
        if (itemNMC == null)
            return null;

        org.bukkit.inventory.ItemStack itemStack = asBukkitCopy(itemNMC);

        return itemStack == null ? null : CraftItemStack.asNMSCopy(itemStack);
    }

    static boolean hasItemMeta(ItemStack item) {
        return item != null && item.getTagCompound() != null && !item.getTagCompound().hasNoTags();
    }

    @Override
    public void processClickWindow(C0EPacketClickWindow p_147351_1_) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = fromNMC(p_147351_1_.getClickedItem());
        PacketPlayInWindowClick packet = new PacketPlayInWindowClick(p_147351_1_.getWindowId(),
                p_147351_1_.getSlot(),
                p_147351_1_.getButton(), p_147351_1_.getActionNumber(), nmsItem,
                p_147351_1_.getMode());
        playerConnection.a(packet);
    }

    @Override
    public void processCloseWindow(C0DPacketCloseWindow p_147356_1_) {
        PacketPlayInCloseWindow packet = new PacketPlayInCloseWindow(p_147356_1_.getWindowId());
        playerConnection.a(packet);
    }

    @Override
    public void processVanilla250Packet(C17PacketCustomPayload p_147349_1_) {
        PacketPlayInCustomPayload packet = new PacketPlayInCustomPayload(p_147349_1_.getChannel(),
                new PacketDataSerializer(Unpooled.wrappedBuffer(p_147349_1_.getData())));
        playerConnection.a(packet);
    }

    @Override
    public void processUseEntity(C02PacketUseEntity p_147340_1_) {
        PacketPlayInUseEntity packet = new PacketPlayInUseEntity(p_147340_1_.getEntityId(),
                net.minecraft.server.v1_8_R3.PacketPlayInUseEntity.EnumEntityUseAction
                        .values()[p_147340_1_.getAction().ordinal() % C02PacketUseEntity.Action.values().length]);

        playerConnection.a(packet);
    }

    @Override
    public void processKeepAlive(C00PacketKeepAlive p_147353_1_) {
        PacketPlayInKeepAlive packet = new PacketPlayInKeepAlive(p_147353_1_.getId());
        playerConnection.a(packet);
    }

    @Override
    public void processPlayer(C03PacketPlayer p_147347_1_) {
        PacketPlayInFlying packet;

        if (p_147347_1_.isHasPos() && p_147347_1_.isHasLook()) {
            packet = new PacketPlayInFlying.PacketPlayInPositionLook(p_147347_1_.getX(), p_147347_1_.getY(),
                    p_147347_1_.getZ(), p_147347_1_.getYaw(), p_147347_1_.getPitch(), p_147347_1_.isOnGround());
        } else if (p_147347_1_.isHasPos()) {
            packet = new PacketPlayInFlying.PacketPlayInPosition(p_147347_1_.getX(), p_147347_1_.getY(),
                    p_147347_1_.getZ(), p_147347_1_.isOnGround());
        } else if (p_147347_1_.isHasLook()) {
            packet = new PacketPlayInFlying.PacketPlayInLook(p_147347_1_.getYaw(), p_147347_1_.getPitch(),
                    p_147347_1_.isOnGround());
        } else {
            packet = new PacketPlayInFlying(p_147347_1_.isOnGround());
        }

        playerConnection.a(packet);
    }

    @Override
    public void processPlayerAbilities(C13PacketPlayerAbilities p_147348_1_) {
        PacketPlayInAbilities packet = new PacketPlayInAbilities(p_147348_1_.isDisableDamage(),
                p_147348_1_.isFlying(), p_147348_1_.isAllowFlying(), p_147348_1_.isCreativeMode(),
                p_147348_1_.getFlySpeed(), p_147348_1_.getWalkSpeed());
        playerConnection.a(packet);
    }

    @Override
    public void processPlayerDigging(C07PacketPlayerDigging p_147345_1_) {
        PacketPlayInBlockDig packet = new PacketPlayInBlockDig(
                new BlockPosition(p_147345_1_.getX(), p_147345_1_.getY(), p_147345_1_.getZ()),
                net.minecraft.server.v1_8_R3.EnumDirection.fromType1(p_147345_1_.getFace()),
                net.minecraft.server.v1_8_R3.PacketPlayInBlockDig.EnumPlayerDigType.values()[p_147345_1_.getStatus()]);

        playerConnection.a(packet);
    }

    @Override
    public void processEntityAction(C0BPacketEntityAction p_147357_1_) {
        net.minecraft.server.v1_8_R3.PacketPlayInEntityAction.EnumPlayerAction nmsAction = net.minecraft.server.v1_8_R3.PacketPlayInEntityAction.EnumPlayerAction
                .values()[p_147357_1_
                        .getActionId() - 1];

        PacketPlayInEntityAction packet = new PacketPlayInEntityAction(p_147357_1_.getEntityId(),
                nmsAction,
                p_147357_1_.getJumpBoost());

        playerConnection.a(packet);
    }

    @Override
    public void processInput(C0CPacketInput p_147358_1_) {
        PacketPlayInSteerVehicle packet = new PacketPlayInSteerVehicle(p_147358_1_.getSideways(),
                p_147358_1_.getForward(), p_147358_1_.isJump(), p_147358_1_.isUnmount());
        playerConnection.a(packet);
    }

    @Override
    public void processHeldItemChange(C09PacketHeldItemChange p_147355_1_) {
        PacketPlayInHeldItemSlot packet = new PacketPlayInHeldItemSlot(p_147355_1_.getSlot());
        playerConnection.a(packet);
    }

    @Override
    public void processCreativeInventoryAction(C10PacketCreativeInventoryAction p_147344_1_) {
        ItemStack clickedItem = p_147344_1_.getClickedItem();

        net.minecraft.server.v1_8_R3.ItemStack nmsItem = fromNMC(clickedItem);
        PacketPlayInSetCreativeSlot packet = new PacketPlayInSetCreativeSlot(p_147344_1_.getSlot(),
                nmsItem);
        playerConnection.a(packet);
    }

    @Override
    public void processUpdateSign(C12PacketUpdateSign p_147343_1_) {
        IChatBaseComponent[] lines = new IChatBaseComponent[4];

        for (int i = 0; i < 4; i++)
            lines[i] = IChatBaseComponent.ChatSerializer.a(p_147343_1_.getLines()[i]);

        PacketPlayInUpdateSign packet = new PacketPlayInUpdateSign(new BlockPosition(p_147343_1_.getX(),
                p_147343_1_.getY(), p_147343_1_.getZ()), lines);
        playerConnection.a(packet);
    }

    @Override
    public void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement p_147346_1_) {
        ItemStack clickedItem = p_147346_1_.getHeldItem();

        net.minecraft.server.v1_8_R3.ItemStack nmsItem = fromNMC(clickedItem);
        PacketPlayInBlockPlace packet = new PacketPlayInBlockPlace(new BlockPosition(p_147346_1_.getX(),
                p_147346_1_.getY(), p_147346_1_.getZ()), p_147346_1_.getDirection(),
                nmsItem,
                p_147346_1_.getCursorX(), p_147346_1_.getCursorY(), p_147346_1_.getCursorZ());
        playerConnection.a(packet);
    }

    @Override
    public void processHandshake(C00Handshake p_147383_1_) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processHandshake'");
    }

    @Override
    public void processLoginStart(C00PacketLoginStart p_147316_1_) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processLoginStart'");
    }

    @Override
    public void processEncryptionResponse(C01PacketEncryptionResponse p_147315_1_) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processEncryptionResponse'");
    }

    @Override
    public void processPing(C01PacketPing p_147311_1_) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processPing'");
    }

    @Override
    public void processServerQuery(C00PacketServerQuery p_147312_1_) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processServerQuery'");
    }

}
