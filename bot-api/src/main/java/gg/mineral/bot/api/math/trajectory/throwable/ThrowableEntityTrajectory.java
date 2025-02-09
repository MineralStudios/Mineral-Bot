package gg.mineral.bot.api.math.trajectory.throwable;

import gg.mineral.bot.api.math.optimization.RecursiveCalculation;
import gg.mineral.bot.api.math.trajectory.Trajectory;
import gg.mineral.bot.api.util.MathUtil;
import gg.mineral.bot.api.world.ClientWorld;
import gg.mineral.bot.api.world.block.Block;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public abstract class ThrowableEntityTrajectory implements MathUtil, Trajectory {
    @Getter
    private final ClientWorld world;
    @Getter
    private int airTimeTicks = 0;
    private float motX, motY, motZ;
    @Getter
    private double x, y, z;
    @Getter
    private Result result = Result.CONTINUE;
    @Getter
    private CollisionFunction collisionFunction;

    /**
     * Initializes the trajectory.
     *
     * @param args the arguments
     */
    public RecursiveCalculation initialize(Object... args) {
        assert args.length == 6;

        if (args[0] instanceof Double x && args[1] instanceof Double y && args[2] instanceof Double z
                && args[3] instanceof Float yaw && args[4] instanceof Float pitch
                && args[5] instanceof CollisionFunction collisionFunction)
            initialize(x, y, z, yaw, pitch, collisionFunction);
        else
            throw new IllegalArgumentException("Invalid arguments");

        return this;
    }

    public void shoot(float motX, float motY, float motZ, float power) {
        val magnitude = sqrt(motX * motX + motY * motY + motZ * motZ);

        motX /= magnitude;
        motY /= magnitude;
        motZ /= magnitude;
        motX *= power;
        motY *= power;
        motZ *= power;
        this.motX = motX;
        this.motY = motY;
        this.motZ = motZ;
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
                    CollisionFunction collisionFunction) {
        assert world != null;
        this.airTimeTicks = 0;
        this.result = Result.CONTINUE;
        this.collisionFunction = collisionFunction;
        this.x = x;
        this.y = y;
        this.z = z;

        val yawRadians = toRadians(yaw);
        val pitchRadians = toRadians(pitch);

        val sinYawRadians = sin(yawRadians);
        val cosYawRadians = cos(yawRadians);

        this.x -= cosYawRadians * 0.16F;
        this.y -= 0.10000000149011612D;
        this.z -= sinYawRadians * 0.16F;
        val multiplier = 0.4F;

        val cosPitchRadians = cos(pitchRadians);
        this.motX = -sinYawRadians * cosPitchRadians * multiplier;
        this.motZ = cosYawRadians * cosPitchRadians * multiplier;
        val offsetPitchRadians = toRadians(pitch + +this.offset());
        this.motY = -sin(offsetPitchRadians) * multiplier;
        this.shoot(this.motX, this.motY, this.motZ, this.power());
    }

    /**
     * The pitch offset of the trajectory.
     *
     * @return the pitch offset
     */
    public abstract float offset();

    /**
     * The power of the trajectory.
     *
     * @return the power
     */
    public abstract float power();

    /**
     * The gravity of the trajectory.
     *
     * @return the gravity
     */
    public abstract float gravity();


    @Override
    public Result tick() {
        int xTile = floor(x);
        int yTile = floor(y);
        int zTile = floor(z);

        val nextLocX = this.x + this.motX;
        val nextLocY = this.y + this.motY;
        val nextLocZ = this.z + this.motZ;
        if (collisionFunction.test(this.x, this.y, this.z)
                || collisionFunction.test(nextLocX, nextLocY, nextLocZ))
            return Result.VALID;

        val block = world != null ? world.getBlockAt(xTile, yTile, zTile) : null;

        if (world != null && block != null && block.getId() != Block.AIR) {
            val collisionBoundingBox = block.getCollisionBoundingBox(world,
                    xTile,
                    yTile, zTile);

            if (collisionBoundingBox != null && collisionBoundingBox.isVecInside(this.x, this.y, this.z)) {
                airTimeTicks = Integer.MAX_VALUE;
                return Result.INVALID;
            }
        }

        ++airTimeTicks;

        this.x += this.motX;
        this.y += this.motY;
        this.z += this.motZ;

        val drag = 0.99F;

        this.motX *= drag;
        this.motY *= drag;
        this.motZ *= drag;
        this.motY -= gravity();

        return Result.CONTINUE;
    }
}
