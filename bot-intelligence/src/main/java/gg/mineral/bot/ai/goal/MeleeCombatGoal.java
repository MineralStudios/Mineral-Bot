package gg.mineral.bot.ai.goal;

import java.util.Collection;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.bot.api.controls.Key;
import gg.mineral.bot.api.controls.MouseButton;
import gg.mineral.bot.api.entity.ClientEntity;
import gg.mineral.bot.api.entity.living.ClientLivingEntity;
import gg.mineral.bot.api.entity.living.player.ClientPlayer;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.event.entity.EntityHurtEvent;
import gg.mineral.bot.api.goal.Goal;
import gg.mineral.bot.api.inv.Inventory;
import gg.mineral.bot.api.inv.item.ItemStack;
import gg.mineral.bot.api.util.MathUtil;
import gg.mineral.bot.api.world.ClientWorld;
import lombok.Getter;

@Getter
public class MeleeCombatGoal extends Goal implements MathUtil {
    @Nullable
    private ClientLivingEntity target;
    int lastAttackerEntityId = -1;

    private final long meanDelay, deviation;

    @Override
    public boolean shouldExecute() {
        return true;
    }

    public MeleeCombatGoal(FakePlayer fakePlayer) {
        super(fakePlayer);
        this.meanDelay = (long) (1000 / fakePlayer.getConfiguration().getAverageCps());
        this.deviation = Math.abs((long) (1000 / (fakePlayer.getConfiguration().getAverageCps() + 1)) - meanDelay);
    }

    // TODO: move from inventory to hotbar
    private void switchToBestMeleeWeapon() {
        int bestMeleeWeaponSlot = 0;
        double damage = 0;
        Inventory inventory = fakePlayer.getInventory();

        if (inventory == null)
            return;
        for (int i = 0; i < 8; i++) {
            ItemStack itemStack = inventory.getItemStackAt(i);
            double attackDamage = itemStack != null ? itemStack.getAttackDamage() : 0;
            if (itemStack != null && attackDamage > damage) {
                bestMeleeWeaponSlot = i;
                damage = attackDamage;
            }
        }

        fakePlayer.getKeyboard().pressKey(10, Key.Type.valueOf("KEY_" + (bestMeleeWeaponSlot + 1)));
    }

    private void findTarget() {
        float targetSearchRange = fakePlayer.getConfiguration().getTargetSearchRange();

        ClientWorld world = fakePlayer.getWorld();

        if (world == null)
            return;

        Collection<ClientEntity> entities = world.getEntities();

        if (lastAttackerEntityId != -1) {
            for (ClientEntity entity : entities) {
                if (entity instanceof ClientLivingEntity living) {
                    if (entity.getEntityId() == lastAttackerEntityId && isTargetValid(living, targetSearchRange)) {
                        this.target = living;
                        return;
                    }
                }
            }
        }

        ClientLivingEntity closestTarget = null;
        double closestYawDistance = Double.MAX_VALUE;

        for (ClientEntity entity : entities) {
            if (entity instanceof ClientLivingEntity living) {
                if (isTargetValid(living, targetSearchRange)) {
                    double yawDistance = getYawDistance(living);
                    if (yawDistance < closestYawDistance) {
                        closestYawDistance = yawDistance;
                        closestTarget = living;
                    }
                }
            }
        }

        if (closestTarget != null)
            this.target = closestTarget;
    }

    private double getYawDistance(ClientLivingEntity livingEntity) {
        return angleDifference(computeOptimalYaw(fakePlayer, livingEntity), fakePlayer.getYaw());
    }

    private boolean isTargetValid(ClientLivingEntity entity, float range) {
        return !fakePlayer.getFriendlyEntityUUIDs().contains(entity.getUuid())
                && fakePlayer.distance3DTo(entity) <= range && entity instanceof ClientPlayer;
    }

    public float getRotationTarget(float current, float target, float turnSpeed, float accuracy, float erraticness) {
        float difference = angleDifference(current, target);

        if (abs(difference) > turnSpeed)
            return current + signum(difference) * turnSpeed;

        if (accuracy >= 1)
            return target;

        accuracy = max(0.01f, accuracy);

        float deviation = 3f / accuracy;
        float newTarget = (float) fakePlayer.getRandom().nextGaussian(target, deviation);
        float newDifference = angleDifference(current, newTarget);

        float erraticnessFactor = min(180 * erraticness, turnSpeed);

        if (abs(newDifference) > erraticnessFactor)
            return current + signum(newDifference) * erraticnessFactor;
        return newTarget;
    }

    private void aimAtTarget() {

        ClientLivingEntity target = this.target;

        if (target == null)
            return;

        float[] optimalAngles = computeOptimalYawAndPitch(fakePlayer, target);

        if (fakePlayer.distance3DTo(target) > 6.0f) {
            fakePlayer.getMouse().setYaw(optimalAngles[1]);
            fakePlayer.getMouse().setPitch(optimalAngles[0]);
            return;
        }

        double yawDiff = abs(angleDifference(fakePlayer.getYaw(),
                optimalAngles[1])), pitchDiff = abs(
                        angleDifference(fakePlayer.getPitch(),
                                optimalAngles[0]));

        double distX = abs(fakePlayer.getX() - target.getX()), distZ = abs(fakePlayer.getZ() - target.getZ());

        double yawSpeed = calculateHorizontalAimSpeed(sqrt(distX * distX + distZ * distZ), yawDiff),
                pitchSpeed = calculateVerticalAimSpeed(pitchDiff);

        fakePlayer.getMouse().setYaw(getRotationTarget(fakePlayer.getYaw(), optimalAngles[1],
                (float) abs(yawSpeed
                        * fakePlayer.getConfiguration().getHorizontalAimSpeed() * 2d),
                fakePlayer.getConfiguration().getHorizontalAimAccuracy(),
                fakePlayer.getConfiguration().getHorizontalErraticness()));

        // Give it a higher chance of aiming down
        float verAccuracy = fakePlayer.getConfiguration().getVerticalAimAccuracy();
        verAccuracy = max(0.01f, verAccuracy);
        float deviation = verAccuracy >= 1 ? 0 : 3f / verAccuracy;

        fakePlayer.getMouse().setPitch(getRotationTarget(fakePlayer.getPitch(),
                optimalAngles[0] + deviation,
                (float) abs(pitchSpeed
                        * fakePlayer.getConfiguration().getVerticalAimSpeed() * 2d),
                verAccuracy,
                fakePlayer.getConfiguration().getVerticalErraticness()));
    }

    public double calculateHorizontalAimSpeed(double distHorizontal, double yawDiff) {
        double yawDiffFactor = 1;
        double distHorizontalFactor = 1;

        if (distHorizontal <= 0.2)
            distHorizontal = 0.21;

        double distanceFactorEquation = (10 / ((5 * distHorizontal) - 1)) + 2;
        double diffFactorEquation = yawDiff / 5;
        return (distHorizontalFactor * distanceFactorEquation)
                + (yawDiffFactor * diffFactorEquation);
    }

    public double calculateVerticalAimSpeed(double pitchDiff) {
        double pitchDiffFactor = 1;
        double diffFactorEquation = 2 * pitchDiff / 5;
        return pitchDiffFactor * diffFactorEquation;
    }

    private long nextClick = 0;

    private void attackTarget() {
        nextClick = (long) (fakePlayer.timeMillis() + fakePlayer.getRandom().nextGaussian(meanDelay, deviation));
        fakePlayer.getMouse().pressButton(MouseButton.Type.LEFT_CLICK, 25);
    }

    // TODO: better sprint resetting (block hit and s tap, hold keys until)
    private void sprintReset() {
        if (fakePlayer.getConfiguration().getSprintResetAccuracy() >= 1
                || fakePlayer.getRandom().nextFloat() < fakePlayer.getConfiguration().getSprintResetAccuracy())
            fakePlayer.getKeyboard().unpressKey(150, Key.Type.KEY_W);
    }

    @Override
    public void onTick() {
        fakePlayer.getKeyboard().pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL);

        findTarget();
        switchToBestMeleeWeapon();
        aimAtTarget();
    }

    @Override
    public boolean onEvent(Event event) {
        if (event instanceof EntityHurtEvent hurt)
            return onEntityHurt(hurt);

        return false;
    }

    public float angleMultiplierTo(@NonNull final ClientLivingEntity target) {
        return 1 - (angleDifference(target.getYaw(),
                computeOptimalYaw(fakePlayer, target))
                / 180.0f);
    }

    public boolean onEntityHurt(EntityHurtEvent event) {
        ClientLivingEntity target = this.target;

        if (target != null && event.getAttackedEntity().getUuid().equals(target.getUuid()))
            sprintReset();

        ClientWorld world = fakePlayer.getWorld();

        if (world == null || event.getAttackedEntity().getUuid().equals(fakePlayer.getUuid()))
            return false;
        for (ClientEntity e : world.getEntities()) {
            if (e instanceof ClientLivingEntity living) {

                if (e.getEntityId() == fakePlayer.getEntityId()
                        || fakePlayer.getFriendlyEntityUUIDs().contains(e.getUuid()))
                    continue;

                if (lastAttackerEntityId == -1) {
                    lastAttackerEntityId = e.getEntityId();
                    continue;
                }

                ClientEntity lastAttacker = world.getEntityByID(lastAttackerEntityId);

                if (lastAttacker == null)
                    continue;

                if (lastAttacker instanceof ClientLivingEntity lastAttackerLiving) {

                    double distance3DTo = fakePlayer.distance3DTo(e);
                    double distance3DToLastAttacker = fakePlayer.distance3DTo(lastAttacker);

                    if (distance3DTo < 3)
                        distance3DTo *= angleMultiplierTo(living);

                    if (distance3DToLastAttacker < 3)
                        distance3DToLastAttacker *= angleMultiplierTo(lastAttackerLiving);

                    if (distance3DTo < distance3DToLastAttacker)
                        lastAttackerEntityId = e.getEntityId();
                }
            }
        }
        return false;
    }

    @Override
    public void onGameLoop() {
        if (fakePlayer.timeMillis() >= nextClick)
            attackTarget();
    }
}
