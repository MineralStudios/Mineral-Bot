package gg.mineral.bot.api.entity.living.player;

public interface ClientPlayerMP extends ClientPlayer {

    /**
     * Gets the player's last reported X position.
     * 
     * @return the player's last reported X position
     */
    double getLastReportedX();

    /**
     * Gets the player's last reported Y position.
     * 
     * @return the player's last reported Y position
     */
    double getLastReportedY();

    /**
     * Gets the player's last reported Z position.
     * 
     * @return the player's last reported Z position
     */
    double getLastReportedZ();

}
