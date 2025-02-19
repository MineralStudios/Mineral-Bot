package net.minecraft.scoreboard;

import gg.mineral.bot.impl.config.BotGlobalConfig;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.val;

import java.util.*;

public class Scoreboard {
    /**
     * Map of objective names to ScoreObjective objects.
     */
    private final Object2ObjectOpenHashMap<String, ScoreObjective> scoreObjectives = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<IScoreObjectiveCriteria, List<ScoreObjective>> scoreObjectiveCriterias = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<String, Object2ObjectOpenHashMap<ScoreObjective, Score>> field_96544_c = new Object2ObjectOpenHashMap<>();
    private final ScoreObjective[] field_96541_d = new ScoreObjective[3];

    /**
     * Map of teamnames to ScorePlayerTeam instances
     */
    private final Object2ObjectOpenHashMap<String, ScorePlayerTeam> teams = new Object2ObjectOpenHashMap<>();

    /**
     * Map of usernames to ScorePlayerTeam objects.
     */
    private final Object2ObjectOpenHashMap<String, ScorePlayerTeam> teamMemberships = new Object2ObjectOpenHashMap<>();

    /**
     * Returns a ScoreObjective for the objective name
     */
    public ScoreObjective getObjective(String p_96518_1_) {
        return this.scoreObjectives.get(p_96518_1_);
    }

    public ScoreObjective addScoreObjective(String p_96535_1_, IScoreObjectiveCriteria p_96535_2_) {
        var var3 = this.getObjective(p_96535_1_);

        if (var3 != null) {
            if (!BotGlobalConfig.headless)
                throw new IllegalArgumentException("An objective with the name \'" + p_96535_1_ + "\' already exists!");
            return var3;
        }

        var3 = new ScoreObjective(this, p_96535_1_, p_96535_2_);
        var var4 = this.scoreObjectiveCriterias.get(p_96535_2_);

        if (var4 == null) {
            var4 = new ArrayList<>();
            this.scoreObjectiveCriterias.put(p_96535_2_, var4);
        }

        var4.add(var3);
        this.scoreObjectives.put(p_96535_1_, var3);
        this.func_96522_a(var3);
        return var3;

    }

    public Collection<ScoreObjective> func_96520_a(IScoreObjectiveCriteria p_96520_1_) {
        var var2 = this.scoreObjectiveCriterias.get(p_96520_1_);
        return var2 == null ? new ArrayList<>() : new ArrayList<>(var2);
    }

    public Score func_96529_a(String p_96529_1_, ScoreObjective p_96529_2_) {
        var var3 = this.field_96544_c.get(p_96529_1_);

        if (var3 == null) {
            var3 = new Object2ObjectOpenHashMap<>();
            this.field_96544_c.put(p_96529_1_, var3);
        }

        Score var4 = var3.get(p_96529_2_);

        if (var4 == null) {
            var4 = new Score(this, p_96529_2_, p_96529_1_);
            var3.put(p_96529_2_, var4);
        }

        return var4;
    }

    public Collection<Score> func_96534_i(ScoreObjective p_96534_1_) {
        ArrayList<Score> var2 = new ArrayList<>();
        val var3 = this.field_96544_c.values().iterator();

        while (var3.hasNext()) {
            val var4 = var3.next();
            val var5 = var4.get(p_96534_1_);

            if (var5 != null) {
                var2.add(var5);
            }
        }

        Collections.sort(var2, Score.field_96658_a);
        return var2;
    }

    public Collection<ScoreObjective> getScoreObjectives() {
        return this.scoreObjectives.values();
    }

    public Collection<String> getObjectiveNames() {
        return this.field_96544_c.keySet();
    }

    public void func_96515_c(String p_96515_1_) {
        val var2 = this.field_96544_c.remove(p_96515_1_);

        if (var2 != null)
            this.func_96516_a(p_96515_1_);
    }

    public Collection<Score> func_96528_e() {
        val var1 = this.field_96544_c.values();
        val var2 = new ArrayList<Score>();
        val var3 = var1.iterator();

        while (var3.hasNext()) {
            val var4 = var3.next();
            var2.addAll(var4.values());
        }

        return var2;
    }

    public Map<ScoreObjective, Score> func_96510_d(String p_96510_1_) {
        var var2 = this.field_96544_c.get(p_96510_1_);

        if (var2 == null)
            var2 = new Object2ObjectOpenHashMap<>();

        return var2;
    }

    public void func_96519_k(ScoreObjective p_96519_1_) {
        this.scoreObjectives.remove(p_96519_1_.getName());

        for (int var2 = 0; var2 < 3; ++var2) {
            if (this.func_96539_a(var2) == p_96519_1_) {
                this.func_96530_a(var2, (ScoreObjective) null);
            }
        }

        val var5 = this.scoreObjectiveCriterias.get(p_96519_1_.getCriteria());

        if (var5 != null)
            var5.remove(p_96519_1_);

        val var3 = this.field_96544_c.values().iterator();

        while (var3.hasNext()) {
            val var4 = var3.next();
            var4.remove(p_96519_1_);
        }

        this.func_96533_c(p_96519_1_);
    }

    public void func_96530_a(int p_96530_1_, ScoreObjective p_96530_2_) {
        this.field_96541_d[p_96530_1_] = p_96530_2_;
    }

    public ScoreObjective func_96539_a(int p_96539_1_) {
        return this.field_96541_d[p_96539_1_];
    }

    /**
     * Retrieve the ScorePlayerTeam instance identified by the passed team name
     */
    public ScorePlayerTeam getTeam(String p_96508_1_) {
        return this.teams.get(p_96508_1_);
    }

    /**
     * Verifies that the given name doesn't already refer to an existing team,
     * creates it otherwise and broadcasts the
     * addition to all players
     */
    public ScorePlayerTeam createTeam(String p_96527_1_) {
        var var2 = this.getTeam(p_96527_1_);

        if (var2 != null) {
            if (!BotGlobalConfig.headless)
                throw new IllegalArgumentException("A team with the name \'" + p_96527_1_ + "\' already exists!");
            return var2;
        } else {
            var2 = new ScorePlayerTeam(this, p_96527_1_);
            this.teams.put(p_96527_1_, var2);
            this.func_96523_a(var2);
            return var2;
        }
    }

    /**
     * Removes the team from the scoreboard, updates all player memberships and
     * broadcasts the deletion to all players
     */
    public void removeTeam(ScorePlayerTeam p_96511_1_) {
        this.teams.remove(p_96511_1_.getRegisteredName());
        val var2 = p_96511_1_.getMembershipCollection().iterator();

        while (var2.hasNext()) {
            val var3 = var2.next();
            this.teamMemberships.remove(var3);
        }

        this.func_96513_c(p_96511_1_);
    }

    public boolean func_151392_a(String p_151392_1_, String p_151392_2_) {
        if (!this.teams.containsKey(p_151392_2_))
            return false;

        ScorePlayerTeam var3 = this.getTeam(p_151392_2_);

        if (this.getPlayersTeam(p_151392_1_) != null) {
            this.func_96524_g(p_151392_1_);
        }

        this.teamMemberships.put(p_151392_1_, var3);
        var3.getMembershipCollection().add(p_151392_1_);
        return true;
    }

    public boolean func_96524_g(String p_96524_1_) {
        ScorePlayerTeam var2 = this.getPlayersTeam(p_96524_1_);

        if (var2 != null) {
            this.removePlayerFromTeam(p_96524_1_, var2);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes the given username from the given ScorePlayerTeam. If the player is
     * not on the team then an
     * IllegalStateException is thrown.
     */
    public void removePlayerFromTeam(String p_96512_1_, ScorePlayerTeam p_96512_2_) {
        if (this.getPlayersTeam(p_96512_1_) != p_96512_2_) {
            if (!BotGlobalConfig.headless)
                throw new IllegalStateException(
                        "Player is either on another team or not on any team. Cannot remove from team \'"
                                + p_96512_2_.getRegisteredName() + "\'.");
        } else {
            this.teamMemberships.remove(p_96512_1_);
            p_96512_2_.getMembershipCollection().remove(p_96512_1_);
        }
    }

    /**
     * Retrieve all registered ScorePlayerTeam names
     */
    public Collection<String> getTeamNames() {
        return this.teams.keySet();
    }

    /**
     * Retrieve all registered ScorePlayerTeam instances
     */
    public Collection<ScorePlayerTeam> getTeams() {
        return this.teams.values();
    }

    /**
     * Gets the ScorePlayerTeam object for the given username.
     */
    public ScorePlayerTeam getPlayersTeam(String p_96509_1_) {
        return (ScorePlayerTeam) this.teamMemberships.get(p_96509_1_);
    }

    public void func_96522_a(ScoreObjective p_96522_1_) {
    }

    public void func_96532_b(ScoreObjective p_96532_1_) {
    }

    public void func_96533_c(ScoreObjective p_96533_1_) {
    }

    public void func_96536_a(Score p_96536_1_) {
    }

    public void func_96516_a(String p_96516_1_) {
    }

    public void func_96523_a(ScorePlayerTeam p_96523_1_) {
    }

    public void func_96538_b(ScorePlayerTeam p_96538_1_) {
    }

    public void func_96513_c(ScorePlayerTeam p_96513_1_) {
    }

    /**
     * Returns 'list' for 0, 'sidebar' for 1, 'belowName for 2, otherwise null.
     */
    public static String getObjectiveDisplaySlot(int p_96517_0_) {
        switch (p_96517_0_) {
            case 0:
                return "list";

            case 1:
                return "sidebar";

            case 2:
                return "belowName";

            default:
                return null;
        }
    }

    /**
     * Returns 0 for (case-insensitive) 'list', 1 for 'sidebar', 2 for 'belowName',
     * otherwise -1.
     */
    public static int getObjectiveDisplaySlotNumber(String p_96537_0_) {
        return p_96537_0_.equalsIgnoreCase("list") ? 0
                : (p_96537_0_.equalsIgnoreCase("sidebar") ? 1 : (p_96537_0_.equalsIgnoreCase("belowName") ? 2 : -1));
    }
}
