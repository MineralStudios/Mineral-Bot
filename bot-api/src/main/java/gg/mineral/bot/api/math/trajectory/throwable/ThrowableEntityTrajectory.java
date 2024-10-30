package gg.mineral.bot.api.math.trajectory.throwable;

import gg.mineral.bot.api.math.optimization.RecursiveCalculation;
import gg.mineral.bot.api.math.trajectory.Trajectory;
import gg.mineral.bot.api.util.MathUtil;

public interface ThrowableEntityTrajectory extends Trajectory, MathUtil {

    /**
     * Initializes the trajectory.
     * 
     * @param args the arguments
     */
    default RecursiveCalculation<Trajectory.Result> initialize(Object... args) {
        assert args.length == 6;

        if (args[0] instanceof Double x && args[1] instanceof Double y && args[2] instanceof Double z
                && args[3] instanceof Float yaw && args[4] instanceof Float pitch
                && args[5] instanceof CollisionFunction collisionFunction)
            initialize(x, y, z, yaw, pitch, collisionFunction);
        else
            throw new IllegalArgumentException("Invalid arguments");

        return this;
    }

    /**
     * Initializes the trajectory.
     * 
     * @param x                 the x-coordinate
     * @param y                 the y-coordinate
     * @param z                 the z-coordinate
     * @param yaw               the yaw
     * @param pitch             the pitch
     * @param collisionFunction the collision function
     */
    void initialize(double x, double y, double z, float yaw, float pitch,
            CollisionFunction collisionFunction);

    /**
     * The pitch offset of the trajectory.
     * 
     * @return the pitch offset
     */
    float offset();

    /**
     * The power of the trajectory.
     * 
     * @return the power
     */
    float power();

    /**
     * The gravity of the trajectory.
     * 
     * @return the gravity
     */
    float gravity();

    /**
     * Shoots the trajectory.
     * 
     * @param motX  the x-motion
     * @param motY  the y-motion
     * @param motZ  the z-motion
     * @param power the power
     */
    void shoot(float motX, float motY, float motZ, float power);

    /**
     * The air time ticks of the trajectory.
     * 
     * @return the air time ticks
     */
    int getAirTimeTicks();
}
