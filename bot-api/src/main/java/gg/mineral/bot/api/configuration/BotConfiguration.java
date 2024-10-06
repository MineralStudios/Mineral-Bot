package gg.mineral.bot.api.configuration;

import java.io.File;
import java.util.UUID;

import gg.mineral.bot.api.entity.living.player.skin.Skins;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class BotConfiguration {
    @Builder.Default
    private String username = "MineralBot", usernamePrefix = "", usernameSuffix = "";
    @Builder.Default
    private UUID uuid = UUID.randomUUID();
    @Builder.Default
    private File runDirectory = new File("run");
    @Builder.Default
    private float averageCps = 10.0f, cpsDeviation = 1.0f;
    @Builder.Default
    private int targetSearchRange = 32;
    @Builder.Default
    float horizontalAimSpeed = 0.5F, verticalAimSpeed = 0.5F, horizontalAimAccuracy = 0.5F, verticalAimAccuracy = 0.5F,
            horizontalErraticness = 0.5F, verticalErraticness = 0.5F;
    @Builder.Default
    private float sprintResetAccuracy = 0.5f, hitSelectAccuracy = 0.5f;
    @Builder.Default
    private int latency = 0, latencyDeviation = 0;
    @Builder.Default
    private int pearlCooldown = 15;
    @Builder.Default
    private Skins skin = Skins.MINERAL_DEFAULT;

    public String getFullUsername() {
        return usernamePrefix + username + usernameSuffix;
    }
}
