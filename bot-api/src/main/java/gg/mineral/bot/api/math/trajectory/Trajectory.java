package gg.mineral.bot.api.math.trajectory;

import gg.mineral.bot.api.math.Positionable;
import gg.mineral.bot.api.math.optimization.RecursiveCalculation;
import gg.mineral.bot.api.world.ClientWorld;

public interface Trajectory
        extends RecursiveCalculation, Positionable {

    /**
     * Ticks the trajectory.
     *
     * @return the result of the tick
     */
    Result tick();

    /**
     * Computes the trajectory.
     *
     * @return the result of the computation
     */
    @Override
    default Result compute(int maxEval) {
        Result result = null;
        for (int iterations = 0; iterations < maxEval
                && result == Result.CONTINUE; iterations++)
            result = tick();
        return result;
    }

    /**
     * The x-coordinate of the trajectory.
     *
     * @return the x-coordinate
     */
    double getX();

    /**
     * The y-coordinate of the trajectory.
     *
     * @return the y-coordinate
     */
    double getY();

    /**
     * The z-coordinate of the trajectory.
     *
     * @return the z-coordinate
     */
    double getZ();

    /**
     * The collision function of the trajectory.
     *
     * @return the collision function
     */
    CollisionFunction getCollisionFunction();

    /**
     * The world of the trajectory.
     *
     * @return the world
     */
    ClientWorld getWorld();

    @FunctionalInterface
    interface CollisionFunction {
        boolean test(double x, double y, double z);
    }
}
