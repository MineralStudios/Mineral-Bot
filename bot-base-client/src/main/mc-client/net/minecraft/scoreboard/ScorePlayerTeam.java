package net.minecraft.scoreboard;

import java.util.Collection;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class ScorePlayerTeam extends Team {
    private final Scoreboard theScoreboard;
    private final String field_96675_b;

    /** A set of all team member usernames. */
    private final Set<String> membershipSet = new ObjectOpenHashSet<>();
    private String teamNameSPT;
    private String namePrefixSPT = "";
    private String colorSuffix = "";
    private boolean allowFriendlyFire = true;
    private boolean canSeeFriendlyInvisibles = true;

    public ScorePlayerTeam(Scoreboard p_i2308_1_, String p_i2308_2_) {
        this.theScoreboard = p_i2308_1_;
        this.field_96675_b = p_i2308_2_;
        this.teamNameSPT = p_i2308_2_;
    }

    /**
     * Retrieve the name by which this team is registered in the scoreboard
     */
    public String getRegisteredName() {
        return this.field_96675_b;
    }

    public String func_96669_c() {
        return this.teamNameSPT;
    }

    public void setTeamName(String p_96664_1_) {
        if (p_96664_1_ == null) {
            throw new IllegalArgumentException("Name cannot be null");
        } else {
            this.teamNameSPT = p_96664_1_;
            this.theScoreboard.func_96538_b(this);
        }
    }

    public Collection<String> getMembershipCollection() {
        return this.membershipSet;
    }

    /**
     * Returns the color prefix for the player's team name
     */
    public String getColorPrefix() {
        return this.namePrefixSPT;
    }

    public void setNamePrefix(String p_96666_1_) {
        if (p_96666_1_ == null)
            throw new IllegalArgumentException("Prefix cannot be null");

        this.namePrefixSPT = p_96666_1_;
        this.theScoreboard.func_96538_b(this);
    }

    /**
     * Returns the color suffix for the player's team name
     */
    public String getColorSuffix() {
        return this.colorSuffix;
    }

    public void setNameSuffix(String p_96662_1_) {
        if (p_96662_1_ == null)
            throw new IllegalArgumentException("Suffix cannot be null");

        this.colorSuffix = p_96662_1_;
        this.theScoreboard.func_96538_b(this);
    }

    public String func_142053_d(String p_142053_1_) {
        return this.getColorPrefix() + p_142053_1_ + this.getColorSuffix();
    }

    /**
     * Returns the player name including the color prefixes and suffixes
     */
    public static String formatPlayerName(Team p_96667_0_, String p_96667_1_) {
        return p_96667_0_ == null ? p_96667_1_ : p_96667_0_.func_142053_d(p_96667_1_);
    }

    public boolean getAllowFriendlyFire() {
        return this.allowFriendlyFire;
    }

    public void setAllowFriendlyFire(boolean p_96660_1_) {
        this.allowFriendlyFire = p_96660_1_;
        this.theScoreboard.func_96538_b(this);
    }

    public boolean func_98297_h() {
        return this.canSeeFriendlyInvisibles;
    }

    public void setSeeFriendlyInvisiblesEnabled(boolean p_98300_1_) {
        this.canSeeFriendlyInvisibles = p_98300_1_;
        this.theScoreboard.func_96538_b(this);
    }

    public int func_98299_i() {
        int var1 = 0;

        if (this.getAllowFriendlyFire()) {
            var1 |= 1;
        }

        if (this.func_98297_h()) {
            var1 |= 2;
        }

        return var1;
    }

    public void func_98298_a(int p_98298_1_) {
        this.setAllowFriendlyFire((p_98298_1_ & 1) > 0);
        this.setSeeFriendlyInvisiblesEnabled((p_98298_1_ & 2) > 0);
    }
}
