package net.minecraft.network.play.server;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

@NoArgsConstructor
@AllArgsConstructor
public class S0EPacketSpawnObject extends Packet {
    private int entityId;
    private int x;
    private int y;
    private int z;
    private int motX;
    private int motY;
    private int motZ;
    private int pitch;
    private int yaw;
    private int field_149019_j;
    private int field_149020_k;

    public S0EPacketSpawnObject(Entity p_i45165_1_, int p_i45165_2_) {
        this(p_i45165_1_, p_i45165_2_, 0);
    }

    public S0EPacketSpawnObject(Entity p_i45166_1_, int p_i45166_2_, int p_i45166_3_) {
        this.entityId = p_i45166_1_.getEntityId();
        this.x = MathHelper.floor_double(p_i45166_1_.posX * 32.0D);
        this.y = MathHelper.floor_double(p_i45166_1_.posY * 32.0D);
        this.z = MathHelper.floor_double(p_i45166_1_.posZ * 32.0D);
        this.pitch = MathHelper.floor_float(p_i45166_1_.rotationPitch * 256.0F / 360.0F);
        this.yaw = MathHelper.floor_float(p_i45166_1_.rotationYaw * 256.0F / 360.0F);
        this.field_149019_j = p_i45166_2_;
        this.field_149020_k = p_i45166_3_;

        if (p_i45166_3_ > 0) {
            double var4 = p_i45166_1_.motionX;
            double var6 = p_i45166_1_.motionY;
            double var8 = p_i45166_1_.motionZ;
            double var10 = 3.9D;

            if (var4 < -var10)
                var4 = -var10;

            if (var6 < -var10)
                var6 = -var10;

            if (var8 < -var10)
                var8 = -var10;

            if (var4 > var10)
                var4 = var10;

            if (var6 > var10)
                var6 = var10;

            if (var8 > var10)
                var8 = var10;

            this.motX = (int) (var4 * 8000.0D);
            this.motY = (int) (var6 * 8000.0D);
            this.motZ = (int) (var8 * 8000.0D);
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.entityId = p_148837_1_.readVarIntFromBuffer();
        this.field_149019_j = p_148837_1_.readByte();
        this.x = p_148837_1_.readInt();
        this.y = p_148837_1_.readInt();
        this.z = p_148837_1_.readInt();
        this.pitch = p_148837_1_.readByte();
        this.yaw = p_148837_1_.readByte();
        this.field_149020_k = p_148837_1_.readInt();

        if (this.field_149020_k > 0) {
            this.motX = p_148837_1_.readShort();
            this.motY = p_148837_1_.readShort();
            this.motZ = p_148837_1_.readShort();
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeVarIntToBuffer(this.entityId);
        p_148840_1_.writeByte(this.field_149019_j);
        p_148840_1_.writeInt(this.x);
        p_148840_1_.writeInt(this.y);
        p_148840_1_.writeInt(this.z);
        p_148840_1_.writeByte(this.pitch);
        p_148840_1_.writeByte(this.yaw);
        p_148840_1_.writeInt(this.field_149020_k);

        if (this.field_149020_k > 0) {
            p_148840_1_.writeShort(this.motX);
            p_148840_1_.writeShort(this.motY);
            p_148840_1_.writeShort(this.motZ);
        }
    }

    public void processPacket(INetHandlerPlayClient p_148833_1_) {
        p_148833_1_.handleSpawnObject(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by
     * Minecraft for logging purposes.
     */
    public String serialize() {
        return String.format("id=%d, type=%d, x=%.2f, y=%.2f, z=%.2f",
                new Object[] { Integer.valueOf(this.entityId), Integer.valueOf(this.field_149019_j),
                        Float.valueOf((float) this.x / 32.0F), Float.valueOf((float) this.y / 32.0F),
                        Float.valueOf((float) this.z / 32.0F) });
    }

    public int func_149001_c() {
        return this.entityId;
    }

    public int func_148997_d() {
        return this.x;
    }

    public int func_148998_e() {
        return this.y;
    }

    public int func_148994_f() {
        return this.z;
    }

    public int func_149010_g() {
        return this.motX;
    }

    public int func_149004_h() {
        return this.motY;
    }

    public int func_148999_i() {
        return this.motZ;
    }

    public int func_149008_j() {
        return this.pitch;
    }

    public int func_149006_k() {
        return this.yaw;
    }

    public int func_148993_l() {
        return this.field_149019_j;
    }

    public int func_149009_m() {
        return this.field_149020_k;
    }

    public void func_148996_a(int p_148996_1_) {
        this.x = p_148996_1_;
    }

    public void func_148995_b(int p_148995_1_) {
        this.y = p_148995_1_;
    }

    public void func_149005_c(int p_149005_1_) {
        this.z = p_149005_1_;
    }

    public void func_149003_d(int p_149003_1_) {
        this.motX = p_149003_1_;
    }

    public void func_149000_e(int p_149000_1_) {
        this.motY = p_149000_1_;
    }

    public void func_149007_f(int p_149007_1_) {
        this.motZ = p_149007_1_;
    }

    public void func_149002_g(int p_149002_1_) {
        this.field_149020_k = p_149002_1_;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
