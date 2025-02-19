package gg.mineral.bot.api.util

import gg.mineral.bot.api.entity.living.ClientLivingEntity
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.math.Positionable
import kotlin.math.atan
import kotlin.math.sign

interface MathUtil {
    fun angleDifference(angle1: Float, angle2: Float): Float {
        var difference = angle2 - angle1
        difference = (difference + 180) % 360
        if (difference < 0) difference += 360f
        return difference - 180
    }

    fun absAngleDifference(angle1: Float, angle2: Float): Float {
        return abs(angleDifference(angle1, angle2).toDouble()).toFloat()
    }

    fun fastArcTan(a: Double): Double {
        if (a < -1 || a > 1) return atan(a)
        return M_PI_4 * a - (a * (abs(a) - 1)
                * (0.2447 + 0.0663 * abs(a)))
    }

    fun fastArcTan2(y: Double, x: Double): Double {
        if (x == 0.0) {
            return if (y > 0.0) Math.PI / 2
            else if (y < 0.0) -Math.PI / 2
            else 0.0 // Undefined, return 0 or handle as needed
        }

        var angle: Double
        if (kotlin.math.abs(x) > kotlin.math.abs(y)) {
            // Use y/x if x has a greater magnitude
            val a = y / x
            angle = fastArcTan(a)

            // Adjust angle based on the quadrant
            if (x < 0.0) {
                if (y >= 0.0) angle += Math.PI
                else angle -= Math.PI
            }
        } else {
            // Use x/y if y has a greater magnitude
            val a = x / y
            angle = fastArcTan(a)

            // Adjust angle based on the sign of y
            angle = if (y > 0.0) Math.PI / 2 - angle
            else -Math.PI / 2 - angle
        }
        return angle
    }

    fun toDegrees(angle: Double): Double {
        return angle * 180.0 / Math.PI
    }

    fun toRadians(angle: Double): Double {
        return angle * Math.PI / 180.0
    }

    fun toRadians(angle: Float): Float {
        return angle * Math.PI.toFloat() / 180.0f
    }

    fun sqrt(x: Double): Double {
        return kotlin.math.sqrt(x)
    }

    fun sqrt(x: Float): Float {
        return kotlin.math.sqrt(x.toDouble()).toFloat()
    }

    fun computeOptimalYawAndPitch(
        player: ClientPlayer,
        entity: ClientPlayer
    ): FloatArray {
        val x = entity.x - player.x
        val y = (entity.y + entity.eyeHeight) - (player.y + player.eyeHeight) - 1.9
        val z = entity.z - player.z

        val newPitch = if (y != 0.0) -toDegrees(fastArcTan(y / sqrt(x * x + z * z))).toFloat() else 0f
        val newYaw = if (z < 0.0 && x < 0.0) (90.0 + toDegrees(fastArcTan(z / x))).toFloat()
        else if (z < 0.0 && x > 0.0) (-90.0 + toDegrees(fastArcTan(z / x))).toFloat()
        else toDegrees(-fastArcTan(x / z)).toFloat()

        return floatArrayOf(newPitch, newYaw)
    }

    fun computeOptimalYaw(entity: ClientLivingEntity, target: Positionable): Float {
        val x = target.x - entity.x
        val z = target.z - entity.z
        val newYaw = if (z < 0.0 && x < 0.0) (90.0 + toDegrees(fastArcTan(z / x))).toFloat()
        else if (z < 0.0 && x > 0.0) (-90.0 + toDegrees(fastArcTan(z / x))).toFloat()
        else toDegrees(-fastArcTan(x / z)).toFloat()

        return newYaw
    }

    fun min(a: Float, b: Float): Float {
        return kotlin.math.min(a.toDouble(), b.toDouble()).toFloat()
    }

    fun max(a: Float, b: Float): Float {
        return kotlin.math.max(a.toDouble(), b.toDouble()).toFloat()
    }

    fun min(a: Double, b: Double): Double {
        return kotlin.math.min(a, b)
    }

    fun max(a: Double, b: Double): Double {
        return kotlin.math.max(a, b)
    }

    fun min(a: Long, b: Long): Long {
        return kotlin.math.min(a.toDouble(), b.toDouble()).toLong()
    }

    fun max(a: Long, b: Long): Long {
        return kotlin.math.max(a.toDouble(), b.toDouble()).toLong()
    }

    fun abs(a: Double): Double {
        return kotlin.math.abs(a)
    }

    fun signum(a: Float): Float {
        return sign(a.toDouble()).toFloat()
    }

    fun tan(a: Double): Double {
        return kotlin.math.tan(a)
    }

    fun cos(a: Double): Double {
        return kotlin.math.cos(a)
    }

    fun sin(a: Double): Double {
        return kotlin.math.sin(a)
    }

    fun sin(a: Float): Float {
        return kotlin.math.sin(a.toDouble()).toFloat()
    }

    fun cos(a: Float): Float {
        return kotlin.math.cos(a.toDouble()).toFloat()
    }

    fun floor(a: Double): Int {
        return kotlin.math.floor(a).toInt()
    }

    fun hypot(a: Double, b: Double): Double {
        return kotlin.math.hypot(a, b)
    }

    fun vectorForRotation(pitch: Float, yaw: Float): DoubleArray {
        val f = cos(-yaw * 0.017453292f - Math.PI.toFloat())
        val f1 = sin(-yaw * 0.017453292f - Math.PI.toFloat())
        val f2 = -cos(-pitch * 0.017453292f)
        val f3 = sin(-pitch * 0.017453292f)
        return doubleArrayOf((f1 * f2).toDouble(), f3.toDouble(), (f * f2).toDouble())
    }

    /**
     * Combines two integers into a long.
     *
     * @param high The high 32 bits of the resulting long.
     * @param low  The low 32 bits of the resulting long.
     * @return A long containing the two input integers.
     */
    fun combineIntsToLong(high: Int, low: Int): Long {
        return ((high.toLong()) shl 32) or (low.toLong() and 0xFFFFFFFFL)
    }

    /**
     * Extracts the high 32 bits from a long as an int.
     *
     * @param value The long value to extract from.
     * @return The high 32 bits of the input long as an int.
     */
    fun highInt(value: Long): Int {
        return (value shr 32).toInt()
    }

    /**
     * Extracts the low 32 bits from a long as an int.
     *
     * @param value The long value to extract from.
     * @return The low 32 bits of the input long as an int.
     */
    fun lowInt(value: Long): Int {
        return value.toInt()
    }

    companion object {
        const val M_PI_4: Double = Math.PI * 0.25
    }
}
