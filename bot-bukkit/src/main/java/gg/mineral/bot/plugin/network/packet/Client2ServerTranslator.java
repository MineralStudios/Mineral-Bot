package gg.mineral.bot.plugin.network.packet;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;

import io.netty.buffer.Unpooled;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;

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
    public void onDisconnect(IChatComponent chatComp) {
        val text = IChatComponent.Serializer.toJson(chatComp);
        val chatComponent = IChatBaseComponent.ChatSerializer.a(text);
        playerConnection.a(chatComponent);
    }

    @Override
    public void onConnectionStateTransition(EnumConnectionState state, EnumConnectionState newState) {
        // TODO: Implement
    }

    @Override
    public void onNetworkTick() {
        // TODO: Implement
    }

    @Override
    @SneakyThrows
    public void processAnimation(C0APacketAnimation clientPacket) {
        val packet = new PacketPlayInArmAnimation();
        packet.timestamp = System.currentTimeMillis();
        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processChatMessage(C01PacketChatMessage clientPacket) {
        val packet = new PacketPlayInChat(clientPacket.getMessage());
        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processTabComplete(C14PacketTabComplete clientPacket) {
        val packet = new PacketPlayInTabComplete(clientPacket.getMessage());
        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processClientStatus(C16PacketClientStatus clientPacket) {
        val packet = new PacketPlayInClientCommand(
                net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand.values()[clientPacket
                        .getState()
                        .ordinal()]);
        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processClientSettings(C15PacketClientSettings clientPacket) {
        val locale = clientPacket.getLocale();
        val viewDistance = clientPacket.getViewDistance();
        val chatFlags = clientPacket.getChatFlags();
        val chatColors = clientPacket.isChatColors();
        val showCape = clientPacket.isShowCape();

        val chatFlagsNMS = net.minecraft.server.v1_8_R3.EntityHuman.EnumChatVisibility
                .a(chatFlags.getChatVisibility());
        byte clothingByte = 0;

        if (showCape)
            clothingByte |= 0x01;

        val packet = new PacketPlayInSettings(locale, viewDistance, chatFlagsNMS, chatColors,
                clothingByte);
        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processConfirmTransaction(C0FPacketConfirmTransaction clientPacket) {
        val packet = new PacketPlayInTransaction(clientPacket.getWindowId(),
                clientPacket.getActionNumber(), clientPacket.isAccepted());
        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processEnchantItem(C11PacketEnchantItem clientPacket) {
        val packet = new PacketPlayInEnchantItem(clientPacket.getWindowId(),
                clientPacket.getEnchantment());
        playerConnection.networkManager.a(null, packet);
    }

    public static Material getMaterial(Item item) {
        @SuppressWarnings("deprecation")
        val material = Material.getMaterial(Item.getIdFromItem(item));
        return material == null ? Material.AIR : material;
    }

    public static org.bukkit.inventory.ItemStack asBukkitCopy(ItemStack original) {
        if (original == null)
            return new org.bukkit.inventory.ItemStack(Material.AIR);

        val stack = new org.bukkit.inventory.ItemStack(getMaterial(original.getItem()),
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

        val itemStack = asBukkitCopy(itemNMC);

        if (itemStack == null)
            throw new IllegalArgumentException("Bukkit ItemStack is null");

        val nmsItemStack = CraftItemStack.asNMSCopy(itemStack);

        if (nmsItemStack == null)
            throw new IllegalArgumentException("NMS ItemStack is null");

        return nmsItemStack;
    }

    static boolean hasItemMeta(ItemStack item) {
        return item != null && item.getTagCompound() != null && !item.getTagCompound().hasNoTags();
    }

    @Override
    @SneakyThrows
    public void processClickWindow(C0EPacketClickWindow clientPacket) {
        val nmsItem = fromNMC(clientPacket.getClickedItem());
        val packet = new PacketPlayInWindowClick(clientPacket.getWindowId(),
                clientPacket.getSlot(),
                clientPacket.getButton(), clientPacket.getActionNumber(), nmsItem,
                clientPacket.getMode());
        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processCloseWindow(C0DPacketCloseWindow clientPacket) {
        val packet = new PacketPlayInCloseWindow(clientPacket.getWindowId());
        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processVanilla250Packet(C17PacketCustomPayload clientPacket) {
        val packet = new PacketPlayInCustomPayload(clientPacket.getChannel(),
                new PacketDataSerializer(Unpooled.wrappedBuffer(clientPacket.getData())));
        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processUseEntity(C02PacketUseEntity clientPacket) {
        val packet = new PacketPlayInUseEntity(clientPacket.getEntityId(),
                net.minecraft.server.v1_8_R3.PacketPlayInUseEntity.EnumEntityUseAction
                        .values()[clientPacket.getAction().ordinal() % C02PacketUseEntity.Action.values().length]);

        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processKeepAlive(C00PacketKeepAlive clientPacket) {
        val packet = new PacketPlayInKeepAlive(clientPacket.getId());
        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processPlayer(C03PacketPlayer clientPacket) {
        PacketPlayInFlying packet;

        if (clientPacket.isHasPos() && clientPacket.isHasLook())
            packet = new PacketPlayInFlying.PacketPlayInPositionLook(clientPacket.getX(), clientPacket.getY(),
                    clientPacket.getZ(), clientPacket.getYaw(), clientPacket.getPitch(), clientPacket.isOnGround());
        else if (clientPacket.isHasPos())
            packet = new PacketPlayInFlying.PacketPlayInPosition(clientPacket.getX(), clientPacket.getY(),
                    clientPacket.getZ(), clientPacket.isOnGround());
        else if (clientPacket.isHasLook())
            packet = new PacketPlayInFlying.PacketPlayInLook(clientPacket.getYaw(), clientPacket.getPitch(),
                    clientPacket.isOnGround());
        else
            packet = new PacketPlayInFlying(clientPacket.isOnGround());

        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processPlayerAbilities(C13PacketPlayerAbilities clientPacket) {
        val packet = new PacketPlayInAbilities(clientPacket.isDisableDamage(),
                clientPacket.isFlying(), clientPacket.isAllowFlying(), clientPacket.isCreativeMode(),
                clientPacket.getFlySpeed(), clientPacket.getWalkSpeed());
        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processPlayerDigging(C07PacketPlayerDigging clientPacket) {
        val packet = new PacketPlayInBlockDig(
                new BlockPosition(clientPacket.getX(), clientPacket.getY(), clientPacket.getZ()),
                net.minecraft.server.v1_8_R3.EnumDirection.fromType1(clientPacket.getFace()),
                net.minecraft.server.v1_8_R3.PacketPlayInBlockDig.EnumPlayerDigType.values()[clientPacket.getStatus()]);

        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processEntityAction(C0BPacketEntityAction clientPacket) {
        val nmsAction = net.minecraft.server.v1_8_R3.PacketPlayInEntityAction.EnumPlayerAction
                .values()[clientPacket
                        .getActionId() - 1];

        val packet = new PacketPlayInEntityAction(clientPacket.getEntityId(),
                nmsAction,
                clientPacket.getJumpBoost());

        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processInput(C0CPacketInput clientPacket) {
        val packet = new PacketPlayInSteerVehicle(clientPacket.getSideways(),
                clientPacket.getForward(), clientPacket.isJump(), clientPacket.isUnmount());
        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processHeldItemChange(C09PacketHeldItemChange clientPacket) {
        val packet = new PacketPlayInHeldItemSlot(clientPacket.getSlot());
        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processCreativeInventoryAction(C10PacketCreativeInventoryAction clientPacket) {
        val clickedItem = clientPacket.getClickedItem();

        val nmsItem = fromNMC(clickedItem);
        val packet = new PacketPlayInSetCreativeSlot(clientPacket.getSlot(),
                nmsItem);
        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processUpdateSign(C12PacketUpdateSign clientPacket) {
        val lines = new IChatBaseComponent[4];

        for (int i = 0; i < 4; i++)
            lines[i] = IChatBaseComponent.ChatSerializer.a(clientPacket.getLines()[i]);

        val packet = new PacketPlayInUpdateSign(new BlockPosition(clientPacket.getX(),
                clientPacket.getY(), clientPacket.getZ()), lines);
        playerConnection.networkManager.a(null, packet);
    }

    @Override
    @SneakyThrows
    public void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement clientPacket) {
        val clickedItem = clientPacket.getHeldItem();

        val nmsItem = fromNMC(clickedItem);
        val packet = new PacketPlayInBlockPlace(new BlockPosition(clientPacket.getX(),
                clientPacket.getY(), clientPacket.getZ()), clientPacket.getDirection(),
                nmsItem,
                clientPacket.getCursorX(), clientPacket.getCursorY(), clientPacket.getCursorZ());
        playerConnection.networkManager.a(null, packet);
    }

    @Override
    public void processHandshake(C00Handshake clientPacket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processHandshake'");
    }

    @Override
    public void processLoginStart(C00PacketLoginStart clientPacket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processLoginStart'");
    }

    @Override
    public void processEncryptionResponse(C01PacketEncryptionResponse clientPacket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processEncryptionResponse'");
    }

    @Override
    public void processPing(C01PacketPing clientPacket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processPing'");
    }

    @Override
    public void processServerQuery(C00PacketServerQuery clientPacket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processServerQuery'");
    }

}
