package optifine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class CustomSky {
    private static CustomSkyLayer[][] worldSkyLayers = (CustomSkyLayer[][]) null;

    public static void reset() {
        worldSkyLayers = (CustomSkyLayer[][]) null;
    }

    public static void update(Minecraft mc) {
        reset();

        if (mc.getConfig().isCustomSky()) {
            worldSkyLayers = readCustomSkies(mc);
        }
    }

    private static CustomSkyLayer[][] readCustomSkies(Minecraft mc) {
        CustomSkyLayer[][] wsls = new CustomSkyLayer[10][0];
        String prefix = "mcpatcher/sky/world";
        int lastWorldId = -1;
        int worldCount = 0;

        while (worldCount < wsls.length) {
            String wslsTrim = prefix + worldCount + "/sky";
            ArrayList i = new ArrayList();
            int sls = 1;

            while (true) {
                if (sls < 1000) {
                    label69:
                    {
                        String path = wslsTrim + sls + ".properties";

                        try {
                            ResourceLocation e = new ResourceLocation(path);
                            InputStream in = mc.getConfig().getResourceStream(e);

                            if (in == null) {
                                break label69;
                            }

                            Properties props = new Properties();
                            props.load(in);
                            in.close();
                            Config.dbg("CustomSky properties: " + path);
                            String defSource = wslsTrim + sls + ".png";
                            CustomSkyLayer sl = new CustomSkyLayer(mc, props, defSource);

                            if (sl.isValid(path)) {
                                ResourceLocation locSource = new ResourceLocation(sl.source);
                                ITextureObject tex = TextureUtils.getTexture(mc, locSource);

                                if (tex == null) {
                                    Config.log("CustomSky: Texture not found: " + locSource);
                                } else {
                                    sl.textureId = tex.getGlTextureId();
                                    i.add(sl);
                                    in.close();
                                }
                            }
                        } catch (FileNotFoundException var15) {
                            break label69;
                        } catch (IOException var16) {
                            var16.printStackTrace();
                        }

                        ++sls;
                        continue;
                    }
                }

                if (i.size() > 0) {
                    CustomSkyLayer[] var19 = (CustomSkyLayer[]) ((CustomSkyLayer[]) i
                            .toArray(new CustomSkyLayer[i.size()]));
                    wsls[worldCount] = var19;
                    lastWorldId = worldCount;
                }

                ++worldCount;
                break;
            }
        }

        if (lastWorldId < 0) {
            return (CustomSkyLayer[][]) null;
        } else {
            worldCount = lastWorldId + 1;
            CustomSkyLayer[][] var17 = new CustomSkyLayer[worldCount][0];

            System.arraycopy(wsls, 0, var17, 0, var17.length);

            return var17;
        }
    }

    public static void renderSky(Minecraft mc, World world, TextureManager re, float celestialAngle, float rainBrightness) {
        if (worldSkyLayers != null) {
            if (mc.getConfig().getGameSettings().renderDistanceChunks >= 8) {
                int dimId = world.provider.dimensionId;

                if (dimId >= 0 && dimId < worldSkyLayers.length) {
                    CustomSkyLayer[] sls = worldSkyLayers[dimId];

                    if (sls != null) {
                        long time = world.getWorldTime();
                        int timeOfDay = (int) (time % 24000L);

                        for (int i = 0; i < sls.length; ++i) {
                            CustomSkyLayer sl = sls[i];

                            if (sl.isActive(timeOfDay)) {
                                sl.render(timeOfDay, celestialAngle, rainBrightness);
                            }
                        }

                        Blender.clearBlend(rainBrightness);
                    }
                }
            }
        }
    }

    public static boolean hasSkyLayers(Minecraft mc, World world) {
        if (worldSkyLayers == null) {
            return false;
        } else if (mc.getConfig().getGameSettings().renderDistanceChunks < 8) {
            return false;
        } else {
            int dimId = world.provider.dimensionId;

            if (dimId >= 0 && dimId < worldSkyLayers.length) {
                CustomSkyLayer[] sls = worldSkyLayers[dimId];
                return sls == null ? false : sls.length > 0;
            } else {
                return false;
            }
        }
    }
}
