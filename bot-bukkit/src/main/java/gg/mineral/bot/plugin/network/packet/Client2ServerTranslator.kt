package gg.mineral.bot.plugin.network.packet

import io.netty.buffer.Unpooled
import lombok.SneakyThrows
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.handshake.INetHandlerHandshakeServer
import net.minecraft.network.handshake.client.C00Handshake
import net.minecraft.network.login.INetHandlerLoginServer
import net.minecraft.network.login.client.C00PacketLoginStart
import net.minecraft.network.login.client.C01PacketEncryptionResponse
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.network.status.INetHandlerStatusServer
import net.minecraft.network.status.client.C00PacketServerQuery
import net.minecraft.network.status.client.C01PacketPing
import net.minecraft.server.v1_8_R3.*
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig.EnumPlayerDigType
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction.EnumPlayerAction
import net.minecraft.server.v1_8_R3.PacketPlayInFlying.*
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity.EnumEntityUseAction
import net.minecraft.util.IChatComponent
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack

class Client2ServerTranslator

    : INetHandlerPlayServer, INetHandlerStatusServer, INetHandlerLoginServer,
    INetHandlerHandshakeServer {
    lateinit var playerConnection: PlayerConnection

    override fun onDisconnect(chatComp: IChatComponent) {
        val text = IChatComponent.Serializer.toJson(chatComp)
        val chatComponent = IChatBaseComponent.ChatSerializer.a(text)
        playerConnection.a(chatComponent)
    }

    override fun onConnectionStateTransition(state: EnumConnectionState, newState: EnumConnectionState) {
        // TODO: Implement
    }

    override fun onNetworkTick() {
        // TODO: Implement
    }

    @SneakyThrows
    override fun processAnimation(clientPacket: C0APacketAnimation) {
        val packet = PacketPlayInArmAnimation()
        packet.timestamp = System.currentTimeMillis()
        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processChatMessage(clientPacket: C01PacketChatMessage) {
        val packet = PacketPlayInChat(clientPacket.message)
        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processTabComplete(clientPacket: C14PacketTabComplete) {
        val packet = PacketPlayInTabComplete(clientPacket.message)
        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processClientStatus(clientPacket: C16PacketClientStatus) {
        val packet = PacketPlayInClientCommand(
            EnumClientCommand.entries.toTypedArray()[clientPacket
                .state
                .ordinal]
        )
        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processClientSettings(clientPacket: C15PacketClientSettings) {
        val locale = clientPacket.locale
        val viewDistance = clientPacket.viewDistance
        val chatFlags = clientPacket.chatFlags
        val chatColors = clientPacket.isChatColors
        val showCape = clientPacket.isShowCape

        val chatFlagsNMS = EntityHuman.EnumChatVisibility
            .a(chatFlags.chatVisibility)
        var clothingByte: Byte = 0

        if (showCape) clothingByte = (clothingByte.toInt() or 0x01).toByte()

        val packet = PacketPlayInSettings(
            locale, viewDistance, chatFlagsNMS, chatColors,
            clothingByte.toInt()
        )
        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processConfirmTransaction(clientPacket: C0FPacketConfirmTransaction) {
        val packet = PacketPlayInTransaction(
            clientPacket.windowId,
            clientPacket.actionNumber, clientPacket.isAccepted
        )
        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processEnchantItem(clientPacket: C11PacketEnchantItem) {
        val packet = PacketPlayInEnchantItem(
            clientPacket.windowId,
            clientPacket.enchantment
        )
        playerConnection.networkManager.a(null, packet)
    }

    private fun fromNMC(itemNMC: ItemStack?): net.minecraft.server.v1_8_R3.ItemStack? {

        val itemStack = asBukkitCopy(itemNMC)

        val nmsItemStack = CraftItemStack.asNMSCopy(itemStack)

        return nmsItemStack
    }

    @SneakyThrows
    override fun processClickWindow(clientPacket: C0EPacketClickWindow) {
        val nmsItem = fromNMC(clientPacket.clickedItem)
        val packet = PacketPlayInWindowClick(
            clientPacket.windowId,
            clientPacket.slot,
            clientPacket.button, clientPacket.actionNumber, nmsItem,
            clientPacket.mode
        )
        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processCloseWindow(clientPacket: C0DPacketCloseWindow) {
        val packet = PacketPlayInCloseWindow(clientPacket.windowId)
        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processVanilla250Packet(clientPacket: C17PacketCustomPayload) {
        val packet = PacketPlayInCustomPayload(
            clientPacket.channel,
            PacketDataSerializer(Unpooled.wrappedBuffer(clientPacket.data))
        )
        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processUseEntity(clientPacket: C02PacketUseEntity) {
        val packet = PacketPlayInUseEntity(
            clientPacket.entityId,
            EnumEntityUseAction.entries.toTypedArray()[clientPacket.action.ordinal % net.minecraft.network.play.client.C02PacketUseEntity.Action.entries.toTypedArray().size]
        )

        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processKeepAlive(clientPacket: C00PacketKeepAlive) {
        val packet = PacketPlayInKeepAlive(clientPacket.id)
        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processPlayer(clientPacket: C03PacketPlayer) {
        val packet = if (clientPacket.isHasPos && clientPacket.isHasLook) PacketPlayInPositionLook(
            clientPacket.x, clientPacket.y,
            clientPacket.z, clientPacket.yaw, clientPacket.pitch, clientPacket.isOnGround
        )
        else if (clientPacket.isHasPos) PacketPlayInPosition(
            clientPacket.x, clientPacket.y,
            clientPacket.z, clientPacket.isOnGround
        )
        else if (clientPacket.isHasLook) PacketPlayInLook(
            clientPacket.yaw, clientPacket.pitch,
            clientPacket.isOnGround
        )
        else PacketPlayInFlying(clientPacket.isOnGround)

        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processPlayerAbilities(clientPacket: C13PacketPlayerAbilities) {
        val packet = PacketPlayInAbilities(
            clientPacket.isDisableDamage,
            clientPacket.isFlying, clientPacket.isAllowFlying, clientPacket.isCreativeMode,
            clientPacket.flySpeed, clientPacket.walkSpeed
        )
        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processPlayerDigging(clientPacket: C07PacketPlayerDigging) {
        val packet = PacketPlayInBlockDig(
            BlockPosition(clientPacket.x, clientPacket.y, clientPacket.z),
            EnumDirection.fromType1(clientPacket.face),
            EnumPlayerDigType.entries.toTypedArray()[clientPacket.status]
        )

        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processEntityAction(clientPacket: C0BPacketEntityAction) {
        val nmsAction: EnumPlayerAction = EnumPlayerAction
            .entries.toTypedArray()[clientPacket
            .actionId - 1]

        val packet = PacketPlayInEntityAction(
            clientPacket.entityId,
            nmsAction,
            clientPacket.jumpBoost
        )

        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processInput(clientPacket: C0CPacketInput) {
        val packet = PacketPlayInSteerVehicle(
            clientPacket.sideways,
            clientPacket.forward, clientPacket.isJump, clientPacket.isUnmount
        )
        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processHeldItemChange(clientPacket: C09PacketHeldItemChange) {
        val packet = PacketPlayInHeldItemSlot(clientPacket.slot)
        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processCreativeInventoryAction(clientPacket: C10PacketCreativeInventoryAction) {
        val clickedItem = clientPacket.clickedItem

        val nmsItem = fromNMC(clickedItem)
        val packet = PacketPlayInSetCreativeSlot(
            clientPacket.slot,
            nmsItem
        )
        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processUpdateSign(clientPacket: C12PacketUpdateSign) {
        val lines = arrayOfNulls<IChatBaseComponent>(4)

        for (i in 0..3) lines[i] = IChatBaseComponent.ChatSerializer.a(clientPacket.lines[i])

        val packet = PacketPlayInUpdateSign(
            BlockPosition(
                clientPacket.x,
                clientPacket.y, clientPacket.z
            ), lines
        )
        playerConnection.networkManager.a(null, packet)
    }

    @SneakyThrows
    override fun processPlayerBlockPlacement(clientPacket: C08PacketPlayerBlockPlacement) {
        val clickedItem = clientPacket.heldItem

        val nmsItem = fromNMC(clickedItem)
        val packet = PacketPlayInBlockPlace(
            BlockPosition(
                clientPacket.x,
                clientPacket.y, clientPacket.z
            ), clientPacket.direction,
            nmsItem,
            clientPacket.cursorX, clientPacket.cursorY, clientPacket.cursorZ
        )
        playerConnection.networkManager.a(null, packet)
    }

    override fun processHandshake(clientPacket: C00Handshake) {
        // TODO Auto-generated method stub
        throw UnsupportedOperationException("Unimplemented method 'processHandshake'")
    }

    override fun processLoginStart(clientPacket: C00PacketLoginStart) {
        // TODO Auto-generated method stub
        throw UnsupportedOperationException("Unimplemented method 'processLoginStart'")
    }

    override fun processEncryptionResponse(clientPacket: C01PacketEncryptionResponse) {
        // TODO Auto-generated method stub
        throw UnsupportedOperationException("Unimplemented method 'processEncryptionResponse'")
    }

    override fun processPing(clientPacket: C01PacketPing) {
        // TODO Auto-generated method stub
        throw UnsupportedOperationException("Unimplemented method 'processPing'")
    }

    override fun processServerQuery(clientPacket: C00PacketServerQuery) {
        // TODO Auto-generated method stub
        throw UnsupportedOperationException("Unimplemented method 'processServerQuery'")
    }

    companion object {
        @Suppress("DEPRECATION")
        private fun getMaterial(item: Item?): Material {
            val material = Material.getMaterial(Item.getIdFromItem(item))
            return material ?: Material.AIR
        }

        fun asBukkitCopy(original: ItemStack?): org.bukkit.inventory.ItemStack {
            if (original == null) return org.bukkit.inventory.ItemStack(Material.AIR)

            val stack = org.bukkit.inventory.ItemStack(
                getMaterial(original.item),
                original.stackSize,
                original.durability.toShort()
            )

            // TODO: if (hasItemMeta(original))
            // stack.setItemMeta(getItemMeta(original));
            return stack
        }

        fun hasItemMeta(item: ItemStack): Boolean {
            return item.tagCompound != null && !item.tagCompound.hasNoTags()
        }
    }
}
