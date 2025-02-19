package gg.mineral.bot.plugin.network.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemFactory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import com.mojang.authlib.GameProfile;

import gg.mineral.bot.plugin.network.packet.chunk.ChunkDataDecoder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import lombok.Cleanup;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketListenerPlayOut;
import net.minecraft.server.v1_8_R3.PacketLoginOutDisconnect;
import net.minecraft.server.v1_8_R3.PacketLoginOutEncryptionBegin;
import net.minecraft.server.v1_8_R3.PacketLoginOutListener;
import net.minecraft.server.v1_8_R3.PacketLoginOutSetCompression;
import net.minecraft.server.v1_8_R3.PacketLoginOutSuccess;
import net.minecraft.server.v1_8_R3.PacketPlayOutAbilities;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutBed;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import net.minecraft.server.v1_8_R3.PacketPlayOutCamera;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutCloseWindow;
import net.minecraft.server.v1_8_R3.PacketPlayOutCollect;
import net.minecraft.server.v1_8_R3.PacketPlayOutCombatEvent;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_8_R3.PacketPlayOutExperience;
import net.minecraft.server.v1_8_R3.PacketPlayOutExplosion;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_8_R3.PacketPlayOutHeldItemSlot;
import net.minecraft.server.v1_8_R3.PacketPlayOutKeepAlive;
import net.minecraft.server.v1_8_R3.PacketPlayOutKickDisconnect;
import net.minecraft.server.v1_8_R3.PacketPlayOutLogin;
import net.minecraft.server.v1_8_R3.PacketPlayOutMap;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunkBulk;
import net.minecraft.server.v1_8_R3.PacketPlayOutMultiBlockChange;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutRemoveEntityEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutResourcePackSend;
import net.minecraft.server.v1_8_R3.PacketPlayOutRespawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.PacketPlayOutServerDifficulty;
import net.minecraft.server.v1_8_R3.PacketPlayOutSetCompression;
import net.minecraft.server.v1_8_R3.PacketPlayOutSetSlot;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityExperienceOrb;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityPainting;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutStatistic;
import net.minecraft.server.v1_8_R3.PacketPlayOutTabComplete;
import net.minecraft.server.v1_8_R3.PacketPlayOutTileEntityData;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTransaction;
import net.minecraft.server.v1_8_R3.PacketPlayOutUpdateAttributes;
import net.minecraft.server.v1_8_R3.PacketPlayOutUpdateEntityNBT;
import net.minecraft.server.v1_8_R3.PacketPlayOutUpdateHealth;
import net.minecraft.server.v1_8_R3.PacketPlayOutUpdateSign;
import net.minecraft.server.v1_8_R3.PacketPlayOutUpdateTime;
import net.minecraft.server.v1_8_R3.PacketPlayOutWindowData;
import net.minecraft.server.v1_8_R3.PacketPlayOutWindowItems;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldEvent;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R3.PacketStatusOutListener;
import net.minecraft.server.v1_8_R3.PacketStatusOutPong;
import net.minecraft.server.v1_8_R3.PacketStatusOutServerInfo;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class Server2ClientTranslator implements PacketListenerPlayOut, PacketLoginOutListener, PacketStatusOutListener {

    public static final Object2IntOpenHashMap<String> WINDOW_TYPE_REGISTRY = new Object2IntOpenHashMap<>();

    static {
        WINDOW_TYPE_REGISTRY.put("minecraft:container", 0);
        WINDOW_TYPE_REGISTRY.put("minecraft:chest", 0);
        WINDOW_TYPE_REGISTRY.put("minecraft:crafting_table", 1);
        WINDOW_TYPE_REGISTRY.put("minecraft:furnace", 2);
        WINDOW_TYPE_REGISTRY.put("minecraft:dispenser", 3);
        WINDOW_TYPE_REGISTRY.put("minecraft:enchanting_table", 4);
        WINDOW_TYPE_REGISTRY.put("minecraft:brewing_stand", 5);
        WINDOW_TYPE_REGISTRY.put("minecraft:villager", 6);
        WINDOW_TYPE_REGISTRY.put("minecraft:beacon", 7);
        WINDOW_TYPE_REGISTRY.put("minecraft:anvil", 8);
        WINDOW_TYPE_REGISTRY.put("minecraft:hopper", 9);
        WINDOW_TYPE_REGISTRY.put("minecraft:dropper", 10);
        WINDOW_TYPE_REGISTRY.put("EntityHorse", 11);
    }

    public static Item getItem(Material material) {
        @SuppressWarnings("deprecation")
        val item = Item.getItemById(material.getId());
        return item;
    }

    @SuppressWarnings("deprecation")
    @Nullable
    public ItemStack asNMCCopy(org.bukkit.inventory.ItemStack original) {
        if (original != null && original.getTypeId() > 0) {
            val item = getItem(original.getType());
            if (item == null)
                return null;

            val stack = new ItemStack(item,
                    original.getAmount(), original.getDurability());
            if (original.hasItemMeta())
                setItemMeta(stack, original.getItemMeta());

            return stack;
        }

        return null;
    }

    public boolean setItemMeta(ItemStack item, ItemMeta itemMeta) {
        if (item == null)
            return false;
        if (CraftItemFactory.instance().equals(itemMeta, (ItemMeta) null)) {
            item.setTagCompound(null);
            return true;
        }
        if (!CraftItemFactory.instance().isApplicable(itemMeta, getType(item)))
            return false;

        itemMeta = CraftItemFactory.instance().asMetaFor(itemMeta, getType(item));
        if (itemMeta == null)
            return true;

        val tag = new NBTTagCompound();
        item.setTagCompound(tag);
        applyToItem(itemMeta, tag);
        return true;
    }

    static Material getType(ItemStack item) {
        if (item == null)
            return Material.AIR;
        @SuppressWarnings("deprecation")
        val material = Material.getMaterial(Item.getIdFromItem(item.getItem()));
        return material == null ? Material.AIR : material;
    }

    void setDisplayTag(NBTTagCompound tag, String key, NBTBase value) {
        val display = tag.getCompoundTag("display");
        if (!tag.hasKey("display"))
            tag.setTag("display", display);

        display.setTag(key, value);
    }

    static NBTTagList createStringList(List<String> list) {
        if (list == null || list.isEmpty())
            return null;

        val tagList = new NBTTagList();
        for (val value : list)
            tagList.appendTag(new NBTTagString(value));

        return tagList;
    }

    void applyToItem(ItemMeta meta, NBTTagCompound itemTag) {
        if (meta.hasDisplayName())
            setDisplayTag(itemTag, "Name", new NBTTagString(meta.getDisplayName()));

        if (meta.hasLore())
            setDisplayTag(itemTag, "Lore", createStringList(meta.getLore()));

        val hideFlag = getHideFlagFromSet(meta.getItemFlags());
        if (hideFlag != 0)
            itemTag.setInteger("HideFlags", hideFlag);

        applyEnchantments(meta.getEnchants(), itemTag);
        if (meta.spigot().isUnbreakable())
            itemTag.setBoolean("Unbreakable", true);

        // TODO: if (meta.hasRepairCost())
        // itemTag.setInt(REPAIR.NBT, this.repairCost);

        // Iterator var2 = meta.unhandledTags.entrySet().iterator();

        // while (var2.hasNext()) {
        // Entry<String, NBTBase> e = (Entry) var2.next();
        // itemTag.set((String) e.getKey(), (NBTBase) e.getValue());
        // }

    }

    @SuppressWarnings("deprecation")
    static void applyEnchantments(Map<org.bukkit.enchantments.Enchantment, Integer> enchantments, NBTTagCompound tag) {
        if (enchantments == null)
            return;

        val list = new NBTTagList();

        for (val entry : enchantments.entrySet()) {
            val subtag = new NBTTagCompound();

            subtag.setShort("id", (short) entry.getKey().getId());
            subtag.setShort("lvl", entry.getValue().shortValue());

            list.appendTag(subtag);
        }

        tag.setTag("ench", list);
    }

    public int getHideFlagFromSet(Set<ItemFlag> itemFlags) {
        int hideFlag = 0; // Start with no flags

        for (val flag : itemFlags)
            hideFlag |= getBitModifier(flag);

        return hideFlag;
    }

    private byte getBitModifier(ItemFlag hideFlag) {
        return (byte) (1 << hideFlag.ordinal());
    }

    @Nullable
    private ItemStack fromNMS(@Nullable net.minecraft.server.v1_8_R3.ItemStack itemNMS) {
        if (itemNMS == null)
            return null;

        val itemStack = org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
                .asBukkitCopy(itemNMS);

        return itemStack == null ? null : asNMCCopy(itemStack);
    }

    @Nullable
    private DataWatcher.WatchableObject fromNMS(
            net.minecraft.server.v1_8_R3.DataWatcher.WatchableObject watchableObject, Entity entity) {
        val id = watchableObject.a();
        var value = watchableObject.b();
        var type = watchableObject.c();

        switch (id) {
            case 12 -> {
                if (entity != null && entity instanceof EntityAgeable && value instanceof Byte byteValue) {
                    type = 2;
                    value = Integer.valueOf(byteValue.intValue());
                }
            }
        }

        value = switch (type) {
            case 5 -> fromNMS((net.minecraft.server.v1_8_R3.ItemStack) value);// ItemStack
            case 6 -> {
                val blockPosition = (net.minecraft.server.v1_8_R3.BlockPosition) value;
                yield new ChunkCoordinates(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
            }// BlockPos
            default -> null;
        };

        if (value == null)
            return null;

        val dataWatcher = new DataWatcher.WatchableObject(type, id, value);

        dataWatcher.setWatched(watchableObject.d());

        return dataWatcher;
    }

    @Setter
    private NetHandlerPlayClient netHandlerPlayClient;

    public void handlePacket(Packet<PacketListenerPlayOut> packet) {
        packet.a(this);
    }

    @Override
    public void a(IChatBaseComponent serverPacket) {
        val text = IChatBaseComponent.ChatSerializer.a(serverPacket);
        val chatComponent = IChatComponent.Serializer.fromJson(text);
        netHandlerPlayClient.onDisconnect(chatComponent);
    }

    @Override
    public void a(PacketPlayOutSpawnEntity serverPacket) {
        val packet = new S0EPacketSpawnObject(serverPacket.getA(), serverPacket.getB(), serverPacket.getC(),
                serverPacket.getD(),
                serverPacket.getE(), serverPacket.getF(), serverPacket.getG(), serverPacket.getH(), serverPacket.getI(),
                serverPacket.getJ(), serverPacket.getK());
        netHandlerPlayClient.handleSpawnObject(packet);
    }

    @Override
    public void a(PacketPlayOutSpawnEntityExperienceOrb serverPacket) {
        val packet = new S11PacketSpawnExperienceOrb(serverPacket.getA(), serverPacket.getB(), serverPacket.getC(),
                serverPacket.getD(), serverPacket.getE());
        netHandlerPlayClient.handleSpawnExperienceOrb(packet);
    }

    @Override
    public void a(PacketPlayOutSpawnEntityWeather serverPacket) {
        val packet = new S2CPacketSpawnGlobalEntity(serverPacket.getA(), serverPacket.getB(), serverPacket.getC(),
                serverPacket.getD(), serverPacket.getE());
        netHandlerPlayClient.handleSpawnGlobalEntity(packet);
    }

    @Override
    public void a(PacketPlayOutSpawnEntityLiving serverPacket) {
        val entityTypeId = serverPacket.getB();

        if (entityTypeId == 101) // TODO: Rabbit
            return;

        val dataWatcherListNMS = serverPacket.getM();
        val dataWatcherList = new ArrayList<>();

        val entityId = serverPacket.getA();

        val entity = netHandlerPlayClient.getClientWorldController().getEntityByID(entityId);

        if (dataWatcherListNMS != null) {
            for (val watchableObjectNMS : dataWatcherListNMS) {
                val watchableObject = fromNMS(watchableObjectNMS, entity);

                if (watchableObject == null)
                    continue;

                dataWatcherList.add(watchableObject);
            }
        }

        val packet = new S0FPacketSpawnMob(entityId, entityTypeId, serverPacket.getC(), serverPacket.getD(),
                serverPacket.getE(), serverPacket.getF(), serverPacket.getG(), serverPacket.getH(), serverPacket.getI(),
                serverPacket.getJ(), serverPacket.getK(), null,
                dataWatcherList);

        netHandlerPlayClient.handleSpawnMob(packet);
    }

    @Override
    public void a(PacketPlayOutScoreboardObjective serverPacket) {
        val name = serverPacket.getA();
        val value = serverPacket.getB();
        val mode = serverPacket.getD();
        val packet = new S3BPacketScoreboardObjective(name, value, mode);
        netHandlerPlayClient.handleScoreboardObjective(packet);
    }

    @Override
    public void a(PacketPlayOutSpawnEntityPainting serverPacket) {
        val blockPos = serverPacket.getB();
        val packet = new S10PacketSpawnPainting(serverPacket.getA(), blockPos.getX(), blockPos.getY(),
                blockPos.getZ(), serverPacket.getC().a(), serverPacket.getD());
        netHandlerPlayClient.handleSpawnPainting(packet);
    }

    @Override
    public void a(PacketPlayOutNamedEntitySpawn serverPacket) {
        @Nullable
        val dataWatcherListNMS = serverPacket.getJ();
        val dataWatcherList = new ArrayList<>();

        val entityId = serverPacket.getA();

        val entity = netHandlerPlayClient.getClientWorldController().getEntityByID(entityId);

        if (dataWatcherListNMS != null) {
            for (val watchableObjectNMS : dataWatcherListNMS) {
                val watchableObject = fromNMS(watchableObjectNMS, entity);

                if (watchableObject != null)
                    dataWatcherList.add(watchableObject);
            }
        }

        // TODO: name
        val packet = new S0CPacketSpawnPlayer(entityId,
                new GameProfile(serverPacket.getB(), ""),
                serverPacket.getC(), serverPacket.getD(),
                serverPacket.getE(), serverPacket.getF(), serverPacket.getG(), serverPacket.getH(), null,
                dataWatcherList);
        netHandlerPlayClient.handleSpawnPlayer(packet);
    }

    @Override
    public void a(PacketPlayOutAnimation serverPacket) {
        val packet = new S0BPacketAnimation(serverPacket.getA(), serverPacket.getB());
        netHandlerPlayClient.handleAnimation(packet);
    }

    @Override
    public void a(PacketPlayOutStatistic serverPacket) {
        val statisticMap = serverPacket.getA();
        val statMap = new Object2IntOpenHashMap<StatBase>();

        for (val e : statisticMap.object2IntEntrySet()) {
            val stat = StatList.func_151177_a(e.getKey().name);
            statMap.put(stat, e.getIntValue());
        }

        val packet = new S37PacketStatistics(statMap);
        netHandlerPlayClient.handleStatistics(packet);
    }

    @Override
    public void a(PacketPlayOutBlockBreakAnimation serverPacket) {
        val packet = new S25PacketBlockBreakAnim(serverPacket.getA(), serverPacket.getB().getX(),
                serverPacket.getB().getY(), serverPacket.getB().getZ(), serverPacket.getC());
        netHandlerPlayClient.handleBlockBreakAnim(packet);
    }

    @Override
    public void a(PacketPlayOutOpenSignEditor serverPacket) {
        val blockPos = serverPacket.getA();
        val packet = new S36PacketSignEditorOpen(blockPos.getX(), blockPos.getY(),
                blockPos.getZ());
        netHandlerPlayClient.handleSignEditorOpen(packet);
    }

    @Override
    @SneakyThrows(IOException.class)
    public void a(PacketPlayOutTileEntityData serverPacket) {
        val nmsNbt = serverPacket.getC();
        @Cleanup
        val baos = new ByteArrayOutputStream();
        @Cleanup
        val buf = new DataOutputStream(baos);

        NBTCompressedStreamTools.a(nmsNbt, (DataOutput) buf);

        val bytes = baos.toByteArray();

        val dis = new DataInputStream(new ByteArrayInputStream(bytes));

        val nbt = CompressedStreamTools.read(dis);

        val packet = new S35PacketUpdateTileEntity(serverPacket.getA().getX(), serverPacket.getA().getY(),
                serverPacket.getA().getZ(), serverPacket.getB(), nbt);
        netHandlerPlayClient.handleUpdateTileEntity(packet);
    }

    @Override
    public void a(PacketPlayOutBlockAction serverPacket) {
        val blockId = net.minecraft.server.v1_8_R3.Block.getId(serverPacket.getD()) & 4095;
        val block = Block.getBlockById(blockId);
        val packet = new S24PacketBlockAction(serverPacket.getA().getX(), serverPacket.getA().getY(),
                serverPacket.getA().getZ(), serverPacket.getB(), serverPacket.getC(), block);
        netHandlerPlayClient.handleBlockAction(packet);
    }

    @Override
    public void a(PacketPlayOutBlockChange serverPacket) {
        val idAndMeta = net.minecraft.server.v1_8_R3.Block.d.b(serverPacket.getBlock());
        val blockId = idAndMeta >> 4;
        val blockMeta = idAndMeta & 15;
        val block = Block.getBlockById(blockId);
        val packet = new S23PacketBlockChange(serverPacket.getA().getX(), serverPacket.getA().getY(),
                serverPacket.getA().getZ(), block, blockMeta);
        netHandlerPlayClient.handleBlockChange(packet);
    }

    @Override
    public void a(PacketPlayOutChat serverPacket) {
        val text = IChatBaseComponent.ChatSerializer.a(serverPacket.getA());
        val chatComponent = IChatComponent.Serializer.fromJson(text);
        if (chatComponent == null)
            return;
        val packet = new S02PacketChat(chatComponent);
        netHandlerPlayClient.handleChat(packet);
    }

    @Override
    public void a(PacketPlayOutTabComplete serverPacket) {
        val packet = new S3APacketTabComplete(serverPacket.getA());
        netHandlerPlayClient.handleTabComplete(packet);
    }

    @Override
    public void a(PacketPlayOutMultiBlockChange serverPacket) {
        val chunkCoordIntPairNMS = serverPacket.getA();
        val chunkCoordIntPair = new ChunkCoordIntPair(chunkCoordIntPairNMS.x, chunkCoordIntPairNMS.z);

        val recordLength = serverPacket.getB().length;

        val buffer = ByteBuffer.allocate(recordLength * 4);

        for (int i = 0; i < recordLength; i++) {
            val record = serverPacket.getB()[i];
            buffer.putShort(record.b());
            val idAndMeta = net.minecraft.server.v1_8_R3.Block.d.b(record.c());
            buffer.putShort((short) (idAndMeta >> 4));
        }

        val packet = new S22PacketMultiBlockChange(chunkCoordIntPair, buffer.array(),
                recordLength);
        netHandlerPlayClient.handleMultiBlockChange(packet);
    }

    @Override
    public void a(PacketPlayOutMap serverPacket) {
        // TODO: implement
    }

    @Override
    public void a(PacketPlayOutTransaction serverPacket) {
        val packet = new S32PacketConfirmTransaction(serverPacket.getA(), serverPacket.getB(), serverPacket.isC());
        netHandlerPlayClient.handleConfirmTransaction(packet);
    }

    @Override
    public void a(PacketPlayOutCloseWindow serverPacket) {
        val packet = new S2EPacketCloseWindow(serverPacket.getA());
        netHandlerPlayClient.handleCloseWindow(packet);
    }

    @Override
    public void a(PacketPlayOutWindowItems serverPacket) {
        val items = new ItemStack[serverPacket.getB().length];

        for (int i = 0; i < items.length; i++)
            items[i] = fromNMS(serverPacket.getB()[i]);

        val packet = new S30PacketWindowItems(serverPacket.getA(), items);
        netHandlerPlayClient.handleWindowItems(packet);
    }

    @Override
    public void a(PacketPlayOutOpenWindow serverPacket) {
        val packet = new S2DPacketOpenWindow(serverPacket.getA(), WINDOW_TYPE_REGISTRY.getInt(serverPacket.getB()),
                serverPacket.getC().c(), serverPacket.getD(), true,
                serverPacket.getE());
        netHandlerPlayClient.handleOpenWindow(packet);
    }

    @Override
    public void a(PacketPlayOutWindowData serverPacket) {
        val packet = new S31PacketWindowProperty(serverPacket.getA(), serverPacket.getB(), serverPacket.getC());
        netHandlerPlayClient.handleWindowProperty(packet);
    }

    @Override
    public void a(PacketPlayOutSetSlot serverPacket) {
        @Nullable
        val itemNMS = serverPacket.getC();
        @Nullable
        val item = fromNMS(itemNMS);

        val packet = new S2FPacketSetSlot(serverPacket.getA(), serverPacket.getB(),
                item);
        netHandlerPlayClient.handleSetSlot(packet);

    }

    @Override
    public void a(PacketPlayOutCustomPayload serverPacket) {
        val packet = new S3FPacketCustomPayload(serverPacket.getA(), serverPacket.getB());
        netHandlerPlayClient.handleCustomPayload(packet);
    }

    @Override
    public void a(PacketPlayOutKickDisconnect serverPacket) {
        val text = IChatBaseComponent.ChatSerializer.a(serverPacket.getA());
        val chatComponent = IChatComponent.Serializer.fromJson(text);
        val packet = new S40PacketDisconnect(chatComponent);
        netHandlerPlayClient.handleDisconnect(packet);
    }

    @Override
    public void a(PacketPlayOutBed serverPacket) {
        val packet = new S0APacketUseBed(serverPacket.getA(), serverPacket.getB().getX(), serverPacket.getB().getY(),
                serverPacket.getB().getZ());
        netHandlerPlayClient.handleUseBed(packet);
    }

    @Override
    public void a(PacketPlayOutEntityStatus serverPacket) {
        val packet = new S19PacketEntityStatus(serverPacket.getA(), serverPacket.getB());
        netHandlerPlayClient.handleEntityStatus(packet);
    }

    @Override
    public void a(PacketPlayOutAttachEntity serverPacket) {
        val packet = new S1BPacketEntityAttach(serverPacket.getA(), serverPacket.getB(), serverPacket.getC());
        netHandlerPlayClient.handleEntityAttach(packet);
    }

    @Override
    public void a(PacketPlayOutExplosion serverPacket) {
        val blockPositionList = serverPacket.getE();
        val chunkPositionList = new ArrayList<ChunkPosition>();

        for (val blockPosition : blockPositionList)
            chunkPositionList.add(new ChunkPosition(blockPosition.getX(), blockPosition.getY(),
                    blockPosition.getZ()));

        val packet = new S27PacketExplosion(serverPacket.getA(), serverPacket.getB(), serverPacket.getC(),
                serverPacket.getD(),
                chunkPositionList, serverPacket.getF(), serverPacket.getG(), serverPacket.getH());
        netHandlerPlayClient.handleExplosion(packet);
    }

    @Override
    public void a(PacketPlayOutGameStateChange serverPacket) {
        val packet = new S2BPacketChangeGameState(serverPacket.getB(), serverPacket.getC());
        netHandlerPlayClient.handleChangeGameState(packet);
    }

    @Override
    public void a(PacketPlayOutKeepAlive serverPacket) {
        val packet = new S00PacketKeepAlive(serverPacket.getA());
        netHandlerPlayClient.handleKeepAlive(packet);
    }

    @Override
    public void a(PacketPlayOutMapChunk serverPacket) {
        val chunkMap = serverPacket.getC();

        val data = chunkMap.a;

        val primaryBitMask = chunkMap.b;
        val groundUpContinuous = serverPacket.isD();

        val result = ChunkDataDecoder.decode(data, primaryBitMask, groundUpContinuous);

        val storageArrays = result.storageArrays();
        val blockBiomeArray = result.blockBiomeArray();

        val packet = new S21PacketChunkData(netHandlerPlayClient.getGameController(), serverPacket.getA(),
                serverPacket.getB(), storageArrays, blockBiomeArray,
                groundUpContinuous, primaryBitMask);
        netHandlerPlayClient.handleChunkData(packet);
    }

    @Override
    public void a(PacketPlayOutMapChunkBulk serverPacket) {
        val chunkXArr = serverPacket.getA();
        val chunkZArr = serverPacket.getB();
        val chunkMapArr = serverPacket.getC();
        val groundUpContinuous = serverPacket.isD();
        val storageArraysArr = new ExtendedBlockStorage[chunkMapArr.length][16];
        val blockBiomeArrayArr = new byte[chunkMapArr.length][256];

        for (int i = 0; i < chunkMapArr.length; i++) {
            val chunkMap = chunkMapArr[i];
            val data = chunkMap.a;
            val primaryBitMask = chunkMap.b;

            val result = ChunkDataDecoder.decode(data, primaryBitMask, groundUpContinuous);

            val storageArrays = result.storageArrays();
            val blockBiomeArray = result.blockBiomeArray();

            storageArraysArr[i] = storageArrays;
            blockBiomeArrayArr[i] = blockBiomeArray;
        }

        val packet = new S26PacketMapChunkBulk(netHandlerPlayClient.getGameController(), chunkXArr,
                chunkZArr, storageArraysArr,
                blockBiomeArrayArr, groundUpContinuous);
        netHandlerPlayClient.handleMapChunkBulk(packet);

    }

    @Override
    public void a(PacketPlayOutWorldEvent serverPacket) {
        val packet = new S28PacketEffect(serverPacket.getA(), serverPacket.getB().getX(), serverPacket.getB().getY(),
                serverPacket.getB().getZ(), serverPacket.getC(), serverPacket.isD());
        netHandlerPlayClient.handleEffect(packet);
    }

    @Override
    public void a(PacketPlayOutLogin serverPacket) {
        val enumGamemode = serverPacket.getC();
        val enumDifficulty = serverPacket.getE();
        val worldType = serverPacket.getG();

        val gameType = WorldSettings.GameType.getByID(enumGamemode.getId());
        val difficulty = EnumDifficulty.getDifficultyEnum(enumDifficulty.a());
        val worldType1 = WorldType.parseWorldType(worldType.name());
        val packet = new S01PacketJoinGame(serverPacket.getA(), serverPacket.isB(), gameType, serverPacket.getD(),
                difficulty, serverPacket.getF(), worldType1);
        netHandlerPlayClient.handleJoinGame(packet);
    }

    @Override
    public void a(PacketPlayOutEntity serverPacket) {
        val packet = new S14PacketEntity(serverPacket.getA(), serverPacket.getB(), serverPacket.getC(),
                serverPacket.getD(), serverPacket.getE(),
                serverPacket.getF(), serverPacket.isG());
        netHandlerPlayClient.handleEntityMovement(packet);
    }

    @Override
    public void a(PacketPlayOutPosition serverPacket) {
        val set = serverPacket.getF();
        val x = serverPacket.getA();
        var y = serverPacket.getB();
        val z = serverPacket.getC();

        val yaw = serverPacket.getD();
        val pitch = serverPacket.getE();

        val mc = netHandlerPlayClient.getGameController();

        val thePlayer = mc.thePlayer;

        if (thePlayer == null)
            return;

        assert thePlayer.posY - thePlayer.boundingBox.minY == 1.62D;

        // x, y, and z
        if (set.contains(net.minecraft.server.v1_8_R3.PacketPlayOutPosition.EnumPlayerTeleportFlags.X))
            serverPacket.setA(x + thePlayer.posX);
        if (set.contains(net.minecraft.server.v1_8_R3.PacketPlayOutPosition.EnumPlayerTeleportFlags.Y))
            y += thePlayer.boundingBox.minY;

        serverPacket.setB(y + 1.62D + 1e-5);

        if (set.contains(net.minecraft.server.v1_8_R3.PacketPlayOutPosition.EnumPlayerTeleportFlags.Z))
            serverPacket.setC(z + thePlayer.posZ);

        // yaw and pitch
        if (set.contains(net.minecraft.server.v1_8_R3.PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT))
            serverPacket.setD(yaw + thePlayer.rotationYaw);
        if (set.contains(net.minecraft.server.v1_8_R3.PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT))
            serverPacket.setE(pitch + thePlayer.rotationPitch);

        val packet = new S08PacketPlayerPosLook(serverPacket.getA(), serverPacket.getB(), serverPacket.getC(),
                serverPacket.getD(),
                serverPacket.getE(), thePlayer.onGround);
        netHandlerPlayClient.handlePlayerPosLook(packet);
    }

    @Override
    public void a(PacketPlayOutWorldParticles serverPacket) {
        val enumParticle = serverPacket.getA();
        val particleName = enumParticle.b();

        if (particleName == null || particleName.isEmpty() || particleName.equalsIgnoreCase("blockdust_")
                || particleName.equalsIgnoreCase("blockcrack_"))
            return;
        val packet = new S2APacketParticles(particleName, serverPacket.getB(), serverPacket.getC(), serverPacket.getD(),
                serverPacket.getE(), serverPacket.getF(), serverPacket.getG(), serverPacket.getH(),
                serverPacket.getI());
        netHandlerPlayClient.handleParticles(packet);
    }

    @Override
    public void a(PacketPlayOutAbilities serverPacket) {
        val packet = new S39PacketPlayerAbilities(serverPacket.isA(), serverPacket.isB(), serverPacket.isC(),
                serverPacket.isD(), serverPacket.getE(), serverPacket.getF());
        netHandlerPlayClient.handlePlayerAbilities(packet);
    }

    @Override
    public void a(PacketPlayOutPlayerInfo serverPacket) {
        val infoAction = serverPacket.getA();
        val data = serverPacket.getB();
        val dataCopy = new ArrayList<>(data);
        for (val playerInfoData : dataCopy) {
            if (playerInfoData == null)
                continue;
            val gameProfile = playerInfoData.a();
            val name = gameProfile.getName();
            val ping = playerInfoData.b();

            val packet = switch (infoAction) {
                case ADD_PLAYER -> new S38PacketPlayerListItem(name, true, ping);
                case REMOVE_PLAYER -> new S38PacketPlayerListItem(name, false, ping);
                case UPDATE_DISPLAY_NAME, UPDATE_GAME_MODE, UPDATE_LATENCY ->
                    null;
            };

            if (packet != null)
                netHandlerPlayClient.handlePlayerListItem(packet);
        }
    }

    @Override
    public void a(PacketPlayOutEntityDestroy serverPacket) {
        val packet = new S13PacketDestroyEntities(serverPacket.getA());
        netHandlerPlayClient.handleDestroyEntities(packet);
    }

    @Override
    public void a(PacketPlayOutRemoveEntityEffect serverPacket) {
        val packet = new S1EPacketRemoveEntityEffect(serverPacket.getA(), serverPacket.getB());
        netHandlerPlayClient.handleRemoveEntityEffect(packet);
    }

    @Override
    public void a(PacketPlayOutRespawn serverPacket) {
        val enumDifficulty = serverPacket.getB();
        val enumGamemode = serverPacket.getC();
        val worldType = serverPacket.getD();

        val gameType = WorldSettings.GameType.getByID(enumGamemode.getId());
        val difficulty = EnumDifficulty.getDifficultyEnum(enumDifficulty.a());
        val worldType1 = WorldType.parseWorldType(worldType.name());
        val packet = new S07PacketRespawn(serverPacket.getA(), difficulty, worldType1, gameType);
        netHandlerPlayClient.handleRespawn(packet);
    }

    @Override
    public void a(PacketPlayOutEntityHeadRotation serverPacket) {
        val packet = new S19PacketEntityHeadLook(serverPacket.getA(), serverPacket.getB());
        netHandlerPlayClient.handleEntityHeadLook(packet);
    }

    @Override
    public void a(PacketPlayOutHeldItemSlot serverPacket) {
        val packet = new S09PacketHeldItemChange(serverPacket.getA());
        netHandlerPlayClient.handleHeldItemChange(packet);
    }

    @Override
    public void a(PacketPlayOutScoreboardDisplayObjective serverPacket) {
        val packet = new S3DPacketDisplayScoreboard(serverPacket.getA(), serverPacket.getB());
        netHandlerPlayClient.handleDisplayScoreboard(packet);
    }

    @Override
    public void a(PacketPlayOutEntityMetadata serverPacket) {
        val dataWatcherListNMS = serverPacket.getB();
        val dataWatcherList = new ArrayList<DataWatcher.WatchableObject>();

        val entityId = serverPacket.getA();

        val entity = netHandlerPlayClient.getClientWorldController().getEntityByID(entityId);

        if (dataWatcherListNMS != null) {
            for (val watchableObjectNMS : dataWatcherListNMS) {
                DataWatcher.WatchableObject watchableObject = fromNMS(watchableObjectNMS, entity);

                if (watchableObject != null)
                    dataWatcherList.add(watchableObject);
            }
        }

        val packet = new S1CPacketEntityMetadata(entityId, dataWatcherList);
        netHandlerPlayClient.handleEntityMetadata(packet);
    }

    @Override
    public void a(PacketPlayOutEntityVelocity serverPacket) {
        val packet = new S12PacketEntityVelocity(serverPacket.getA(), serverPacket.getB() / 8000D,
                serverPacket.getC() / 8000D,
                serverPacket.getD() / 8000D);
        netHandlerPlayClient.handleEntityVelocity(packet);
    }

    @Override
    public void a(PacketPlayOutEntityEquipment serverPacket) {

        @Nullable
        val itemNMS = serverPacket.getC();
        @Nullable
        val item = fromNMS(itemNMS);

        val packet = new S04PacketEntityEquipment(serverPacket.getA(), serverPacket.getB(),
                item);
        netHandlerPlayClient.handleEntityEquipment(packet);
    }

    @Override
    public void a(PacketPlayOutExperience serverPacket) {
        val packet = new S1FPacketSetExperience(serverPacket.getA(), serverPacket.getB(), serverPacket.getC());
        netHandlerPlayClient.handleSetExperience(packet);
    }

    @Override
    public void a(PacketPlayOutUpdateHealth serverPacket) {
        val packet = new S06PacketUpdateHealth(serverPacket.getA(), serverPacket.getB(), serverPacket.getC());
        netHandlerPlayClient.handleUpdateHealth(packet);
    }

    @Override
    public void a(PacketPlayOutScoreboardTeam serverPacket) {
        val packet = new S3EPacketTeams(serverPacket.getA(), serverPacket.getB(), serverPacket.getC(),
                serverPacket.getD(), serverPacket.getG(),
                serverPacket.getH(), serverPacket.getI());
        netHandlerPlayClient.handleTeams(packet);
    }

    @Override
    public void a(PacketPlayOutScoreboardScore serverPacket) {
        val packet = new S3CPacketUpdateScore(serverPacket.getA(), serverPacket.getB(), serverPacket.getC(),
                serverPacket.getD().ordinal());
        netHandlerPlayClient.handleUpdateScore(packet);
    }

    @Override
    public void a(PacketPlayOutSpawnPosition serverPacket) {
        val packet = new S05PacketSpawnPosition(serverPacket.getPosition().getX(),
                serverPacket.getPosition().getY(), serverPacket.getPosition().getZ());
        netHandlerPlayClient.handleSpawnPosition(packet);
    }

    @Override
    public void a(PacketPlayOutUpdateTime serverPacket) {
        val packet = new S03PacketTimeUpdate(serverPacket.getA(), serverPacket.getB());
        netHandlerPlayClient.handleTimeUpdate(packet);
    }

    @Override
    public void a(PacketPlayOutUpdateSign serverPacket) {
        val nmsChatComponents = serverPacket.getC();
        val lines = new String[nmsChatComponents.length];

        for (int i = 0; i < nmsChatComponents.length; i++) {
            val text = IChatBaseComponent.ChatSerializer.a(nmsChatComponents[i]);
            val chatComponent = IChatComponent.Serializer.fromJson(text);
            lines[i] = chatComponent.getUnformattedText();
        }

        val packet = new S33PacketUpdateSign(serverPacket.getB().getX(), serverPacket.getB().getY(),
                serverPacket.getB().getZ(),
                lines);
        netHandlerPlayClient.handleUpdateSign(packet);
    }

    @Override
    public void a(PacketPlayOutNamedSoundEffect serverPacket) {
        val packet = new S29PacketSoundEffect(serverPacket.getA(), serverPacket.getB(), serverPacket.getC(),
                serverPacket.getD(),
                serverPacket.getE(), serverPacket.getF());
        netHandlerPlayClient.handleSoundEffect(packet);
    }

    @Override
    public void a(PacketPlayOutCollect serverPacket) {
        val packet = new S0DPacketCollectItem(serverPacket.getA(), serverPacket.getB());
        netHandlerPlayClient.handleCollectItem(packet);
    }

    @Override
    public void a(PacketPlayOutEntityTeleport serverPacket) {
        val packet = new S18PacketEntityTeleport(serverPacket.getA(), serverPacket.getB(), serverPacket.getC(),
                serverPacket.getD(), serverPacket.getE(), serverPacket.getF());
        netHandlerPlayClient.handleEntityTeleport(packet);
    }

    @Override
    public void a(PacketPlayOutUpdateAttributes serverPacket) {
        val snapshotListNMS = serverPacket.getB();
        val snapshotList = new ArrayList<S20PacketEntityProperties.Snapshot>();

        for (val attributeSnapshot : snapshotListNMS) {
            val attributeName = attributeSnapshot.a();
            val baseValue = attributeSnapshot.b();
            val modifiersNMS = attributeSnapshot.c();
            val modifiers = new ArrayList<AttributeModifier>();

            for (val attributeModifier : modifiersNMS) {
                val uuid = attributeModifier.a();
                val name = attributeModifier.b();
                val amount = attributeModifier.d();
                val operation = attributeModifier.c();
                val modifier = new AttributeModifier(uuid, name, amount, operation);
                modifiers.add(modifier);
            }

            val snapshot = new S20PacketEntityProperties.Snapshot(
                    attributeName, baseValue, modifiers);
            snapshotList.add(snapshot);
        }

        val packet = new S20PacketEntityProperties(serverPacket.getA(), snapshotList);
        netHandlerPlayClient.handleEntityProperties(packet);
    }

    @Override
    public void a(PacketPlayOutEntityEffect serverPacket) {
        val packet = new S1DPacketEntityEffect(serverPacket.getA(), serverPacket.getB(), serverPacket.getC(),
                (short) serverPacket.getD());
        netHandlerPlayClient.handleEntityEffect(packet);
    }

    @Override
    public void a(PacketPlayOutCombatEvent serverPacket) {
        // TODO: implement
    }

    @Override
    public void a(PacketPlayOutServerDifficulty serverPacket) {
        // TODO: implement
    }

    @Override
    public void a(PacketPlayOutCamera serverPacket) {
        // TODO: implement
    }

    @Override
    public void a(PacketPlayOutWorldBorder serverPacket) {
        // TODO: implement
    }

    @Override
    public void a(PacketPlayOutTitle serverPacket) {
        // TODO: implement
    }

    @Override
    public void a(PacketPlayOutSetCompression serverPacket) {
        // TODO: implement
    }

    @Override
    public void a(PacketPlayOutPlayerListHeaderFooter serverPacket) {
        // TODO: implement
    }

    @Override
    public void a(PacketPlayOutResourcePackSend serverPacket) {
        // TODO: implement
    }

    @Override
    public void a(PacketPlayOutUpdateEntityNBT serverPacket) {
        // TODO: implement
    }

    @Override
    public void a(PacketStatusOutServerInfo serverPacket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'a'");
    }

    @Override
    public void a(PacketStatusOutPong serverPacket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'a'");
    }

    @Override
    public void a(PacketLoginOutEncryptionBegin serverPacket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'a'");
    }

    @Override
    public void a(PacketLoginOutSuccess serverPacket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'a'");
    }

    @Override
    public void a(PacketLoginOutDisconnect serverPacket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'a'");
    }

    @Override
    public void a(PacketLoginOutSetCompression serverPacket) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'a'");
    }

}
