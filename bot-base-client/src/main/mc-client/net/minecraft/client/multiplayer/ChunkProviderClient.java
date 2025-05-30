package net.minecraft.client.multiplayer;

import gg.mineral.bot.impl.config.BotGlobalConfig;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChunkProviderClient implements IChunkProvider {
    private static final Logger logger = LogManager.getLogger(ChunkProviderClient.class);

    /**
     * The completely empty chunk used by ChunkProviderClient when chunkMapping
     * doesn't contain the requested
     * coordinates.
     */
    private Chunk blankChunk;

    /**
     * The mapping between ChunkCoordinates and Chunks that ChunkProviderClient
     * maintains.
     */
    private Long2ObjectOpenHashMap<Chunk> chunkMapping = new Long2ObjectOpenHashMap<>();

    /**
     * This may have been intended to be an iterable version of all currently loaded
     * chunks (MultiplayerChunkCache),
     * with identical contents to chunkMapping's values. However it is never
     * actually added to.
     */
    private List<Chunk> chunkListing = new ArrayList<>();

    /**
     * Reference to the World object.
     */
    private World worldObj;

    public ChunkProviderClient(World p_i1184_1_) {
        this.blankChunk = new EmptyChunk(p_i1184_1_, 0, 0);
        this.worldObj = p_i1184_1_;
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    public boolean chunkExists(int p_73149_1_, int p_73149_2_) {
        return true;
    }

    /**
     * Unload chunk from ChunkProviderClient's hashmap. Called in response to a
     * Packet50PreChunk with its mode field set
     * to false
     */
    public void unloadChunk(int p_73234_1_, int p_73234_2_) {
        Chunk var3 = this.provideChunk(p_73234_1_, p_73234_2_);

        if (!var3.isEmpty()) {
            var3.onChunkUnload();
        }

        this.chunkMapping.remove(ChunkCoordIntPair.chunkXZ2Int(p_73234_1_, p_73234_2_));
        this.chunkListing.remove(var3);
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    public Chunk loadChunk(int p_73158_1_, int p_73158_2_) {
        Chunk var3 = new Chunk(this.worldObj, p_73158_1_, p_73158_2_);
        this.chunkMapping.put(ChunkCoordIntPair.chunkXZ2Int(p_73158_1_, p_73158_2_), var3);
        this.chunkListing.add(var3);
        var3.isChunkLoaded = true;
        return var3;
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will
     * generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(int p_73154_1_, int p_73154_2_) {
        return this.chunkMapping.getOrDefault(ChunkCoordIntPair.chunkXZ2Int(p_73154_1_, p_73154_2_), this.blankChunk);
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go. If passed
     * false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_) {
        return true;
    }

    /**
     * Save extra data not associated with any Chunk. Not saved during autosave,
     * only during world unload. Currently
     * unimplemented.
     */
    public void saveExtraData() {
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to
     * unload every such chunk.
     */
    public boolean unloadQueuedChunks() {
        long var1 = System.currentTimeMillis();
        Iterator var3 = this.chunkListing.iterator();

        while (var3.hasNext()) {
            Chunk var4 = (Chunk) var3.next();
            var4.func_150804_b(System.currentTimeMillis() - var1 > 5L);

            if (BotGlobalConfig.optimizedGameLoop)
                break;
        }

        if (System.currentTimeMillis() - var1 > 100L) {
            logger.info("Warning: Clientside chunk ticking took {} ms",
                    new Object[]{Long.valueOf(System.currentTimeMillis() - var1)});
        }

        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave() {
        return false;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_) {
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString() {
        return "MultiplayerChunkCache: " + /*this.chunkMapping.getNumHashElements() +*/ ", " + this.chunkListing.size();
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given
     * location.
     */
    public List getPossibleCreatures(EnumCreatureType p_73155_1_, int p_73155_2_, int p_73155_3_, int p_73155_4_) {
        return null;
    }

    public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_,
                                       int p_147416_5_) {
        return null;
    }

    public int getLoadedChunkCount() {
        return this.chunkListing.size();
    }

    public void recreateStructures(int p_82695_1_, int p_82695_2_) {
    }
}
