package gg.mineral.bot.api.math

interface BoundingBox {
    /**
     * Gets the minimum x value of the bounding box.
     *
     * @return the minimum x value of the bounding box
     */
    /**
     * Sets the minimum x value of the bounding box.
     *
     * @param minX
     * the minimum x value of the bounding box
     */
    var minX: Double

    /**
     * Gets the minimum y value of the bounding box.
     *
     * @return the minimum y value of the bounding box
     */
    /**
     * Sets the minimum y value of the bounding box.
     *
     * @param minY
     * the minimum y value of the bounding box
     */
    var minY: Double

    /**
     * Gets the minimum z value of the bounding box.
     *
     * @return the minimum z value of the bounding box
     */
    /**
     * Sets the minimum z value of the bounding box.
     *
     * @param minZ
     * the minimum z value of the bounding box
     */
    var minZ: Double

    /**
     * Gets the maximum x value of the bounding box.
     *
     * @return the maximum x value of the bounding box
     */
    /**
     * Sets the maximum x value of the bounding box.
     *
     * @param maxX
     * the maximum x value of the bounding box
     */
    var maxX: Double

    /**
     * Gets the maximum y value of the bounding box.
     *
     * @return the maximum y value of the bounding box
     */
    /**
     * Sets the maximum y value of the bounding box.
     *
     * @param maxY
     * the maximum y value of the bounding box
     */
    var maxY: Double

    /**
     * Gets the maximum z value of the bounding box.
     *
     * @return the maximum z value of the bounding box
     */
    /**
     * Sets the maximum z value of the bounding box.
     *
     * @param maxZ
     * the maximum z value of the bounding box
     */
    var maxZ: Double

    /**
     * Checks if the specified vector is inside the bounding box.
     *
     * @param x
     * the x-coordinate of the vector
     * @param y
     * the y-coordinate of the vector
     * @param z
     * the z-coordinate of the vector
     * @return `true` if the vector is inside the bounding box, otherwise
     * `false`
     */
    fun isVecInside(x: Double, y: Double, z: Double): Boolean {
        return x >= minX && x < maxX && y >= minY && y < maxY && z >= minZ && z < maxZ
    }
}
