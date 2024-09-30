package net.minecraft.client.resources;

import java.io.IOException;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonParseException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class ResourcePackListEntryDefault extends ResourcePackListEntry {
    private static final Logger logger = LogManager.getLogger();
    private final IResourcePack field_148320_d;
    @Nullable
    private final ResourceLocation field_148321_e;

    public ResourcePackListEntryDefault(Minecraft mc, GuiScreenResourcePacks p_i45052_1_) {
        super(mc, p_i45052_1_);
        this.field_148320_d = this.mc.getResourcePackRepository().rprDefaultResourcePack;
        @Nullable
        DynamicTexture var2 = null;

        TextureUtil textureUtil = mc.textureUtil;

        if (textureUtil != null) {
            try {
                var2 = new DynamicTexture(mc, textureUtil.dataBuffer, this.field_148320_d.getPackImage());
            } catch (IOException var4) {
                var2 = textureUtil.missingTexture;
            }
        }

        TextureManager textureManager = this.mc.getTextureManager();

        this.field_148321_e = textureManager != null && var2 != null
                ? textureManager.getDynamicTextureLocation("texturepackicon",
                        var2)
                : null;
    }

    protected String func_148311_a() {
        try {
            PackMetadataSection var1 = (PackMetadataSection) this.field_148320_d
                    .getPackMetadata(this.mc.getResourcePackRepository().rprMetadataSerializer, "pack");

            if (var1 != null) {
                return var1.func_152805_a().getFormattedText();
            }
        } catch (JsonParseException var2) {
            logger.error("Couldn\'t load metadata info", var2);
        } catch (IOException var3) {
            logger.error("Couldn\'t load metadata info", var3);
        }

        return EnumChatFormatting.RED + "Missing " + "pack.mcmeta" + " :(";
    }

    protected boolean func_148309_e() {
        return false;
    }

    protected boolean func_148308_f() {
        return false;
    }

    protected boolean func_148314_g() {
        return false;
    }

    protected boolean func_148307_h() {
        return false;
    }

    protected String func_148312_b() {
        return "Default";
    }

    protected void func_148313_c() {
        TextureManager textureManager = this.mc.getTextureManager();

        if (textureManager != null)
            textureManager.bindTexture(this.field_148321_e);

    }

    protected boolean func_148310_d() {
        return false;
    }
}
