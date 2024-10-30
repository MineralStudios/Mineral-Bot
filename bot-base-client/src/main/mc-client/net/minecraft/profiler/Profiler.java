package net.minecraft.profiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import lombok.RequiredArgsConstructor;
import lombok.val;
import optifine.Config;

public class Profiler {
    private static final Logger logger = LogManager.getLogger(Profiler.class);

    /** List of parent sections */
    private final List<String> sectionList = new ArrayList<>();

    /** List of timestamps (System.nanoTime) */
    private final LongList timestampList = new LongArrayList();

    /** Flag profiling enabled */
    public boolean profilingEnabled;

    /** Current profiling section */
    private String profilingSection = "";

    /** Profiling map */
    private final Object2LongOpenHashMap<String> profilingMap = new Object2LongOpenHashMap<>();
    public boolean profilerGlobalEnabled = true;
    private boolean profilerLocalEnabled;
    private long startTickNano;
    public long timeTickNano;
    private long startUpdateChunksNano;
    public long timeUpdateChunksNano;

    public Profiler() {
        this.profilerLocalEnabled = this.profilerGlobalEnabled;
        this.startTickNano = 0L;
        this.timeTickNano = 0L;
        this.startUpdateChunksNano = 0L;
        this.timeUpdateChunksNano = 0L;
    }

    /**
     * Clear profiling.
     */
    public void clearProfiling() {
        this.profilingMap.clear();
        this.profilingSection = "";
        this.sectionList.clear();
        this.profilerLocalEnabled = this.profilerGlobalEnabled;
    }

    /**
     * Start section
     */
    public void startSection(String par1Str) {
        if (Config.getGameSettings().showDebugInfo) {
            if (this.startTickNano == 0L && par1Str.equals("tick")) {
                this.startTickNano = System.nanoTime();
            }

            if (this.startTickNano != 0L && par1Str.equals("preRenderErrors")) {
                this.timeTickNano = System.nanoTime() - this.startTickNano;
                this.startTickNano = 0L;
            }

            if (this.startUpdateChunksNano == 0L && par1Str.equals("updatechunks"))
                this.startUpdateChunksNano = System.nanoTime();

            if (this.startUpdateChunksNano != 0L && par1Str.equals("terrain")) {
                this.timeUpdateChunksNano = System.nanoTime() - this.startUpdateChunksNano;
                this.startUpdateChunksNano = 0L;
            }
        }

        if (this.profilerLocalEnabled) {
            if (this.profilingEnabled) {
                if (this.profilingSection.length() > 0)
                    this.profilingSection = this.profilingSection + ".";

                this.profilingSection = this.profilingSection + par1Str;
                this.sectionList.add(this.profilingSection);
                this.timestampList.add(System.nanoTime());
            }
        }
    }

    /**
     * End section
     */
    public void endSection() {
        if (this.profilerLocalEnabled) {
            if (this.profilingEnabled) {
                long currentTime = System.nanoTime();
                long endTime = this.timestampList.removeLong(this.timestampList.size() - 1);
                this.sectionList.remove(this.sectionList.size() - 1);
                long duration = currentTime - endTime;

                this.profilingMap.put(this.profilingSection,
                        this.profilingMap.getLong(this.profilingSection) + duration);

                if (duration > 100000000L)
                    logger.warn("Something\'s taking too long! \'" + this.profilingSection + "\' took aprox "
                            + (double) duration / 1000000.0D + " ms");

                this.profilingSection = !this.sectionList.isEmpty()
                        ? (String) this.sectionList.get(this.sectionList.size() - 1)
                        : "";
            }
        }
    }

    /**
     * Get profiling data
     */
    @Nullable
    public List<Profiler.Result> getProfilingData(String par1Str) {
        this.profilerLocalEnabled = this.profilerGlobalEnabled;

        if (!this.profilerLocalEnabled)
            return new ArrayList<>(Arrays.asList(new Profiler.Result("root", 0.0D, 0.0D)));
        if (!this.profilingEnabled)
            return null;

        long var3 = this.profilingMap.containsKey("root") ? this.profilingMap.getLong("root") : 0L;
        long var5 = this.profilingMap.containsKey(par1Str) ? this.profilingMap.getLong(par1Str)
                : -1L;
        val profilingData = new ArrayList<Profiler.Result>();

        if (par1Str.length() > 0)
            par1Str = par1Str + ".";

        long totalTime = 0L;

        for (val e : this.profilingMap.object2LongEntrySet()) {
            val key = e.getKey();

            if (key.length() > par1Str.length() && key.startsWith(par1Str)
                    && key.indexOf(".", par1Str.length() + 1) < 0)
                totalTime += e.getLongValue();
        }

        float totalTimeFloat = (float) totalTime;

        if (totalTime < var5)
            totalTime = var5;

        if (var3 < totalTime)
            var3 = totalTime;

        String key;

        for (val e : this.profilingMap.object2LongEntrySet()) {
            key = e.getKey();

            if (key.length() > par1Str.length() && key.startsWith(par1Str)
                    && key.indexOf(".", par1Str.length() + 1) < 0) {
                long var13 = e.getLongValue();
                double var15 = (double) var13 * 100.0D / (double) totalTime;
                double var17 = (double) var13 * 100.0D / (double) var3;
                String var19 = key.substring(par1Str.length());
                profilingData.add(new Profiler.Result(var19, var15, var17));
            }
        }

        for (val e : this.profilingMap.object2LongEntrySet()) {
            key = e.getKey();
            this.profilingMap.put(key,
                    e.getLongValue() * 999L / 1000L);
        }

        if ((float) totalTime > totalTimeFloat) {
            profilingData
                    .add(new Profiler.Result("unspecified",
                            (double) ((float) totalTime - totalTimeFloat) * 100.0D / (double) totalTime,
                            (double) ((float) totalTime - totalTimeFloat) * 100.0D / (double) var3));
        }

        Collections.sort(profilingData);
        profilingData.add(0, new Profiler.Result(par1Str, 100.0D, (double) totalTime * 100.0D / (double) var3));
        return profilingData;

    }

    /**
     * End current section and start a new section
     */
    public void endStartSection(String par1Str) {
        if (this.profilerLocalEnabled) {
            this.endSection();
            this.startSection(par1Str);
        }
    }

    public String getNameOfLastSection() {
        return this.sectionList.size() == 0 ? "[UNKNOWN]" : (String) this.sectionList.get(this.sectionList.size() - 1);
    }

    @RequiredArgsConstructor
    public static final class Result implements Comparable<Result> {
        public final String field_76331_c;
        public final double field_76332_a;
        public final double percentage;

        @Override
        public int compareTo(Profiler.Result par1Obj) {
            return par1Obj.field_76332_a < this.field_76332_a ? -1
                    : (par1Obj.field_76332_a > this.field_76332_a ? 1
                            : par1Obj.field_76331_c.compareTo(this.field_76331_c));
        }

        public int func_76329_a() {
            return (this.field_76331_c.hashCode() & 11184810) + 4473924;
        }
    }
}
