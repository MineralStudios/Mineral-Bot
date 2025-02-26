package optifine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NaturalTextures {
    private static NaturalProperties[] propertiesByIndex = new NaturalProperties[0];

    public static void update(Minecraft mc) {
        propertiesByIndex = new NaturalProperties[0];

        if (mc.getConfig().isNaturalTextures()) {
            String fileName = "optifine/natural.properties";

            try {
                ResourceLocation e = new ResourceLocation(fileName);

                if (!mc.getConfig().hasResource(e)) {
                    Config.dbg("NaturalTextures: configuration \"" + fileName + "\" not found");
                    propertiesByIndex = makeDefaultProperties(mc);
                    return;
                }

                InputStream in = mc.getConfig().getResourceStream(e);
                ArrayList list = new ArrayList(256);
                String configStr = Config.readInputStream(in);
                in.close();
                String[] configLines = Config.tokenize(configStr, "\n\r");
                Config.dbg("Natural Textures: Parsing configuration \"" + fileName + "\"");

                for (int i = 0; i < configLines.length; ++i) {
                    String line = configLines[i].trim();

                    if (!line.startsWith("#")) {
                        String[] strs = Config.tokenize(line, "=");

                        if (strs.length != 2) {
                            Config.warn("Natural Textures: Invalid \"" + fileName + "\" line: " + line);
                        } else {
                            String key = strs[0].trim();
                            String type = strs[1].trim();
                            TextureAtlasSprite ts = TextureMap.textureMapBlocks.getIconSafe(key);

                            if (ts == null) {
                                Config.warn("Natural Textures: Texture not found: \"" + fileName + "\" line: " + line);
                            } else {
                                int tileNum = ts.getIndexInMap();

                                if (tileNum < 0) {
                                    Config.warn("Natural Textures: Invalid \"" + fileName + "\" line: " + line);
                                } else {
                                    NaturalProperties props = new NaturalProperties(type);

                                    if (props.isValid()) {
                                        while (list.size() <= tileNum) {
                                            list.add((Object) null);
                                        }

                                        list.set(tileNum, props);
                                        Config.dbg("NaturalTextures: " + key + " = " + type);
                                    }
                                }
                            }
                        }
                    }
                }

                propertiesByIndex = (NaturalProperties[]) ((NaturalProperties[]) list.toArray(new NaturalProperties[list.size()]));
            } catch (FileNotFoundException var15) {
                Config.warn("NaturalTextures: configuration \"" + fileName + "\" not found");
                propertiesByIndex = makeDefaultProperties(mc);
                return;
            } catch (Exception var16) {
                var16.printStackTrace();
            }
        }
    }

    public static NaturalProperties getNaturalProperties(IIcon icon) {
        if (!(icon instanceof TextureAtlasSprite)) {
            return null;
        } else {
            TextureAtlasSprite ts = (TextureAtlasSprite) icon;
            int tileNum = ts.getIndexInMap();

            if (tileNum >= 0 && tileNum < propertiesByIndex.length) {
                NaturalProperties props = propertiesByIndex[tileNum];
                return props;
            } else {
                return null;
            }
        }
    }

    private static NaturalProperties[] makeDefaultProperties(Minecraft mc) {
        Config.dbg("NaturalTextures: Checking default configuration.");
        ArrayList propsList = new ArrayList();
        setIconProperties(mc, propsList, "grass_top", "4F");
        setIconProperties(mc, propsList, "stone", "2F");
        setIconProperties(mc, propsList, "dirt", "4F");
        setIconProperties(mc, propsList, "grass_side", "F");
        setIconProperties(mc, propsList, "grass_side_overlay", "F");
        setIconProperties(mc, propsList, "stone_slab_top", "F");
        setIconProperties(mc, propsList, "bedrock", "2F");
        setIconProperties(mc, propsList, "sand", "4F");
        setIconProperties(mc, propsList, "gravel", "2");
        setIconProperties(mc, propsList, "log_oak", "2F");
        setIconProperties(mc, propsList, "log_oak_top", "4F");
        setIconProperties(mc, propsList, "gold_ore", "2F");
        setIconProperties(mc, propsList, "iron_ore", "2F");
        setIconProperties(mc, propsList, "coal_ore", "2F");
        setIconProperties(mc, propsList, "diamond_ore", "2F");
        setIconProperties(mc, propsList, "redstone_ore", "2F");
        setIconProperties(mc, propsList, "lapis_ore", "2F");
        setIconProperties(mc, propsList, "obsidian", "4F");
        setIconProperties(mc, propsList, "leaves_oak", "2F");
        setIconProperties(mc, propsList, "leaves_oak_opaque", "2F");
        setIconProperties(mc, propsList, "leaves_jungle", "2");
        setIconProperties(mc, propsList, "leaves_jungle_opaque", "2");
        setIconProperties(mc, propsList, "snow", "4F");
        setIconProperties(mc, propsList, "grass_side_snowed", "F");
        setIconProperties(mc, propsList, "cactus_side", "2F");
        setIconProperties(mc, propsList, "clay", "4F");
        setIconProperties(mc, propsList, "mycelium_side", "F");
        setIconProperties(mc, propsList, "mycelium_top", "4F");
        setIconProperties(mc, propsList, "farmland_wet", "2F");
        setIconProperties(mc, propsList, "farmland_dry", "2F");
        setIconProperties(mc, propsList, "netherrack", "4F");
        setIconProperties(mc, propsList, "soul_sand", "4F");
        setIconProperties(mc, propsList, "glowstone", "4");
        setIconProperties(mc, propsList, "log_spruce", "2F");
        setIconProperties(mc, propsList, "log_birch", "F");
        setIconProperties(mc, propsList, "leaves_spruce", "2F");
        setIconProperties(mc, propsList, "leaves_spruce_opaque", "2F");
        setIconProperties(mc, propsList, "log_jungle", "2F");
        setIconProperties(mc, propsList, "end_stone", "4");
        setIconProperties(mc, propsList, "sandstone_top", "4");
        setIconProperties(mc, propsList, "sandstone_bottom", "4F");
        setIconProperties(mc, propsList, "redstone_lamp_on", "4F");
        NaturalProperties[] terrainProps = (NaturalProperties[]) ((NaturalProperties[]) propsList.toArray(new NaturalProperties[propsList.size()]));
        return terrainProps;
    }

    private static void setIconProperties(Minecraft mc, List propsList, String iconName, String propStr) {
        TextureMap terrainMap = TextureMap.textureMapBlocks;
        TextureAtlasSprite icon = terrainMap.getIconSafe(iconName);

        if (icon == null) {
            Config.warn("*** NaturalProperties: Icon not found: " + iconName + " ***");
        } else if (!(icon instanceof TextureAtlasSprite)) {
            Config.warn("*** NaturalProperties: Icon is not IconStitched: " + iconName + ": " + icon.getClass().getName() + " ***");
        } else {
            TextureAtlasSprite ts = (TextureAtlasSprite) icon;
            int index = ts.getIndexInMap();

            if (index < 0) {
                Config.warn("*** NaturalProperties: Invalid index for icon: " + iconName + ": " + index + " ***");
            } else if (mc.getConfig().isFromDefaultResourcePack(new ResourceLocation("textures/blocks/" + iconName + ".png"))) {
                while (index >= propsList.size()) {
                    propsList.add((Object) null);
                }

                NaturalProperties props = new NaturalProperties(propStr);
                propsList.set(index, props);
                Config.dbg("NaturalTextures: " + iconName + " = " + propStr);
            }
        }
    }
}
