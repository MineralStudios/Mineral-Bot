package gg.mineral.bot.api.math

import kotlin.math.sqrt

@JvmDefaultWithCompatibility
interface Positionable {
    /**
     * Gets the X position.
     *
     * @return the X position
     */
    val x: Double

    /**
     * Gets the Y position.
     *
     * @return the Y position
     */
    val y: Double

    /**
     * Gets the Z position.
     *
     * @return the Z position
     */
    val z: Double

    fun distance3DTo(entityIn: Positionable): Double {
        return sqrt(distance3DToSq(entityIn))
    }
    
    fun distance3DToSq(entityIn: Positionable): Double {
        val dX = this.x - entityIn.x
        val dY = this.y - entityIn.y
        val dZ = this.z - entityIn.z
        return dX * dX + dY * dY + dZ * dZ
    }

    fun distance3DToSq(x: Double, y: Double, z: Double): Double {
        val dX = this.x - x
        val dY = this.y - y
        val dZ = this.z - z
        return dX * dX + dY * dY + dZ * dZ
    }

    fun distance2DToSq(x: Double, z: Double): Double {
        val dX = this.x - x
        val dZ = this.z - z
        return dX * dX + dZ * dZ
    }

    fun distance2DTo(x: Double, z: Double): Double {
        return sqrt(distance2DToSq(x, z))
    }
}
