package optifine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResUtils {
    public static String[] collectFiles(Minecraft mc, String prefix, String suffix) {
        return collectFiles(mc, new String[]{prefix}, new String[]{suffix});
    }

    public static String[] collectFiles(Minecraft mc, String[] prefixes, String[] suffixes) {
        LinkedHashSet setPaths = new LinkedHashSet();
        IResourcePack[] rps = mc.getConfig().getResourcePacks();

        for (int paths = 0; paths < rps.length; ++paths) {
            IResourcePack rp = rps[paths];
            String[] ps = collectFiles(rp, prefixes, suffixes, (String[]) null);
            setPaths.addAll(Arrays.asList(ps));
        }

        String[] var7 = (String[]) setPaths.toArray(new String[setPaths.size()]);
        return var7;
    }

    public static String[] collectFiles(IResourcePack rp, String prefix, String suffix, String[] defaultPaths) {
        return collectFiles(rp, new String[]{prefix}, new String[]{suffix}, defaultPaths);
    }

    public static String[] collectFiles(IResourcePack rp, String[] prefixes, String[] suffixes) {
        return collectFiles(rp, prefixes, suffixes, (String[]) null);
    }

    public static String[] collectFiles(IResourcePack rp, String[] prefixes, String[] suffixes, String[] defaultPaths) {
        if (rp instanceof DefaultResourcePack) {
            return collectFilesFixed(rp, defaultPaths);
        } else if (!(rp instanceof AbstractResourcePack)) {
            return new String[0];
        } else {
            AbstractResourcePack arp = (AbstractResourcePack) rp;
            File tpFile = ResourceUtils.getResourcePackFile(arp);
            return tpFile == null ? new String[0] : (tpFile.isDirectory() ? collectFilesFolder(tpFile, "", prefixes, suffixes) : (tpFile.isFile() ? collectFilesZIP(tpFile, prefixes, suffixes) : new String[0]));
        }
    }

    private static String[] collectFilesFixed(IResourcePack rp, String[] paths) {
        if (paths == null) {
            return new String[0];
        } else {
            ArrayList list = new ArrayList();

            for (int pathArr = 0; pathArr < paths.length; ++pathArr) {
                String path = paths[pathArr];
                ResourceLocation loc = new ResourceLocation(path);

                if (rp.resourceExists(loc)) {
                    list.add(path);
                }
            }

            String[] var6 = (String[]) ((String[]) list.toArray(new String[list.size()]));
            return var6;
        }
    }

    private static String[] collectFilesFolder(File tpFile, String basePath, String[] prefixes, String[] suffixes) {
        ArrayList list = new ArrayList();
        String prefixAssets = "assets/minecraft/";
        File[] files = tpFile.listFiles();

        if (files == null) {
            return new String[0];
        } else {
            for (int names = 0; names < files.length; ++names) {
                File file = files[names];
                String dirPath;

                if (file.isFile()) {
                    dirPath = basePath + file.getName();

                    if (dirPath.startsWith(prefixAssets)) {
                        dirPath = dirPath.substring(prefixAssets.length());

                        if (StrUtils.startsWith(dirPath, prefixes) && StrUtils.endsWith(dirPath, suffixes)) {
                            list.add(dirPath);
                        }
                    }
                } else if (file.isDirectory()) {
                    dirPath = basePath + file.getName() + "/";
                    String[] names1 = collectFilesFolder(file, dirPath, prefixes, suffixes);

                    list.addAll(Arrays.asList(names1));
                }
            }

            String[] var13 = (String[]) ((String[]) list.toArray(new String[list.size()]));
            return var13;
        }
    }

    private static String[] collectFilesZIP(File tpFile, String[] prefixes, String[] suffixes) {
        ArrayList list = new ArrayList();
        String prefixAssets = "assets/minecraft/";

        try {
            ZipFile e = new ZipFile(tpFile);
            Enumeration en = e.entries();

            while (en.hasMoreElements()) {
                ZipEntry names = (ZipEntry) en.nextElement();
                String name = names.getName();

                if (name.startsWith(prefixAssets)) {
                    name = name.substring(prefixAssets.length());

                    if (StrUtils.startsWith(name, prefixes) && StrUtils.endsWith(name, suffixes)) {
                        list.add(name);
                    }
                }
            }

            e.close();
            String[] names1 = (String[]) ((String[]) list.toArray(new String[list.size()]));
            return names1;
        } catch (IOException var9) {
            var9.printStackTrace();
            return new String[0];
        }
    }
}
