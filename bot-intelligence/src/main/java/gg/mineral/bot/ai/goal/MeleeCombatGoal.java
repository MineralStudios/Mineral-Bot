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

import lombok.Getter;
import lombok.Setter;
import lombok.val;

@Getter
public class MeleeCombatGoal extends Goal {
    @Nullable
    private ClientLivingEntity target;

    private final long meanDelay, deviation;

    private int lastTargetSwitchTick = 0;

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
        return !fakePlayer.getFriendlyEntityUUIDs().contains(entity.getUuid())
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

    public double calculateVerticalAimSpeed(double pitchDiff) {
        val pitchDiffFactor = 1;
        val diffFactorEquation = 2 * pitchDiff / 5;
        return pitchDiffFactor * diffFactorEquation;
    }

    private long nextClick = 0;

    private void attackTarget() {
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

        val distance = fakePlayer.distance2DTo(target.getX(), target.getZ());
        if (!fakePlayer.isOnGround() || distance > 2.85 /*
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
    }

    @Override
    public boolean onEvent(Event event) {
        if (event instanceof EntityHurtEvent hurt)
            return onEntityHurt(hurt);

        return false;
    }

    public boolean onEntityHurt(EntityHurtEvent event) {
        val entity = event.getAttackedEntity();

        if (entity == null)
            return false;

        val target = this.target;

        if (target != null && entity.getUuid().equals(target.getUuid()))
            sprintReset();

        return false;
    }

    @Override
    public void onGameLoop() {
        if (timeMillis() >= nextClick)
            attackTarget();
    }
}
