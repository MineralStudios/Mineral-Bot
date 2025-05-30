package net.minecraft.network.login.client;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;

import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.util.CryptManager;

@NoArgsConstructor
public class C01PacketEncryptionResponse extends Packet {
    private byte[] field_149302_a = new byte[0], field_149301_b = new byte[0];

    public C01PacketEncryptionResponse(SecretKey p_i45271_1_, PublicKey p_i45271_2_, byte[] p_i45271_3_) {
        this.field_149302_a = CryptManager.encryptData(p_i45271_2_, p_i45271_1_.getEncoded());
        this.field_149301_b = CryptManager.encryptData(p_i45271_2_, p_i45271_3_);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.field_149302_a = readBlob(p_148837_1_);
        this.field_149301_b = readBlob(p_148837_1_);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        writeBlob(p_148840_1_, this.field_149302_a);
        writeBlob(p_148840_1_, this.field_149301_b);
    }

    public void processPacket(INetHandlerLoginServer p_148833_1_) {
        p_148833_1_.processEncryptionResponse(this);
    }

    public SecretKey func_149300_a(PrivateKey p_149300_1_) {
        return CryptManager.decryptSharedKey(p_149300_1_, this.field_149302_a);
    }

    public byte[] func_149299_b(PrivateKey p_149299_1_) {
        return p_149299_1_ == null ? this.field_149301_b : CryptManager.decryptData(p_149299_1_, this.field_149301_b);
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerLoginServer) p_148833_1_);
    }
}
