package net.minecraft.network.play.client;

import java.io.IOException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

@NoArgsConstructor
@Setter
@Getter
public class C03PacketPlayer extends Packet {
    protected double x, y, z, headY;
    protected float yaw, pitch;
    protected boolean onGround, hasPos, hasLook;

    public C03PacketPlayer(boolean p_i45256_1_) {
        this.onGround = p_i45256_1_;
    }

    public void processPacket(INetHandlerPlayServer p_148833_1_) {
        p_148833_1_.processPlayer(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.onGround = p_148837_1_.readUnsignedByte() != 0;
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeByte(this.onGround ? 1 : 0);
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayServer) p_148833_1_);
    }

    public static class C04PacketPlayerPosition extends C03PacketPlayer {

        public C04PacketPlayerPosition() {
            this.hasPos = true;
        }

        public C04PacketPlayerPosition(double p_i45253_1_, double p_i45253_3_, double p_i45253_5_, double p_i45253_7_,
                boolean p_i45253_9_) {
            this.x = p_i45253_1_;
            this.y = p_i45253_3_;
            this.headY = p_i45253_5_;
            this.z = p_i45253_7_;
            this.onGround = p_i45253_9_;
            this.hasPos = true;
        }

        @Override
        public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
            this.x = p_148837_1_.readDouble();
            this.y = p_148837_1_.readDouble();
            this.headY = p_148837_1_.readDouble();
            this.z = p_148837_1_.readDouble();
            super.readPacketData(p_148837_1_, mc);
        }

        public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
            p_148840_1_.writeDouble(this.x);
            p_148840_1_.writeDouble(this.y);
            p_148840_1_.writeDouble(this.headY);
            p_148840_1_.writeDouble(this.z);
            super.writePacketData(p_148840_1_);
        }

        public void processPacket(INetHandler p_148833_1_) {
            super.processPacket((INetHandlerPlayServer) p_148833_1_);
        }
    }

    public static class C05PacketPlayerLook extends C03PacketPlayer {

        public C05PacketPlayerLook() {
            this.hasLook = true;
        }

        public C05PacketPlayerLook(float p_i45255_1_, float p_i45255_2_, boolean p_i45255_3_) {
            this.yaw = p_i45255_1_;
            this.pitch = p_i45255_2_;
            this.onGround = p_i45255_3_;
            this.hasLook = true;
        }

        @Override
        public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
            this.yaw = p_148837_1_.readFloat();
            this.pitch = p_148837_1_.readFloat();
            super.readPacketData(p_148837_1_, mc);
        }

        @Override
        public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
            p_148840_1_.writeFloat(this.yaw);
            p_148840_1_.writeFloat(this.pitch);
            super.writePacketData(p_148840_1_);
        }

        public void processPacket(INetHandler p_148833_1_) {
            super.processPacket((INetHandlerPlayServer) p_148833_1_);
        }
    }

    public static class C06PacketPlayerPosLook extends C03PacketPlayer {

        public C06PacketPlayerPosLook() {
            this.hasPos = true;
            this.hasLook = true;
        }

        public C06PacketPlayerPosLook(double p_i45254_1_, double p_i45254_3_, double p_i45254_5_, double p_i45254_7_,
                float p_i45254_9_, float p_i45254_10_, boolean p_i45254_11_) {
            this.x = p_i45254_1_;
            this.y = p_i45254_3_;
            this.headY = p_i45254_5_;
            this.z = p_i45254_7_;
            this.yaw = p_i45254_9_;
            this.pitch = p_i45254_10_;
            this.onGround = p_i45254_11_;
            this.hasLook = true;
            this.hasPos = true;
        }

        @Override
        public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
            this.x = p_148837_1_.readDouble();
            this.y = p_148837_1_.readDouble();
            this.headY = p_148837_1_.readDouble();
            this.z = p_148837_1_.readDouble();
            this.yaw = p_148837_1_.readFloat();
            this.pitch = p_148837_1_.readFloat();
            super.readPacketData(p_148837_1_, mc);
        }

        @Override
        public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
            p_148840_1_.writeDouble(this.x);
            p_148840_1_.writeDouble(this.y);
            p_148840_1_.writeDouble(this.headY);
            p_148840_1_.writeDouble(this.z);
            p_148840_1_.writeFloat(this.yaw);
            p_148840_1_.writeFloat(this.pitch);
            super.writePacketData(p_148840_1_);
        }

        public void processPacket(INetHandler p_148833_1_) {
            super.processPacket((INetHandlerPlayServer) p_148833_1_);
        }
    }
}
