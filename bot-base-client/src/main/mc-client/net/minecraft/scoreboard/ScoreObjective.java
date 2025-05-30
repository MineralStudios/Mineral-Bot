package net.minecraft.scoreboard;

public class ScoreObjective {
    private final Scoreboard theScoreboard;
    private final String name;

    /** The ScoreObjectiveCriteria for this objetive */
    private final IScoreObjectiveCriteria objectiveCriteria;
    private String displayName;

    public ScoreObjective(Scoreboard p_i2307_1_, String p_i2307_2_, IScoreObjectiveCriteria p_i2307_3_) {
        this.theScoreboard = p_i2307_1_;
        this.name = p_i2307_2_;
        this.objectiveCriteria = p_i2307_3_;
        this.displayName = p_i2307_2_;
    }

    public Scoreboard getScoreboard() {
        return this.theScoreboard;
    }

    public String getName() {
        return this.name;
    }

    public IScoreObjectiveCriteria getCriteria() {
        return this.objectiveCriteria;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String p_96681_1_) {
        this.displayName = p_96681_1_;
        this.theScoreboard.func_96532_b(this);
    }
}
