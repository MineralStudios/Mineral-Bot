package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import org.apache.logging.log4j.LogManager;

import java.util.Iterator;
import java.util.Map;

public enum EnumConnectionState {
    HANDSHAKING("HANDSHAKING", 0, -1, null) {
        {
            this.func_150751_a(0, C00Handshake.class);
        }
    },
    PLAY("PLAY", 1, 0, null) {
        {
            this.func_150756_b(0, S00PacketKeepAlive.class);
            this.func_150756_b(1, S01PacketJoinGame.class);
            this.func_150756_b(2, S02PacketChat.class);
            this.func_150756_b(3, S03PacketTimeUpdate.class);
            this.func_150756_b(4, S04PacketEntityEquipment.class);
            this.func_150756_b(5, S05PacketSpawnPosition.class);
            this.func_150756_b(6, S06PacketUpdateHealth.class);
            this.func_150756_b(7, S07PacketRespawn.class);
            this.func_150756_b(8, S08PacketPlayerPosLook.class);
            this.func_150756_b(9, S09PacketHeldItemChange.class);
            this.func_150756_b(10, S0APacketUseBed.class);
            this.func_150756_b(11, S0BPacketAnimation.class);
            this.func_150756_b(12, S0CPacketSpawnPlayer.class);
            this.func_150756_b(13, S0DPacketCollectItem.class);
            this.func_150756_b(14, S0EPacketSpawnObject.class);
            this.func_150756_b(15, S0FPacketSpawnMob.class);
            this.func_150756_b(16, S10PacketSpawnPainting.class);
            this.func_150756_b(17, S11PacketSpawnExperienceOrb.class);
            this.func_150756_b(18, S12PacketEntityVelocity.class);
            this.func_150756_b(19, S13PacketDestroyEntities.class);
            this.func_150756_b(20, S14PacketEntity.class);
            this.func_150756_b(21, S14PacketEntity.S15PacketEntityRelMove.class);
            this.func_150756_b(22, S14PacketEntity.S16PacketEntityLook.class);
            this.func_150756_b(23, S14PacketEntity.S17PacketEntityLookMove.class);
            this.func_150756_b(24, S18PacketEntityTeleport.class);
            this.func_150756_b(25, S19PacketEntityHeadLook.class);
            this.func_150756_b(26, S19PacketEntityStatus.class);
            this.func_150756_b(27, S1BPacketEntityAttach.class);
            this.func_150756_b(28, S1CPacketEntityMetadata.class);
            this.func_150756_b(29, S1DPacketEntityEffect.class);
            this.func_150756_b(30, S1EPacketRemoveEntityEffect.class);
            this.func_150756_b(31, S1FPacketSetExperience.class);
            this.func_150756_b(32, S20PacketEntityProperties.class);
            this.func_150756_b(33, S21PacketChunkData.class);
            this.func_150756_b(34, S22PacketMultiBlockChange.class);
            this.func_150756_b(35, S23PacketBlockChange.class);
            this.func_150756_b(36, S24PacketBlockAction.class);
            this.func_150756_b(37, S25PacketBlockBreakAnim.class);
            this.func_150756_b(38, S26PacketMapChunkBulk.class);
            this.func_150756_b(39, S27PacketExplosion.class);
            this.func_150756_b(40, S28PacketEffect.class);
            this.func_150756_b(41, S29PacketSoundEffect.class);
            this.func_150756_b(42, S2APacketParticles.class);
            this.func_150756_b(43, S2BPacketChangeGameState.class);
            this.func_150756_b(44, S2CPacketSpawnGlobalEntity.class);
            this.func_150756_b(45, S2DPacketOpenWindow.class);
            this.func_150756_b(46, S2EPacketCloseWindow.class);
            this.func_150756_b(47, S2FPacketSetSlot.class);
            this.func_150756_b(48, S30PacketWindowItems.class);
            this.func_150756_b(49, S31PacketWindowProperty.class);
            this.func_150756_b(50, S32PacketConfirmTransaction.class);
            this.func_150756_b(51, S33PacketUpdateSign.class);
            this.func_150756_b(52, S34PacketMaps.class);
            this.func_150756_b(53, S35PacketUpdateTileEntity.class);
            this.func_150756_b(54, S36PacketSignEditorOpen.class);
            this.func_150756_b(55, S37PacketStatistics.class);
            this.func_150756_b(56, S38PacketPlayerListItem.class);
            this.func_150756_b(57, S39PacketPlayerAbilities.class);
            this.func_150756_b(58, S3APacketTabComplete.class);
            this.func_150756_b(59, S3BPacketScoreboardObjective.class);
            this.func_150756_b(60, S3CPacketUpdateScore.class);
            this.func_150756_b(61, S3DPacketDisplayScoreboard.class);
            this.func_150756_b(62, S3EPacketTeams.class);
            this.func_150756_b(63, S3FPacketCustomPayload.class);
            this.func_150756_b(64, S40PacketDisconnect.class);
            this.func_150751_a(0, C00PacketKeepAlive.class);
            this.func_150751_a(1, C01PacketChatMessage.class);
            this.func_150751_a(2, C02PacketUseEntity.class);
            this.func_150751_a(3, C03PacketPlayer.class);
            this.func_150751_a(4, C03PacketPlayer.C04PacketPlayerPosition.class);
            this.func_150751_a(5, C03PacketPlayer.C05PacketPlayerLook.class);
            this.func_150751_a(6, C03PacketPlayer.C06PacketPlayerPosLook.class);
            this.func_150751_a(7, C07PacketPlayerDigging.class);
            this.func_150751_a(8, C08PacketPlayerBlockPlacement.class);
            this.func_150751_a(9, C09PacketHeldItemChange.class);
            this.func_150751_a(10, C0APacketAnimation.class);
            this.func_150751_a(11, C0BPacketEntityAction.class);
            this.func_150751_a(12, C0CPacketInput.class);
            this.func_150751_a(13, C0DPacketCloseWindow.class);
            this.func_150751_a(14, C0EPacketClickWindow.class);
            this.func_150751_a(15, C0FPacketConfirmTransaction.class);
            this.func_150751_a(16, C10PacketCreativeInventoryAction.class);
            this.func_150751_a(17, C11PacketEnchantItem.class);
            this.func_150751_a(18, C12PacketUpdateSign.class);
            this.func_150751_a(19, C13PacketPlayerAbilities.class);
            this.func_150751_a(20, C14PacketTabComplete.class);
            this.func_150751_a(21, C15PacketClientSettings.class);
            this.func_150751_a(22, C16PacketClientStatus.class);
            this.func_150751_a(23, C17PacketCustomPayload.class);
        }
    },
    STATUS("STATUS", 2, 1, null) {
        {
            this.func_150751_a(0, C00PacketServerQuery.class);
            this.func_150756_b(0, S00PacketServerInfo.class);
            this.func_150751_a(1, C01PacketPing.class);
            this.func_150756_b(1, S01PacketPong.class);
        }
    },
    LOGIN("LOGIN", 3, 2, null) {
        {
            this.func_150756_b(0, S00PacketDisconnect.class);
            this.func_150756_b(1, S01PacketEncryptionRequest.class);
            this.func_150756_b(2, S02PacketLoginSuccess.class);
            this.func_150751_a(0, C00PacketLoginStart.class);
            this.func_150751_a(1, C01PacketEncryptionResponse.class);
        }
    };

    private static final Int2ObjectOpenHashMap<EnumConnectionState> field_150764_e = new Int2ObjectOpenHashMap<>();
    private static final Map field_150761_f = Maps.newHashMap();
    private final int field_150762_g;
    private final BiMap field_150769_h;
    private final BiMap field_150770_i;

    EnumConnectionState(int p_i45152_3_) {
        this.field_150769_h = HashBiMap.create();
        this.field_150770_i = HashBiMap.create();
        this.field_150762_g = p_i45152_3_;
    }

    protected EnumConnectionState func_150751_a(int p_150751_1_, Class p_150751_2_) {
        String var3;

        if (this.field_150769_h.containsKey(Integer.valueOf(p_150751_1_))) {
            var3 = "Serverbound packet ID " + p_150751_1_ + " is already assigned to "
                    + this.field_150769_h.get(Integer.valueOf(p_150751_1_)) + "; cannot re-assign to " + p_150751_2_;
            LogManager.getLogger(EnumConnectionState.class).fatal(var3);
            throw new IllegalArgumentException(var3);
        } else if (this.field_150769_h.containsValue(p_150751_2_)) {
            var3 = "Serverbound packet " + p_150751_2_ + " is already assigned to ID "
                    + this.field_150769_h.inverse().get(p_150751_2_) + "; cannot re-assign to " + p_150751_1_;
            LogManager.getLogger(EnumConnectionState.class).fatal(var3);
            throw new IllegalArgumentException(var3);
        } else {
            this.field_150769_h.put(Integer.valueOf(p_150751_1_), p_150751_2_);
            return this;
        }
    }

    protected EnumConnectionState func_150756_b(int p_150756_1_, Class p_150756_2_) {
        String var3;

        if (this.field_150770_i.containsKey(Integer.valueOf(p_150756_1_))) {
            var3 = "Clientbound packet ID " + p_150756_1_ + " is already assigned to "
                    + this.field_150770_i.get(Integer.valueOf(p_150756_1_)) + "; cannot re-assign to " + p_150756_2_;
            LogManager.getLogger(EnumConnectionState.class).fatal(var3);
            throw new IllegalArgumentException(var3);
        } else if (this.field_150770_i.containsValue(p_150756_2_)) {
            var3 = "Clientbound packet " + p_150756_2_ + " is already assigned to ID "
                    + this.field_150770_i.inverse().get(p_150756_2_) + "; cannot re-assign to " + p_150756_1_;
            LogManager.getLogger(EnumConnectionState.class).fatal(var3);
            throw new IllegalArgumentException(var3);
        } else {
            this.field_150770_i.put(Integer.valueOf(p_150756_1_), p_150756_2_);
            return this;
        }
    }

    public BiMap func_150753_a() {
        return this.field_150769_h;
    }

    public BiMap func_150755_b() {
        return this.field_150770_i;
    }

    public BiMap func_150757_a(boolean p_150757_1_) {
        return p_150757_1_ ? this.func_150755_b() : this.func_150753_a();
    }

    public BiMap func_150754_b(boolean p_150754_1_) {
        return p_150754_1_ ? this.func_150753_a() : this.func_150755_b();
    }

    public int func_150759_c() {
        return this.field_150762_g;
    }

    public static EnumConnectionState func_150760_a(int p_150760_0_) {
        return field_150764_e.get(p_150760_0_);
    }

    public static EnumConnectionState func_150752_a(Packet p_150752_0_) {
        return (EnumConnectionState) field_150761_f.get(p_150752_0_.getClass());
    }

    EnumConnectionState(String ignore1, int ignore2, int p_i1197_3_, Object p_i1197_4_) {
        this(p_i1197_3_);
    }

    static {
        EnumConnectionState[] var0 = values();
        int var1 = var0.length;

        for (int var2 = 0; var2 < var1; ++var2) {
            EnumConnectionState var3 = var0[var2];
            field_150764_e.put(var3.func_150759_c(), var3);
            Iterator var4 = Iterables.concat(var3.func_150755_b().values(), var3.func_150753_a().values()).iterator();

            while (var4.hasNext()) {
                Class var5 = (Class) var4.next();

                if (field_150761_f.containsKey(var5) && field_150761_f.get(var5) != var3) {
                    throw new Error("Packet " + var5 + " is already assigned to protocol " + field_150761_f.get(var5)
                            + " - can't reassign to " + var3);
                }

                field_150761_f.put(var5, var3);
            }
        }
    }
}
