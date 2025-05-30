package net.minecraft.world.storage;

import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class WorldInfo {
    /**
     * Holds the seed of the currently world.
     */
    private long randomSeed;
    private WorldType terrainType;
    private String generatorOptions;

    /**
     * The spawn zone position X coordinate.
     */
    private int spawnX;

    /**
     * The spawn zone position Y coordinate.
     */
    private int spawnY;

    /**
     * The spawn zone position Z coordinate.
     */
    private int spawnZ;

    /**
     * Total time for this world.
     */
    private long totalTime;

    /**
     * The current world time in ticks, ranging from 0 to 23999.
     */
    private long worldTime;

    /**
     * The last time the player was in this world.
     */
    private long lastTimePlayed;

    /**
     * The size of entire save of current world on the disk, isn't exactly.
     */
    private long sizeOnDisk;
    private NBTTagCompound playerTag;
    private int dimension;

    /**
     * The name of the save defined at world creation.
     */
    private String levelName;

    /**
     * Introduced in beta 1.3, is the save version for future control.
     */
    private int saveVersion;

    /**
     * True if it's raining, false otherwise.
     */
    private boolean raining;

    /**
     * Number of ticks until next rain.
     */
    private int rainTime;

    /**
     * Is thunderbolts failing now?
     */
    private boolean thundering;

    /**
     * Number of ticks untils next thunderbolt.
     */
    private int thunderTime;

    /**
     * The Game Type.
     */
    private WorldSettings.GameType theGameType;

    /**
     * Whether the map features (e.g. strongholds) generation is enabled or
     * disabled.
     */
    private boolean mapFeaturesEnabled;

    /**
     * Hardcore mode flag
     */
    private boolean hardcore;
    private boolean allowCommands;
    private boolean initialized;
    private GameRules theGameRules;

    protected WorldInfo() {
        this.terrainType = WorldType.DEFAULT;
        this.generatorOptions = "";
        this.theGameRules = new GameRules();
    }

    public WorldInfo(NBTTagCompound p_i2157_1_) {
        this.terrainType = WorldType.DEFAULT;
        this.generatorOptions = "";
        this.theGameRules = new GameRules();
        this.randomSeed = p_i2157_1_.getLong("RandomSeed");

        if (p_i2157_1_.func_150297_b("generatorName", 8)) {
            String var2 = p_i2157_1_.getString("generatorName");
            this.terrainType = WorldType.parseWorldType(var2);

            if (this.terrainType == null) {
                this.terrainType = WorldType.DEFAULT;
            } else if (this.terrainType.isVersioned()) {
                int var3 = 0;

                if (p_i2157_1_.func_150297_b("generatorVersion", 99)) {
                    var3 = p_i2157_1_.getInteger("generatorVersion");
                }

                this.terrainType = this.terrainType.getWorldTypeForGeneratorVersion(var3);
            }

            if (p_i2157_1_.func_150297_b("generatorOptions", 8)) {
                this.generatorOptions = p_i2157_1_.getString("generatorOptions");
            }
        }

        this.theGameType = WorldSettings.GameType.getByID(p_i2157_1_.getInteger("GameType"));

        if (p_i2157_1_.func_150297_b("MapFeatures", 99)) {
            this.mapFeaturesEnabled = p_i2157_1_.getBoolean("MapFeatures");
        } else {
            this.mapFeaturesEnabled = true;
        }

        this.spawnX = p_i2157_1_.getInteger("SpawnX");
        this.spawnY = p_i2157_1_.getInteger("SpawnY");
        this.spawnZ = p_i2157_1_.getInteger("SpawnZ");
        this.totalTime = p_i2157_1_.getLong("Time");

        if (p_i2157_1_.func_150297_b("DayTime", 99)) {
            this.worldTime = p_i2157_1_.getLong("DayTime");
        } else {
            this.worldTime = this.totalTime;
        }

        this.lastTimePlayed = p_i2157_1_.getLong("LastPlayed");
        this.sizeOnDisk = p_i2157_1_.getLong("SizeOnDisk");
        this.levelName = p_i2157_1_.getString("LevelName");
        this.saveVersion = p_i2157_1_.getInteger("version");
        this.rainTime = p_i2157_1_.getInteger("rainTime");
        this.raining = p_i2157_1_.getBoolean("raining");
        this.thunderTime = p_i2157_1_.getInteger("thunderTime");
        this.thundering = p_i2157_1_.getBoolean("thundering");
        this.hardcore = p_i2157_1_.getBoolean("hardcore");

        if (p_i2157_1_.func_150297_b("initialized", 99)) {
            this.initialized = p_i2157_1_.getBoolean("initialized");
        } else {
            this.initialized = true;
        }

        if (p_i2157_1_.func_150297_b("allowCommands", 99)) {
            this.allowCommands = p_i2157_1_.getBoolean("allowCommands");
        } else {
            this.allowCommands = this.theGameType == WorldSettings.GameType.CREATIVE;
        }

        if (p_i2157_1_.func_150297_b("Player", 10)) {
            this.playerTag = p_i2157_1_.getCompoundTag("Player");
            this.dimension = this.playerTag.getInteger("Dimension");
        }

        if (p_i2157_1_.func_150297_b("GameRules", 10)) {
            this.theGameRules.readGameRulesFromNBT(p_i2157_1_.getCompoundTag("GameRules"));
        }
    }

    public WorldInfo(WorldSettings p_i2158_1_, String p_i2158_2_) {
        this.terrainType = WorldType.DEFAULT;
        this.generatorOptions = "";
        this.theGameRules = new GameRules();
        this.randomSeed = p_i2158_1_.getSeed();
        this.theGameType = p_i2158_1_.getGameType();
        this.mapFeaturesEnabled = p_i2158_1_.isMapFeaturesEnabled();
        this.levelName = p_i2158_2_;
        this.hardcore = p_i2158_1_.getHardcoreEnabled();
        this.terrainType = p_i2158_1_.getTerrainType();
        this.generatorOptions = p_i2158_1_.func_82749_j();
        this.allowCommands = p_i2158_1_.areCommandsAllowed();
        this.initialized = false;
    }

    public WorldInfo(WorldInfo p_i2159_1_) {
        this.terrainType = WorldType.DEFAULT;
        this.generatorOptions = "";
        this.theGameRules = new GameRules();
        this.randomSeed = p_i2159_1_.randomSeed;
        this.terrainType = p_i2159_1_.terrainType;
        this.generatorOptions = p_i2159_1_.generatorOptions;
        this.theGameType = p_i2159_1_.theGameType;
        this.mapFeaturesEnabled = p_i2159_1_.mapFeaturesEnabled;
        this.spawnX = p_i2159_1_.spawnX;
        this.spawnY = p_i2159_1_.spawnY;
        this.spawnZ = p_i2159_1_.spawnZ;
        this.totalTime = p_i2159_1_.totalTime;
        this.worldTime = p_i2159_1_.worldTime;
        this.lastTimePlayed = p_i2159_1_.lastTimePlayed;
        this.sizeOnDisk = p_i2159_1_.sizeOnDisk;
        this.playerTag = p_i2159_1_.playerTag;
        this.dimension = p_i2159_1_.dimension;
        this.levelName = p_i2159_1_.levelName;
        this.saveVersion = p_i2159_1_.saveVersion;
        this.rainTime = p_i2159_1_.rainTime;
        this.raining = p_i2159_1_.raining;
        this.thunderTime = p_i2159_1_.thunderTime;
        this.thundering = p_i2159_1_.thundering;
        this.hardcore = p_i2159_1_.hardcore;
        this.allowCommands = p_i2159_1_.allowCommands;
        this.initialized = p_i2159_1_.initialized;
        this.theGameRules = p_i2159_1_.theGameRules;
    }

    /**
     * Gets the NBTTagCompound for the worldInfo
     */
    public NBTTagCompound getNBTTagCompound() {
        NBTTagCompound var1 = new NBTTagCompound();
        this.updateTagCompound(var1, this.playerTag);
        return var1;
    }

    /**
     * Creates a new NBTTagCompound for the world, with the given NBTTag as the
     * "Player"
     */
    public NBTTagCompound cloneNBTCompound(NBTTagCompound p_76082_1_) {
        NBTTagCompound var2 = new NBTTagCompound();
        this.updateTagCompound(var2, p_76082_1_);
        return var2;
    }

    private void updateTagCompound(NBTTagCompound p_76064_1_, NBTTagCompound p_76064_2_) {
        p_76064_1_.setLong("RandomSeed", this.randomSeed);
        p_76064_1_.setString("generatorName", this.terrainType.getWorldTypeName());
        p_76064_1_.setInteger("generatorVersion", this.terrainType.getGeneratorVersion());
        p_76064_1_.setString("generatorOptions", this.generatorOptions);
        p_76064_1_.setInteger("GameType", this.theGameType.getID());
        p_76064_1_.setBoolean("MapFeatures", this.mapFeaturesEnabled);
        p_76064_1_.setInteger("SpawnX", this.spawnX);
        p_76064_1_.setInteger("SpawnY", this.spawnY);
        p_76064_1_.setInteger("SpawnZ", this.spawnZ);
        p_76064_1_.setLong("Time", this.totalTime);
        p_76064_1_.setLong("DayTime", this.worldTime);
        p_76064_1_.setLong("SizeOnDisk", this.sizeOnDisk);
        p_76064_1_.setLong("LastPlayed", MinecraftServer.getSystemTimeMillis());
        p_76064_1_.setString("LevelName", this.levelName);
        p_76064_1_.setInteger("version", this.saveVersion);
        p_76064_1_.setInteger("rainTime", this.rainTime);
        p_76064_1_.setBoolean("raining", this.raining);
        p_76064_1_.setInteger("thunderTime", this.thunderTime);
        p_76064_1_.setBoolean("thundering", this.thundering);
        p_76064_1_.setBoolean("hardcore", this.hardcore);
        p_76064_1_.setBoolean("allowCommands", this.allowCommands);
        p_76064_1_.setBoolean("initialized", this.initialized);
        p_76064_1_.setTag("GameRules", this.theGameRules.writeGameRulesToNBT());

        if (p_76064_2_ != null) {
            p_76064_1_.setTag("Player", p_76064_2_);
        }
    }

    /**
     * Returns the seed of current world.
     */
    public long getSeed() {
        return this.randomSeed;
    }

    /**
     * Returns the x spawn position
     */
    public int getSpawnX() {
        return this.spawnX;
    }

    /**
     * Return the Y axis spawning point of the player.
     */
    public int getSpawnY() {
        return this.spawnY;
    }

    /**
     * Returns the z spawn position
     */
    public int getSpawnZ() {
        return this.spawnZ;
    }

    public long getWorldTotalTime() {
        return this.totalTime;
    }

    /**
     * Get current world time
     */
    public long getWorldTime() {
        return this.worldTime;
    }

    public long getSizeOnDisk() {
        return this.sizeOnDisk;
    }

    /**
     * Returns the player's NBTTagCompound to be loaded
     */
    public NBTTagCompound getPlayerNBTTagCompound() {
        return this.playerTag;
    }

    /**
     * Returns vanilla MC dimension (-1,0,1). For custom dimension compatibility,
     * always prefer
     * WorldProvider.dimensionID accessed from World.provider.dimensionID
     */
    public int getVanillaDimension() {
        return this.dimension;
    }

    /**
     * Set the x spawn position to the passed in value
     */
    public void setSpawnX(int p_76058_1_) {
        this.spawnX = p_76058_1_;
    }

    /**
     * Sets the y spawn position
     */
    public void setSpawnY(int p_76056_1_) {
        this.spawnY = p_76056_1_;
    }

    /**
     * Set the z spawn position to the passed in value
     */
    public void setSpawnZ(int p_76087_1_) {
        this.spawnZ = p_76087_1_;
    }

    public void incrementTotalWorldTime(long p_82572_1_) {
        this.totalTime = p_82572_1_;
    }

    /**
     * Set current world time
     */
    public void setWorldTime(long p_76068_1_) {
        this.worldTime = p_76068_1_;
    }

    /**
     * Sets the spawn zone position. Args: x, y, z
     */
    public void setSpawnPosition(int p_76081_1_, int p_76081_2_, int p_76081_3_) {
        this.spawnX = p_76081_1_;
        this.spawnY = p_76081_2_;
        this.spawnZ = p_76081_3_;
    }

    /**
     * Get current world name
     */
    public String getWorldName() {
        return this.levelName;
    }

    public void setWorldName(String p_76062_1_) {
        this.levelName = p_76062_1_;
    }

    /**
     * Returns the save version of this world
     */
    public int getSaveVersion() {
        return this.saveVersion;
    }

    /**
     * Sets the save version of the world
     */
    public void setSaveVersion(int p_76078_1_) {
        this.saveVersion = p_76078_1_;
    }

    /**
     * Return the last time the player was in this world.
     */
    public long getLastTimePlayed() {
        return this.lastTimePlayed;
    }

    /**
     * Returns true if it is thundering, false otherwise.
     */
    public boolean isThundering() {
        return this.thundering;
    }

    /**
     * Sets whether it is thundering or not.
     */
    public void setThundering(boolean p_76069_1_) {
        this.thundering = p_76069_1_;
    }

    /**
     * Returns the number of ticks until next thunderbolt.
     */
    public int getThunderTime() {
        return this.thunderTime;
    }

    /**
     * Defines the number of ticks until next thunderbolt.
     */
    public void setThunderTime(int p_76090_1_) {
        this.thunderTime = p_76090_1_;
    }

    /**
     * Returns true if it is raining, false otherwise.
     */
    public boolean isRaining() {
        return this.raining;
    }

    /**
     * Sets whether it is raining or not.
     */
    public void setRaining(boolean p_76084_1_) {
        this.raining = p_76084_1_;
    }

    /**
     * Return the number of ticks until rain.
     */
    public int getRainTime() {
        return this.rainTime;
    }

    /**
     * Sets the number of ticks until rain.
     */
    public void setRainTime(int p_76080_1_) {
        this.rainTime = p_76080_1_;
    }

    /**
     * Gets the GameType.
     */
    public WorldSettings.GameType getGameType() {
        return this.theGameType;
    }

    /**
     * Get whether the map features (e.g. strongholds) generation is enabled or
     * disabled.
     */
    public boolean isMapFeaturesEnabled() {
        return this.mapFeaturesEnabled;
    }

    /**
     * Sets the GameType.
     */
    public void setGameType(WorldSettings.GameType p_76060_1_) {
        this.theGameType = p_76060_1_;
    }

    /**
     * Returns true if hardcore mode is enabled, otherwise false
     */
    public boolean isHardcoreModeEnabled() {
        return this.hardcore;
    }

    public WorldType getTerrainType() {
        return this.terrainType;
    }

    public void setTerrainType(WorldType p_76085_1_) {
        this.terrainType = p_76085_1_;
    }

    public String getGeneratorOptions() {
        return this.generatorOptions;
    }

    /**
     * Returns true if commands are allowed on this World.
     */
    public boolean areCommandsAllowed() {
        return this.allowCommands;
    }

    /**
     * Returns true if the World is initialized.
     */
    public boolean isInitialized() {
        return this.initialized;
    }

    /**
     * Sets the initialization status of the World.
     */
    public void setServerInitialized(boolean p_76091_1_) {
        this.initialized = p_76091_1_;
    }

    /**
     * Gets the GameRules class Instance.
     */
    public GameRules getGameRulesInstance() {
        return this.theGameRules;
    }

    /**
     * Adds this WorldInfo instance to the crash report.
     */
    public void addToCrashReport(CrashReportCategory p_85118_1_) {
        p_85118_1_.addCrashSectionCallable("Level seed", () -> String.valueOf(WorldInfo.this.getSeed()));
        p_85118_1_.addCrashSectionCallable("Level generator",
                () -> String.format("ID %02d - %s, ver %d. Features enabled: %b",
                        new Object[]{Integer.valueOf(WorldInfo.this.terrainType.getWorldTypeID()),
                                WorldInfo.this.terrainType.getWorldTypeName(),
                                Integer.valueOf(WorldInfo.this.terrainType.getGeneratorVersion()),
                                Boolean.valueOf(WorldInfo.this.mapFeaturesEnabled)}));
        p_85118_1_.addCrashSectionCallable("Level generator options", () -> WorldInfo.this.generatorOptions);
        p_85118_1_.addCrashSectionCallable("Level spawn location",
                () -> CrashReportCategory.getLocationInfo(WorldInfo.this.spawnX, WorldInfo.this.spawnY,
                        WorldInfo.this.spawnZ));
        p_85118_1_.addCrashSectionCallable("Level time",
                () -> String.format("%d game time, %d day time", new Object[]{Long.valueOf(WorldInfo.this.totalTime),
                        Long.valueOf(WorldInfo.this.worldTime)}));
        p_85118_1_.addCrashSectionCallable("Level dimension", () -> String.valueOf(WorldInfo.this.dimension));
        p_85118_1_.addCrashSectionCallable("Level storage version", () -> {
            String var1 = "Unknown?";

            try {
                switch (WorldInfo.this.saveVersion) {
                    case 19132:
                        var1 = "McRegion";
                        break;

                    case 19133:
                        var1 = "Anvil";
                }
            } catch (Throwable var3) {
                ;
            }

            return String.format("0x%05X - %s", new Object[]{Integer.valueOf(WorldInfo.this.saveVersion), var1});
        });
        p_85118_1_.addCrashSectionCallable("Level weather",
                () -> String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)",
                        new Object[]{Integer.valueOf(WorldInfo.this.rainTime),
                                Boolean.valueOf(WorldInfo.this.raining), Integer.valueOf(WorldInfo.this.thunderTime),
                                Boolean.valueOf(WorldInfo.this.thundering)}));
        p_85118_1_.addCrashSectionCallable("Level game mode",
                () -> String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", new Object[]{
                        WorldInfo.this.theGameType.getName(), Integer.valueOf(WorldInfo.this.theGameType.getID()),
                        Boolean.valueOf(WorldInfo.this.hardcore), Boolean.valueOf(WorldInfo.this.allowCommands)}));
    }
}
