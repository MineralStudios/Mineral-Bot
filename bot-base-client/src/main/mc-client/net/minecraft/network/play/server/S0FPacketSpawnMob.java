package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

@NoArgsConstructor
@AllArgsConstructor
public class S0FPacketSpawnMob extends Packet {
    private int field_149042_a;
    private int entityTypeId;
    private int field_149041_c;
    private int field_149038_d;
    private int field_149039_e;
    private int field_149036_f;
    private int field_149037_g;
    private int field_149047_h;
    private byte field_149048_i;
    private byte field_149045_j;
    private byte field_149046_k;
    private DataWatcher dataWatcher;
    private List field_149044_m;

    public S0FPacketSpawnMob(EntityLivingBase p_i45192_1_) {
        this.field_149042_a = p_i45192_1_.getEntityId();
        this.entityTypeId = (byte) EntityList.getEntityID(p_i45192_1_);
        this.field_149041_c = p_i45192_1_.myEntitySize.multiplyBy32AndRound(p_i45192_1_.posX);
        this.field_149038_d = MathHelper.floor_double(p_i45192_1_.posY * 32.0D);
        this.field_149039_e = p_i45192_1_.myEntitySize.multiplyBy32AndRound(p_i45192_1_.posZ);
        this.field_149048_i = (byte) ((int) (p_i45192_1_.rotationYaw * 256.0F / 360.0F));
        this.field_149045_j = (byte) ((int) (p_i45192_1_.rotationPitch * 256.0F / 360.0F));
        this.field_149046_k = (byte) ((int) (p_i45192_1_.rotationYawHead * 256.0F / 360.0F));
        double var2 = 3.9D;
        double var4 = p_i45192_1_.motionX;
        double var6 = p_i45192_1_.motionY;
        double var8 = p_i45192_1_.motionZ;

        if (var4 < -var2)
            var4 = -var2;

        if (var6 < -var2)
            var6 = -var2;

        if (var8 < -var2)
            var8 = -var2;

        if (var4 > var2)
            var4 = var2;

        if (var6 > var2)
            var6 = var2;

        if (var8 > var2)
            var8 = var2;

        this.field_149036_f = (int) (var4 * 8000.0D);
        this.field_149037_g = (int) (var6 * 8000.0D);
        this.field_149047_h = (int) (var8 * 8000.0D);
        this.dataWatcher = p_i45192_1_.getDataWatcher();
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.field_149042_a = p_148837_1_.readVarIntFromBuffer();
        this.entityTypeId = p_148837_1_.readByte() & 255;
        this.field_149041_c = p_148837_1_.readInt();
        this.field_149038_d = p_148837_1_.readInt();
        this.field_149039_e = p_148837_1_.readInt();
        this.field_149048_i = p_148837_1_.readByte();
        this.field_149045_j = p_148837_1_.readByte();
        this.field_149046_k = p_148837_1_.readByte();
        this.field_149036_f = p_148837_1_.readShort();
        this.field_149037_g = p_148837_1_.readShort();
        this.field_149047_h = p_148837_1_.readShort();
        this.field_149044_m = DataWatcher.readWatchedListFromPacketBuffer(p_148837_1_);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeVarIntToBuffer(this.field_149042_a);
        p_148840_1_.writeByte(this.entityTypeId & 255);
        p_148840_1_.writeInt(this.field_149041_c);
        p_148840_1_.writeInt(this.field_149038_d);
        p_148840_1_.writeInt(this.field_149039_e);
        p_148840_1_.writeByte(this.field_149048_i);
        p_148840_1_.writeByte(this.field_149045_j);
        p_148840_1_.writeByte(this.field_149046_k);
        p_148840_1_.writeShort(this.field_149036_f);
        p_148840_1_.writeShort(this.field_149037_g);
        p_148840_1_.writeShort(this.field_149047_h);
        this.dataWatcher.func_151509_a(p_148840_1_);
    }

    public void processPacket(INetHandlerPlayClient p_148833_1_) {
        p_148833_1_.handleSpawnMob(this);
    }

    public List func_149027_c() {
        if (this.field_149044_m == null) {
            this.field_149044_m = this.dataWatcher.getAllWatched();
        }

        return this.field_149044_m;
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by
     * Minecraft for logging purposes.
     */
    public String serialize() {
        return String.format("id=%d, type=%d, x=%.2f, y=%.2f, z=%.2f, xd=%.2f, yd=%.2f, zd=%.2f",
                new Object[] { Integer.valueOf(this.field_149042_a), Integer.valueOf(this.entityTypeId),
                        Float.valueOf((float) this.field_149041_c / 32.0F),
                        Float.valueOf((float) this.field_149038_d / 32.0F),
                        Float.valueOf((float) this.field_149039_e / 32.0F),
                        Float.valueOf((float) this.field_149036_f / 8000.0F),
                        Float.valueOf((float) this.field_149037_g / 8000.0F),
                        Float.valueOf((float) this.field_149047_h / 8000.0F) });
    }

    public int func_149024_d() {
        return this.field_149042_a;
    }

    public int getEntityTypeId() {
        return this.entityTypeId;
    }

    public int func_149023_f() {
        return this.field_149041_c;
    }

    public int func_149034_g() {
        return this.field_149038_d;
    }

    public int func_149029_h() {
        return this.field_149039_e;
    }

    public int func_149026_i() {
        return this.field_149036_f;
    }

    public int func_149033_j() {
        return this.field_149037_g;
    }

    public int func_149031_k() {
        return this.field_149047_h;
    }

    public byte func_149028_l() {
        return this.field_149048_i;
    }

    public byte func_149030_m() {
        return this.field_149045_j;
    }

    public byte func_149032_n() {
        return this.field_149046_k;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
