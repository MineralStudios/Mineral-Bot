package net.minecraft.network.login.server;

import java.io.IOException;
import java.security.PublicKey;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.CryptManager;

public class S01PacketEncryptionRequest extends Packet {
    private String field_149612_a;
    private PublicKey field_149610_b;
    private byte[] field_149611_c;

    public S01PacketEncryptionRequest() {
    }

    public S01PacketEncryptionRequest(String p_i45268_1_, PublicKey p_i45268_2_, byte[] p_i45268_3_) {
        this.field_149612_a = p_i45268_1_;
        this.field_149610_b = p_i45268_2_;
        this.field_149611_c = p_i45268_3_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.field_149612_a = p_148837_1_.readStringFromBuffer(20);
        this.field_149610_b = CryptManager.decodePublicKey(readBlob(p_148837_1_));
        this.field_149611_c = readBlob(p_148837_1_);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeStringToBuffer(this.field_149612_a);
        writeBlob(p_148840_1_, this.field_149610_b.getEncoded());
        writeBlob(p_148840_1_, this.field_149611_c);
    }

    public void processPacket(INetHandlerLoginClient p_148833_1_) {
        p_148833_1_.handleEncryptionRequest(this);
    }

    public String func_149609_c() {
        return this.field_149612_a;
    }

    public PublicKey func_149608_d() {
        return this.field_149610_b;
    }

    public byte[] func_149607_e() {
        return this.field_149611_c;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerLoginClient) p_148833_1_);
    }
}
