package net.minecraft.client.main;

import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.bot.base.client.instance.ClientInstance;
import gg.mineral.bot.base.client.manager.InstanceManager;
import gg.mineral.bot.base.client.tick.GameLoop;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import lombok.val;
import net.minecraft.client.Minecraft;
import org.eclipse.jdt.annotation.NonNull;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Main {
    private static final java.lang.reflect.Type field_152370_a = new ParameterizedType() {

        public java.lang.reflect.Type @NonNull [] getActualTypeArguments() {
            return new java.lang.reflect.Type[]{String.class, new ParameterizedType() {

                public java.lang.reflect.Type @NonNull [] getActualTypeArguments() {
                    return new java.lang.reflect.Type[]{String.class};
                }

                public java.lang.reflect.@NonNull Type getRawType() {
                    return Collection.class;
                }

                public java.lang.reflect.Type getOwnerType() {
                    return null;
                }
            }
            };
        }

        public java.lang.reflect.Type getRawType() {
            return Map.class;
        }

        public java.lang.reflect.Type getOwnerType() {
            return null;
        }
    };

    public static void main(String[] p_main_0_) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        OptionParser var1 = new OptionParser();
        var1.allowsUnrecognizedOptions();
        var1.accepts("demo");
        var1.accepts("fullscreen");
        ArgumentAcceptingOptionSpec<String> var2 = var1.accepts("server").withRequiredArg();
        ArgumentAcceptingOptionSpec<Integer> portOption = var1.accepts("port").withRequiredArg().ofType(Integer.class)
                .defaultsTo(Integer.valueOf(25565), new Integer[0]);
        ArgumentAcceptingOptionSpec<File> gameDirOption = var1.accepts("gameDir").withRequiredArg().ofType(File.class)
                .defaultsTo(new File("."), new File[0]);
        ArgumentAcceptingOptionSpec<File> var5 = var1.accepts("assetsDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<File> var6 = var1.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<String> var7 = var1.accepts("proxyHost").withRequiredArg();
        ArgumentAcceptingOptionSpec<Integer> var8 = var1.accepts("proxyPort").withRequiredArg()
                .defaultsTo("8080", new String[0])
                .ofType(Integer.class);
        ArgumentAcceptingOptionSpec<String> var9 = var1.accepts("proxyUser").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> var10 = var1.accepts("proxyPass").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> var11 = var1.accepts("username").withRequiredArg()
                .defaultsTo("Player" + Minecraft.getSystemTime() % 1000L, new String[0]);
        ArgumentAcceptingOptionSpec<String> var12 = var1.accepts("uuid").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> var13 = var1.accepts("accessToken").withRequiredArg().required();
        ArgumentAcceptingOptionSpec<String> var14 = var1.accepts("version").withRequiredArg().required();
        ArgumentAcceptingOptionSpec<Integer> widthOption = var1.accepts("width").withRequiredArg().ofType(Integer.class)
                .defaultsTo(Integer.valueOf(854), new Integer[0]);
        ArgumentAcceptingOptionSpec<Integer> heightOption = var1.accepts("height").withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(Integer.valueOf(480), new Integer[0]);
        ArgumentAcceptingOptionSpec<String> var17 = var1.accepts("userProperties").withRequiredArg().required();
        ArgumentAcceptingOptionSpec<String> var18 = var1.accepts("assetIndex").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> var19 = var1.accepts("userType").withRequiredArg().defaultsTo("legacy",
                new String[0]);
        NonOptionArgumentSpec<String> var20 = var1.nonOptions();
        OptionSet var21 = var1.parse(p_main_0_);
        List<String> var22 = var21.valuesOf(var20);
        String var23 = (String) var21.valueOf(var7);
        Proxy proxy = Proxy.NO_PROXY;

        if (var23 != null) {
            try {
                proxy = new Proxy(Type.SOCKS, new InetSocketAddress(var23, ((Integer) var21.valueOf(var8)).intValue()));
            } catch (Exception var41) {
                ;
            }
        }

        final String var25 = (String) var21.valueOf(var9);
        final String var26 = (String) var21.valueOf(var10);

        if (!proxy.equals(Proxy.NO_PROXY) && func_110121_a(var25) && func_110121_a(var26)) {
            Authenticator.setDefault(new Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(var25, var26.toCharArray());
                }
            });
        }

        int width = ((Integer) var21.valueOf(widthOption)).intValue();
        int height = ((Integer) var21.valueOf(heightOption)).intValue();
        boolean fullscreen = var21.has("fullscreen");
        boolean demo = var21.has("demo");
        String version = (String) var21.valueOf(var14);
        HashMultimap userProperties = HashMultimap.create();
        Iterator var33 = ((Map) (new Gson()).fromJson((String) var21.valueOf(var17), field_152370_a)).entrySet()
                .iterator();

        while (var33.hasNext()) {
            Entry var34 = (Entry) var33.next();
            userProperties.putAll(var34.getKey(), (Iterable) var34.getValue());
        }

        File gameDir = (File) var21.valueOf(gameDirOption);
        File assetsDir = var21.has(var5) ? (File) var21.valueOf(var5) : new File(gameDir, "assets/");
        File resourcePackDir = var21.has(var6) ? (File) var21.valueOf(var6) : new File(gameDir, "resourcepacks/");
        String var36 = var21.has(var12) ? (String) var12.value(var21) : (String) var11.value(var21);
        String assetIndex = var21.has(var18) ? (String) var18.value(var21) : null;
        val configuration = BotConfiguration.builder().username((String) var11.value(var21)).build();
        val minecraftInstance = new ClientInstance(
                configuration, width, height,
                fullscreen, demo, gameDir,
                assetsDir,
                resourcePackDir,
                proxy,
                version, userProperties,
                assetIndex);
        val serverAddress = (String) var21.valueOf(var2);

        if (serverAddress != null)
            minecraftInstance.setServer(serverAddress, ((Integer) var21.valueOf(portOption)).intValue());

        val uuid = configuration.getUuid();

        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {

            @Override
            public void run() {
                InstanceManager.getInstances().remove(uuid);
                minecraftInstance.stopIntegratedServer();
            }
        });

        if (!var22.isEmpty())
            System.out.println("Completely ignored arguments: " + var22);

        Thread.currentThread().setName("Client thread");
        InstanceManager.getPendingInstances().put(uuid, minecraftInstance);
        minecraftInstance.run();
        InstanceManager.getPendingInstances().remove(uuid);
        InstanceManager.getInstances().put(uuid, minecraftInstance);
        GameLoop.start();
    }

    private static boolean func_110121_a(String p_110121_0_) {
        return p_110121_0_ != null && !p_110121_0_.isEmpty();
    }
}
