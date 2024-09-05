package gg.mineral.bot.plugin.network.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
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
import net.minecraft.server.v1_8_R3.PacketPlayOutMultiBlockChange.MultiBlockChangeInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.PlayerInfoData;
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
import net.minecraft.server.v1_8_R3.Statistic;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.NibbleArray;
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

    @Nullable
    private ItemStack fromNMS(net.minecraft.server.v1_8_R3.ItemStack itemNMS) {
        if (itemNMS == null)
            return null;

        net.minecraft.server.v1_8_R3.NBTTagCompound nmsNbt = new net.minecraft.server.v1_8_R3.NBTTagCompound();
        itemNMS.save(nmsNbt);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream buf = new DataOutputStream(baos);

        try {
            NBTCompressedStreamTools.a(nmsNbt, (DataOutput) buf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] bytes = baos.toByteArray();

        try {
            baos.close();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();

        }

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));

        try {
            NBTTagCompound nbt = CompressedStreamTools.read(dis);
            ItemStack item = ItemStack.loadItemStackFromNBT(nbt);
            return item;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    private DataWatcher.WatchableObject fromNMS(
            net.minecraft.server.v1_8_R3.DataWatcher.WatchableObject watchableObject, Entity entity) {
        int type = watchableObject.c();
        int id = watchableObject.a();
        Object value = watchableObject.b();

        switch (id) {
            case 12:
                if (entity != null && entity instanceof EntityAgeable) {
                    type = 2;
                    value = Integer.valueOf(((Byte) value).intValue());
                }
                break;
            default:
                break;
        }

        switch (type) {
            case 5: // ItemStack
                @Nullable
                net.minecraft.server.v1_8_R3.ItemStack itemNMS = (net.minecraft.server.v1_8_R3.ItemStack) value;

                value = fromNMS(itemNMS);

                if (value == null)
                    return null;
                break;
            case 6: // BlockPos
                net.minecraft.server.v1_8_R3.BlockPosition blockPosition = (net.minecraft.server.v1_8_R3.BlockPosition) value;
                value = new ChunkCoordinates(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
                break;
            case 7: // Rotations
                return null;
        }

        DataWatcher.WatchableObject dataWatcher = new DataWatcher.WatchableObject(type, id, value);

        dataWatcher.setWatched(watchableObject.d());

        return dataWatcher;
    }

    @Setter
    private NetHandlerPlayClient netHandlerPlayClient;

    public void handlePacket(Packet<PacketListenerPlayOut> packet) {
        packet.a(this);
    }

    @Override
    public void a(IChatBaseComponent arg0) {
        String text = IChatBaseComponent.ChatSerializer.a(arg0);
        IChatComponent chatComponent = IChatComponent.Serializer.fromJson(text);
        netHandlerPlayClient.onDisconnect(chatComponent);
    }

    @Override
    public void a(PacketPlayOutSpawnEntity arg0) {
        S0EPacketSpawnObject packet = new S0EPacketSpawnObject(arg0.getA(), arg0.getB(), arg0.getC(), arg0.getD(),
                arg0.getE(), arg0.getF(), arg0.getG(), arg0.getH(), arg0.getI(), arg0.getJ(), arg0.getK());
        netHandlerPlayClient.handleSpawnObject(packet);
    }

    @Override
    public void a(PacketPlayOutSpawnEntityExperienceOrb arg0) {
        S11PacketSpawnExperienceOrb packet = new S11PacketSpawnExperienceOrb(arg0.getA(), arg0.getB(), arg0.getC(),
                arg0.getD(), arg0.getE());
        netHandlerPlayClient.handleSpawnExperienceOrb(packet);
    }

    @Override
    public void a(PacketPlayOutSpawnEntityWeather arg0) {
        S2CPacketSpawnGlobalEntity packet = new S2CPacketSpawnGlobalEntity(arg0.getA(), arg0.getB(), arg0.getC(),
                arg0.getD(), arg0.getE());
        netHandlerPlayClient.handleSpawnGlobalEntity(packet);
    }

    @Override
    public void a(PacketPlayOutSpawnEntityLiving arg0) {
        int entityTypeId = arg0.getB();

        if (entityTypeId == 101) // TODO: Rabbit
            return;

        List<net.minecraft.server.v1_8_R3.DataWatcher.WatchableObject> list = arg0.getM();
        List<DataWatcher.WatchableObject> dataWatcherList = new ArrayList<>();

        int entityId = arg0.getA();

        Entity entity = netHandlerPlayClient.getClientWorldController().getEntityByID(entityId);

        if (list != null) {
            for (net.minecraft.server.v1_8_R3.DataWatcher.WatchableObject watchableObject : list) {
                DataWatcher.WatchableObject dataWatcher = fromNMS(watchableObject, entity);

                if (dataWatcher == null)
                    continue;

                dataWatcherList.add(dataWatcher);
            }
        }

        S0FPacketSpawnMob packet = new S0FPacketSpawnMob(entityId, entityTypeId, arg0.getC(), arg0.getD(),
                arg0.getE(), arg0.getF(), arg0.getG(), arg0.getH(), arg0.getI(), arg0.getJ(), arg0.getK(), null,
                dataWatcherList);

        netHandlerPlayClient.handleSpawnMob(packet);
    }

    @Override
    public void a(PacketPlayOutScoreboardObjective arg0) {
        String name = arg0.getA();
        String value = arg0.getB();
        int mode = arg0.getD();
        S3BPacketScoreboardObjective packet = new S3BPacketScoreboardObjective(name, value, mode);
        netHandlerPlayClient.handleScoreboardObjective(packet);
    }

    @Override
    public void a(PacketPlayOutSpawnEntityPainting arg0) {
        S10PacketSpawnPainting packet = new S10PacketSpawnPainting(arg0.getA(), arg0.getB().getX(), arg0.getB().getY(),
                arg0.getB().getZ(), arg0.getC().a(), arg0.getD());
        netHandlerPlayClient.handleSpawnPainting(packet);
    }

    @Override
    public void a(PacketPlayOutNamedEntitySpawn arg0) {
        @Nullable
        List<net.minecraft.server.v1_8_R3.DataWatcher.WatchableObject> list = arg0.getJ();
        List<DataWatcher.WatchableObject> dataWatcherList = new ArrayList<>();

        int entityId = arg0.getA();

        Entity entity = netHandlerPlayClient.getClientWorldController().getEntityByID(entityId);

        if (list != null) {
            for (net.minecraft.server.v1_8_R3.DataWatcher.WatchableObject watchableObject : list) {
                DataWatcher.WatchableObject dataWatcher = fromNMS(watchableObject, entity);

                if (dataWatcher == null)
                    continue;
                dataWatcherList.add(dataWatcher);
            }
        }

        // TODO: name
        S0CPacketSpawnPlayer packet = new S0CPacketSpawnPlayer(entityId,
                new GameProfile(arg0.getB(), ""),
                arg0.getC(), arg0.getD(),
                arg0.getE(), arg0.getF(), arg0.getG(), arg0.getH(), null, dataWatcherList);
        netHandlerPlayClient.handleSpawnPlayer(packet);
    }

    @Override
    public void a(PacketPlayOutAnimation arg0) {
        S0BPacketAnimation packet = new S0BPacketAnimation(arg0.getA(), arg0.getB());
        netHandlerPlayClient.handleAnimation(packet);
    }

    @Override
    public void a(PacketPlayOutStatistic arg0) {
        Object2IntOpenHashMap<Statistic> statisticMap = arg0.getA();
        Object2IntOpenHashMap<StatBase> statMap = new Object2IntOpenHashMap<>();

        for (Entry<Statistic> e : statisticMap.object2IntEntrySet()) {
            StatBase stat = StatList.func_151177_a(e.getKey().name);
            statMap.put(stat, e.getIntValue());
        }

        S37PacketStatistics packet = new S37PacketStatistics(statMap);
        netHandlerPlayClient.handleStatistics(packet);
    }

    @Override
    public void a(PacketPlayOutBlockBreakAnimation arg0) {
        S25PacketBlockBreakAnim packet = new S25PacketBlockBreakAnim(arg0.getA(), arg0.getB().getX(),
                arg0.getB().getY(), arg0.getB().getZ(), arg0.getC());
        netHandlerPlayClient.handleBlockBreakAnim(packet);
    }

    @Override
    public void a(PacketPlayOutOpenSignEditor arg0) {
        S36PacketSignEditorOpen packet = new S36PacketSignEditorOpen(arg0.getA().getX(), arg0.getA().getY(),
                arg0.getA().getZ());
        netHandlerPlayClient.handleSignEditorOpen(packet);
    }

    @Override
    public void a(PacketPlayOutTileEntityData arg0) {

        net.minecraft.server.v1_8_R3.NBTTagCompound nmsNbt = arg0.getC();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream buf = new DataOutputStream(baos);

        try {
            NBTCompressedStreamTools.a(nmsNbt, (DataOutput) buf);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }

        byte[] bytes = baos.toByteArray();

        try {
            baos.close();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));

        try {
            NBTTagCompound nbt = CompressedStreamTools.read(dis);

            S35PacketUpdateTileEntity packet = new S35PacketUpdateTileEntity(arg0.getA().getX(), arg0.getA().getY(),
                    arg0.getA().getZ(), arg0.getB(), nbt);
            netHandlerPlayClient.handleUpdateTileEntity(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void a(PacketPlayOutBlockAction arg0) {
        int blockId = net.minecraft.server.v1_8_R3.Block.getId(arg0.getD()) & 4095;
        Block block = Block.getBlockById(blockId);
        S24PacketBlockAction packet = new S24PacketBlockAction(arg0.getA().getX(), arg0.getA().getY(),
                arg0.getA().getZ(), arg0.getB(), arg0.getC(), block);
        netHandlerPlayClient.handleBlockAction(packet);
    }

    @Override
    public void a(PacketPlayOutBlockChange arg0) {
        int idAndMeta = net.minecraft.server.v1_8_R3.Block.d.b(arg0.getBlock());
        int blockId = idAndMeta >> 4;
        int blockMeta = idAndMeta & 15;
        Block block = Block.getBlockById(blockId);
        S23PacketBlockChange packet = new S23PacketBlockChange(arg0.getA().getX(), arg0.getA().getY(),
                arg0.getA().getZ(), block, blockMeta);
        netHandlerPlayClient.handleBlockChange(packet);
    }

    @Override
    public void a(PacketPlayOutChat arg0) {
        String text = IChatBaseComponent.ChatSerializer.a(arg0.getA());
        IChatComponent chatComponent = IChatComponent.Serializer.fromJson(text);
        if (chatComponent == null)
            return;
        S02PacketChat packet = new S02PacketChat(chatComponent);
        netHandlerPlayClient.handleChat(packet);
    }

    @Override
    public void a(PacketPlayOutTabComplete arg0) {
        S3APacketTabComplete packet = new S3APacketTabComplete(arg0.getA());
        netHandlerPlayClient.handleTabComplete(packet);
    }

    @Override
    public void a(PacketPlayOutMultiBlockChange arg0) {
        net.minecraft.server.v1_8_R3.ChunkCoordIntPair chunkCoordIntPair = arg0.getA();
        ChunkCoordIntPair chunkCoordIntPair1 = new ChunkCoordIntPair(chunkCoordIntPair.x, chunkCoordIntPair.z);

        int recordLength = arg0.getB().length;

        ByteBuffer buffer = ByteBuffer.allocate(recordLength * 4);

        for (int i = 0; i < recordLength; i++) {
            MultiBlockChangeInfo record = arg0.getB()[i];
            buffer.putShort(record.b());
            int idAndMeta = net.minecraft.server.v1_8_R3.Block.d.b(record.c());
            buffer.putShort((short) (idAndMeta >> 4));
        }

        S22PacketMultiBlockChange packet = new S22PacketMultiBlockChange(chunkCoordIntPair1, buffer.array(),
                recordLength);
        netHandlerPlayClient.handleMultiBlockChange(packet);
    }

    @Override
    public void a(PacketPlayOutMap arg0) {
        // TODO: implement
    }

    @Override
    public void a(PacketPlayOutTransaction arg0) {
        S32PacketConfirmTransaction packet = new S32PacketConfirmTransaction(arg0.getA(), arg0.getB(), arg0.isC());
        netHandlerPlayClient.handleConfirmTransaction(packet);
    }

    @Override
    public void a(PacketPlayOutCloseWindow arg0) {
        S2EPacketCloseWindow packet = new S2EPacketCloseWindow(arg0.getA());
        netHandlerPlayClient.handleCloseWindow(packet);
    }

    @Override
    public void a(PacketPlayOutWindowItems arg0) {
        ItemStack[] items = new ItemStack[arg0.getB().length];

        for (int i = 0; i < items.length; i++) {
            @Nullable
            net.minecraft.server.v1_8_R3.ItemStack itemNMS = arg0.getB()[i];
            items[i] = fromNMS(itemNMS);
        }

        S30PacketWindowItems packet = new S30PacketWindowItems(arg0.getA(), items);
        netHandlerPlayClient.handleWindowItems(packet);
    }

    @Override
    public void a(PacketPlayOutOpenWindow arg0) {
        S2DPacketOpenWindow packet = new S2DPacketOpenWindow(arg0.getA(), WINDOW_TYPE_REGISTRY.getInt(arg0.getB()),
                arg0.getC().c(), arg0.getD(), true,
                arg0.getE());
        netHandlerPlayClient.handleOpenWindow(packet);
    }

    @Override
    public void a(PacketPlayOutWindowData arg0) {
        S31PacketWindowProperty packet = new S31PacketWindowProperty(arg0.getA(), arg0.getB(), arg0.getC());
        netHandlerPlayClient.handleWindowProperty(packet);
    }

    @Override
    public void a(PacketPlayOutSetSlot arg0) {
        @Nullable
        net.minecraft.server.v1_8_R3.ItemStack itemNMS = arg0.getC();
        @Nullable
        ItemStack item = fromNMS(itemNMS);

        S2FPacketSetSlot packet = new S2FPacketSetSlot(arg0.getA(), arg0.getB(),
                item);

        if (item != null)
            System.out.println(item.hasDisplayName());
        netHandlerPlayClient.handleSetSlot(packet);
    }

    @Override
    public void a(PacketPlayOutCustomPayload arg0) {
        S3FPacketCustomPayload packet = new S3FPacketCustomPayload(arg0.getA(), arg0.getB());
        netHandlerPlayClient.handleCustomPayload(packet);
    }

    @Override
    public void a(PacketPlayOutKickDisconnect arg0) {
        String text = IChatBaseComponent.ChatSerializer.a(arg0.getA());
        IChatComponent chatComponent = IChatComponent.Serializer.fromJson(text);
        S40PacketDisconnect packet = new S40PacketDisconnect(chatComponent);
        netHandlerPlayClient.handleDisconnect(packet);
    }

    @Override
    public void a(PacketPlayOutBed arg0) {
        S0APacketUseBed packet = new S0APacketUseBed(arg0.getA(), arg0.getB().getX(), arg0.getB().getY(),
                arg0.getB().getZ());
        netHandlerPlayClient.handleUseBed(packet);
    }

    @Override
    public void a(PacketPlayOutEntityStatus arg0) {
        S19PacketEntityStatus packet = new S19PacketEntityStatus(arg0.getA(), arg0.getB());
        netHandlerPlayClient.handleEntityStatus(packet);
    }

    @Override
    public void a(PacketPlayOutAttachEntity arg0) {
        S1BPacketEntityAttach packet = new S1BPacketEntityAttach(arg0.getA(), arg0.getB(), arg0.getC());
        netHandlerPlayClient.handleEntityAttach(packet);
    }

    @Override
    public void a(PacketPlayOutExplosion arg0) {
        List<net.minecraft.server.v1_8_R3.BlockPosition> list = arg0.getE();
        List<ChunkPosition> chunkPositionList = new ArrayList<>();

        for (net.minecraft.server.v1_8_R3.BlockPosition blockPosition : list)
            chunkPositionList.add(new ChunkPosition(blockPosition.getX(), blockPosition.getY(),
                    blockPosition.getZ()));

        S27PacketExplosion packet = new S27PacketExplosion(arg0.getA(), arg0.getB(), arg0.getC(), arg0.getD(),
                chunkPositionList, arg0.getF(), arg0.getG(), arg0.getH());
        netHandlerPlayClient.handleExplosion(packet);
    }

    @Override
    public void a(PacketPlayOutGameStateChange arg0) {
        S2BPacketChangeGameState packet = new S2BPacketChangeGameState(arg0.getB(), arg0.getC());
        netHandlerPlayClient.handleChangeGameState(packet);
    }

    @Override
    public void a(PacketPlayOutKeepAlive arg0) {
        S00PacketKeepAlive packet = new S00PacketKeepAlive(arg0.getA());
        netHandlerPlayClient.handleKeepAlive(packet);
    }

    @Override
    public void a(PacketPlayOutMapChunk arg0) {
        net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk.ChunkMap chunkMap = arg0.getC();

        byte[] data = chunkMap.a;

        int primaryBitMask = chunkMap.b;
        boolean groundUpContinuous = arg0.isD();

        Object[] result = fillChunk(data, primaryBitMask, groundUpContinuous);

        ExtendedBlockStorage[] storageArrays = (ExtendedBlockStorage[]) result[0];
        byte[] blockBiomeArray = (byte[]) result[1];

        S21PacketChunkData packet = new S21PacketChunkData(netHandlerPlayClient.getGameController(), arg0.getA(),
                arg0.getB(), storageArrays, blockBiomeArray,
                groundUpContinuous, primaryBitMask);
        netHandlerPlayClient.handleChunkData(packet);
    }

    public Object[] fillChunk(byte[] data, int primaryBitMask, boolean groundUpContinuous) {
        int i = 0;
        boolean hasSky = true;

        ExtendedBlockStorage[] storageArrays = new ExtendedBlockStorage[16];
        byte[] blockBiomeArray = new byte[256];

        // Process each section of the chunk
        for (int j = 0; j < storageArrays.length; ++j) {
            if ((primaryBitMask & (1 << j)) != 0) {
                if (storageArrays[j] == null)
                    storageArrays[j] = new ExtendedBlockStorage(j << 4, hasSky);

                ExtendedBlockStorage storage = storageArrays[j];

                // Iterate through the block storage and directly assign values
                for (int k = 0; k < 4096; ++k) {
                    if (i + 1 >= data.length)
                        break;

                    int x = k & 15, y = (k >> 8) & 15, z = (k >> 4) & 15;

                    // Retrieve block ID and metadata from the data array
                    int blockId = ((data[i + 1] & 255) << 8) | (data[i] & 255);
                    int metadata = blockId & 15; // The last 4 bits are the metadata
                    blockId >>= 4; // The remaining bits are the block ID

                    // Set the block ID and metadata directly into the storage arrays
                    storage.getBlockLSBArray()[k] = (byte) (blockId & 255);

                    if (blockId > 255) {
                        if (storage.getBlockMSBArray() == null)
                            storage.setBlockMSBArray(new NibbleArray(4096, 4));

                        storage.getBlockMSBArray().set(x, y, z, (blockId >> 8) & 15);
                    } else if (storage.getBlockMSBArray() != null)
                        storage.getBlockMSBArray().set(x, y, z, 0);

                    storage.getMetadataArray().set(x, y, z, metadata);

                    i += 2;
                }
            } else if (groundUpContinuous && storageArrays[j] != null)
                storageArrays[j] = null;
        }

        // Copy block light array data
        for (int l = 0; l < storageArrays.length; ++l) {
            if ((primaryBitMask & (1 << l)) != 0 && storageArrays[l] != null) {
                NibbleArray blocklightArray = storageArrays[l].getBlocklightArray();
                if (i + blocklightArray.getData().length > data.length)
                    break;

                System.arraycopy(data, i, blocklightArray.getData(), 0, blocklightArray.getData().length);
                i += blocklightArray.getData().length;
            }
        }

        // Copy sky light array data if applicable
        if (hasSky) {
            for (int m = 0; m < storageArrays.length; ++m) {
                if ((primaryBitMask & (1 << m)) != 0 && storageArrays[m] != null) {
                    NibbleArray skylightArray = storageArrays[m].getSkylightArray();
                    if (i + skylightArray.getData().length > data.length)
                        break;

                    System.arraycopy(data, i, skylightArray.getData(), 0, skylightArray.getData().length);
                    i += skylightArray.getData().length;
                }
            }
        }

        // Copy biome data if ground-up continuous
        if (groundUpContinuous)
            if (i + blockBiomeArray.length <= data.length)
                System.arraycopy(data, i, blockBiomeArray, 0, blockBiomeArray.length);

        // Recalculate reference counts for each section
        for (int n = 0; n < storageArrays.length; ++n)
            if (storageArrays[n] != null && (primaryBitMask & (1 << n)) != 0)
                storageArrays[n].removeInvalidBlocks();

        return new Object[] { storageArrays, blockBiomeArray };
    }

    @Override
    public void a(PacketPlayOutMapChunkBulk arg0) {
        int[] chunkXArr = arg0.getA();
        int[] chunkZArr = arg0.getB();
        net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk.ChunkMap[] chunkMapArr = arg0.getC();
        boolean groundUpContinuous = arg0.isD();

        ExtendedBlockStorage[][] storageArraysArr = new ExtendedBlockStorage[chunkMapArr.length][16];
        byte[][] blockBiomeArrayArr = new byte[chunkMapArr.length][256];

        for (int i = 0; i < chunkMapArr.length; i++) {
            net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk.ChunkMap chunkMap = chunkMapArr[i];
            byte[] data = chunkMap.a;
            int primaryBitMask = chunkMap.b;

            Object[] result = fillChunk(data, primaryBitMask, groundUpContinuous);

            ExtendedBlockStorage[] storageArrays = (ExtendedBlockStorage[]) result[0];
            byte[] blockBiomeArray = (byte[]) result[1];

            storageArraysArr[i] = storageArrays;
            blockBiomeArrayArr[i] = blockBiomeArray;
        }

        S26PacketMapChunkBulk packet = new S26PacketMapChunkBulk(netHandlerPlayClient.getGameController(), chunkXArr,
                chunkZArr, storageArraysArr,
                blockBiomeArrayArr, groundUpContinuous);
        netHandlerPlayClient.handleMapChunkBulk(packet);

    }

    @Override
    public void a(PacketPlayOutWorldEvent arg0) {
        S28PacketEffect packet = new S28PacketEffect(arg0.getA(), arg0.getB().getX(), arg0.getB().getY(),
                arg0.getB().getZ(), arg0.getC(), arg0.isD());
        netHandlerPlayClient.handleEffect(packet);
    }

    @Override
    public void a(PacketPlayOutLogin arg0) {
        net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode enumGamemode = arg0.getC();
        net.minecraft.server.v1_8_R3.EnumDifficulty enumDifficulty = arg0.getE();
        net.minecraft.server.v1_8_R3.WorldType worldType = arg0.getG();

        WorldSettings.GameType gameType = WorldSettings.GameType.getByID(enumGamemode.getId());
        EnumDifficulty difficulty = EnumDifficulty.getDifficultyEnum(enumDifficulty.a());
        WorldType worldType1 = WorldType.parseWorldType(worldType.name());
        S01PacketJoinGame packet = new S01PacketJoinGame(arg0.getA(), arg0.isB(), gameType, arg0.getD(),
                difficulty, arg0.getF(), worldType1);
        netHandlerPlayClient.handleJoinGame(packet);
    }

    @Override
    public void a(PacketPlayOutEntity arg0) {
        S14PacketEntity packet = new S14PacketEntity(arg0.getA(), arg0.getB(), arg0.getC(), arg0.getD(), arg0.getE(),
                arg0.getF(), arg0.isG());
        netHandlerPlayClient.handleEntityMovement(packet);
    }

    @Override
    public void a(PacketPlayOutPosition arg0) {
        Set<net.minecraft.server.v1_8_R3.PacketPlayOutPosition.EnumPlayerTeleportFlags> set = arg0.getF();
        final double x = arg0.getA();
        double y = arg0.getB();
        final double z = arg0.getC();

        final float yaw = arg0.getD(), pitch = arg0.getE();

        Minecraft mc = netHandlerPlayClient.getGameController();

        EntityClientPlayerMP thePlayer = mc.thePlayer;

        if (thePlayer == null)
            return;

        assert thePlayer.posY - thePlayer.boundingBox.minY == 1.62D;

        // x, y, and z
        if (set.contains(net.minecraft.server.v1_8_R3.PacketPlayOutPosition.EnumPlayerTeleportFlags.X))
            arg0.setA(x + thePlayer.posX);
        if (set.contains(net.minecraft.server.v1_8_R3.PacketPlayOutPosition.EnumPlayerTeleportFlags.Y))
            y += thePlayer.boundingBox.minY;

        arg0.setB(y + 1.62D + 1e-5);

        if (set.contains(net.minecraft.server.v1_8_R3.PacketPlayOutPosition.EnumPlayerTeleportFlags.Z))
            arg0.setC(z + thePlayer.posZ);

        // yaw and pitch
        if (set.contains(net.minecraft.server.v1_8_R3.PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT))
            arg0.setD(yaw + thePlayer.rotationYaw);
        if (set.contains(net.minecraft.server.v1_8_R3.PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT))
            arg0.setE(pitch + thePlayer.rotationPitch);

        S08PacketPlayerPosLook packet = new S08PacketPlayerPosLook(arg0.getA(), arg0.getB(), arg0.getC(), arg0.getD(),
                arg0.getE(), thePlayer.onGround);
        netHandlerPlayClient.handlePlayerPosLook(packet);
    }

    @Override
    public void a(PacketPlayOutWorldParticles arg0) {
        net.minecraft.server.v1_8_R3.EnumParticle enumParticle = arg0.getA();
        String particleName = enumParticle.b();

        if (particleName == null || particleName.isEmpty() || particleName.equalsIgnoreCase("blockdust_")
                || particleName.equalsIgnoreCase("blockcrack_"))
            return;
        S2APacketParticles packet = new S2APacketParticles(particleName, arg0.getB(), arg0.getC(), arg0.getD(),
                arg0.getE(), arg0.getF(), arg0.getG(), arg0.getH(), arg0.getI());
        netHandlerPlayClient.handleParticles(packet);
    }

    @Override
    public void a(PacketPlayOutAbilities arg0) {
        S39PacketPlayerAbilities packet = new S39PacketPlayerAbilities(arg0.isA(), arg0.isB(), arg0.isC(),
                arg0.isD(), arg0.getE(), arg0.getF());
        netHandlerPlayClient.handlePlayerAbilities(packet);
    }

    @Override
    public void a(PacketPlayOutPlayerInfo arg0) {
        net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction a = arg0.getA();
        List<net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.PlayerInfoData> b = arg0.getB();
        int numberOfPlayers = b.size();

        for (int i = 0; i < numberOfPlayers; i++) {
            PlayerInfoData playerInfoData = b.get(i);
            GameProfile gameProfile = playerInfoData.a();
            String name = gameProfile.getName();
            int ping = playerInfoData.b();

            S38PacketPlayerListItem packet = null;

            switch (a) {
                case ADD_PLAYER:
                    packet = new S38PacketPlayerListItem(name, true, ping);
                    break;
                case REMOVE_PLAYER:
                    packet = new S38PacketPlayerListItem(name, false, ping);
                    break;
                case UPDATE_DISPLAY_NAME:
                    // TODO: implement display name
                    break;
                case UPDATE_GAME_MODE:
                    // TODO: set equiped item if gamemode 3
                    break;
                case UPDATE_LATENCY:
                    // TODO: update latency
                    break;
                default:
                    break;
            }

            if (packet != null)
                netHandlerPlayClient.handlePlayerListItem(packet);
        }
    }

    @Override
    public void a(PacketPlayOutEntityDestroy arg0) {
        S13PacketDestroyEntities packet = new S13PacketDestroyEntities(arg0.getA());
        netHandlerPlayClient.handleDestroyEntities(packet);
    }

    @Override
    public void a(PacketPlayOutRemoveEntityEffect arg0) {
        S1EPacketRemoveEntityEffect packet = new S1EPacketRemoveEntityEffect(arg0.getA(), arg0.getB());
        netHandlerPlayClient.handleRemoveEntityEffect(packet);
    }

    @Override
    public void a(PacketPlayOutRespawn arg0) {
        net.minecraft.server.v1_8_R3.EnumDifficulty enumDifficulty = arg0.getB();
        net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode enumGamemode = arg0.getC();
        net.minecraft.server.v1_8_R3.WorldType worldType = arg0.getD();

        WorldSettings.GameType gameType = WorldSettings.GameType.getByID(enumGamemode.getId());
        EnumDifficulty difficulty = EnumDifficulty.getDifficultyEnum(enumDifficulty.a());
        WorldType worldType1 = WorldType.parseWorldType(worldType.name());
        S07PacketRespawn packet = new S07PacketRespawn(arg0.getA(), difficulty, worldType1, gameType);
        netHandlerPlayClient.handleRespawn(packet);
    }

    @Override
    public void a(PacketPlayOutEntityHeadRotation arg0) {
        S19PacketEntityHeadLook packet = new S19PacketEntityHeadLook(arg0.getA(), arg0.getB());
        netHandlerPlayClient.handleEntityHeadLook(packet);
    }

    @Override
    public void a(PacketPlayOutHeldItemSlot arg0) {
        S09PacketHeldItemChange packet = new S09PacketHeldItemChange(arg0.getA());
        netHandlerPlayClient.handleHeldItemChange(packet);
    }

    @Override
    public void a(PacketPlayOutScoreboardDisplayObjective arg0) {
        S3DPacketDisplayScoreboard packet = new S3DPacketDisplayScoreboard(arg0.getA(), arg0.getB());
        netHandlerPlayClient.handleDisplayScoreboard(packet);
    }

    @Override
    public void a(PacketPlayOutEntityMetadata arg0) {
        List<net.minecraft.server.v1_8_R3.DataWatcher.WatchableObject> list = arg0.getB();
        List<DataWatcher.WatchableObject> dataWatcherList = new ArrayList<>();

        int entityId = arg0.getA();

        Entity entity = netHandlerPlayClient.getClientWorldController().getEntityByID(entityId);

        if (list != null) {
            for (net.minecraft.server.v1_8_R3.DataWatcher.WatchableObject watchableObject : list) {
                DataWatcher.WatchableObject dataWatcher = fromNMS(watchableObject, entity);

                if (dataWatcher == null)
                    continue;
                dataWatcherList.add(dataWatcher);
            }
        }

        S1CPacketEntityMetadata packet = new S1CPacketEntityMetadata(entityId, dataWatcherList);
        netHandlerPlayClient.handleEntityMetadata(packet);
    }

    @Override
    public void a(PacketPlayOutEntityVelocity arg0) {
        S12PacketEntityVelocity packet = new S12PacketEntityVelocity(arg0.getA(), arg0.getB() / 8000D,
                arg0.getC() / 8000D,
                arg0.getD() / 8000D);
        netHandlerPlayClient.handleEntityVelocity(packet);
    }

    @Override
    public void a(PacketPlayOutEntityEquipment arg0) {

        @Nullable
        net.minecraft.server.v1_8_R3.ItemStack itemNMS = arg0.getC();
        @Nullable
        ItemStack item = fromNMS(itemNMS);

        S04PacketEntityEquipment packet = new S04PacketEntityEquipment(arg0.getA(), arg0.getB(),
                item);
        netHandlerPlayClient.handleEntityEquipment(packet);
    }

    @Override
    public void a(PacketPlayOutExperience arg0) {
        S1FPacketSetExperience packet = new S1FPacketSetExperience(arg0.getA(), arg0.getB(), arg0.getC());
        netHandlerPlayClient.handleSetExperience(packet);
    }

    @Override
    public void a(PacketPlayOutUpdateHealth arg0) {
        S06PacketUpdateHealth packet = new S06PacketUpdateHealth(arg0.getA(), arg0.getB(), arg0.getC());
        netHandlerPlayClient.handleUpdateHealth(packet);
    }

    @Override
    public void a(PacketPlayOutScoreboardTeam arg0) {
        S3EPacketTeams packet = new S3EPacketTeams(arg0.getA(), arg0.getB(), arg0.getC(), arg0.getD(), arg0.getG(),
                arg0.getH(), arg0.getI());
        netHandlerPlayClient.handleTeams(packet);
    }

    @Override
    public void a(PacketPlayOutScoreboardScore arg0) {
        S3CPacketUpdateScore packet = new S3CPacketUpdateScore(arg0.getA(), arg0.getB(), arg0.getC(),
                arg0.getD().ordinal());
        netHandlerPlayClient.handleUpdateScore(packet);
    }

    @Override
    public void a(PacketPlayOutSpawnPosition arg0) {
        S05PacketSpawnPosition packet = new S05PacketSpawnPosition(arg0.getPosition().getX(),
                arg0.getPosition().getY(), arg0.getPosition().getZ());
        netHandlerPlayClient.handleSpawnPosition(packet);
    }

    @Override
    public void a(PacketPlayOutUpdateTime arg0) {
        S03PacketTimeUpdate packet = new S03PacketTimeUpdate(arg0.getA(), arg0.getB());
        netHandlerPlayClient.handleTimeUpdate(packet);
    }

    @Override
    public void a(PacketPlayOutUpdateSign arg0) {
        net.minecraft.server.v1_8_R3.IChatBaseComponent[] c = arg0.getC();
        String[] lines = new String[c.length];

        for (int i = 0; i < c.length; i++) {
            String text = IChatBaseComponent.ChatSerializer.a(c[i]);
            IChatComponent chatComponent = IChatComponent.Serializer.fromJson(text);
            lines[i] = chatComponent.getUnformattedText();
        }

        S33PacketUpdateSign packet = new S33PacketUpdateSign(arg0.getB().getX(), arg0.getB().getY(), arg0.getB().getZ(),
                lines);
        netHandlerPlayClient.handleUpdateSign(packet);
    }

    @Override
    public void a(PacketPlayOutNamedSoundEffect arg0) {
        S29PacketSoundEffect packet = new S29PacketSoundEffect(arg0.getA(), arg0.getB(), arg0.getC(), arg0.getD(),
                arg0.getE(), arg0.getF());
        netHandlerPlayClient.handleSoundEffect(packet);
    }

    @Override
    public void a(PacketPlayOutCollect arg0) {
        S0DPacketCollectItem packet = new S0DPacketCollectItem(arg0.getA(), arg0.getB());
        netHandlerPlayClient.handleCollectItem(packet);
    }

    @Override
    public void a(PacketPlayOutEntityTeleport arg0) {
        S18PacketEntityTeleport packet = new S18PacketEntityTeleport(arg0.getA(), arg0.getB(), arg0.getC(),
                arg0.getD(), arg0.getE(), arg0.getF());
        netHandlerPlayClient.handleEntityTeleport(packet);
    }

    @Override
    public void a(PacketPlayOutUpdateAttributes arg0) {
        List<net.minecraft.server.v1_8_R3.PacketPlayOutUpdateAttributes.AttributeSnapshot> list = arg0.getB();
        List<S20PacketEntityProperties.Snapshot> snapshotList = new ArrayList<>();

        for (net.minecraft.server.v1_8_R3.PacketPlayOutUpdateAttributes.AttributeSnapshot attributeSnapshot : list) {
            String attributeName = attributeSnapshot.a();
            double baseValue = attributeSnapshot.b();
            Collection<net.minecraft.server.v1_8_R3.AttributeModifier> modifiers = attributeSnapshot.c();
            Collection<AttributeModifier> modifiers1 = new ArrayList<>();

            for (net.minecraft.server.v1_8_R3.AttributeModifier attributeModifier : modifiers) {
                UUID uuid = attributeModifier.a();
                String name = attributeModifier.b();
                double amount = attributeModifier.d();
                int operation = attributeModifier.c();
                AttributeModifier modifier = new AttributeModifier(uuid, name, amount, operation);
                modifiers1.add(modifier);
            }

            S20PacketEntityProperties.Snapshot snapshot = new S20PacketEntityProperties.Snapshot(
                    attributeName, baseValue, modifiers1);
            snapshotList.add(snapshot);
        }

        S20PacketEntityProperties packet = new S20PacketEntityProperties(arg0.getA(), snapshotList);
        netHandlerPlayClient.handleEntityProperties(packet);
    }

    @Override
    public void a(PacketPlayOutEntityEffect arg0) {
        S1DPacketEntityEffect packet = new S1DPacketEntityEffect(arg0.getA(), arg0.getB(), arg0.getC(),
                (short) arg0.getD());
        netHandlerPlayClient.handleEntityEffect(packet);
    }

    @Override
    public void a(PacketPlayOutCombatEvent arg0) {
        // TODO: implement
    }

    @Override
    public void a(PacketPlayOutServerDifficulty arg0) {
        // TODO: implement
    }

    @Override
    public void a(PacketPlayOutCamera arg0) {
        // TODO: implement
    }

    @Override
    public void a(PacketPlayOutWorldBorder arg0) {
        // TODO: implement
    }

    @Override
    public void a(PacketPlayOutTitle arg0) {
        // TODO: implement
    }

    @Override
    public void a(PacketPlayOutSetCompression arg0) {
        // TODO: implement
    }

    @Override
    public void a(PacketPlayOutPlayerListHeaderFooter arg0) {
        // TODO: implement
    }

    @Override
    public void a(PacketPlayOutResourcePackSend arg0) {
        // TODO: implement
    }

    @Override
    public void a(PacketPlayOutUpdateEntityNBT arg0) {
        // TODO: implement
    }

    @Override
    public void a(PacketStatusOutServerInfo arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'a'");
    }

    @Override
    public void a(PacketStatusOutPong arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'a'");
    }

    @Override
    public void a(PacketLoginOutEncryptionBegin arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'a'");
    }

    @Override
    public void a(PacketLoginOutSuccess arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'a'");
    }

    @Override
    public void a(PacketLoginOutDisconnect arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'a'");
    }

    @Override
    public void a(PacketLoginOutSetCompression arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'a'");
    }

}
