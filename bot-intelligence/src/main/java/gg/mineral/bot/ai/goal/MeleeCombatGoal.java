package gg.mineral.bot.ai.goal;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.bot.api.controls.Key;
import gg.mineral.bot.api.controls.MouseButton;
import gg.mineral.bot.api.entity.ClientEntity;
import gg.mineral.bot.api.entity.living.ClientLivingEntity;
import gg.mineral.bot.api.entity.living.player.ClientPlayer;

import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.event.entity.EntityHurtEvent;
import gg.mineral.bot.api.goal.Goal;

import gg.mineral.bot.api.instance.ClientInstance;
import gg.mineral.bot.api.inv.item.Item;
import gg.mineral.bot.api.world.block.Block;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

@Getter
public class MeleeCombatGoal extends Goal {
    @Nullable
    private ClientLivingEntity target;

    private final long meanDelay, deviation;
    private long lastBounceTime;

    private int lastTargetSwitchTick = 0, lastSprintResetTick = 0;

    @Override
    public boolean shouldExecute() {
        return true;
    }

    public MeleeCombatGoal(ClientInstance clientInstance) {
        super(clientInstance);
        this.meanDelay = (long) (1000 / clientInstance.getConfiguration().getAverageCps());
        this.deviation = Math.abs((long) (1000 / (clientInstance.getConfiguration().getAverageCps() + 1)) - meanDelay);
    }

    // TODO: move from inventory to hotbar
    private void switchToBestMeleeWeapon() {
        var bestMeleeWeaponSlot = 0;
        var damage = 0.0D;
        val fakePlayer = clientInstance.getFakePlayer();
        val inventory = fakePlayer.getInventory();

        if (inventory == null)
            return;
        for (int i = 0; i < 8; i++) {
            val itemStack = inventory.getItemStackAt(i);
            double attackDamage = itemStack != null ? itemStack.getAttackDamage() : 0;
            if (itemStack != null && attackDamage > damage) {
                bestMeleeWeaponSlot = i;
                damage = attackDamage;
            }
        }

        if (inventory.getHeldSlot() != bestMeleeWeaponSlot)
            pressKey(10, Key.Type.valueOf("KEY_" + (bestMeleeWeaponSlot + 1)));
    }

    private void findTarget() {
        val targetSearchRange = clientInstance.getConfiguration().getTargetSearchRange();

        val fakePlayer = clientInstance.getFakePlayer();
        val world = fakePlayer.getWorld();

        if (world == null)
            return;

        val entities = world.getEntities();

        if (clientInstance.getCurrentTick() - lastTargetSwitchTick < 20 && target != null && entities.contains(target)
                && isTargetValid(target, targetSearchRange))
            return;

        ClientPlayer closestTarget = null;
        var closestDistance = Double.MAX_VALUE;

        for (val entity : entities) {
            if (entity instanceof ClientPlayer living) {
                if (isTargetValid(living, targetSearchRange)) {
                    double distance = fakePlayer.distance3DTo(living);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestTarget = living;
                    }
                }
            }
        }

        if (closestTarget != this.target) {
            lastTargetSwitchTick = clientInstance.getCurrentTick();
            this.target = closestTarget;
        }
    }

    private boolean isTargetValid(ClientLivingEntity entity, float range) {
        val fakePlayer = clientInstance.getFakePlayer();
        return !clientInstance.getConfiguration().getFriendlyUUIDs().contains(entity.getUuid())
                && fakePlayer.distance3DTo(entity) <= range && entity instanceof ClientPlayer;
    }

    public float getRotationTarget(float current, float target, float turnSpeed, float accuracy, float erraticness) {
        val difference = angleDifference(current, target);

        if (abs(difference) > turnSpeed)
            return current + signum(difference) * turnSpeed;

        if (accuracy >= 1)
            return target;

        accuracy = max(0.01f, accuracy);

        val deviation = 3f / accuracy;
        val fakePlayer = clientInstance.getFakePlayer();
        val newTarget = (float) fakePlayer.getRandom().nextGaussian(target, deviation);
        val newDifference = angleDifference(current, newTarget);

        val erraticnessFactor = min(180 * erraticness, turnSpeed);

        if (abs(newDifference) > erraticnessFactor)
            return current + signum(newDifference) * erraticnessFactor;
        return newTarget;
    }

    private void aimAtTarget() {

        val target = this.target;

        if (target == null)
            return;

        val fakePlayer = clientInstance.getFakePlayer();
        val optimalAngles = computeOptimalYawAndPitch(fakePlayer, target);

        if (fakePlayer.distance3DTo(target) > 6.0f) {
            setMouseYaw(optimalAngles[1]);
            setMousePitch(optimalAngles[0]);
            return;
        }

        val yawDiff = abs(angleDifference(fakePlayer.getYaw(),
                optimalAngles[1]));
        val pitchDiff = abs(
                angleDifference(fakePlayer.getPitch(),
                        optimalAngles[0]));

        val distX = abs(fakePlayer.getX() - target.getX());
        val distZ = abs(fakePlayer.getZ() - target.getZ());

        val yawSpeed = calculateHorizontalAimSpeed(sqrt(distX * distX + distZ * distZ), yawDiff);
        val pitchSpeed = calculateVerticalAimSpeed(pitchDiff);

        val config = clientInstance.getConfiguration();

        setMouseYaw(getRotationTarget(fakePlayer.getYaw(), optimalAngles[1],
                (float) abs(yawSpeed
                        * config.getHorizontalAimSpeed() * 2d),
                config.getHorizontalAimAccuracy(),
                config.getHorizontalErraticness()));

        // Give it a higher chance of aiming down
        val verAccuracy = max(0.01f, config.getVerticalAimAccuracy());
        float deviation = verAccuracy >= 1 ? 0 : 3f / verAccuracy;

        setMousePitch(getRotationTarget(fakePlayer.getPitch(),
                optimalAngles[0] + deviation,
                (float) abs(pitchSpeed
                        * config.getVerticalAimSpeed() * 2d),
                verAccuracy,
                config.getVerticalErraticness()));
    }

    public double calculateHorizontalAimSpeed(double distHorizontal, double yawDiff) {
        val yawDiffFactor = 1;
        val distHorizontalFactor = 1;

        if (distHorizontal <= 0.2)
            distHorizontal = 0.21;

        val distanceFactorEquation = (10 / ((5 * distHorizontal) - 1)) + 2;
        val diffFactorEquation = yawDiff / 5;
        return (distHorizontalFactor * distanceFactorEquation)
                + (yawDiffFactor * diffFactorEquation);
    }

    private boolean isCollidingWithWall() {
        val fakePlayer = clientInstance.getFakePlayer();
        val world = fakePlayer.getWorld();
        if (world == null)
            return false;

        double posX = fakePlayer.getX();
        double posY = fakePlayer.getY() + fakePlayer.getEyeHeight();
        double posZ = fakePlayer.getZ();
        float yaw = fakePlayer.getYaw();
        float pitch = 0;

        double checkDistance = 1.0;

        // Check for collision in the direction the bot is facing
        double[] dir = vectorForRotation(pitch, yaw);
        double checkX = posX + dir[0] * checkDistance;
        double checkY = posY + dir[1] * checkDistance;
        double checkZ = posZ + dir[2] * checkDistance;

        val block = world.getBlockAt(checkX, checkY, checkZ);
        return block != null && block.getId() != Block.AIR;
    }

    private double[] getCollisionNormal() {
        val fakePlayer = clientInstance.getFakePlayer();
        val world = fakePlayer.getWorld();
        if (world == null)
            return null;

        double posX = fakePlayer.getX();
        double posY = fakePlayer.getY() + fakePlayer.getEyeHeight();

        // Sampling multiple points around the bot
        int sampleCount = 8;
        double checkDistance = 0.5;
        double[] normal = new double[] { 0, 0, 0 };

        for (int i = 0; i < sampleCount; i++) {
            float angle = fakePlayer.getYaw() + (float) (360.0 / sampleCount * i);
            double[] dir = vectorForRotation(0, angle);

            double checkX = posX + dir[0] * checkDistance;
            double checkY = posY;
            double checkZ = fakePlayer.getZ() + dir[2] * checkDistance;

            val block = world.getBlockAt(checkX, checkY, checkZ);
            if (block != null && block.getId() != Block.AIR) {
                // Add the opposite of the direction to the normal
                normal[0] += -dir[0];
                normal[1] += 0; // We ignore Y for horizontal normal
                normal[2] += -dir[2];
            }
        }

        double length = sqrt(normal[0] * normal[0] + normal[2] * normal[2]);
        if (length == 0)
            return null;

        // Normalize the normal vector
        normal[0] /= length;
        normal[2] /= length;

        return normal;
    }

    private void reflectOffWall() {
        val fakePlayer = clientInstance.getFakePlayer();
        double[] normal = getCollisionNormal();
        if (normal == null)
            return;

        // Normalize the normal vector (should already be normalized)
        double normX = normal[0];
        double normZ = normal[2];

        // Get direction vector
        float yaw = fakePlayer.getYaw();

        // Convert yaw to radians
        double yawRad = Math.toRadians(yaw);

        // Direction vector in x and z
        double dirX = -Math.sin(yawRad);
        double dirZ = Math.cos(yawRad);

        // Perform reflection R = V - 2(V ⋅ N)N
        double dot = dirX * normX + dirZ * normZ; // Only x and z components
        double reflectedX = dirX - 2 * dot * normX;
        double reflectedZ = dirZ - 2 * dot * normZ;

        float newYaw = (float) toDegrees(fastArcTan2(-reflectedX, reflectedZ));

        setMouseYaw(newYaw);

        this.lastBounceTime = timeMillis();
    }

    public double calculateVerticalAimSpeed(double pitchDiff) {
        val pitchDiffFactor = 1;
        val diffFactorEquation = 2 * pitchDiff / 5;
        return pitchDiffFactor * diffFactorEquation;
    }

    private long nextClick = 0;

    private void attackTarget() {
        val fakePlayer = clientInstance.getFakePlayer();
        nextClick = (long) (timeMillis() + fakePlayer.getRandom().nextGaussian(meanDelay, deviation));
        pressButton(25, MouseButton.Type.LEFT_CLICK);
    }

    @Setter
    private ResetType resetType = ResetType.OFFENSIVE, lastResetType = ResetType.OFFENSIVE;
    @Setter
    private byte strafeDirection = 0;

    private void strafe() {
        val target = this.target;

        if (target == null)
            return;

        val fakePlayer = clientInstance.getFakePlayer();
        val distance = fakePlayer.distance2DTo(target.getX(), target.getZ());
        if (!fakePlayer.isOnGround() || distance > 2.95 /*
                                                         * || timeMillis() - fakePlayer.getLastHitSelected()
                                                         * < 1000
                                                         */)
            return;

        strafeDirection = strafeDirection(target);
        if (strafeDirection == 1)
            pressKey(50, Key.Type.KEY_A);
        else if (strafeDirection == 2)
            pressKey(50, Key.Type.KEY_D);
    }

    private byte strafeDirection(ClientEntity target) {
        val fakePlayer = clientInstance.getFakePlayer();
        val toPlayer = new double[] { fakePlayer.getX() - target.getX(),
                fakePlayer.getY() - target.getY(),
                fakePlayer.getZ() - target.getZ() };
        val aimVector = vectorForRotation(target.getPitch(),
                target.getYaw());

        val crossProduct = crossProduct2D(toPlayer, aimVector);

        // If cross product is positive, player is to the right of the aim direction
        // If cross product is negative, player is to the left of the aim direction
        return crossProduct > 0 ? (byte) 2 : 1;
    }

    public float crossProduct2D(double[] vec, double[] other) {
        return (float) (vec[0] * other[2] - vec[2] * other[0]);
    }

    private void sprintReset() {

        val target = this.target;

        if (target == null)
            return;

        val fakePlayer = clientInstance.getFakePlayer();
        val meanX = (fakePlayer.getX() + target.getX()) / 2;
        val meanY = (fakePlayer.getY() + target.getY()) / 2;
        val meanZ = (fakePlayer.getZ() + target.getZ()) / 2;

        // Offensive if dealing more kb to the target
        val kb = getKB(fakePlayer, meanX, meanY, meanZ);
        val targetKB = getKB(target, meanX, meanY, meanZ);

        val dist = fakePlayer.distance3DTo(target);

        val inventory = fakePlayer.getInventory();
        val itemStack = inventory != null ? inventory.getHeldItemStack() : null;

        if (kb < targetKB)
            setResetType(
                    dist < 2 && fakePlayer.isOnGround()
                            ? ResetType.EXTRA_OFFENSIVE
                            : ResetType.OFFENSIVE);
        else if (lastResetType == ResetType.DEFENSIVE
                && itemStack != null && itemStack.getItem().getId() == Item.DIAMOND_SWORD)
            setResetType(ResetType.EXTRA_DEFENSIVE);
        else
            setResetType(ResetType.DEFENSIVE);

        val config = clientInstance.getConfiguration();

        Runnable runnable = () -> {
            switch (resetType) {
                case EXTRA_OFFENSIVE:
                    if (config.getSprintResetAccuracy() >= 1
                            || fakePlayer.getRandom().nextFloat() < config
                                    .getSprintResetAccuracy())
                        pressKey(150, Key.Type.KEY_S);
                case DEFENSIVE:
                case OFFENSIVE:
                    if (config.getSprintResetAccuracy() >= 1
                            || fakePlayer.getRandom().nextFloat() < config
                                    .getSprintResetAccuracy())
                        unpressKey(150, Key.Type.KEY_W);
                    break;
                case EXTRA_DEFENSIVE:
                    if (config.getSprintResetAccuracy() >= 1
                            || fakePlayer.getRandom().nextFloat() < config
                                    .getSprintResetAccuracy())
                        pressButton(75, MouseButton.Type.RIGHT_CLICK);
                    break;
            }
        };

        if (resetType == ResetType.OFFENSIVE || resetType == ResetType.EXTRA_OFFENSIVE) {
            runnable.run();
            return;
        }

        schedule(runnable, 350);
    }

    private double getKB(@NonNull ClientLivingEntity entity, double meanX, double meanY, double meanZ) {
        val motX = entity.getX() - entity.getLastX();
        val motY = entity.getY() - entity.getLastY();
        val motZ = entity.getZ() - entity.getLastZ();

        val newX = entity.getX() + motX;
        val newY = entity.getY() + motY;
        val newZ = entity.getZ() + motZ;

        val kbX = newX - meanX;
        val kbY = newY - meanY;
        val kbZ = newZ - meanZ;

        return sqrt(kbX * kbX + kbY * kbY + kbZ * kbZ);
    }

    static enum ResetType {
        EXTRA_OFFENSIVE, OFFENSIVE, DEFENSIVE, EXTRA_DEFENSIVE;
    }

    @Override
    public void onTick() {
        pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL);

        findTarget();
        switchToBestMeleeWeapon();
        aimAtTarget();
        strafe();

        if (this.target == null && timeMillis() - lastBounceTime > 1000)
            if (isCollidingWithWall())
                reflectOffWall();
    }

    @Override
    public boolean onEvent(Event event) {
        if (event instanceof EntityHurtEvent hurt)
            return onEntityHurt(hurt);

        return false;
    }

    public boolean onEntityHurt(EntityHurtEvent event) {
        if (clientInstance.getCurrentTick() - lastSprintResetTick < 9)
            return false;

        val entity = event.getAttackedEntity();

        val fakePlayer = clientInstance.getFakePlayer();
        if (entity == null || entity.getY() - fakePlayer.getY() > 1.5)
            return false;

        val target = this.target;

        if (target != null && entity.getUuid().equals(target.getUuid())) {
            sprintReset();
            lastSprintResetTick = clientInstance.getCurrentTick();
        }

        return false;
    }

    @Override
    public void onGameLoop() {
        if (timeMillis() >= nextClick)
            attackTarget();
    }
}
