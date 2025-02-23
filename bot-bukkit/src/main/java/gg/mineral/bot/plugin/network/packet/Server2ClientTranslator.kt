package gg.mineral.bot.plugin.network.packet

import com.mojang.authlib.GameProfile
import gg.mineral.bot.plugin.network.packet.chunk.ChunkDataDecoder
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import lombok.Cleanup
import lombok.SneakyThrows
import net.minecraft.block.Block
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.entity.DataWatcher
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityAgeable
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.item.Item
import net.minecraft.nbt.*
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.nbt.NBTTagString
import net.minecraft.network.play.server.*
import net.minecraft.server.v1_8_R3.*
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction
import net.minecraft.server.v1_8_R3.PacketPlayOutPosition.EnumPlayerTeleportFlags
import net.minecraft.stats.StatBase
import net.minecraft.stats.StatList
import net.minecraft.util.ChunkCoordinates
import net.minecraft.util.IChatComponent
import net.minecraft.world.*
import net.minecraft.world.ChunkCoordIntPair
import net.minecraft.world.EnumDifficulty
import net.minecraft.world.WorldSettings
import net.minecraft.world.WorldType
import net.minecraft.world.chunk.storage.ExtendedBlockStorage
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemFactory
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.io.*
import java.nio.ByteBuffer

class Server2ClientTranslator : PacketListenerPlayOut, PacketLoginOutListener, PacketStatusOutListener {
    @Suppress("DEPRECATION")
    private fun asNMCCopy(original: ItemStack): net.minecraft.item.ItemStack? {
        if (original.typeId > 0) {
            val item = getItem(original.type) ?: return null

            val stack = net.minecraft.item.ItemStack(
                item,
                original.amount, original.durability.toInt()
            )
            if (original.hasItemMeta()) setItemMeta(stack, original.itemMeta)

            return stack
        }

        return null
    }

    private fun setItemMeta(item: net.minecraft.item.ItemStack?, itemMeta: ItemMeta?): Boolean {
        var itemMeta = itemMeta
        if (item == null) return false
        if (CraftItemFactory.instance().equals(itemMeta, null as ItemMeta?)) {
            item.tagCompound = null
            return true
        }
        if (!CraftItemFactory.instance().isApplicable(itemMeta, getType(item))) return false

        itemMeta = CraftItemFactory.instance().asMetaFor(itemMeta, getType(item))
        if (itemMeta == null) return true

        val tag = NBTTagCompound()
        item.tagCompound = tag
        applyToItem(itemMeta, tag)
        return true
    }

    private fun setDisplayTag(tag: NBTTagCompound, key: String?, value: NBTBase?) {
        val display = tag.getCompoundTag("display")
        if (!tag.hasKey("display")) tag.setTag("display", display)

        display.setTag(key, value)
    }

    private fun applyToItem(meta: ItemMeta, itemTag: NBTTagCompound) {
        if (meta.hasDisplayName()) setDisplayTag(itemTag, "Name", NBTTagString(meta.displayName))

        if (meta.hasLore()) setDisplayTag(itemTag, "Lore", createStringList(meta.lore))

        val hideFlag = getHideFlagFromSet(meta.itemFlags)
        if (hideFlag != 0) itemTag.setInteger("HideFlags", hideFlag)

        applyEnchantments(meta.enchants, itemTag)
        if (meta.spigot().isUnbreakable) itemTag.setBoolean("Unbreakable", true)

        // TODO: if (meta.hasRepairCost())
        // itemTag.setInt(REPAIR.NBT, this.repairCost);

        // Iterator var2 = meta.unhandledTags.entrySet().iterator();

        // while (var2.hasNext()) {
        // Entry<String, NBTBase> e = (Entry) var2.next();
        // itemTag.set((String) e.getKey(), (NBTBase) e.getValue());
        // }
    }

    private fun getHideFlagFromSet(itemFlags: Set<ItemFlag>): Int {
        var hideFlag = 0 // Start with no flags

        for (flag in itemFlags) hideFlag = hideFlag or getBitModifier(flag).toInt()

        return hideFlag
    }

    private fun getBitModifier(hideFlag: ItemFlag): Byte {
        return (1 shl hideFlag.ordinal).toByte()
    }

    private fun fromNMS(itemNMS: net.minecraft.server.v1_8_R3.ItemStack?): net.minecraft.item.ItemStack? {
        if (itemNMS == null) return null

        val itemStack = CraftItemStack
            .asBukkitCopy(itemNMS)

        return if (itemStack == null) null else asNMCCopy(itemStack)
    }

    private fun fromNMS(
        watchableObject: net.minecraft.server.v1_8_R3.DataWatcher.WatchableObject, entity: Entity
    ): DataWatcher.WatchableObject? {
        val id = watchableObject.a()
        var value = watchableObject.b()
        var type = watchableObject.c()

        when (id) {
            12 -> {
                if (entity is EntityAgeable && value is Byte) {
                    type = 2
                    value = value.toInt()
                }
            }
        }

        value = when (type) {
            5 -> fromNMS(value as net.minecraft.server.v1_8_R3.ItemStack)
            6 -> {
                val blockPosition = value as BlockPosition
                ChunkCoordinates(blockPosition.x, blockPosition.y, blockPosition.z)
            }

            else -> null
        }

        if (value == null) return null

        val dataWatcher = DataWatcher.WatchableObject(type, id, value)

        dataWatcher.isWatched = watchableObject.d()

        return dataWatcher
    }

    lateinit var netHandlerPlayClient: NetHandlerPlayClient

    fun handlePacket(packet: Packet<PacketListenerPlayOut>) {
        packet.a(this)
    }

    override fun a(serverPacket: IChatBaseComponent) {
        val text = IChatBaseComponent.ChatSerializer.a(serverPacket)
        val chatComponent = IChatComponent.Serializer.fromJson(text)
        netHandlerPlayClient.onDisconnect(chatComponent)
    }

    override fun a(serverPacket: PacketPlayOutSpawnEntity) {
        val packet = S0EPacketSpawnObject(
            serverPacket.a, serverPacket.b, serverPacket.c,
            serverPacket.d,
            serverPacket.e, serverPacket.f, serverPacket.g, serverPacket.h, serverPacket.i,
            serverPacket.j, serverPacket.k
        )
        netHandlerPlayClient.handleSpawnObject(packet)
    }

    override fun a(serverPacket: PacketPlayOutSpawnEntityExperienceOrb) {
        val packet = S11PacketSpawnExperienceOrb(
            serverPacket.a, serverPacket.b, serverPacket.c,
            serverPacket.d, serverPacket.e
        )
        netHandlerPlayClient.handleSpawnExperienceOrb(packet)
    }

    override fun a(serverPacket: PacketPlayOutSpawnEntityWeather) {
        val packet = S2CPacketSpawnGlobalEntity(
            serverPacket.a, serverPacket.b, serverPacket.c,
            serverPacket.d, serverPacket.e
        )
        netHandlerPlayClient.handleSpawnGlobalEntity(packet)
    }

    override fun a(serverPacket: PacketPlayOutSpawnEntityLiving) {
        val entityTypeId = serverPacket.b

        if (entityTypeId == 101)  // TODO: Rabbit
            return

        val dataWatcherListNMS = serverPacket.m
        val dataWatcherList = ArrayList<Any?>()

        val entityId = serverPacket.a

        val entity = netHandlerPlayClient.clientWorldController.getEntityByID(entityId)

        if (dataWatcherListNMS != null) {
            for (watchableObjectNMS in dataWatcherListNMS) {
                val watchableObject = fromNMS(
                    watchableObjectNMS,
                    entity!!
                )!!

                dataWatcherList.add(watchableObject)
            }
        }

        val packet = S0FPacketSpawnMob(
            entityId, entityTypeId, serverPacket.c, serverPacket.d,
            serverPacket.e, serverPacket.f, serverPacket.g, serverPacket.h, serverPacket.i,
            serverPacket.j, serverPacket.k, null,
            dataWatcherList
        )

        netHandlerPlayClient.handleSpawnMob(packet)
    }

    override fun a(serverPacket: PacketPlayOutScoreboardObjective) {
        val name = serverPacket.a
        val value = serverPacket.b
        val mode = serverPacket.d
        val packet = S3BPacketScoreboardObjective(name, value, mode)
        netHandlerPlayClient.handleScoreboardObjective(packet)
    }

    override fun a(serverPacket: PacketPlayOutSpawnEntityPainting) {
        val blockPos = serverPacket.b
        val packet = S10PacketSpawnPainting(
            serverPacket.a, blockPos.x, blockPos.y,
            blockPos.z, serverPacket.c.a(), serverPacket.d
        )
        netHandlerPlayClient.handleSpawnPainting(packet)
    }

    override fun a(serverPacket: PacketPlayOutNamedEntitySpawn) {
        val dataWatcherListNMS = serverPacket.j
        val dataWatcherList = ArrayList<Any?>()

        val entityId = serverPacket.a

        val entity = netHandlerPlayClient.clientWorldController.getEntityByID(entityId)

        if (dataWatcherListNMS != null) {
            for (watchableObjectNMS in dataWatcherListNMS) {
                val watchableObject = fromNMS(
                    watchableObjectNMS,
                    entity!!
                )!!

                dataWatcherList.add(watchableObject)
            }
        }

        // TODO: name
        val packet = S0CPacketSpawnPlayer(
            entityId,
            GameProfile(serverPacket.b, ""),
            serverPacket.c, serverPacket.d,
            serverPacket.e, serverPacket.f, serverPacket.g, serverPacket.h, null,
            dataWatcherList
        )
        netHandlerPlayClient.handleSpawnPlayer(packet)
    }

    override fun a(serverPacket: PacketPlayOutAnimation) {
        val packet = S0BPacketAnimation(serverPacket.a, serverPacket.b)
        netHandlerPlayClient.handleAnimation(packet)
    }

    override fun a(serverPacket: PacketPlayOutStatistic) {
        val statisticMap = serverPacket.a
        val statMap = Object2IntOpenHashMap<StatBase>()

        for (e in statisticMap.object2IntEntrySet()) {
            val stat = StatList.func_151177_a(e.key.name)
            statMap.put(stat, e.intValue)
        }

        val packet = S37PacketStatistics(statMap)
        netHandlerPlayClient.handleStatistics(packet)
    }

    override fun a(serverPacket: PacketPlayOutBlockBreakAnimation) {
        val packet = S25PacketBlockBreakAnim(
            serverPacket.a, serverPacket.b.x,
            serverPacket.b.y, serverPacket.b.z, serverPacket.c
        )
        netHandlerPlayClient.handleBlockBreakAnim(packet)
    }

    override fun a(serverPacket: PacketPlayOutOpenSignEditor) {
        val blockPos = serverPacket.a
        val packet = S36PacketSignEditorOpen(
            blockPos.x, blockPos.y,
            blockPos.z
        )
        netHandlerPlayClient.handleSignEditorOpen(packet)
    }

    @SneakyThrows(IOException::class)
    override fun a(serverPacket: PacketPlayOutTileEntityData) {
        val nmsNbt = serverPacket.c
        @Cleanup val baos = ByteArrayOutputStream()
        @Cleanup val buf = DataOutputStream(baos)

        NBTCompressedStreamTools.a(nmsNbt, buf as DataOutput)

        val bytes = baos.toByteArray()

        val dis = DataInputStream(ByteArrayInputStream(bytes))

        val nbt = CompressedStreamTools.read(dis)

        val packet = S35PacketUpdateTileEntity(
            serverPacket.a.x, serverPacket.a.y,
            serverPacket.a.z, serverPacket.b, nbt
        )
        netHandlerPlayClient.handleUpdateTileEntity(packet)
    }

    override fun a(serverPacket: PacketPlayOutBlockAction) {
        val blockId = net.minecraft.server.v1_8_R3.Block.getId(serverPacket.d) and 4095
        val block = Block.getBlockById(blockId)
        val packet = S24PacketBlockAction(
            serverPacket.a.x, serverPacket.a.y,
            serverPacket.a.z, serverPacket.b, serverPacket.c, block
        )
        netHandlerPlayClient.handleBlockAction(packet)
    }

    override fun a(serverPacket: PacketPlayOutBlockChange) {
        val idAndMeta = net.minecraft.server.v1_8_R3.Block.d.b(serverPacket.getBlock())
        val blockId = idAndMeta shr 4
        val blockMeta = idAndMeta and 15
        val block = Block.getBlockById(blockId)
        val packet = S23PacketBlockChange(
            serverPacket.a.x, serverPacket.a.y,
            serverPacket.a.z, block, blockMeta
        )
        netHandlerPlayClient.handleBlockChange(packet)
    }

    override fun a(serverPacket: PacketPlayOutChat) {
        val text = IChatBaseComponent.ChatSerializer.a(serverPacket.a)
        val chatComponent = IChatComponent.Serializer.fromJson(text) ?: return
        val packet = S02PacketChat(chatComponent)
        netHandlerPlayClient.handleChat(packet)
    }

    override fun a(serverPacket: PacketPlayOutTabComplete) {
        val packet = S3APacketTabComplete(serverPacket.a)
        netHandlerPlayClient.handleTabComplete(packet)
    }

    override fun a(serverPacket: PacketPlayOutMultiBlockChange) {
        val chunkCoordIntPairNMS = serverPacket.a
        val chunkCoordIntPair = ChunkCoordIntPair(chunkCoordIntPairNMS.x, chunkCoordIntPairNMS.z)

        val recordLength = serverPacket.b.size

        val buffer = ByteBuffer.allocate(recordLength * 4)

        for (i in 0..<recordLength) {
            val record = serverPacket.b[i]
            buffer.putShort(record.b())
            val idAndMeta = net.minecraft.server.v1_8_R3.Block.d.b(record.c())
            buffer.putShort((idAndMeta shr 4).toShort())
        }

        val packet = S22PacketMultiBlockChange(
            chunkCoordIntPair, buffer.array(),
            recordLength
        )
        netHandlerPlayClient.handleMultiBlockChange(packet)
    }

    override fun a(serverPacket: PacketPlayOutMap) {
        // TODO: implement
    }

    override fun a(serverPacket: PacketPlayOutTransaction) {
        val packet = S32PacketConfirmTransaction(serverPacket.a, serverPacket.b, serverPacket.isC)
        netHandlerPlayClient.handleConfirmTransaction(packet)
    }

    override fun a(serverPacket: PacketPlayOutCloseWindow) {
        val packet = S2EPacketCloseWindow(serverPacket.a)
        netHandlerPlayClient.handleCloseWindow(packet)
    }

    override fun a(serverPacket: PacketPlayOutWindowItems) {
        val items = arrayOfNulls<net.minecraft.item.ItemStack>(serverPacket.b.size)

        for (i in items.indices) items[i] = fromNMS(serverPacket.b[i])

        val packet = S30PacketWindowItems(serverPacket.a, items)
        netHandlerPlayClient.handleWindowItems(packet)
    }

    override fun a(serverPacket: PacketPlayOutOpenWindow) {
        val packet = S2DPacketOpenWindow(
            serverPacket.a, WINDOW_TYPE_REGISTRY.getInt(serverPacket.b),
            serverPacket.c.c(), serverPacket.d, true,
            serverPacket.e
        )
        netHandlerPlayClient.handleOpenWindow(packet)
    }

    override fun a(serverPacket: PacketPlayOutWindowData) {
        val packet = S31PacketWindowProperty(serverPacket.a, serverPacket.b, serverPacket.c)
        netHandlerPlayClient.handleWindowProperty(packet)
    }

    override fun a(serverPacket: PacketPlayOutSetSlot) {
        val itemNMS = serverPacket.c
        val item = fromNMS(itemNMS)

        val packet = S2FPacketSetSlot(
            serverPacket.a, serverPacket.b,
            item
        )
        netHandlerPlayClient.handleSetSlot(packet)
    }

    override fun a(serverPacket: PacketPlayOutCustomPayload) {
        val packet = S3FPacketCustomPayload(serverPacket.a, serverPacket.b)
        netHandlerPlayClient.handleCustomPayload(packet)
    }

    override fun a(serverPacket: PacketPlayOutKickDisconnect) {
        val text = IChatBaseComponent.ChatSerializer.a(serverPacket.a)
        val chatComponent = IChatComponent.Serializer.fromJson(text)
        val packet = S40PacketDisconnect(chatComponent)
        netHandlerPlayClient.handleDisconnect(packet)
    }

    override fun a(serverPacket: PacketPlayOutBed) {
        val packet = S0APacketUseBed(
            serverPacket.a, serverPacket.b.x, serverPacket.b.y,
            serverPacket.b.z
        )
        netHandlerPlayClient.handleUseBed(packet)
    }

    override fun a(serverPacket: PacketPlayOutEntityStatus) {
        val packet = S19PacketEntityStatus(serverPacket.a, serverPacket.b)
        netHandlerPlayClient.handleEntityStatus(packet)
    }

    override fun a(serverPacket: PacketPlayOutAttachEntity) {
        val packet = S1BPacketEntityAttach(serverPacket.a, serverPacket.b, serverPacket.c)
        netHandlerPlayClient.handleEntityAttach(packet)
    }

    override fun a(serverPacket: PacketPlayOutExplosion) {
        val blockPositionList = serverPacket.e
        val chunkPositionList = ArrayList<ChunkPosition>()

        for (blockPosition in blockPositionList) chunkPositionList.add(
            ChunkPosition(
                blockPosition.x, blockPosition.y,
                blockPosition.z
            )
        )

        val packet = S27PacketExplosion(
            serverPacket.a, serverPacket.b, serverPacket.c,
            serverPacket.d,
            chunkPositionList, serverPacket.f, serverPacket.g, serverPacket.h
        )
        netHandlerPlayClient.handleExplosion(packet)
    }

    override fun a(serverPacket: PacketPlayOutGameStateChange) {
        val packet = S2BPacketChangeGameState(serverPacket.b, serverPacket.c)
        netHandlerPlayClient.handleChangeGameState(packet)
    }

    override fun a(serverPacket: PacketPlayOutKeepAlive) {
        val packet = S00PacketKeepAlive(serverPacket.a)
        netHandlerPlayClient.handleKeepAlive(packet)
    }

    override fun a(serverPacket: PacketPlayOutMapChunk) {
        val chunkMap = serverPacket.c

        val data = chunkMap.a

        val primaryBitMask = chunkMap.b
        val groundUpContinuous = serverPacket.isD

        val result = ChunkDataDecoder.decode(data, primaryBitMask, groundUpContinuous)

        val storageArrays = result.storageArrays
        val blockBiomeArray = result.blockBiomeArray

        val packet = S21PacketChunkData(
            netHandlerPlayClient.gameController, serverPacket.a,
            serverPacket.b, storageArrays, blockBiomeArray,
            groundUpContinuous, primaryBitMask
        )
        netHandlerPlayClient.handleChunkData(packet)
    }

    override fun a(serverPacket: PacketPlayOutMapChunkBulk) {
        val chunkXArr = serverPacket.a
        val chunkZArr = serverPacket.b
        val chunkMapArr = serverPacket.c
        val groundUpContinuous = serverPacket.isD
        val storageArraysArr = Array(chunkMapArr.size) { arrayOfNulls<ExtendedBlockStorage>(16) }
        val blockBiomeArrayArr = Array(chunkMapArr.size) { ByteArray(256) }

        for (i in chunkMapArr.indices) {
            val chunkMap = chunkMapArr[i]
            val data = chunkMap.a
            val primaryBitMask = chunkMap.b

            val result = ChunkDataDecoder.decode(data, primaryBitMask, groundUpContinuous)

            val storageArrays = result.storageArrays
            val blockBiomeArray = result.blockBiomeArray

            storageArraysArr[i] = storageArrays
            blockBiomeArrayArr[i] = blockBiomeArray
        }

        val packet = S26PacketMapChunkBulk(
            netHandlerPlayClient.gameController, chunkXArr,
            chunkZArr, storageArraysArr,
            blockBiomeArrayArr, groundUpContinuous
        )
        netHandlerPlayClient.handleMapChunkBulk(packet)
    }

    override fun a(serverPacket: PacketPlayOutWorldEvent) {
        val packet = S28PacketEffect(
            serverPacket.a, serverPacket.b.x, serverPacket.b.y,
            serverPacket.b.z, serverPacket.c, serverPacket.isD
        )
        netHandlerPlayClient.handleEffect(packet)
    }

    override fun a(serverPacket: PacketPlayOutLogin) {
        val enumGamemode = serverPacket.c
        val enumDifficulty = serverPacket.e
        val worldType = serverPacket.g

        val gameType = WorldSettings.GameType.getByID(enumGamemode.id)
        val difficulty = EnumDifficulty.getDifficultyEnum(enumDifficulty.a())
        val worldType1 = WorldType.parseWorldType(worldType.name())
        val packet = S01PacketJoinGame(
            serverPacket.a, serverPacket.isB, gameType, serverPacket.d,
            difficulty, serverPacket.f, worldType1
        )
        netHandlerPlayClient.handleJoinGame(packet)
    }

    override fun a(serverPacket: PacketPlayOutEntity) {
        val packet = S14PacketEntity(
            serverPacket.a, serverPacket.b, serverPacket.c,
            serverPacket.d, serverPacket.e,
            serverPacket.f, serverPacket.isG
        )
        netHandlerPlayClient.handleEntityMovement(packet)
    }

    override fun a(serverPacket: PacketPlayOutPosition) {
        val set = serverPacket.f
        val x = serverPacket.a
        var y = serverPacket.b
        val z = serverPacket.c

        val yaw = serverPacket.d
        val pitch = serverPacket.e

        val mc = netHandlerPlayClient.gameController

        val thePlayer = mc.thePlayer ?: return

        assert(thePlayer.posY - thePlayer.boundingBox.minY == 1.62)

        // x, y, and z
        if (set.contains(EnumPlayerTeleportFlags.X)) serverPacket.a =
            x + thePlayer.posX
        if (set.contains(EnumPlayerTeleportFlags.Y)) y += thePlayer.boundingBox.minY

        serverPacket.b = y + 1.62 + 1e-5

        if (set.contains(EnumPlayerTeleportFlags.Z)) serverPacket.c =
            z + thePlayer.posZ

        // yaw and pitch
        if (set.contains(EnumPlayerTeleportFlags.X_ROT)) serverPacket.d =
            yaw + thePlayer.rotationYaw
        if (set.contains(EnumPlayerTeleportFlags.Y_ROT)) serverPacket.e =
            pitch + thePlayer.rotationPitch

        val packet = S08PacketPlayerPosLook(
            serverPacket.a, serverPacket.b, serverPacket.c,
            serverPacket.d,
            serverPacket.e, thePlayer.onGround
        )
        netHandlerPlayClient.handlePlayerPosLook(packet)
    }

    override fun a(serverPacket: PacketPlayOutWorldParticles) {
        val enumParticle = serverPacket.a
        val particleName = enumParticle.b()

        if (particleName == null || particleName.isEmpty() || particleName.equals("blockdust_", ignoreCase = true)
            || particleName.equals("blockcrack_", ignoreCase = true)
        ) return
        val packet = S2APacketParticles(
            particleName, serverPacket.b, serverPacket.c, serverPacket.d,
            serverPacket.e, serverPacket.f, serverPacket.g, serverPacket.h,
            serverPacket.i
        )
        netHandlerPlayClient.handleParticles(packet)
    }

    override fun a(serverPacket: PacketPlayOutAbilities) {
        val packet = S39PacketPlayerAbilities(
            serverPacket.isA, serverPacket.isB, serverPacket.isC,
            serverPacket.isD, serverPacket.e, serverPacket.f
        )
        netHandlerPlayClient.handlePlayerAbilities(packet)
    }

    override fun a(serverPacket: PacketPlayOutPlayerInfo) {
        val infoAction = serverPacket.a
        val data = serverPacket.b
        val dataCopy = ArrayList(data)
        for (playerInfoData in dataCopy) {
            if (playerInfoData == null) continue
            val gameProfile = playerInfoData.a()
            val name = gameProfile.name
            val ping = playerInfoData.b()

            val packet = when (infoAction) {
                EnumPlayerInfoAction.ADD_PLAYER -> S38PacketPlayerListItem(name, true, ping)
                EnumPlayerInfoAction.REMOVE_PLAYER -> S38PacketPlayerListItem(name, false, ping)
                EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, EnumPlayerInfoAction.UPDATE_GAME_MODE, EnumPlayerInfoAction.UPDATE_LATENCY -> null
            }

            if (packet != null) netHandlerPlayClient.handlePlayerListItem(packet)
        }
    }

    override fun a(serverPacket: PacketPlayOutEntityDestroy) {
        val packet = S13PacketDestroyEntities(*serverPacket.a)
        netHandlerPlayClient.handleDestroyEntities(packet)
    }

    override fun a(serverPacket: PacketPlayOutRemoveEntityEffect) {
        val packet = S1EPacketRemoveEntityEffect(serverPacket.a, serverPacket.b)
        netHandlerPlayClient.handleRemoveEntityEffect(packet)
    }

    override fun a(serverPacket: PacketPlayOutRespawn) {
        val enumDifficulty = serverPacket.b
        val enumGamemode = serverPacket.c
        val worldType = serverPacket.d

        val gameType = WorldSettings.GameType.getByID(enumGamemode.id)
        val difficulty = EnumDifficulty.getDifficultyEnum(enumDifficulty.a())
        val worldType1 = WorldType.parseWorldType(worldType.name())
        val packet = S07PacketRespawn(serverPacket.a, difficulty, worldType1, gameType)
        netHandlerPlayClient.handleRespawn(packet)
    }

    override fun a(serverPacket: PacketPlayOutEntityHeadRotation) {
        val packet = S19PacketEntityHeadLook(serverPacket.a, serverPacket.b)
        netHandlerPlayClient.handleEntityHeadLook(packet)
    }

    override fun a(serverPacket: PacketPlayOutHeldItemSlot) {
        val packet = S09PacketHeldItemChange(serverPacket.a)
        netHandlerPlayClient.handleHeldItemChange(packet)
    }

    override fun a(serverPacket: PacketPlayOutScoreboardDisplayObjective) {
        val packet = S3DPacketDisplayScoreboard(serverPacket.a, serverPacket.b)
        netHandlerPlayClient.handleDisplayScoreboard(packet)
    }

    override fun a(serverPacket: PacketPlayOutEntityMetadata) {
        val dataWatcherListNMS = serverPacket.b
        val dataWatcherList = ArrayList<DataWatcher.WatchableObject>()

        val entityId = serverPacket.a

        val entity = netHandlerPlayClient.clientWorldController.getEntityByID(entityId)

        if (dataWatcherListNMS != null) {
            for (watchableObjectNMS in dataWatcherListNMS) {
                val watchableObject = entity?.let {
                    fromNMS(
                        watchableObjectNMS,
                        it
                    )
                } ?: continue

                dataWatcherList.add(watchableObject)
            }
        }

        val packet = S1CPacketEntityMetadata(entityId, dataWatcherList)
        netHandlerPlayClient.handleEntityMetadata(packet)
    }

    override fun a(serverPacket: PacketPlayOutEntityVelocity) {
        val packet = S12PacketEntityVelocity(
            serverPacket.a, serverPacket.b / 8000.0,
            serverPacket.c / 8000.0,
            serverPacket.d / 8000.0
        )
        netHandlerPlayClient.handleEntityVelocity(packet)
    }

    override fun a(serverPacket: PacketPlayOutEntityEquipment) {
        val itemNMS = serverPacket.c
        val item = fromNMS(itemNMS)

        val packet = S04PacketEntityEquipment(
            serverPacket.a, serverPacket.b,
            item
        )
        netHandlerPlayClient.handleEntityEquipment(packet)
    }

    override fun a(serverPacket: PacketPlayOutExperience) {
        val packet = S1FPacketSetExperience(serverPacket.a, serverPacket.b, serverPacket.c)
        netHandlerPlayClient.handleSetExperience(packet)
    }

    override fun a(serverPacket: PacketPlayOutUpdateHealth) {
        val packet = S06PacketUpdateHealth(serverPacket.a, serverPacket.b, serverPacket.c)
        netHandlerPlayClient.handleUpdateHealth(packet)
    }

    override fun a(serverPacket: PacketPlayOutScoreboardTeam) {
        val packet = S3EPacketTeams(
            serverPacket.a, serverPacket.b, serverPacket.c,
            serverPacket.d, serverPacket.g,
            serverPacket.h, serverPacket.i
        )
        netHandlerPlayClient.handleTeams(packet)
    }

    override fun a(serverPacket: PacketPlayOutScoreboardScore) {
        val packet = S3CPacketUpdateScore(
            serverPacket.a, serverPacket.b, serverPacket.c,
            serverPacket.d.ordinal
        )
        netHandlerPlayClient.handleUpdateScore(packet)
    }

    override fun a(serverPacket: PacketPlayOutSpawnPosition) {
        val packet = S05PacketSpawnPosition(
            serverPacket.getPosition().x,
            serverPacket.getPosition().y, serverPacket.getPosition().z
        )
        netHandlerPlayClient.handleSpawnPosition(packet)
    }

    override fun a(serverPacket: PacketPlayOutUpdateTime) {
        val packet = S03PacketTimeUpdate(serverPacket.a, serverPacket.b)
        netHandlerPlayClient.handleTimeUpdate(packet)
    }

    override fun a(serverPacket: PacketPlayOutUpdateSign) {
        val nmsChatComponents = serverPacket.c
        val lines = arrayOfNulls<String>(nmsChatComponents.size)

        for (i in nmsChatComponents.indices) {
            val text = IChatBaseComponent.ChatSerializer.a(nmsChatComponents[i])
            val chatComponent = IChatComponent.Serializer.fromJson(text)
            lines[i] = chatComponent.unformattedText
        }

        val packet = S33PacketUpdateSign(
            serverPacket.b.x, serverPacket.b.y,
            serverPacket.b.z,
            lines
        )
        netHandlerPlayClient.handleUpdateSign(packet)
    }

    override fun a(serverPacket: PacketPlayOutNamedSoundEffect) {
        val packet = S29PacketSoundEffect(
            serverPacket.a, serverPacket.b.toDouble(), serverPacket.c.toDouble(),
            serverPacket.d.toDouble(),
            serverPacket.e, serverPacket.f.toFloat()
        )
        netHandlerPlayClient.handleSoundEffect(packet)
    }

    override fun a(serverPacket: PacketPlayOutCollect) {
        val packet = S0DPacketCollectItem(serverPacket.a, serverPacket.b)
        netHandlerPlayClient.handleCollectItem(packet)
    }

    override fun a(serverPacket: PacketPlayOutEntityTeleport) {
        val packet = S18PacketEntityTeleport(
            serverPacket.a, serverPacket.b, serverPacket.c,
            serverPacket.d, serverPacket.e, serverPacket.f
        )
        netHandlerPlayClient.handleEntityTeleport(packet)
    }

    override fun a(serverPacket: PacketPlayOutUpdateAttributes) {
        val snapshotListNMS = serverPacket.b
        val snapshotList = ArrayList<S20PacketEntityProperties.Snapshot>()

        for (attributeSnapshot in snapshotListNMS) {
            val attributeName = attributeSnapshot.a()
            val baseValue = attributeSnapshot.b()
            val modifiersNMS = attributeSnapshot.c()
            val modifiers = ArrayList<AttributeModifier>()

            for (attributeModifier in modifiersNMS) {
                val uuid = attributeModifier.a()
                val name = attributeModifier.b()
                val amount = attributeModifier.d()
                val operation = attributeModifier.c()
                val modifier = AttributeModifier(uuid, name, amount, operation)
                modifiers.add(modifier)
            }

            val snapshot = S20PacketEntityProperties.Snapshot(
                attributeName, baseValue, modifiers
            )
            snapshotList.add(snapshot)
        }

        val packet = S20PacketEntityProperties(serverPacket.a, snapshotList)
        netHandlerPlayClient.handleEntityProperties(packet)
    }

    override fun a(serverPacket: PacketPlayOutEntityEffect) {
        val packet = S1DPacketEntityEffect(
            serverPacket.a, serverPacket.b, serverPacket.c,
            serverPacket.d.toShort()
        )
        netHandlerPlayClient.handleEntityEffect(packet)
    }

    override fun a(serverPacket: PacketPlayOutCombatEvent) {
        // TODO: implement
    }

    override fun a(serverPacket: PacketPlayOutServerDifficulty) {
        // TODO: implement
    }

    override fun a(serverPacket: PacketPlayOutCamera) {
        // TODO: implement
    }

    override fun a(serverPacket: PacketPlayOutWorldBorder) {
        // TODO: implement
    }

    override fun a(serverPacket: PacketPlayOutTitle) {
        // TODO: implement
    }

    override fun a(serverPacket: PacketPlayOutSetCompression) {
        // TODO: implement
    }

    override fun a(serverPacket: PacketPlayOutPlayerListHeaderFooter) {
        // TODO: implement
    }

    override fun a(serverPacket: PacketPlayOutResourcePackSend) {
        // TODO: implement
    }

    override fun a(serverPacket: PacketPlayOutUpdateEntityNBT) {
        // TODO: implement
    }

    override fun a(serverPacket: PacketStatusOutServerInfo) {
        // TODO Auto-generated method stub
        throw UnsupportedOperationException("Unimplemented method 'a'")
    }

    override fun a(serverPacket: PacketStatusOutPong) {
        // TODO Auto-generated method stub
        throw UnsupportedOperationException("Unimplemented method 'a'")
    }

    override fun a(serverPacket: PacketLoginOutEncryptionBegin) {
        // TODO Auto-generated method stub
        throw UnsupportedOperationException("Unimplemented method 'a'")
    }

    override fun a(serverPacket: PacketLoginOutSuccess) {
        // TODO Auto-generated method stub
        throw UnsupportedOperationException("Unimplemented method 'a'")
    }

    override fun a(serverPacket: PacketLoginOutDisconnect) {
        // TODO Auto-generated method stub
        throw UnsupportedOperationException("Unimplemented method 'a'")
    }

    override fun a(serverPacket: PacketLoginOutSetCompression) {
        // TODO Auto-generated method stub
        throw UnsupportedOperationException("Unimplemented method 'a'")
    }

    companion object {
        val WINDOW_TYPE_REGISTRY: Object2IntOpenHashMap<String> = Object2IntOpenHashMap()

        init {
            WINDOW_TYPE_REGISTRY.put("minecraft:container", 0)
            WINDOW_TYPE_REGISTRY.put("minecraft:chest", 0)
            WINDOW_TYPE_REGISTRY.put("minecraft:crafting_table", 1)
            WINDOW_TYPE_REGISTRY.put("minecraft:furnace", 2)
            WINDOW_TYPE_REGISTRY.put("minecraft:dispenser", 3)
            WINDOW_TYPE_REGISTRY.put("minecraft:enchanting_table", 4)
            WINDOW_TYPE_REGISTRY.put("minecraft:brewing_stand", 5)
            WINDOW_TYPE_REGISTRY.put("minecraft:villager", 6)
            WINDOW_TYPE_REGISTRY.put("minecraft:beacon", 7)
            WINDOW_TYPE_REGISTRY.put("minecraft:anvil", 8)
            WINDOW_TYPE_REGISTRY.put("minecraft:hopper", 9)
            WINDOW_TYPE_REGISTRY.put("minecraft:dropper", 10)
            WINDOW_TYPE_REGISTRY.put("EntityHorse", 11)
        }

        @Suppress("DEPRECATION")
        fun getItem(material: Material): Item? {
            val item = Item.getItemById(material.id)
            return item
        }

        @Suppress("DEPRECATION")
        fun getType(item: net.minecraft.item.ItemStack): Material {
            val material = Material.getMaterial(Item.getIdFromItem(item.item))
            return material ?: Material.AIR
        }

        fun createStringList(list: List<String>): NBTTagList {
            val tagList = NBTTagList()
            if (list.isEmpty()) return tagList

            for (value in list) tagList.appendTag(NBTTagString(value))

            return tagList
        }

        @Suppress("DEPRECATION")
        fun applyEnchantments(enchantments: Map<Enchantment, Int>, tag: NBTTagCompound) {

            val list = NBTTagList()

            for ((key, value) in enchantments) {
                val subtag = NBTTagCompound()

                subtag.setShort("id", key.id.toShort())
                subtag.setShort("lvl", value.toShort())

                list.appendTag(subtag)
            }

            tag.setTag("ench", list)
        }
    }
}
