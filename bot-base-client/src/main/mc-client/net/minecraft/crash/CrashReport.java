package net.minecraft.crash;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ReportedException;
import net.minecraft.world.gen.layer.IntCache;
import optifine.Reflector;

public class CrashReport {
    private static final Logger logger = LogManager.getLogger(CrashReport.class);

    /** Description of the crash report. */
    private final String description;

    /** The Throwable that is the "cause" for this crash and Crash Report. */
    private final Throwable cause;

    /** Category of crash */
    private final CrashReportCategory theReportCategory = new CrashReportCategory(this, "System Details");

    /** Holds the keys and values of all crash report sections. */
    private final List crashReportSections = new ArrayList();

    /** File of crash report. */
    private File crashReportFile;
    private boolean field_85059_f = true;
    private StackTraceElement[] stacktrace = new StackTraceElement[0];
    private boolean reported = false;

    public CrashReport(String par1Str, Throwable par2Throwable) {
        this.description = par1Str;
        this.cause = par2Throwable;
        this.populateEnvironment();
    }

    /**
     * Populates this crash report with initial information about the running server
     * and operating system / java
     * environment
     */
    private void populateEnvironment() {
        this.theReportCategory.addCrashSectionCallable("Minecraft Version", () -> "1.7.10");
        this.theReportCategory.addCrashSectionCallable("Operating System",
                () -> System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version "
                        + System.getProperty("os.version"));
        this.theReportCategory.addCrashSectionCallable("Java Version",
                () -> System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        this.theReportCategory.addCrashSectionCallable("Java VM Version",
                () -> System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), "
                        + System.getProperty("java.vm.vendor"));
        this.theReportCategory.addCrashSectionCallable("Memory", () -> {
            Runtime var1 = Runtime.getRuntime();
            long var2 = var1.maxMemory();
            long var4 = var1.totalMemory();
            long var6 = var1.freeMemory();
            long var8 = var2 / 1024L / 1024L;
            long var10 = var4 / 1024L / 1024L;
            long var12 = var6 / 1024L / 1024L;
            return var6 + " bytes (" + var12 + " MB) / " + var4 + " bytes (" + var10 + " MB) up to " + var2
                    + " bytes (" + var8 + " MB)";
        });
        this.theReportCategory.addCrashSectionCallable("JVM Flags", () -> {
            RuntimeMXBean var1 = ManagementFactory.getRuntimeMXBean();
            List var2 = var1.getInputArguments();
            int var3 = 0;
            StringBuilder var4 = new StringBuilder();
            Iterator var5 = var2.iterator();

            while (var5.hasNext()) {
                String var6 = (String) var5.next();

                if (var6.startsWith("-X")) {
                    if (var3++ > 0)
                        var4.append(" ");

                    var4.append(var6);
                }
            }

            return String.format("%d total; %s", new Object[] { Integer.valueOf(var3), var4.toString() });
        });
        this.theReportCategory.addCrashSectionCallable("AABB Pool Size", () -> {
            byte var1 = 0;
            int var2 = 56 * var1;
            int var3 = var2 / 1024 / 1024;
            byte var4 = 0;
            int var5 = 56 * var4;
            int var6 = var5 / 1024 / 1024;
            return var1 + " (" + var2 + " bytes; " + var3 + " MB) allocated, " + var4 + " (" + var5 + " bytes; "
                    + var6 + " MB) used";
        });
        this.theReportCategory.addCrashSectionCallable("IntCache", () -> IntCache.getCacheSizes());

        if (Reflector.FMLCommonHandler_enhanceCrashReport.exists()) {
            Object instance = Reflector.call(Reflector.FMLCommonHandler_instance, new Object[0]);
            Reflector.callString(instance, Reflector.FMLCommonHandler_enhanceCrashReport,
                    new Object[] { this, this.theReportCategory });
        }
    }

    /**
     * Returns the description of the Crash Report.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the Throwable object that is the cause for the crash and Crash
     * Report.
     */
    public Throwable getCrashCause() {
        return this.cause;
    }

    /**
     * Gets the various sections of the crash report into the given StringBuilder
     */
    public void getSectionsInStringBuilder(StringBuilder par1StringBuilder) {
        if ((this.stacktrace == null || this.stacktrace.length <= 0) && this.crashReportSections.size() > 0) {
            this.stacktrace = (StackTraceElement[]) ((StackTraceElement[]) ArrayUtils
                    .subarray(((CrashReportCategory) this.crashReportSections.get(0)).func_147152_a(), 0, 1));
        }

        if (this.stacktrace != null && this.stacktrace.length > 0) {
            par1StringBuilder.append("-- Head --\n");
            par1StringBuilder.append("Stacktrace:\n");
            StackTraceElement[] var6 = this.stacktrace;
            int var7 = var6.length;

            for (int var4 = 0; var4 < var7; ++var4) {
                StackTraceElement var5 = var6[var4];
                par1StringBuilder.append("\t").append("at ").append(var5.toString());
                par1StringBuilder.append("\n");
            }

            par1StringBuilder.append("\n");
        }

        Iterator var61 = this.crashReportSections.iterator();

        while (var61.hasNext()) {
            CrashReportCategory var71 = (CrashReportCategory) var61.next();
            var71.appendToStringBuilder(par1StringBuilder);
            par1StringBuilder.append("\n\n");
        }

        this.theReportCategory.appendToStringBuilder(par1StringBuilder);
    }

    /**
     * Gets the stack trace of the Throwable that caused this crash report, or if
     * that fails, the cause .toString().
     */
    public String getCauseStackTraceOrString() {
        StringWriter var1 = null;
        PrintWriter var2 = null;
        Object var3 = this.cause;

        if (((Throwable) var3).getMessage() == null) {
            if (var3 instanceof NullPointerException) {
                var3 = new NullPointerException(this.description);
            } else if (var3 instanceof StackOverflowError) {
                var3 = new StackOverflowError(this.description);
            } else if (var3 instanceof OutOfMemoryError) {
                var3 = new OutOfMemoryError(this.description);
            }

            ((Throwable) var3).setStackTrace(this.cause.getStackTrace());
        }

        String var4 = ((Throwable) var3).toString();

        try {
            var1 = new StringWriter();
            var2 = new PrintWriter(var1);
            ((Throwable) var3).printStackTrace(var2);
            var4 = var1.toString();
        } finally {
            IOUtils.closeQuietly(var1);
            IOUtils.closeQuietly(var2);
        }

        return var4;
    }

    /**
     * Gets the complete report with headers, stack trace, and different sections as
     * a string.
     */
    public String getCompleteReport(Minecraft mc) {
        if (!this.reported)
            this.reported = true;

        StringBuilder var1 = new StringBuilder();
        var1.append("---- Minecraft Crash Report ----\n");
        var1.append("// ");
        var1.append(getWittyComment());
        var1.append("\n\n");
        var1.append("Time: ");
        var1.append((new SimpleDateFormat()).format(new Date()));
        var1.append("\n");
        var1.append("Description: ");
        var1.append(this.description);
        var1.append("\n\n");
        var1.append(this.getCauseStackTraceOrString());
        var1.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

        for (int var2 = 0; var2 < 87; ++var2) {
            var1.append("-");
        }

        var1.append("\n\n");
        this.getSectionsInStringBuilder(var1);
        return var1.toString();
    }

    /**
     * Gets the file this crash report is saved into.
     */
    public File getFile() {
        return this.crashReportFile;
    }

    /**
     * Saves this CrashReport to the given file and returns a value indicating
     * whether we were successful at doing so.
     */
    public boolean saveToFile(Minecraft mc, File p_147149_1_) {
        if (this.crashReportFile != null) {
            return false;
        } else {
            if (p_147149_1_.getParentFile() != null) {
                p_147149_1_.getParentFile().mkdirs();
            }

            try {
                FileWriter var3 = new FileWriter(p_147149_1_);
                var3.write(this.getCompleteReport(mc));
                var3.close();
                this.crashReportFile = p_147149_1_;
                return true;
            } catch (Throwable var31) {
                logger.error("Could not save crash report to " + p_147149_1_, var31);
                return false;
            }
        }
    }

    public CrashReportCategory getCategory() {
        return this.theReportCategory;
    }

    /**
     * Creates a CrashReportCategory
     */
    public CrashReportCategory makeCategory(String par1Str) {
        return this.makeCategoryDepth(par1Str, 1);
    }

    /**
     * Creates a CrashReportCategory for the given stack trace depth
     */
    public CrashReportCategory makeCategoryDepth(String par1Str, int par2) {
        CrashReportCategory var3 = new CrashReportCategory(this, par1Str);

        if (this.field_85059_f) {
            int var4 = var3.getPrunedStackTrace(par2);
            StackTraceElement[] var5 = this.cause.getStackTrace();
            StackTraceElement var6 = null;
            StackTraceElement var7 = null;
            int var8 = var5.length - var4;

            if (var8 < 0) {
                System.out.println("Negative index in crash report handler (" + var5.length + "/" + var4 + ")");
            }

            if (var5 != null && 0 <= var8 && var8 < var5.length) {
                var6 = var5[var8];

                if (var5.length + 1 - var4 < var5.length) {
                    var7 = var5[var5.length + 1 - var4];
                }
            }

            this.field_85059_f = var3.firstTwoElementsOfStackTraceMatch(var6, var7);

            if (var4 > 0 && !this.crashReportSections.isEmpty()) {
                CrashReportCategory var9 = (CrashReportCategory) this.crashReportSections
                        .get(this.crashReportSections.size() - 1);
                var9.trimStackTraceEntriesFromBottom(var4);
            } else if (var5 != null && var5.length >= var4 && 0 <= var8 && var8 < var5.length) {
                this.stacktrace = new StackTraceElement[var8];
                System.arraycopy(var5, 0, this.stacktrace, 0, this.stacktrace.length);
            } else {
                this.field_85059_f = false;
            }
        }

        this.crashReportSections.add(var3);
        return var3;
    }

    /**
     * Gets a random witty comment for inclusion in this CrashReport
     */
    private static String getWittyComment() {
        String[] var0 = new String[] { "Who set us up the TNT?",
                "Everything\'s going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?",
                "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I\'m sorry, Dave.",
                "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...",
                "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.",
                "You should try our sister game, Minceraft!", "Don\'t be sad. I\'ll do better next time, I promise!",
                "Don\'t be sad, have a hug! <3", "I just don\'t know what went wrong :(", "Shall we play a game?",
                "Quite honestly, I wouldn\'t worry myself about that.", "I bet Cylons wouldn\'t have this problem.",
                "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?",
                "Hi. I\'m Minecraft, and I\'m a crashaholic.", "Ooh. Shiny.", "This doesn\'t make any sense!",
                "Why is it breaking :(", "Don\'t do that.", "Ouch. That hurt :(", "You\'re mean.",
                "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!",
                "But it works on my machine." };

        try {
            return var0[(int) (System.nanoTime() % (long) var0.length)];
        } catch (Throwable var2) {
            return "Witty comment unavailable :(";
        }
    }

    /**
     * Creates a crash report for the exception
     */
    public static CrashReport makeCrashReport(Throwable par0Throwable, String par1Str) {
        CrashReport var2;

        if (par0Throwable instanceof ReportedException) {
            var2 = ((ReportedException) par0Throwable).getCrashReport();
        } else {
            var2 = new CrashReport(par1Str, par0Throwable);
        }

        return var2;
    }
}
