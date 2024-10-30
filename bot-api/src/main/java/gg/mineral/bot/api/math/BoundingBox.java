package gg.mineral.bot.api.math;

public interface BoundingBox {

    /**
     * Sets the minimum x value of the bounding box.
     * 
     * @param minX
     *             the minimum x value of the bounding box
     */
    void setMinX(double minX);

    /**
     * Sets the minimum y value of the bounding box.
     * 
     * @param minY
     *             the minimum y value of the bounding box
     */
    void setMinY(double minY);

    /**
     * Sets the minimum z value of the bounding box.
     * 
     * @param minZ
     *             the minimum z value of the bounding box
     */
    void setMinZ(double minZ);

    /**
     * Sets the maximum x value of the bounding box.
     * 
     * @param maxX
     *             the maximum x value of the bounding box
     */
    void setMaxX(double maxX);

    /**
     * Sets the maximum y value of the bounding box.
     * 
     * @param maxY
     *             the maximum y value of the bounding box
     */
    void setMaxY(double maxY);

    /**
     * Sets the maximum z value of the bounding box.
     * 
     * @param maxZ
     *             the maximum z value of the bounding box
     */
    void setMaxZ(double maxZ);

    /**
     * Gets the minimum x value of the bounding box.
     * 
     * @return the minimum x value of the bounding box
     */
    double getMinX();

    /**
     * Gets the minimum y value of the bounding box.
     * 
     * @return the minimum y value of the bounding box
     */
    double getMinY();

    /**
     * Gets the minimum z value of the bounding box.
     * 
     * @return the minimum z value of the bounding box
     */
    double getMinZ();

    /**
     * Gets the maximum x value of the bounding box.
     * 
     * @return the maximum x value of the bounding box
     */
    double getMaxX();

    /**
     * Gets the maximum y value of the bounding box.
     * 
     * @return the maximum y value of the bounding box
     */
    double getMaxY();

    /**
     * Gets the maximum z value of the bounding box.
     * 
     * @return the maximum z value of the bounding box
     */
    double getMaxZ();

    /**
     * Checks if the specified vector is inside the bounding box.
     * 
     * @param x
     *          the x-coordinate of the vector
     * @param y
     *          the y-coordinate of the vector
     * @param z
     *          the z-coordinate of the vector
     * @return {@code true} if the vector is inside the bounding box, otherwise
     *         {@code false}
     */
    default boolean isVecInside(double x, double y, double z) {
        return x >= getMinX() && x < getMaxX() && y >= getMinY() && y < getMaxY() && z >= getMinZ() && z < getMaxZ();
    }
}
