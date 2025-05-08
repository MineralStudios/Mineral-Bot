package gg.mineral.bot.api.configuration

import gg.mineral.bot.api.BotAPI
import gg.mineral.bot.api.entity.living.player.skin.Skins
import java.io.File
import java.util.*

data class BotConfiguration(
    var username: String = "MineralBot",
    var usernamePrefix: String = "",
    var usernameSuffix: String = "",
    var uuid: UUID = UUID.randomUUID(),
    var runDirectory: File = File("run"),
    var averageCps: Float = 10.0f,
    var cpsDeviation: Float = 1.0f,
    var targetSearchRange: Int = 32,
    var horizontalAimSpeed: Float = 0.5f,
    var verticalAimSpeed: Float = 0.5f,
    var horizontalAimAcceleration: Float = 1.015f,
    var verticalAimAcceleration: Float = 1.015f,
    var horizontalAimMaxAccel: Float = 1.5f,
    var verticalAimMaxAccel: Float = 1.5f,
    var horizontalAimAccuracy: Float = 0.5f,
    var verticalAimAccuracy: Float = 0.5f,
    var horizontalErraticness: Float = 0.5f,
    var verticalErraticness: Float = 0.5f,
    var reach: Float = 3.0f,
    var sprintResetAccuracy: Float = 0.5f,
    var hitSelectAccuracy: Float = 0.5f,
    var latency: Int = 0,
    var instantFlush: Boolean = false,
    var predictionHorizon: Int = 5,
    var latencyDeviation: Int = 0,
    var pearlCooldown: Int = 15,
    var skin: Skins = Skins.MINERAL_DEFAULT,
    var debug: Boolean = false,
    var disableEntityCollisions: Boolean = true,
    var friendlyUUIDs: MutableSet<UUID> = BotAPI.INSTANCE.collections().newSet(),
    var potAccuracy: Double = 0.5
) {
    val fullUsername: String
        get() = "$usernamePrefix$username$usernameSuffix"

    companion object {
        @JvmStatic
        @Deprecated(
            "Legacy builder is deprecated. Use named arguments instead.", level = DeprecationLevel.WARNING,
            replaceWith = ReplaceWith("BotConfiguration(args...)", "gg.mineral.bot.api.configuration.BotConfiguration")
        )
        fun builder() = BotConfigurationBuilder()

        class BotConfigurationBuilder {
            private var username: String = "MineralBot"
            private var usernamePrefix: String = ""
            private var usernameSuffix: String = ""
            private var uuid: UUID = UUID.randomUUID()
            private var runDirectory: File = File("run")
            private var averageCps: Float = 10.0f
            private var cpsDeviation: Float = 1.0f
            private var targetSearchRange: Int = 32
            private var horizontalAimSpeed: Float = 0.5f
            private var horizontalAimAcceleration: Float = 1.015f
            private var verticalAimAcceleration: Float = 1.015f
            private var horizontalAimMaxAccel: Float = 1.5f
            private var verticalAimMaxAccel: Float = 1.5f
            private var verticalAimSpeed: Float = 0.5f
            private var horizontalAimAccuracy: Float = 0.5f
            private var verticalAimAccuracy: Float = 0.5f
            private var horizontalErraticness: Float = 0.5f
            private var verticalErraticness: Float = 0.5f
            private var reach: Float = 3.0f
            private var sprintResetAccuracy: Float = 0.5f
            private var hitSelectAccuracy: Float = 0.5f
            private var latency: Int = 0
            private var instantFlush: Boolean = false
            private var predictionHorizon: Int = 5
            private var latencyDeviation: Int = 0
            private var pearlCooldown: Int = 15
            private var skin: Skins = Skins.MINERAL_DEFAULT
            private var debug: Boolean = false
            private var disableEntityCollisions: Boolean = true
            private var friendlyUUIDs: MutableSet<UUID> = BotAPI.INSTANCE.collections().newSet()
            private var potAccuracy: Double = 0.5

            fun username(username: String) = apply { this.username = username }
            fun usernamePrefix(usernamePrefix: String) = apply { this.usernamePrefix = usernamePrefix }
            fun usernameSuffix(usernameSuffix: String) = apply { this.usernameSuffix = usernameSuffix }
            fun uuid(uuid: UUID) = apply { this.uuid = uuid }
            fun runDirectory(runDirectory: File) = apply { this.runDirectory = runDirectory }
            fun averageCps(averageCps: Float) = apply { this.averageCps = averageCps }
            fun cpsDeviation(cpsDeviation: Float) = apply { this.cpsDeviation = cpsDeviation }
            fun targetSearchRange(targetSearchRange: Int) = apply { this.targetSearchRange = targetSearchRange }
            fun horizontalAimSpeed(horizontalAimSpeed: Float) = apply { this.horizontalAimSpeed = horizontalAimSpeed }
            fun verticalAimSpeed(verticalAimSpeed: Float) = apply { this.verticalAimSpeed = verticalAimSpeed }
            fun horizontalAimAcceleration(horizontalAimAcceleration: Float) =
                apply { this.horizontalAimAcceleration = horizontalAimAcceleration }

            fun verticalAimAcceleration(verticalAimAcceleration: Float) =
                apply { this.verticalAimAcceleration = verticalAimAcceleration }

            fun horizontalAimMaxAccel(horizontalAimMaxAccel: Float) =
                apply { this.horizontalAimMaxAccel = horizontalAimMaxAccel }

            fun verticalAimMaxAccel(verticalAimMaxAccel: Float) =
                apply { this.verticalAimMaxAccel = verticalAimMaxAccel }

            fun horizontalAimAccuracy(horizontalAimAccuracy: Float) =
                apply { this.horizontalAimAccuracy = horizontalAimAccuracy }

            fun verticalAimAccuracy(verticalAimAccuracy: Float) =
                apply { this.verticalAimAccuracy = verticalAimAccuracy }

            fun horizontalErraticness(horizontalErraticness: Float) =
                apply { this.horizontalErraticness = horizontalErraticness }

            fun verticalErraticness(verticalErraticness: Float) =
                apply { this.verticalErraticness = verticalErraticness }

            fun reach(reach: Float) = apply { this.reach = reach }
            fun sprintResetAccuracy(sprintResetAccuracy: Float) =
                apply { this.sprintResetAccuracy = sprintResetAccuracy }

            fun hitSelectAccuracy(hitSelectAccuracy: Float) = apply { this.hitSelectAccuracy = hitSelectAccuracy }
            fun latency(latency: Int) = apply { this.latency = latency }
            fun tcpNoDelay(tcpNoDelay: Boolean) = apply { this.instantFlush = tcpNoDelay }
            fun latencyDeviation(latencyDeviation: Int) = apply { this.latencyDeviation = latencyDeviation }
            fun pearlCooldown(pearlCooldown: Int) = apply { this.pearlCooldown = pearlCooldown }
            fun skin(skin: Skins) = apply { this.skin = skin }
            fun debug(debug: Boolean) = apply { this.debug = debug }
            fun friendlyUUIDs(friendlyUUIDs: MutableSet<UUID>) = apply { this.friendlyUUIDs = friendlyUUIDs }
            fun potAccuracy(potAccuracy: Double) = apply { this.potAccuracy = potAccuracy }
            fun predictionHorizon(predictionHorizon: Int) = apply { this.predictionHorizon = predictionHorizon }
            fun disableEntityCollisions(disableEntityCollisions: Boolean) =
                apply { this.disableEntityCollisions = disableEntityCollisions }

            fun build() = BotConfiguration(
                username,
                usernamePrefix,
                usernameSuffix,
                uuid,
                runDirectory,
                averageCps,
                cpsDeviation,
                targetSearchRange,
                horizontalAimSpeed,
                verticalAimSpeed,
                horizontalAimAcceleration,
                verticalAimAcceleration,
                horizontalAimMaxAccel,
                verticalAimMaxAccel,
                horizontalAimAccuracy,
                verticalAimAccuracy,
                horizontalErraticness,
                verticalErraticness,
                reach,
                sprintResetAccuracy,
                hitSelectAccuracy,
                latency,
                instantFlush,
                predictionHorizon,
                latencyDeviation,
                pearlCooldown,
                skin,
                debug,
                disableEntityCollisions,
                friendlyUUIDs,
                potAccuracy
            )
        }
    }
}
