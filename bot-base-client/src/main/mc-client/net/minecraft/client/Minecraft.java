package net.minecraft.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.event.EventHandler;
import gg.mineral.bot.base.client.gui.GuiConnecting;
import gg.mineral.bot.base.client.manager.InstanceManager;
import gg.mineral.bot.base.lwjgl.Sys;
import gg.mineral.bot.base.lwjgl.input.Keyboard;
import gg.mineral.bot.base.lwjgl.input.Mouse;
import gg.mineral.bot.base.lwjgl.opengl.Display;
import gg.mineral.bot.base.lwjgl.opengl.GL11;
import gg.mineral.bot.impl.config.BotGlobalConfig;
import gg.mineral.bot.impl.thread.ThreadManager;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntityRendererChestHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.data.*;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.Timer;
import net.minecraft.util.*;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import optifine.Config;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.NonNull;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class Minecraft {
    public static final Logger logger = LogManager.getLogger(Minecraft.class);
    private static final ResourceLocation locationMojangPng = new ResourceLocation("textures/gui/title/mojang.png");
    public static final boolean isRunningOnMac = Util.getOSType() == Util.EnumOS.OSX;

    /**
     * A 10MiB preallocation to ensure the heap is reasonably sized.
     */
    public static byte[] memoryReserve = BotGlobalConfig.optimizedGameLoop ? new byte[0] : new byte[10485760];
    private static final List<DisplayMode> macDisplayModes = Lists
            .newArrayList(new DisplayMode[]{new DisplayMode(2560, 1600), new DisplayMode(2880, 1800)});
    private final File fileResourcepacks;
    private final Multimap field_152356_J;
    private ServerData currentServerData;
    @Getter
    private Config config = new Config();

    /**
     * The RenderEngine instance used by Minecraft
     */
    @Nullable
    private TextureManager renderEngine;

    public PlayerControllerMP playerController;
    private boolean fullscreen;
    public boolean hasCrashed;

    /**
     * Instance of CrashReport.
     */
    public CrashReport crashReporter;
    public int displayWidth, displayHeight;
    private Timer timer = new Timer(20.0F);

    @Nullable
    public WorldClient theWorld;
    @Nullable
    public RenderGlobal renderGlobal;
    @Nullable
    public EntityClientPlayerMP thePlayer;
    @Nullable
    public final RenderManager renderManager;
    @Nullable
    public TextureUtil textureUtil;

    /**
     * The Entity from which the renderer determines the render viewpoint. Currently
     * is always the parent Minecraft
     * class's 'thePlayer' instance. Modification of its location, rotation, or
     * other settings at render time will
     * modify the camera likewise, with the caveat of triggering chunk rebuilds as
     * it moves, making it unsuitable for
     * changing the viewpoint mid-render.
     */
    public EntityLivingBase renderViewEntity;
    public Entity pointedEntity;
    @Nullable
    public EffectRenderer effectRenderer;
    @Getter
    private final Session session;
    private boolean isGamePaused;

    /**
     * The font renderer used for displaying and measuring text.
     */
    @Nullable
    public FontRenderer fontRenderer;
    @Nullable
    public FontRenderer standardGalacticFontRenderer;

    /**
     * The GuiScreen that's being displayed at the moment.
     */
    @Nullable
    public GuiScreen currentScreen;
    public LoadingScreenRenderer loadingScreen;
    public EntityRenderer entityRenderer;

    /**
     * Mouse left click counter
     */
    private int leftClickCounter;
    @Getter
    @Setter
    @Nullable
    private Tessellator tessellator;

    /**
     * Display width
     */
    private int tempDisplayWidth;

    /**
     * Display height
     */
    private int tempDisplayHeight;

    /**
     * Instance of IntegratedServer.
     */
    private IntegratedServer theIntegratedServer;

    /**
     * Gui achievement
     */
    @Nullable
    public GuiAchievement guiAchievement;
    public GuiIngame ingameGUI;

    /**
     * Skip render world
     */
    public boolean skipRenderWorld;

    /**
     * The ray trace hit that the mouse is over.
     */
    public MovingObjectPosition objectMouseOver;

    /**
     * The game settings that currently hold effect.
     */
    public GameSettings gameSettings;

    /**
     * Mouse helper instance.
     */
    public MouseHelper mouseHelper;
    public final File mcDataDir;
    private final File fileAssets;
    private final String launchedVersion;
    @Getter
    private final Proxy proxy;
    @Getter
    private ISaveFormat saveLoader;

    /**
     * This is set to fpsCounter every debug screen update, and is shown on the
     * debug screen. It's also sent as part of
     * the usage snooping.
     */
    private int debugFPS;

    /**
     * When you place a block, it's set to 6, decremented once per tick, when it's
     * 0, you can place another block.
     */
    private int rightClickDelayTimer;

    /**
     * Checked in Minecraft's while(running) loop, if true it's set to false and the
     * textures refreshed.
     */
    private boolean refreshTexturePacksScheduled;
    private String serverName;
    private int serverPort;

    /**
     * Does the actual gameplay have focus. If so then mouse and keys will effect
     * the player instead of menus.
     */
    public boolean inGameHasFocus;
    long systemTime = getSystemTime();

    /**
     * Join player counter
     */
    private int joinPlayerCounter;
    private final static boolean jvm64bit;
    private final boolean isDemo;
    private NetworkManager myNetworkManager;
    private boolean integratedServerIsRunning;

    /**
     * The profiler instance
     */
    public final Profiler mcProfiler = new Profiler(this);
    private long field_83002_am = -1L;
    private IReloadableResourceManager mcResourceManager;
    private final IMetadataSerializer metadataSerializer_ = new IMetadataSerializer();
    private List<IResourcePack> defaultResourcePacks = Lists.newArrayList();
    @Nullable
    private DefaultResourcePack mcDefaultResourcePack;
    private ResourcePackRepository mcResourcePackRepository;
    private LanguageManager mcLanguageManager;
    private Framebuffer mcFramebuffer;
    @Nullable
    private TextureMap textureMapBlocks;
    @Nullable
    protected SoundHandler mcSoundHandler;
    @Nullable
    private MusicTicker mcMusicTicker;
    @Nullable
    private ResourceLocation minecraftLogoTexture;
    @Nullable
    @Getter
    private final MinecraftSessionService authenticationService;
    @Getter
    @Nullable
    private SkinManager skinManager;
    private final Queue<FutureTask> futureTaskQueue = Queues.newArrayDeque();
    protected Thread mainThread = Thread.currentThread();

    /**
     * Set to true to keep the game loop running. Set to false by shutdown() to
     * allow the game loop to exit cleanly.
     */
    public volatile boolean running = true;

    /**
     * String that shows the debug information
     */
    public String debug = "";

    /**
     * Approximate time (in ms) of last update to debug string
     */
    long debugUpdateTime = getSystemTime();

    /**
     * holds the current fps
     */
    int fpsCounter;
    long prevFrameTime = -1L;

    /**
     * Profiler currently displayed in the debug screen pie chart
     */
    private String debugProfilerName = "root";
    @Nullable
    public final TileEntityRendererDispatcher tileEntityRendererDispatcher;
    @Nullable
    public final TileEntityRendererChestHelper tileEntityRendererChestHelper;

    @Getter
    protected final Mouse mouse;
    @Getter
    protected final Keyboard keyboard;

    @Getter
    private final List<KeyBinding> keybindArray = new ArrayList<>();
    @Getter
    private final Int2ObjectOpenHashMap<KeyBinding> keyBindHash = new Int2ObjectOpenHashMap<>();

    @Getter
    @Setter
    private byte[] readCompressedDataBuffer = new byte[196864];
    @Getter
    @Setter
    private byte[] readBuffer = new byte[0];

    static {
        jvm64bit = isJvm64bit();
        if (!BotGlobalConfig.optimizedGameLoop)
            startTimerHackThread();
        Bootstrap.func_151354_b();
    }

    public static void init() {

    }

    public Minecraft(Session p_i1103_1_, int p_i1103_2_, int p_i1103_3_, boolean p_i1103_4_, boolean p_i1103_5_,
                     File p_i1103_6_, File p_i1103_7_, File p_i1103_8_, Proxy p_i1103_9_, String p_i1103_10_,
                     Multimap p_i1103_11_, String p_i1103_12_) {
        if (this instanceof EventHandler eventHandler) {
            this.keyboard = new Keyboard(eventHandler);
            this.mouse = new Mouse(eventHandler);
        } else {
            this.keyboard = new Keyboard(new EventHandler() {
                @Override
                public <T extends Event> boolean callEvent(@NonNull T event) {
                    return false;
                }
            });

            this.mouse = new Mouse(new EventHandler() {
                @Override
                public <T extends Event> boolean callEvent(@NonNull T event) {
                    return false;
                }
            });
        }

        this.mcDataDir = p_i1103_6_;
        this.fileAssets = p_i1103_7_;
        this.renderManager = !BotGlobalConfig.optimizedGameLoop ? new RenderManager(this) : null;

        this.tileEntityRendererDispatcher = !BotGlobalConfig.optimizedGameLoop
                ? new TileEntityRendererDispatcher(this)
                : null;
        this.tileEntityRendererChestHelper = !BotGlobalConfig.optimizedGameLoop
                ? new TileEntityRendererChestHelper(this)
                : null;

        this.fileResourcepacks = p_i1103_8_;
        this.launchedVersion = p_i1103_10_;
        this.field_152356_J = p_i1103_11_;
        if (!BotGlobalConfig.optimizedGameLoop)
            this.mcDefaultResourcePack = new DefaultResourcePack(
                    BotGlobalConfig.optimizedGameLoop ? new Object2ObjectOpenHashMap<String, File>()
                            : (new ResourceIndex(p_i1103_7_, p_i1103_12_)).func_152782_a());
        this.addDefaultResourcePack();
        this.proxy = p_i1103_9_ == null ? Proxy.NO_PROXY : p_i1103_9_;
        this.authenticationService = BotGlobalConfig.disableConnection ? null
                : (new YggdrasilAuthenticationService(p_i1103_9_, UUID.randomUUID().toString()))
                .createMinecraftSessionService();
        this.session = p_i1103_1_;
        logger.debug("Setting user: " + p_i1103_1_.getUsername());
        logger.debug("(Session ID is " + p_i1103_1_.getSessionID() + ")");
        this.isDemo = p_i1103_5_;
        this.displayWidth = p_i1103_2_;
        this.displayHeight = p_i1103_3_;
        this.tempDisplayWidth = p_i1103_2_;
        this.tempDisplayHeight = p_i1103_3_;
        this.fullscreen = p_i1103_4_;
        if (!BotGlobalConfig.optimizedGameLoop) {
            ImageIO.setUseCache(false);
            this.tessellator = new Tessellator(524288);
        }
    }

    private static boolean isJvm64bit() {
        String[] var0 = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
        String[] var1 = var0;
        int var2 = var0.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            String var4 = var1[var3];
            String var5 = System.getProperty(var4);

            if (var5 != null && var5.contains("64"))
                return true;
        }

        return false;
    }

    public Framebuffer getFramebuffer() {
        return this.mcFramebuffer;
    }

    private static void startTimerHackThread() {
        val t = new Thread("Timer hack thread") {

            public void run() {
                while (InstanceManager.isRunning()) {
                    try {
                        Thread.sleep(2147483647L);
                    } catch (InterruptedException var2) {
                        ;
                    }
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }

    public void crashed(CrashReport p_71404_1_) {
        this.hasCrashed = true;
        this.crashReporter = p_71404_1_;
    }

    /**
     * Wrapper around displayCrashReportInternal
     */
    public void displayCrashReport(CrashReport crashReport) {
        val crashReportsDir = new File(this.mcDataDir, "crash-reports");
        val crashFile = new File(crashReportsDir,
                "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-client.txt");
        System.out.println(crashReport.getCompleteReport(this));

        if (crashReport.getFile() != null) {
            System.out.println("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReport.getFile());
            // System.exit(-1);
            this.shutdown();
        } else if (crashReport.saveToFile(this, crashFile)) {
            System.out.println("#@!@# Game crashed! Crash report saved to: #@!@# " + crashFile.getAbsolutePath());
            // System.exit(-1);
            this.shutdown();
        } else {
            System.out.println("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            // System.exit(-2);
            this.shutdown();
        }
    }

    public void setServer(String name, int port) {
        this.serverName = name;
        this.serverPort = port;
    }

    /**
     * Starts the game: initializes the canvas, the title, the settings, etcetera.
     */
    private void startGame() throws LWJGLException {
        this.gameSettings = new GameSettings(this, this.mcDataDir);
        if (!BotGlobalConfig.optimizedGameLoop)
            this.textureUtil = new TextureUtil(this);

        if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0) {
            this.displayWidth = this.gameSettings.overrideWidth;
            this.displayHeight = this.gameSettings.overrideHeight;
        }

        if (this.fullscreen) {
            Display.setFullscreen(true);
            this.displayWidth = Display.getDisplayMode().getWidth();
            this.displayHeight = Display.getDisplayMode().getHeight();

            if (this.displayWidth <= 0)
                this.displayWidth = 1;

            if (this.displayHeight <= 0)
                this.displayHeight = 1;

        } else
            Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));

        Display.setResizable(true);
        Display.setTitle("Mineral Bot Client 1.7.10");

        logger.debug("LWJGL Version: " + Sys.getVersion());

        val osType = Util.getOSType();

        val mcDefaultResourcePack = this.mcDefaultResourcePack;
        if (osType != Util.EnumOS.OSX && mcDefaultResourcePack != null) {
            try {
                val icon16x = mcDefaultResourcePack
                        .func_152780_c(new ResourceLocation("icons/icon_16x16.png"));
                val icon32x = mcDefaultResourcePack
                        .func_152780_c(new ResourceLocation("icons/icon_32x32.png"));

                if (icon16x != null && icon32x != null)
                    Display.setIcon(new ByteBuffer[]{this.func_152340_a(icon16x), this.func_152340_a(icon32x)});

            } catch (IOException var8) {
                logger.error("Couldn\'t set icon", var8);
            }
        }

        try {
            Display.create((new PixelFormat()).withDepthBits(24));
        } catch (LWJGLException var7) {
            logger.error("Couldn\'t set pixel format", var7);

            if (!BotGlobalConfig.optimizedGameLoop) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException var6) {
                    ;
                }
            }

            if (this.fullscreen)
                this.updateDisplayMode();

            Display.create();
        }

        OpenGlHelper.initializeTextures(this);

        this.mcFramebuffer = new Framebuffer(this, this.displayWidth, this.displayHeight, true);
        this.mcFramebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        if (!BotGlobalConfig.optimizedGameLoop)
            this.guiAchievement = new GuiAchievement(this);
        this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(),
                TextureMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(),
                FontMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(),
                AnimationMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(),
                PackMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(),
                LanguageMetadataSection.class);
        this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
        this.mcResourcePackRepository = new ResourcePackRepository(this, this.fileResourcepacks,
                new File(this.mcDataDir, "server-resource-packs"), this.mcDefaultResourcePack, this.metadataSerializer_,
                this.gameSettings);
        this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
        this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
        if (!BotGlobalConfig.optimizedGameLoop)
            this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
        this.refreshResources();
        if (!BotGlobalConfig.optimizedGameLoop) {
            this.renderEngine = new TextureManager(this, this.mcResourceManager);
            this.mcResourceManager.registerReloadListener(this.renderEngine);
            this.skinManager = new SkinManager(this, this.renderEngine, new File(this.fileAssets, "skins"),
                    this.authenticationService);
        }
        this.loadScreen();
        if (!BotGlobalConfig.optimizedGameLoop)
            this.mcSoundHandler = new SoundHandler(this, this.mcResourceManager, this.gameSettings);
        if (!BotGlobalConfig.headless && !BotGlobalConfig.optimizedGameLoop)
            this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
        if (!BotGlobalConfig.optimizedGameLoop) {
            this.mcMusicTicker = new MusicTicker(this);

            this.fontRenderer = new FontRenderer(this, this.gameSettings,
                    new ResourceLocation("textures/font/ascii.png"),
                    this.renderEngine, false);

            if (this.gameSettings.language != null) {
                if (this.fontRenderer != null)
                    this.fontRenderer.setUnicodeFlag(this.func_152349_b());
                if (this.fontRenderer != null)
                    this.fontRenderer.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
            }

            this.standardGalacticFontRenderer = new FontRenderer(this, this.gameSettings,
                    new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);

            if (this.fontRenderer != null)
                this.mcResourceManager.registerReloadListener(this.fontRenderer);
            this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
            this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
            this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
        }
        val renderManager = this.renderManager;

        if (renderManager != null)
            renderManager.itemRenderer = new ItemRenderer(this);
        this.entityRenderer = new EntityRenderer(this, this.mcResourceManager);
        if (!BotGlobalConfig.optimizedGameLoop)
            this.mcResourceManager.registerReloadListener(this.entityRenderer);
        AchievementList.openInventory.setStatStringFormatter(new IStatStringFormat() {

            public String formatString(String p_74535_1_) {
                try {
                    return String.format(p_74535_1_, new Object[]{GameSettings
                            .getKeyDisplayString(Minecraft.this,
                            Minecraft.this.gameSettings.keyBindInventory.getKeyCode())});
                } catch (Exception var3) {
                    return "Error: " + var3.getLocalizedMessage();
                }
            }
        });
        this.mouseHelper = new MouseHelper(this);
        this.checkGLError("Pre startup");
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glClearDepth(1.0D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        this.checkGLError("Startup");
        if (!BotGlobalConfig.optimizedGameLoop) {
            this.renderGlobal = new RenderGlobal(this);

            this.textureMapBlocks = new TextureMap(this, 0, "textures/blocks");

            if (this.textureMapBlocks != null)
                this.textureMapBlocks.func_147632_b(this.gameSettings.anisotropicFiltering);
            if (this.textureMapBlocks != null)
                this.textureMapBlocks.func_147633_a(this.gameSettings.mipmapLevels);

            val renderEngine = this.renderEngine;
            if (renderEngine != null) {
                renderEngine.loadTextureMap(TextureMap.locationBlocksTexture, this.textureMapBlocks);
                renderEngine.loadTextureMap(TextureMap.locationItemsTexture,
                        new TextureMap(this, 1, "textures/items"));
            }
        }
        GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
        if (!BotGlobalConfig.optimizedGameLoop)
            this.effectRenderer = new EffectRenderer(this, this.theWorld, this.renderEngine);
        this.checkGLError("Post startup");
        this.ingameGUI = new GuiIngame(this);

        if (this.serverName != null)
            this.displayGuiScreen(new GuiConnecting(new GuiMainMenu(this), this, this.serverName, this.serverPort));
        else
            this.displayGuiScreen(new GuiMainMenu(this));

        if (this.renderEngine != null)
            this.renderEngine.func_147645_c(this.minecraftLogoTexture);
        this.minecraftLogoTexture = null;
        this.loadingScreen = new LoadingScreenRenderer(this);

        if (this.gameSettings.fullScreen && !this.fullscreen)
            this.toggleFullscreen();

        try {
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
        } catch (OpenGLException var4) {
            this.gameSettings.enableVsync = false;
            this.gameSettings.saveOptions();
        }

        logger.debug("Game has been started successfully!");
    }

    public boolean func_152349_b() {
        return this.mcLanguageManager.isCurrentLocaleUnicode() || this.gameSettings.forceUnicodeFont;
    }

    public void refreshResources() {
        if (BotGlobalConfig.optimizedGameLoop)
            return;
        val var1 = Lists.newArrayList(this.defaultResourcePacks);

        for (val entry : this.mcResourcePackRepository.getRepositoryEntries())
            var1.add(entry.getResourcePack());

        if (this.mcResourcePackRepository.func_148530_e() != null)
            var1.add(this.mcResourcePackRepository.func_148530_e());

        try {
            this.mcResourceManager.reloadResources(var1);
        } catch (RuntimeException var4) {
            logger.info("Caught error stitching, removing all assigned resourcepacks", var4);
            var1.clear();
            var1.addAll(this.defaultResourcePacks);
            this.mcResourcePackRepository.func_148527_a(Collections.emptyList());
            this.mcResourceManager.reloadResources(var1);
            this.gameSettings.resourcePacks.clear();
            this.gameSettings.saveOptions();
        }

        this.mcLanguageManager.parseLanguageMetadata(var1);

        if (this.renderGlobal != null)
            this.renderGlobal.loadRenderers();

    }

    private void addDefaultResourcePack() {
        if (mcDefaultResourcePack == null)
            return;
        this.defaultResourcePacks.add(this.mcDefaultResourcePack);
    }

    private ByteBuffer func_152340_a(InputStream inputStream) throws IOException {
        val bufferedImage = ImageIO.read(inputStream);
        val rgb = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0,
                bufferedImage.getWidth());
        val byteBuf = ByteBuffer.allocate(4 * rgb.length);
        val rgbLen = rgb.length;

        for (int i = 0; i < rgbLen; ++i) {
            val value = rgb[i];
            byteBuf.putInt(value << 8 | value >> 24 & 255);
        }

        byteBuf.flip();
        return byteBuf;
    }

    private void updateDisplayMode() throws LWJGLException {
        val displayModes = new ObjectOpenHashSet<DisplayMode>();
        Collections.addAll(displayModes, Display.getAvailableDisplayModes());
        var selectedMode = Display.getDesktopDisplayMode();

        if (!displayModes.contains(selectedMode) && Util.getOSType() == Util.EnumOS.OSX) {
            val macDisplayIter = macDisplayModes.iterator();

            while (macDisplayIter.hasNext()) {
                val displayMode = macDisplayIter.next();
                boolean var5 = true;
                var displayIter = displayModes.iterator();
                DisplayMode displayMode1;

                while (displayIter.hasNext()) {
                    displayMode1 = displayIter.next();

                    if (displayMode1.getBitsPerPixel() == 32 && displayMode1.getWidth() == displayMode.getWidth()
                            && displayMode1.getHeight() == displayMode.getHeight()) {
                        var5 = false;
                        break;
                    }
                }

                if (!var5) {
                    displayIter = displayModes.iterator();

                    while (displayIter.hasNext()) {
                        displayMode1 = displayIter.next();

                        if (displayMode1.getBitsPerPixel() == 32
                                && displayMode1.getWidth() == displayMode.getWidth() / 2
                                && displayMode1.getHeight() == displayMode.getHeight() / 2) {
                            selectedMode = displayMode1;
                            break;
                        }
                    }
                }
            }
        }

        Display.setDisplayMode(selectedMode);
        this.displayWidth = selectedMode.getWidth();
        this.displayHeight = selectedMode.getHeight();
    }

    /**
     * Displays a new screen.
     */
    private void loadScreen() throws LWJGLException {
        val scaledRes = new ScaledResolution(this, this.displayWidth, this.displayHeight);
        val scaleFactor = scaledRes.getScaleFactor();
        val framebuffer = new Framebuffer(this, scaledRes.getScaledWidth() * scaleFactor,
                scaledRes.getScaledHeight() * scaleFactor,
                true);
        framebuffer.bindFramebuffer(false);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, (double) scaledRes.getScaledWidth(), (double) scaledRes.getScaledHeight(), 0.0D, 1000.0D,
                3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        val renderEngine = this.renderEngine;
        val textureUtil = this.textureUtil;

        if (!BotGlobalConfig.optimizedGameLoop && renderEngine != null && textureUtil != null
                && this.mcDefaultResourcePack != null) {
            try {
                this.minecraftLogoTexture = renderEngine.getDynamicTextureLocation("logo",
                        new DynamicTexture(this, textureUtil.dataBuffer,
                                ImageIO.read(this.mcDefaultResourcePack.getInputStream(locationMojangPng))));
                renderEngine.bindTexture(this.minecraftLogoTexture);
            } catch (IOException var7) {
                logger.error("Unable to load logo: " + locationMojangPng, var7);
            }
        }

        val tessellator = this.getTessellator();
        if (tessellator != null) {
            tessellator.startDrawingQuads();
            tessellator.setColorOpaque_I(16777215);
            tessellator.addVertexWithUV(0.0D, (double) this.displayHeight, 0.0D, 0.0D, 0.0D);
            tessellator.addVertexWithUV((double) this.displayWidth, (double) this.displayHeight, 0.0D, 0.0D, 0.0D);
            tessellator.addVertexWithUV((double) this.displayWidth, 0.0D, 0.0D, 0.0D, 0.0D);
            tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
            tessellator.draw();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            tessellator.setColorOpaque_I(16777215);
        }
        short width = 256, height = 256;
        this.scaledTessellator((scaledRes.getScaledWidth() - width) / 2, (scaledRes.getScaledHeight() - height) / 2, 0,
                0,
                width,
                height);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(scaledRes.getScaledWidth() * scaleFactor,
                scaledRes.getScaledHeight() * scaleFactor);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glFlush();

        this.func_147120_f();
    }

    /**
     * Loads Tessellator with a scaled resolution
     */
    public void scaledTessellator(int p_71392_1_, int p_71392_2_, int p_71392_3_, int p_71392_4_, int p_71392_5_,
                                  int p_71392_6_) {
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        val tessellator = this.getTessellator();

        if (tessellator == null)
            return;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) (p_71392_1_ + 0), (double) (p_71392_2_ + p_71392_6_), 0.0D,
                (double) ((float) (p_71392_3_ + 0) * var7), (double) ((float) (p_71392_4_ + p_71392_6_) * var8));
        tessellator.addVertexWithUV((double) (p_71392_1_ + p_71392_5_), (double) (p_71392_2_ + p_71392_6_), 0.0D,
                (double) ((float) (p_71392_3_ + p_71392_5_) * var7),
                (double) ((float) (p_71392_4_ + p_71392_6_) * var8));
        tessellator.addVertexWithUV((double) (p_71392_1_ + p_71392_5_), (double) (p_71392_2_ + 0), 0.0D,
                (double) ((float) (p_71392_3_ + p_71392_5_) * var7), (double) ((float) (p_71392_4_ + 0) * var8));
        tessellator.addVertexWithUV((double) (p_71392_1_ + 0), (double) (p_71392_2_ + 0), 0.0D,
                (double) ((float) (p_71392_3_ + 0) * var7), (double) ((float) (p_71392_4_ + 0) * var8));
        tessellator.draw();
    }

    /**
     * Sets the argument GuiScreen as the main (topmost visible) screen.
     */
    public void displayGuiScreen(GuiScreen p_147108_1_) {
        if (this.currentScreen != null)
            this.currentScreen.onGuiClosed();

        if (p_147108_1_ instanceof gg.mineral.bot.base.client.gui.GuiConnecting connecting)
            connecting.initConnectingGui();

        EntityClientPlayerMP thePlayer = this.thePlayer;

        if (p_147108_1_ == null && this.theWorld == null)
            p_147108_1_ = new GuiMainMenu(this);
        else if (p_147108_1_ == null && thePlayer != null && thePlayer.getHealth() <= 0.0F)
            p_147108_1_ = new GuiGameOver(this);

        if (p_147108_1_ instanceof GuiMainMenu) {
            this.gameSettings.showDebugInfo = false;
            this.ingameGUI.getChatGUI().func_146231_a();
        }

        this.currentScreen = (GuiScreen) p_147108_1_;

        if (p_147108_1_ != null) {
            this.setIngameNotInFocus();
            ScaledResolution var2 = new ScaledResolution(this, this.displayWidth, this.displayHeight);
            int var3 = var2.getScaledWidth();
            int var4 = var2.getScaledHeight();
            ((GuiScreen) p_147108_1_).setWorldAndResolution(this, var3, var4);
            this.skipRenderWorld = false;
        } else {
            if (this.mcSoundHandler != null)
                this.mcSoundHandler.func_147687_e();

            this.setIngameFocus();
        }
    }

    /**
     * Checks for an OpenGL error. If there is one, prints the error ID and error
     * string.
     */
    private void checkGLError(String p_71361_1_) {
        val error = GL11.glGetError();

        if (error != 0) {
            String var3 = GLU.gluErrorString(error);
            logger.error("########## GL ERROR ##########");
            logger.error("@ " + p_71361_1_);
            logger.error(error + ": " + var3);
        }
    }

    /**
     * Shuts down the minecraft applet by stopping the resource downloads, and
     * clearing up GL stuff; called when the
     * application (or web page) is exited.
     */
    public void shutdownMinecraftApplet() {
        ThreadManager.shutdown();
        try {
            logger.debug("Stopping!");

            try {
                this.loadWorld((WorldClient) null);
            } catch (Throwable var7) {
                ;
            }

            try {
                GLAllocation.deleteTexturesAndDisplayLists();
            } catch (Throwable var6) {
                ;
            }

            if (this.mcSoundHandler != null)
                this.mcSoundHandler.func_147685_d();
        } finally {
            Display.destroy();

            if (!this.hasCrashed)
                System.exit(0);
        }

        if (BotGlobalConfig.manualGarbageCollection)
            System.gc();
    }

    public void run() {
        this.running = true;
        CrashReport var2;

        try {
            this.startGame();
        } catch (Throwable var11) {
            var2 = CrashReport.makeCrashReport(var11, "Initializing game");
            var2.makeCategory("Initialization");
            this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(var2));
            return;
        }

        /*
         * ThreadManager.getGameLoopExecutor().scheduleWithFixedDelay(() -> {
         * if (!this.running) {
         * ThreadManager.shutdown();
         * return;
         * }
         *
         * try {
         * if (!this.hasCrashed || this.crashReporter == null) {
         * try {
         * this.runGameLoop();
         * } catch (OutOfMemoryError var10) {
         * this.freeMemory();
         * this.displayGuiScreen(new GuiMemoryErrorScreen(this));
         * System.gc();
         * }
         * } else {
         * this.displayCrashReport(this.crashReporter);
         * this.running = false;
         * }
         * } catch (MinecraftError var12) {
         * // Handle Minecraft-specific errors
         * } catch (ReportedException var13) {
         * this.addGraphicsAndWorldToCrashReport(var13.getCrashReport());
         * this.freeMemory();
         * logger.fatal("Reported exception thrown!", var13);
         * this.displayCrashReport(var13.getCrashReport());
         * } catch (Throwable var14) {
         * CrashReport headlessCrashReport = this.addGraphicsAndWorldToCrashReport(
         * new CrashReport("Unexpected error", var14));
         * this.freeMemory();
         * logger.fatal("Unreported exception thrown!", var14);
         * this.displayCrashReport(headlessCrashReport);
         * } finally {
         * if (!this.running) {
         * this.shutdownMinecraftApplet();
         * }
         * }
         * }, 0, BotGlobalConfig.getGameLoopDelay(), TimeUnit.MILLISECONDS);
         */

    }

    /**
     * Called repeatedly from run()
     */
    public void runGameLoop() {
        this.mcProfiler.startSection("root");

        if (Display.isCreated() && Display.isCloseRequested())
            this.shutdown();

        if (this.isGamePaused && this.theWorld != null) {
            float var1 = this.timer.renderPartialTicks;
            this.timer.updateTimer();
            this.timer.renderPartialTicks = var1;
        } else {
            this.timer.updateTimer();
        }

        if ((this.theWorld == null || this.currentScreen == null) && this.refreshTexturePacksScheduled) {
            this.refreshTexturePacksScheduled = false;
            this.refreshResources();
        }

        long var5 = System.nanoTime();
        this.mcProfiler.startSection("tick");

        for (int var3 = 0; var3 < this.timer.elapsedTicks; ++var3)
            this.runTick();

        this.mcProfiler.endStartSection("preRenderErrors");
        long var6 = System.nanoTime() - var5;
        this.checkGLError("Pre render");
        RenderBlocks.fancyGrass = this.gameSettings.fancyGraphics;
        this.mcProfiler.endStartSection("sound");
        if (this.mcSoundHandler != null)
            this.mcSoundHandler.func_147691_a(this.thePlayer, this.timer.renderPartialTicks);
        this.mcProfiler.endSection();
        this.mcProfiler.startSection("render");
        GL11.glPushMatrix();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        this.mcFramebuffer.bindFramebuffer(true);
        this.mcProfiler.startSection("display");
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        if (!BotGlobalConfig.optimizedGameLoop && this.thePlayer != null
                && this.thePlayer.isEntityInsideOpaqueBlock())
            this.gameSettings.thirdPersonView = 0;

        this.mcProfiler.endSection();

        if (!this.skipRenderWorld) {
            this.mcProfiler.endStartSection("gameRenderer");
            this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks);
            this.mcProfiler.endSection();
        }

        GL11.glFlush();

        this.mcProfiler.endSection();

        if (!Display.isActive() && this.fullscreen)
            this.toggleFullscreen();

        if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart) {
            if (!this.mcProfiler.profilingEnabled)
                this.mcProfiler.clearProfiling();

            this.mcProfiler.profilingEnabled = true;
            this.displayDebugInfo(var6);
        } else {
            this.mcProfiler.profilingEnabled = false;
            this.prevFrameTime = System.nanoTime();
        }

        if (this.guiAchievement != null)
            this.guiAchievement.func_146254_a();
        this.mcFramebuffer.unbindFramebuffer();
        GL11.glPopMatrix();
        GL11.glPushMatrix();

        this.mcFramebuffer.framebufferRender(this.displayWidth, this.displayHeight);

        GL11.glPopMatrix();
        GL11.glPushMatrix();

        this.entityRenderer.func_152430_c(this.timer.renderPartialTicks);

        GL11.glPopMatrix();

        this.mcProfiler.startSection("root");
        this.func_147120_f();
        if (!BotGlobalConfig.optimizedGameLoop)
            Thread.yield();
        this.mcProfiler.startSection("stream");
        this.mcProfiler.startSection("update");
        this.mcProfiler.endStartSection("submit");
        this.mcProfiler.endSection();
        this.mcProfiler.endSection();
        this.checkGLError("Post render");
        ++this.fpsCounter;
        this.isGamePaused = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame()
                && !this.theIntegratedServer.getPublic();

        while (getSystemTime() >= this.debugUpdateTime + 1000L) {
            debugFPS = this.fpsCounter;
            this.debug = debugFPS + " fps, " + WorldRenderer.chunksUpdated + " chunk updates";
            WorldRenderer.chunksUpdated = 0;
            this.debugUpdateTime += 1000L;
            this.fpsCounter = 0;
        }

        this.mcProfiler.endSection();

        if (this.isFramerateLimitBelowMax())
            Display.sync(this.getLimitFramerate());
    }

    public void func_147120_f() {
        Display.update();

        if (!this.fullscreen && Display.wasResized()) {
            int var1 = this.displayWidth;
            int var2 = this.displayHeight;
            this.displayWidth = Display.getWidth();
            this.displayHeight = Display.getHeight();

            if (this.displayWidth != var1 || this.displayHeight != var2) {
                if (this.displayWidth <= 0)
                    this.displayWidth = 1;

                if (this.displayHeight <= 0)
                    this.displayHeight = 1;

                this.resize(this.displayWidth, this.displayHeight);
            }
        }
    }

    public int getLimitFramerate() {
        return this.theWorld == null && this.currentScreen != null ? 30 : this.gameSettings.limitFramerate;
    }

    public boolean isFramerateLimitBelowMax() {
        return (float) this.getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
    }

    public void freeMemory() {
        try {
            memoryReserve = new byte[0];
            val renderGlobal = this.renderGlobal;

            if (renderGlobal != null)
                renderGlobal.deleteAllDisplayLists();

        } catch (Throwable var4) {
            ;
        }

        try {
            if (BotGlobalConfig.manualGarbageCollection)
                System.gc();
        } catch (Throwable var3) {
            ;
        }

        try {
            if (BotGlobalConfig.manualGarbageCollection)
                System.gc();
            this.loadWorld((WorldClient) null);
        } catch (Throwable var2) {
            ;
        }

        if (BotGlobalConfig.manualGarbageCollection)
            System.gc();
    }

    /**
     * Update debugProfilerName in response to number keys in debug screen
     */
    private void updateDebugProfilerName(int p_71383_1_) {
        val profilerData = this.mcProfiler.getProfilingData(this.debugProfilerName);

        if (profilerData != null && !profilerData.isEmpty()) {
            val result = profilerData.remove(0);

            if (p_71383_1_ == 0) {
                if (result.field_76331_c.length() > 0) {
                    val var4 = this.debugProfilerName.lastIndexOf(".");

                    if (var4 >= 0)
                        this.debugProfilerName = this.debugProfilerName.substring(0, var4);

                }
            } else {
                --p_71383_1_;

                if (p_71383_1_ < profilerData.size()
                        && !profilerData.get(p_71383_1_).field_76331_c.equals("unspecified")) {
                    if (this.debugProfilerName.length() > 0)
                        this.debugProfilerName = this.debugProfilerName + ".";

                    this.debugProfilerName = this.debugProfilerName
                            + ((Profiler.Result) profilerData.get(p_71383_1_)).field_76331_c;
                }
            }
        }
    }

    private void displayDebugInfo(long p_71366_1_) {
        if (this.mcProfiler.profilingEnabled) {
            val profilerData = this.mcProfiler.getProfilingData(this.debugProfilerName);
            @Nullable
            Profiler.Result result = profilerData == null ? null : profilerData.remove(0);
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0D, (double) this.displayWidth, (double) this.displayHeight, 0.0D, 1000.0D, 3000.0D);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
            GL11.glLineWidth(1.0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            Tessellator var5 = this.getTessellator();
            short var6 = 160;
            int var7 = this.displayWidth - var6 - 10;
            int var8 = this.displayHeight - var6 * 2;
            GL11.glEnable(GL11.GL_BLEND);
            if (var5 != null) {
                var5.startDrawingQuads();
                var5.setColorRGBA_I(0, 200);
                var5.addVertex((double) ((float) var7 - (float) var6 * 1.1F),
                        (double) ((float) var8 - (float) var6 * 0.6F - 16.0F), 0.0D);
                var5.addVertex((double) ((float) var7 - (float) var6 * 1.1F), (double) (var8 + var6 * 2), 0.0D);
                var5.addVertex((double) ((float) var7 + (float) var6 * 1.1F), (double) (var8 + var6 * 2), 0.0D);
                var5.addVertex((double) ((float) var7 + (float) var6 * 1.1F),
                        (double) ((float) var8 - (float) var6 * 0.6F - 16.0F), 0.0D);
                var5.draw();
            }
            GL11.glDisable(GL11.GL_BLEND);
            double var9 = 0.0D;
            int var13;

            if (profilerData != null)
                for (int var11 = 0; var11 < profilerData.size(); ++var11) {
                    Profiler.Result var12 = (Profiler.Result) profilerData.get(var11);
                    var13 = MathHelper.floor_double(var12.field_76332_a / 4.0D) + 1;
                    if (var5 != null) {
                        var5.startDrawing(6);
                        var5.setColorOpaque_I(var12.func_76329_a());
                        var5.addVertex((double) var7, (double) var8, 0.0D);
                    }
                    int var14;
                    float var15;
                    float var16;
                    float var17;

                    for (var14 = var13; var14 >= 0; --var14) {
                        var15 = (float) ((var9 + var12.field_76332_a * (double) var14 / (double) var13) * Math.PI * 2.0D
                                / 100.0D);
                        var16 = MathHelper.sin(var15) * (float) var6;
                        var17 = MathHelper.cos(var15) * (float) var6 * 0.5F;
                        if (var5 != null)
                            var5.addVertex((double) ((float) var7 + var16), (double) ((float) var8 - var17), 0.0D);
                    }

                    if (var5 != null) {
                        var5.draw();
                        var5.startDrawing(5);
                        var5.setColorOpaque_I((var12.func_76329_a() & 16711422) >> 1);
                    }

                    for (var14 = var13; var14 >= 0; --var14) {
                        var15 = (float) ((var9 + var12.field_76332_a * (double) var14 / (double) var13) * Math.PI * 2.0D
                                / 100.0D);
                        var16 = MathHelper.sin(var15) * (float) var6;
                        var17 = MathHelper.cos(var15) * (float) var6 * 0.5F;
                        if (var5 != null) {
                            var5.addVertex((double) ((float) var7 + var16), (double) ((float) var8 - var17), 0.0D);
                            var5.addVertex((double) ((float) var7 + var16), (double) ((float) var8 - var17 + 10.0F),
                                    0.0D);
                        }
                    }

                    if (var5 != null)
                        var5.draw();
                    var9 += var12.field_76332_a;
                }

            val df = new DecimalFormat("##0.00");
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            String var19 = "";

            if (result != null) {
                if (!result.field_76331_c.equals("unspecified"))
                    var19 = var19 + "[0] ";

                if (result.field_76331_c.length() == 0)
                    var19 = var19 + "ROOT ";
                else
                    var19 = var19 + result.field_76331_c + " ";
            }

            var13 = 16777215;
            val fontRenderer = this.fontRenderer;
            if (fontRenderer != null) {
                fontRenderer.drawStringWithShadow(var19, var7 - var6, var8 - var6 / 2 - 16, var13);
                if (result != null)
                    fontRenderer.drawStringWithShadow(var19 = df.format(result.percentage) + "%",
                            var7 + var6 - fontRenderer.getStringWidth(var19), var8 - var6 / 2 - 16, var13);

                if (profilerData != null)
                    for (int var20 = 0; var20 < profilerData.size(); ++var20) {
                        Profiler.Result var21 = (Profiler.Result) profilerData.get(var20);
                        String var22 = "";

                        if (var21.field_76331_c.equals("unspecified"))
                            var22 = var22 + "[?] ";
                        else
                            var22 = var22 + "[" + (var20 + 1) + "] ";

                        var22 = var22 + var21.field_76331_c;

                        fontRenderer.drawStringWithShadow(var22, var7 - var6, var8 + var6 / 2 + var20 * 8 + 20,
                                var21.func_76329_a());
                        fontRenderer.drawStringWithShadow(var22 = df.format(var21.field_76332_a) + "%",
                                var7 + var6 - 50 - fontRenderer.getStringWidth(var22),
                                var8 + var6 / 2 + var20 * 8 + 20,
                                var21.func_76329_a());
                        fontRenderer.drawStringWithShadow(var22 = df.format(var21.percentage) + "%",
                                var7 + var6 - fontRenderer.getStringWidth(var22), var8 + var6 / 2 + var20 * 8 + 20,
                                var21.func_76329_a());
                    }
            }
        }
    }

    /**
     * Called when the window is closing. Sets 'running' to false which allows the
     * game loop to exit cleanly.
     */
    public void shutdown() {
        this.running = false;
    }

    /**
     * Will set the focus to ingame if the Minecraft window is the active with
     * focus. Also clears any GUI screen
     * currently displayed
     */
    public void setIngameFocus() {
        if (Display.isActive()) {
            if (!this.inGameHasFocus) {
                this.inGameHasFocus = true;
                this.mouseHelper.grabMouseCursor();
                this.displayGuiScreen((GuiScreen) null);
                this.leftClickCounter = 10000;
            }
        }
    }

    /**
     * Resets the player keystate, disables the ingame focus, and ungrabs the mouse
     * cursor.
     */
    public void setIngameNotInFocus() {
        if (this.inGameHasFocus) {
            KeyBinding.unPressAllKeys(this);
            this.inGameHasFocus = false;
            this.mouseHelper.ungrabMouseCursor();
        }
    }

    /**
     * Displays the ingame menu
     */
    public void displayInGameMenu() {
        if (this.currentScreen == null) {
            this.displayGuiScreen(new GuiIngameMenu(this));

            if (this.isSingleplayer() && !this.theIntegratedServer.getPublic())
                if (this.mcSoundHandler != null)
                    this.mcSoundHandler.func_147689_b();

        }
    }

    private void func_147115_a(boolean p_147115_1_) {
        if (!p_147115_1_)
            this.leftClickCounter = 0;

        if (this.leftClickCounter <= 0) {
            if (p_147115_1_ && this.objectMouseOver != null
                    && this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                int var2 = this.objectMouseOver.blockX;
                int var3 = this.objectMouseOver.blockY;
                int var4 = this.objectMouseOver.blockZ;

                if (this.theWorld != null && this.theWorld.getBlock(var2, var3, var4).getMaterial() != Material.air) {
                    this.playerController.onPlayerDamageBlock(var2, var3, var4, this.objectMouseOver.sideHit);

                    EntityClientPlayerMP thePlayer = this.thePlayer;

                    if (thePlayer != null && thePlayer.isCurrentToolAdventureModeExempt(var2, var3, var4)) {
                        if (this.effectRenderer != null)
                            this.effectRenderer.addBlockHitEffects(var2, var3, var4, this.objectMouseOver.sideHit);
                        thePlayer.swingItem();
                    }
                }
            } else {
                this.playerController.resetBlockRemoving();
            }
        }
    }

    private void func_147116_af() {
        if (this.leftClickCounter <= 0) {
            if (this.thePlayer != null)
                this.thePlayer.swingItem();

            if (this.objectMouseOver == null) {
                logger.error("Null returned as \'hitResult\', this shouldn\'t happen!");

                if (this.playerController.isNotCreative())
                    this.leftClickCounter = 10;

            } else {
                switch (this.objectMouseOver.typeOfHit) {
                    case ENTITY:
                        this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
                        break;
                    case BLOCK:
                        int var1 = this.objectMouseOver.blockX;
                        int var2 = this.objectMouseOver.blockY;
                        int var3 = this.objectMouseOver.blockZ;

                        if (this.theWorld != null
                                && this.theWorld.getBlock(var1, var2, var3).getMaterial() == Material.air) {
                            if (this.playerController.isNotCreative())
                                this.leftClickCounter = 10;

                        } else
                            this.playerController.clickBlock(var1, var2, var3, this.objectMouseOver.sideHit);
                    default:
                        break;
                }
            }
        }
    }

    private void func_147121_ag() {
        this.rightClickDelayTimer = 4;
        boolean var1 = true;
        val thePlayer = this.thePlayer;

        @Nullable
        ItemStack var2 = thePlayer != null ? thePlayer.inventory.getCurrentItem() : null;

        if (this.objectMouseOver == null) {
            logger.warn("Null returned as \'hitResult\', this shouldn\'t happen!");
            return;
        }

        switch (this.objectMouseOver.typeOfHit) {
            case ENTITY:
                if (this.playerController.interactWithEntitySendPacket(this.thePlayer,
                        this.objectMouseOver.entityHit))
                    var1 = false;

                break;

            case BLOCK:
                int var3 = this.objectMouseOver.blockX;
                int var4 = this.objectMouseOver.blockY;
                int var5 = this.objectMouseOver.blockZ;

                if (this.theWorld != null
                        && this.theWorld.getBlock(var3, var4, var5).getMaterial() != Material.air) {
                    int var6 = var2 != null ? var2.stackSize : 0;

                    if (this.playerController.onPlayerRightClick(this.thePlayer, this.theWorld, var2, var3, var4,
                            var5, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec)) {
                        var1 = false;
                        if (this.thePlayer != null)
                            this.thePlayer.swingItem();
                    }

                    if (var2 == null)
                        return;

                    if (var2.stackSize == 0) {
                        if (this.thePlayer != null)
                            this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
                    } else if (var2.stackSize != var6 || this.playerController.isInCreativeMode())
                        this.entityRenderer.itemRenderer.resetEquippedProgress();
                }
            default:
                break;
        }

        if (var1) {
            ItemStack var7 = thePlayer != null ? thePlayer.inventory.getCurrentItem() : null;

            if (var7 != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, var7))
                this.entityRenderer.itemRenderer.resetEquippedProgress2();
        }
    }

    /**
     * Toggles fullscreen mode.
     */
    public void toggleFullscreen() {
        try {
            this.fullscreen = !this.fullscreen;

            if (this.fullscreen) {
                this.updateDisplayMode();
                this.displayWidth = Display.getDisplayMode().getWidth();
                this.displayHeight = Display.getDisplayMode().getHeight();

                if (this.displayWidth <= 0)
                    this.displayWidth = 1;

                if (this.displayHeight <= 0)
                    this.displayHeight = 1;

            } else {
                Display.setDisplayMode(new DisplayMode(this.tempDisplayWidth, this.tempDisplayHeight));
                this.displayWidth = this.tempDisplayWidth;
                this.displayHeight = this.tempDisplayHeight;

                if (this.displayWidth <= 0)
                    this.displayWidth = 1;

                if (this.displayHeight <= 0)
                    this.displayHeight = 1;

            }

            if (this.currentScreen != null) {
                this.resize(this.displayWidth, this.displayHeight);
            } else {
                this.updateFramebufferSize();
            }

            Display.setFullscreen(this.fullscreen);
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
            this.func_147120_f();
        } catch (Exception var2) {
            logger.error("Couldn\'t toggle fullscreen", var2);
        }
    }

    /**
     * Called to resize the current screen.
     */
    private void resize(int p_71370_1_, int p_71370_2_) {
        this.displayWidth = p_71370_1_ <= 0 ? 1 : p_71370_1_;
        this.displayHeight = p_71370_2_ <= 0 ? 1 : p_71370_2_;

        GuiScreen currentScreen = this.currentScreen;

        if (currentScreen != null) {
            ScaledResolution var3 = new ScaledResolution(this, p_71370_1_, p_71370_2_);
            int var4 = var3.getScaledWidth();
            int var5 = var3.getScaledHeight();
            currentScreen.setWorldAndResolution(this, var4, var5);
        }

        this.loadingScreen = new LoadingScreenRenderer(this);
        this.updateFramebufferSize();
    }

    private void updateFramebufferSize() {
        this.mcFramebuffer.createBindFramebuffer(this.displayWidth, this.displayHeight);

        if (this.entityRenderer != null) {
            this.entityRenderer.updateShaderGroupSize(this.displayWidth, this.displayHeight);
        }
    }

    /**
     * Runs the current tick.
     */
    public void runTick() {
        this.mcProfiler.startSection("scheduledExecutables");
        synchronized (this.futureTaskQueue) {
            while (!this.futureTaskQueue.isEmpty())
                this.futureTaskQueue.poll().run();
        }

        this.mcProfiler.endSection();

        if (this.rightClickDelayTimer > 0)
            --this.rightClickDelayTimer;

        this.mcProfiler.startSection("gui");

        if (!this.isGamePaused)
            this.ingameGUI.updateTick();

        this.mcProfiler.endStartSection("pick");
        this.entityRenderer.getMouseOver(1.0F);
        this.mcProfiler.endStartSection("gameMode");

        if (!this.isGamePaused && this.theWorld != null)
            this.playerController.updateController();

        this.mcProfiler.endStartSection("textures");

        if (!this.isGamePaused && this.renderEngine != null)
            this.renderEngine.tick();

        EntityClientPlayerMP thePlayer = this.thePlayer;
        if (this.currentScreen == null && thePlayer != null) {
            if (thePlayer.getHealth() <= 0.0F) {
                this.displayGuiScreen(null);
            } else if (thePlayer.isPlayerSleeping() && this.theWorld != null) {
                this.displayGuiScreen(new GuiSleepMP(this));
            }
        } else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP
                && thePlayer != null && !thePlayer.isPlayerSleeping()) {
            this.displayGuiScreen((GuiScreen) null);
        }

        if (this.currentScreen != null)
            this.leftClickCounter = 10000;

        CrashReport var2;
        CrashReportCategory var3;

        if (this.currentScreen != null) {
            try {
                this.currentScreen.handleInput();
            } catch (Throwable var6) {
                var2 = CrashReport.makeCrashReport(var6, "Updating screen events");
                var3 = var2.makeCategory("Affected screen");
                var3.addCrashSectionCallable("Screen name", () -> {
                    GuiScreen currentScreen = Minecraft.this.currentScreen;

                    if (currentScreen == null)
                        return "null";

                    return currentScreen.getClass().getCanonicalName();
                });
                throw new ReportedException(var2);
            }

            if (this.currentScreen != null) {
                try {
                    this.currentScreen.updateScreen();
                } catch (Throwable var5) {
                    var2 = CrashReport.makeCrashReport(var5, "Ticking screen");
                    var3 = var2.makeCategory("Affected screen");
                    var3.addCrashSectionCallable("Screen name", () -> {
                        GuiScreen currentScreen = Minecraft.this.currentScreen;

                        if (currentScreen == null)
                            return "null";
                        return currentScreen.getClass().getCanonicalName();
                    });
                    throw new ReportedException(var2);
                }
            }
        }

        GuiScreen currentScreen = this.currentScreen;

        if (currentScreen == null || currentScreen.field_146291_p) {
            this.mcProfiler.endStartSection("mouse");

            int var9;

            while (this.getMouse().next()) {
                var9 = this.getMouse().getEventButton();
                KeyBinding.setKeyBindState(this, var9 - 100, this.getMouse().getEventButtonState());

                if (this.getMouse().getEventButtonState())
                    KeyBinding.onTick(this, var9 - 100);

                long var11 = getSystemTime() - this.systemTime;

                if (var11 <= 200L) {
                    int var4 = this.getMouse().getEventDWheel();

                    if (var4 != 0) {
                        if (this.thePlayer != null)
                            this.thePlayer.inventory.changeCurrentItem(var4);

                        if (this.gameSettings.noclip) {
                            if (var4 > 0)
                                var4 = 1;

                            if (var4 < 0)
                                var4 = -1;

                            this.gameSettings.noclipRate += (float) var4 * 0.25F;
                        }
                    }

                    if (this.currentScreen == null) {
                        if (!this.inGameHasFocus && this.getMouse().getEventButtonState())
                            this.setIngameFocus();
                    } else if (this.currentScreen != null) {
                        this.currentScreen.handleMouseInput();
                    }
                }
            }

            if (this.leftClickCounter > 0)
                --this.leftClickCounter;

            this.mcProfiler.endStartSection("keyboard");
            boolean var10;

            while (this.keyboard.next()) {
                KeyBinding.setKeyBindState(this, this.keyboard.getEventKey(), this.keyboard.getEventKeyState());

                if (this.keyboard.getEventKeyState())
                    KeyBinding.onTick(this, this.keyboard.getEventKey());

                if (this.field_83002_am > 0L) {
                    if (getSystemTime() - this.field_83002_am >= 6000L)
                        throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));

                    if (!this.keyboard.isKeyDown(46) || !this.keyboard.isKeyDown(61))
                        this.field_83002_am = -1L;

                } else if (this.keyboard.isKeyDown(46) && this.keyboard.isKeyDown(61))
                    this.field_83002_am = getSystemTime();

                this.func_152348_aa();

                if (this.keyboard.getEventKeyState()) {
                    if (this.keyboard.getEventKey() == 62 && this.entityRenderer != null)
                        this.entityRenderer.deactivateShader();

                    if (this.currentScreen != null) {
                        this.currentScreen.handleKeyboardInput();
                    } else {
                        if (this.keyboard.getEventKey() == 1)
                            this.displayInGameMenu();

                        if (this.keyboard.getEventKey() == 31 && this.keyboard.isKeyDown(61))
                            this.refreshResources();

                        if (this.keyboard.getEventKey() == 20 && this.keyboard.isKeyDown(61))
                            this.refreshResources();

                        if (this.keyboard.getEventKey() == 33 && this.keyboard.isKeyDown(61)) {
                            var10 = this.keyboard.isKeyDown(42) | this.keyboard.isKeyDown(54);
                            this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, var10 ? -1 : 1);
                        }

                        if (this.keyboard.getEventKey() == 30 && this.keyboard.isKeyDown(61)) {
                            RenderGlobal renderGlobal = this.renderGlobal;

                            if (renderGlobal != null)
                                renderGlobal.loadRenderers();
                        }

                        if (this.keyboard.getEventKey() == 35 && this.keyboard.isKeyDown(61)) {
                            this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
                            this.gameSettings.saveOptions();
                        }

                        if (this.keyboard.getEventKey() == 48 && this.keyboard.isKeyDown(61))
                            RenderManager.field_85095_o = !RenderManager.field_85095_o;

                        if (this.keyboard.getEventKey() == 25 && this.keyboard.isKeyDown(61)) {
                            this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
                            this.gameSettings.saveOptions();
                        }

                        if (this.keyboard.getEventKey() == 59)
                            this.gameSettings.hideGUI = !this.gameSettings.hideGUI;

                        if (this.keyboard.getEventKey() == 61) {
                            this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
                            this.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown(this);
                        }

                        if (this.gameSettings.keyBindTogglePerspective.isPressed()) {
                            ++this.gameSettings.thirdPersonView;

                            if (this.gameSettings.thirdPersonView > 2)
                                this.gameSettings.thirdPersonView = 0;
                        }

                        if (this.gameSettings.keyBindSmoothCamera.isPressed())
                            this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
                    }

                    if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart) {
                        if (this.keyboard.getEventKey() == 11)
                            this.updateDebugProfilerName(0);

                        for (var9 = 0; var9 < 9; ++var9)
                            if (this.keyboard.getEventKey() == 2 + var9)
                                this.updateDebugProfilerName(var9 + 1);
                    }
                }
            }

            for (var9 = 0; var9 < 9; ++var9)
                if (this.gameSettings.keyBindsHotbar[var9].isPressed())
                    if (this.thePlayer != null)
                        this.thePlayer.inventory.currentItem = var9;

            var10 = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

            while (this.gameSettings.keyBindInventory.isPressed()) {
                if (this.playerController.func_110738_j()) {
                    if (this.thePlayer != null)
                        this.thePlayer.func_110322_i();
                } else {
                    this.getNetHandler().addToSendQueue(
                            new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                    this.displayGuiScreen(new GuiInventory(this, this.thePlayer));
                }
            }

            while (this.gameSettings.keyBindDrop.isPressed())
                if (this.thePlayer != null)
                    this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown(this));

            while (this.gameSettings.keyBindChat.isPressed() && var10)
                this.displayGuiScreen(new GuiChat(this));

            if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed() && var10)
                this.displayGuiScreen(new GuiChat(this, "/"));

            if (this.thePlayer != null && this.thePlayer.isUsingItem()) {
                if (!this.gameSettings.keyBindUseItem.getIsKeyPressed())
                    this.playerController.onStoppedUsingItem(this.thePlayer);

                label391:

                while (true) {
                    if (!this.gameSettings.keyBindAttack.isPressed()) {
                        while (this.gameSettings.keyBindUseItem.isPressed()) {
                            ;
                        }

                        while (true) {
                            if (this.gameSettings.keyBindPickBlock.isPressed())
                                continue;

                            break label391;
                        }
                    }
                }
            } else {
                while (this.gameSettings.keyBindAttack.isPressed())
                    this.func_147116_af();

                while (this.gameSettings.keyBindUseItem.isPressed())
                    this.func_147121_ag();

                while (this.gameSettings.keyBindPickBlock.isPressed())
                    this.func_147112_ai();

            }

            if (this.gameSettings.keyBindUseItem.getIsKeyPressed() && this.rightClickDelayTimer == 0
                    && thePlayer != null
                    && !thePlayer.isUsingItem())
                this.func_147121_ag();

            this.func_147115_a(this.currentScreen == null && this.gameSettings.keyBindAttack.getIsKeyPressed()
                    && this.inGameHasFocus);
        }

        if (this.theWorld != null) {
            if (this.thePlayer != null) {
                ++this.joinPlayerCounter;

                if (this.joinPlayerCounter == 30) {
                    this.joinPlayerCounter = 0;
                    if (this.theWorld != null)
                        this.theWorld.joinEntityInSurroundings(this.thePlayer);
                }
            }

            this.mcProfiler.endStartSection("gameRenderer");

            if (!this.isGamePaused)
                this.entityRenderer.updateRenderer();

            this.mcProfiler.endStartSection("levelRenderer");

            if (!this.isGamePaused) {
                RenderGlobal renderGlobal = this.renderGlobal;

                if (renderGlobal != null)
                    renderGlobal.updateClouds();
            }

            this.mcProfiler.endStartSection("level");

            if (!this.isGamePaused) {
                WorldClient theWorld = this.theWorld;

                if (theWorld != null) {
                    if (theWorld.lastLightningBolt > 0)
                        --theWorld.lastLightningBolt;

                    theWorld.updateEntities();
                }
            }
        }

        if (!BotGlobalConfig.optimizedGameLoop && !BotGlobalConfig.headless && !this.isGamePaused) {
            if (this.mcMusicTicker != null)
                this.mcMusicTicker.update();

            if (this.mcSoundHandler != null)
                this.mcSoundHandler.update();
        }

        WorldClient theWorld = this.theWorld;

        if (theWorld != null) {
            if (!this.isGamePaused) {
                theWorld.setAllowedSpawnTypes(theWorld.difficultySetting != EnumDifficulty.PEACEFUL, true);

                try {
                    theWorld.tick();
                } catch (Throwable var7) {
                    var2 = CrashReport.makeCrashReport(var7, "Exception in world tick");

                    if (this.theWorld == null) {
                        var3 = var2.makeCategory("Affected level");
                        var3.addCrashSection("Problem", "Level is null!");
                    } else {
                        theWorld.addWorldInfoToCrashReport(var2);
                    }

                    throw new ReportedException(var2);
                }
            }

            this.mcProfiler.endStartSection("animateTick");

            if (!BotGlobalConfig.optimizedGameLoop && !this.isGamePaused && this.theWorld != null
                    && thePlayer != null)
                this.theWorld.doVoidFogParticles(MathHelper.floor_double(thePlayer.posX),
                        MathHelper.floor_double(thePlayer.posY), MathHelper.floor_double(thePlayer.posZ));

            this.mcProfiler.endStartSection("particles");

            if (!this.isGamePaused && this.effectRenderer != null)
                this.effectRenderer.updateEffects();

        } else if (this.myNetworkManager != null) {
            this.mcProfiler.endStartSection("pendingConnection");
            this.myNetworkManager.processReceivedPackets();
        }

        this.mcProfiler.endSection();
        this.systemTime =

                getSystemTime();

    }

    /**
     * Arguments: World foldername, World ingame name, WorldSettings
     */
    public void launchIntegratedServer(String p_71371_1_, String p_71371_2_, WorldSettings p_71371_3_) {
        this.loadWorld((WorldClient) null);
        if (BotGlobalConfig.manualGarbageCollection)
            System.gc();
        ISaveHandler var4 = this.saveLoader.getSaveLoader(p_71371_1_, false);
        WorldInfo var5 = var4.loadWorldInfo();

        if (var5 == null && p_71371_3_ != null) {
            var5 = new WorldInfo(p_71371_3_, p_71371_1_);
            var4.saveWorldInfo(var5);
        }

        if (p_71371_3_ == null)
            p_71371_3_ = new WorldSettings(var5);

        try {
            this.theIntegratedServer = new IntegratedServer(this, p_71371_1_, p_71371_2_, p_71371_3_);
            this.theIntegratedServer.startServerThread();
            this.integratedServerIsRunning = true;
        } catch (Throwable var10) {
            CrashReport var7 = CrashReport.makeCrashReport(var10, "Starting integrated server");
            CrashReportCategory var8 = var7.makeCategory("Starting integrated server");
            var8.addCrashSection("Level ID", p_71371_1_);
            var8.addCrashSection("Level Name", p_71371_2_);
            throw new ReportedException(var7);
        }

        this.loadingScreen.displayProgressMessage(I18n.format("menu.loadingLevel", new Object[0]));

        while (!this.theIntegratedServer.serverIsInRunLoop()) {
            String var6 = this.theIntegratedServer.getUserMessage();

            if (var6 != null)
                this.loadingScreen.resetProgresAndWorkingMessage(I18n.format(var6, new Object[0]));
            else
                this.loadingScreen.resetProgresAndWorkingMessage("");

            try {
                Thread.sleep(200L);
            } catch (InterruptedException var9) {
                ;
            }
        }

        this.displayGuiScreen((GuiScreen) null);
        SocketAddress var11 = this.theIntegratedServer.func_147137_ag().addLocalEndpoint();
        NetworkManager var12 = NetworkManager.provideLocalClient(this, var11);
        var12.setNetHandler(new NetHandlerLoginClient(var12, this, (GuiScreen) null));
        var12.scheduleOutboundPacket(new C00Handshake(5, var11.toString(), 0, EnumConnectionState.LOGIN),
                new GenericFutureListener[0]);
        var12.scheduleOutboundPacket(new C00PacketLoginStart(this.getSession().getGameProfile()),
                new GenericFutureListener[0]);
        this.myNetworkManager = var12;
    }

    /**
     * unloads the current world first
     */
    public void loadWorld(WorldClient p_71403_1_) {
        this.loadWorld(p_71403_1_, "");
    }

    /**
     * par2Str is displayed on the loading screen to the user unloads the current
     * world first
     */
    public void loadWorld(WorldClient p_71353_1_, String p_71353_2_) {
        if (p_71353_1_ == null) {
            NetHandlerPlayClient var3 = this.getNetHandler();

            if (var3 != null)
                var3.cleanup();

            if (this.theIntegratedServer != null)
                this.theIntegratedServer.initiateShutdown();

            this.theIntegratedServer = null;
            if (this.guiAchievement != null)
                this.guiAchievement.func_146257_b();
            this.entityRenderer.getMapItemRenderer().func_148249_a();
        }

        this.renderViewEntity = null;
        this.myNetworkManager = null;

        if (this.loadingScreen != null) {
            this.loadingScreen.resetProgressAndMessage(p_71353_2_);
            this.loadingScreen.resetProgresAndWorkingMessage("");
        }

        if (p_71353_1_ == null && this.theWorld != null) {
            if (this.mcResourcePackRepository.func_148530_e() != null) {
                this.scheduleResourcesRefresh();
            }

            this.mcResourcePackRepository.func_148529_f();
            this.setServerData((ServerData) null);
            this.integratedServerIsRunning = false;
        }

        if (this.mcSoundHandler != null)
            this.mcSoundHandler.func_147690_c();
        this.theWorld = p_71353_1_;

        if (p_71353_1_ != null) {
            if (this.renderGlobal != null)
                this.renderGlobal.setWorldAndLoadRenderers(p_71353_1_);

            if (this.effectRenderer != null)
                this.effectRenderer.clearEffects(p_71353_1_);

            if (this.thePlayer == null) {
                this.thePlayer = this.playerController.createClientPlayerMP(p_71353_1_, new StatFileWriter());
                this.playerController.flipPlayer(this.thePlayer);
            }

            if (this.thePlayer != null)
                this.thePlayer.preparePlayerToSpawn();
            p_71353_1_.spawnEntityInWorld(this.thePlayer);
            if (this.thePlayer != null)
                this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
            this.playerController.setPlayerCapabilities(this.thePlayer);
            this.renderViewEntity = this.thePlayer;
        } else {
            this.saveLoader.flushCache();
            this.thePlayer = null;
        }

        if (BotGlobalConfig.manualGarbageCollection)
            System.gc();
        this.systemTime = 0L;
    }

    /**
     * A String of renderGlobal.getDebugInfoRenders
     */
    public String debugInfoRenders() {
        RenderGlobal renderGlobal = this.renderGlobal;

        if (renderGlobal != null)
            return renderGlobal.getDebugInfoRenders();
        return "N/A";
    }

    /**
     * Gets the information in the F3 menu about how many entities are
     * infront/around you
     */
    public String getEntityDebug() {
        RenderGlobal renderGlobal = this.renderGlobal;

        if (renderGlobal != null)
            return renderGlobal.getDebugInfoEntities();
        return "N/A";
    }

    /**
     * Gets the name of the world's current chunk provider
     */
    public String getWorldProviderName() {
        return this.theWorld != null ? this.theWorld.getProviderName() : "Unknown";
    }

    /**
     * A String of how many entities are in the world
     */
    public String debugInfoEntities() {
        return "P: " + (this.effectRenderer != null ? this.effectRenderer.getStatistics() : "") + ". T: "
                + (this.theWorld != null ? this.theWorld.getDebugLoadedEntities() : "N/A");
    }

    public void setDimensionAndSpawnPlayer(int p_71354_1_) {
        WorldClient theWorld = this.theWorld;

        if (theWorld != null) {
            theWorld.setSpawnLocation();
            theWorld.removeAllEntities();
        }
        int var2 = 0;
        @Nullable
        String var3 = null;

        EntityClientPlayerMP thePlayer = this.thePlayer;

        if (thePlayer != null) {
            var2 = thePlayer.getEntityId();
            if (theWorld != null)
                theWorld.removeEntity(this.thePlayer);
            var3 = thePlayer.func_142021_k();
        }

        this.renderViewEntity = null;
        this.thePlayer = this.playerController.createClientPlayerMP(this.theWorld,
                this.thePlayer == null ? new StatFileWriter() : this.thePlayer.getStatFileWriter());
        if (this.thePlayer != null)
            this.thePlayer.dimension = p_71354_1_;
        this.renderViewEntity = this.thePlayer;
        thePlayer = this.thePlayer;
        if (thePlayer != null) {
            thePlayer.preparePlayerToSpawn();
            thePlayer.func_142020_c(var3);
        }

        if (this.theWorld != null)
            this.theWorld.spawnEntityInWorld(this.thePlayer);
        this.playerController.flipPlayer(this.thePlayer);
        if (thePlayer != null) {
            thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
            thePlayer.setEntityId(var2);
        }
        this.playerController.setPlayerCapabilities(this.thePlayer);

        if (this.currentScreen instanceof GuiGameOver)
            this.displayGuiScreen(null);
    }

    /**
     * Gets whether this is a demo or not.
     */
    public final boolean isDemo() {
        return this.isDemo;
    }

    public NetHandlerPlayClient getNetHandler() {
        return this.thePlayer != null ? this.thePlayer.sendQueue : null;
    }

    public boolean isGuiEnabled() {
        return !this.gameSettings.hideGUI;
    }

    public boolean isFancyGraphicsEnabled() {
        return this.gameSettings.fancyGraphics;
    }

    /**
     * Returns if ambient occlusion is enabled
     */
    public boolean isAmbientOcclusionEnabled() {
        return this.gameSettings.ambientOcclusion != 0;
    }

    private void func_147112_ai() {
        if (this.objectMouseOver != null) {
            boolean var1 = this.thePlayer != null && this.thePlayer.capabilities.isCreativeMode;
            int var3 = 0;
            boolean var4 = false;
            Item var2;
            int var5;

            if (this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                var5 = this.objectMouseOver.blockX;
                int var6 = this.objectMouseOver.blockY;
                int var7 = this.objectMouseOver.blockZ;
                @Nullable
                Block var8 = this.theWorld != null ? this.theWorld.getBlock(var5, var6, var7) : null;

                if (var8 == null || var8.getMaterial() == Material.air)
                    return;

                var2 = var8.getItem(this.theWorld, var5, var6, var7);

                if (var2 == null)
                    return;

                var4 = var2.getHasSubtypes();
                Block var9 = var2 instanceof ItemBlock && !var8.isFlowerPot() ? Block.getBlockFromItem(var2) : var8;
                var3 = var9.getDamageValue(this.theWorld, var5, var6, var7);
            } else {
                if (this.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY
                        || this.objectMouseOver.entityHit == null || !var1)
                    return;

                if (this.objectMouseOver.entityHit instanceof EntityPainting) {
                    var2 = Items.painting;
                } else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot) {
                    var2 = Items.lead;
                } else if (this.objectMouseOver.entityHit instanceof EntityItemFrame) {
                    EntityItemFrame var10 = (EntityItemFrame) this.objectMouseOver.entityHit;
                    ItemStack var12 = var10.getDisplayedItem();

                    if (var12 == null) {
                        var2 = Items.item_frame;
                    } else {
                        var2 = var12.getItem();
                        var3 = var12.getItemDamage();
                        var4 = true;
                    }
                } else if (this.objectMouseOver.entityHit instanceof EntityMinecart) {
                    EntityMinecart var11 = (EntityMinecart) this.objectMouseOver.entityHit;

                    if (var11.getMinecartType() == 2) {
                        var2 = Items.furnace_minecart;
                    } else if (var11.getMinecartType() == 1) {
                        var2 = Items.chest_minecart;
                    } else if (var11.getMinecartType() == 3) {
                        var2 = Items.tnt_minecart;
                    } else if (var11.getMinecartType() == 5) {
                        var2 = Items.hopper_minecart;
                    } else if (var11.getMinecartType() == 6) {
                        var2 = Items.command_block_minecart;
                    } else {
                        var2 = Items.minecart;
                    }
                } else if (this.objectMouseOver.entityHit instanceof EntityBoat) {
                    var2 = Items.boat;
                } else {
                    var2 = Items.spawn_egg;
                    var3 = EntityList.getEntityID(this.objectMouseOver.entityHit);
                    var4 = true;

                    if (var3 <= 0 || !EntityList.entityEggs.containsKey(var3))
                        return;

                }
            }

            EntityClientPlayerMP thePlayer = this.thePlayer;

            if (thePlayer != null) {
                thePlayer.inventory.func_146030_a(var2, var3, var4, var1);

                if (var1) {
                    var5 = thePlayer.inventoryContainer.inventorySlots.size() - 9
                            + thePlayer.inventory.currentItem;
                    this.playerController.sendSlotPacket(
                            thePlayer.inventory.getStackInSlot(thePlayer.inventory.currentItem), var5);
                }
            }
        }
    }

    /**
     * adds core server Info (GL version , Texture pack, isModded, type), and the
     * worldInfo to the crash report
     */
    public CrashReport addGraphicsAndWorldToCrashReport(CrashReport p_71396_1_) {
        p_71396_1_.getCategory().addCrashSectionCallable("Launched Version", () -> Minecraft.this.launchedVersion);
        p_71396_1_.getCategory().addCrashSectionCallable("LWJGL", () -> Sys.getVersion());
        p_71396_1_.getCategory().addCrashSectionCallable("OpenGL",
                () -> GL11.glGetString(GL11.GL_RENDERER) + " GL version " + GL11.glGetString(GL11.GL_VERSION) + ", "
                        + GL11.glGetString(GL11.GL_VENDOR));
        p_71396_1_.getCategory().addCrashSectionCallable("GL Caps", () -> OpenGlHelper.getGLGaps());

        p_71396_1_.getCategory().addCrashSectionCallable("Is Modded", () -> {
            String var1 = ClientBrandRetriever.getClientModName();
            return !var1.equals("vanilla") ? "Definitely; Client brand changed to \'" + var1 + "\'"
                    : (Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated"
                    : "Probably not. Jar signature remains and client brand is untouched.");
        });
        p_71396_1_.getCategory().addCrashSectionCallable("Type", () -> "Client (map_client.txt)");
        p_71396_1_.getCategory().addCrashSectionCallable("Resource Packs",
                () -> Minecraft.this.gameSettings.resourcePacks.toString());
        p_71396_1_.getCategory().addCrashSectionCallable("Current Language",
                () -> Minecraft.this.mcLanguageManager.getCurrentLanguage().toString());
        p_71396_1_.getCategory().addCrashSectionCallable("Profiler Position",
                () -> Minecraft.this.mcProfiler.profilingEnabled ? Minecraft.this.mcProfiler.getNameOfLastSection()
                        : "N/A (disabled)");
        p_71396_1_.getCategory().addCrashSectionCallable("Vec3 Pool Size", () -> {
            byte var1 = 0;
            int var2 = 56 * var1;
            int var3 = var2 / 1024 / 1024;
            byte var4 = 0;
            int var5 = 56 * var4;
            int var6 = var5 / 1024 / 1024;
            return var1 + " (" + var2 + " bytes; " + var3 + " MB) allocated, " + var4 + " (" + var5 + " bytes; "
                    + var6 + " MB) used";
        });
        p_71396_1_.getCategory().addCrashSectionCallable("Anisotropic Filtering",
                () -> Minecraft.this.gameSettings.anisotropicFiltering == 1 ? "Off (1)"
                        : "On (" + Minecraft.this.gameSettings.anisotropicFiltering + ")");

        if (this.theWorld != null)
            this.theWorld.addWorldInfoToCrashReport(p_71396_1_);

        return p_71396_1_;
    }

    public void scheduleResourcesRefresh() {
        this.refreshTexturePacksScheduled = true;
    }

    /**
     * Used in the usage snooper.
     */
    public static int getGLMaximumTextureSize() {
        for (int var0 = 16384; var0 > 0; var0 >>= 1) {
            GL11.glTexImage2D(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_RGBA, var0, var0, 0, GL11.GL_RGBA,
                    GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
            int var1 = GL11.glGetTexLevelParameteri(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);

            if (var1 != 0) {
                return var0;
            }
        }

        return -1;
    }

    /**
     * Set the current ServerData instance.
     */
    public void setServerData(ServerData p_71351_1_) {
        this.currentServerData = p_71351_1_;
    }

    public ServerData func_147104_D() {
        return this.currentServerData;
    }

    public boolean isIntegratedServerRunning() {
        return this.integratedServerIsRunning;
    }

    /**
     * Returns true if there is only one player playing, and the current server is
     * the integrated one.
     */
    public boolean isSingleplayer() {
        return this.integratedServerIsRunning && this.theIntegratedServer != null;
    }

    /**
     * Returns the currently running integrated server
     */
    public IntegratedServer getIntegratedServer() {
        return this.theIntegratedServer;
    }

    public void stopIntegratedServer() {
        val integratedServer = this.getIntegratedServer();

        if (integratedServer != null)
            integratedServer.stopServer();
    }

    /**
     * Gets the system time in milliseconds.
     */
    public static long getSystemTime() {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }

    /**
     * Returns whether we're in full screen or not.
     */
    public boolean isFullScreen() {
        return this.fullscreen;
    }

    public Multimap func_152341_N() {
        return this.field_152356_J;
    }

    @Nullable
    public TextureManager getTextureManager() {
        return this.renderEngine;
    }

    public IResourceManager getResourceManager() {
        return this.mcResourceManager;
    }

    public ResourcePackRepository getResourcePackRepository() {
        return this.mcResourcePackRepository;
    }

    public LanguageManager getLanguageManager() {
        return this.mcLanguageManager;
    }

    @Nullable
    public TextureMap getTextureMapBlocks() {
        return this.textureMapBlocks;
    }

    public boolean isJava64bit() {
        return jvm64bit;
    }

    public boolean func_147113_T() {
        return this.isGamePaused;
    }

    @Nullable
    public SoundHandler getSoundHandler() {
        return this.mcSoundHandler;
    }

    public MusicTicker.MusicType func_147109_W() {
        EntityClientPlayerMP thePlayer = this.thePlayer;
        return this.currentScreen instanceof GuiWinGame ? MusicTicker.MusicType.CREDITS
                : (thePlayer != null ? (thePlayer.worldObj.provider instanceof WorldProviderHell
                ? MusicTicker.MusicType.NETHER
                : (thePlayer.worldObj.provider instanceof WorldProviderEnd
                ? (BossStatus.bossName != null && BossStatus.statusBarTime > 0
                ? MusicTicker.MusicType.END_BOSS
                : MusicTicker.MusicType.END)
                : (thePlayer.capabilities.isCreativeMode && thePlayer.capabilities.allowFlying
                ? MusicTicker.MusicType.CREATIVE
                : MusicTicker.MusicType.GAME)))
                : MusicTicker.MusicType.MENU);
    }

    public void func_152348_aa() {
        int var1 = this.keyboard.getEventKey();

        if (var1 != 0 && !this.keyboard.isRepeatEvent()) {
            if (!(this.currentScreen instanceof GuiControls controlsScreen)
                    || controlsScreen.field_152177_g <= getSystemTime() - 20L) {
                if (this.keyboard.getEventKeyState()) {
                    if (var1 == this.gameSettings.toggleFullscreen.getKeyCode())
                        this.toggleFullscreen();
                    else if (var1 == this.gameSettings.keyBindScreenshot.getKeyCode())
                        this.ingameGUI.getChatGUI().func_146227_a(ScreenShotHelper.saveScreenshot(this, this.mcDataDir,
                                this.displayWidth, this.displayHeight, this.mcFramebuffer));
                }
            }
        }
    }

    public ListenableFuture<?> scheduleOnMainThread(Callable<?> callable) {
        Validate.notNull(callable, "Callable is null");

        if (!this.isMainThread()) {
            ListenableFutureTask<?> listenFutureTask = ListenableFutureTask.create(callable);

            synchronized (this.futureTaskQueue) {
                this.futureTaskQueue.add(listenFutureTask);
                return listenFutureTask;
            }
        }

        try {
            return Futures.immediateFuture(callable.call());
        } catch (Exception e) {
            return Futures.immediateFailedFuture(e);
        }
    }

    public ListenableFuture<?> scheduleOnMainThread(Runnable runnable) {
        Validate.notNull(runnable, "Runnable is null");
        return this.scheduleOnMainThread(Executors.callable(runnable));
    }

    public boolean isMainThread() {
        return Thread.currentThread() == this.mainThread;
    }
}
