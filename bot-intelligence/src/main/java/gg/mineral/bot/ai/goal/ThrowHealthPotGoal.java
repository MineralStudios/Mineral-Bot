package gg.mineral.bot.ai.goal;

import gg.mineral.bot.api.controls.Key;
import gg.mineral.bot.api.controls.MouseButton;
import gg.mineral.bot.api.entity.living.ClientLivingEntity;
import gg.mineral.bot.api.entity.living.player.ClientPlayer;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.goal.Goal;

import gg.mineral.bot.api.instance.ClientInstance;
import gg.mineral.bot.api.inv.item.Item;

import gg.mineral.bot.api.math.trajectory.Trajectory.CollisionFunction;
import gg.mineral.bot.api.math.trajectory.throwable.EnderPearlTrajectory;
import gg.mineral.bot.api.screen.type.ContainerScreen;
import gg.mineral.bot.api.util.MathUtil;
import gg.mineral.bot.api.world.ClientWorld;
import gg.mineral.bot.api.world.block.Block;
import lombok.RequiredArgsConstructor;
import lombok.val;

public class ThrowHealthPotGoal extends Goal {

    private int lastPearledTick = 0;
    private boolean inventoryOpen = false;

    public ThrowHealthPotGoal(ClientInstance clientInstance) {
        super(clientInstance);
    }

    @RequiredArgsConstructor
    private enum Type implements MathUtil {
        RETREAT() {

            @Override
            public boolean test(FakePlayer fakePlayer, ClientLivingEntity entity) {
                return false;
            }

        },
        SIDE() {

            @Override
            public boolean test(FakePlayer fakePlayer, ClientLivingEntity entity) {
                val distance = fakePlayer.distance3DTo(entity);
                return distance < 6.0 && !fakePlayer.isOnGround();
            }

        },
        FORWARD() {

            @Override
            public boolean test(FakePlayer fakePlayer, ClientLivingEntity entity) {
                val distance = fakePlayer.distance3DTo(entity);
                return distance > 16.0D;
            }

        };

        public abstract boolean test(FakePlayer fakePlayer, ClientLivingEntity entity);
    }

    private void switchToPearl() {
        var pearlSlot = -1;
        val inventory = fakePlayer.getInventory();

        if (inventory == null)
            return;

        // Search hotbar
        for (int i = 0; i < 36; i++) {
            val itemStack = inventory.getItemStackAt(i);
            if (itemStack == null)
                continue;
            val item = itemStack.getItem();
            if (item.getId() == Item.ENDER_PEARL) {
                pearlSlot = i;
                break;
            }
        }

        if (pearlSlot > 8) {
            if (!inventoryOpen) {
                inventoryOpen = true;
                pressKey(10, Key.Type.KEY_E);
                return;
            }

            val screen = clientInstance.getCurrentScreen();

            if (screen == null) {
                inventoryOpen = false;
                return;
            }
            // Move mouse
            val inventoryContainer = fakePlayer.getInventoryContainer();

            if (inventoryContainer == null) {
                inventoryOpen = false;
                return;
            }

            val slot = inventoryContainer.getSlot(inventory, pearlSlot);

            if (slot == null) {
                inventoryOpen = false;
                pressKey(10, Key.Type.KEY_ESCAPE);
                return;
            }

            if (screen instanceof ContainerScreen containerScreen) {
                val x = containerScreen.getSlotX(slot);
                val y = containerScreen.getSlotY(slot);

                val currX = getMouseX();
                val currY = getMouseY();

                if (currX != x || currY != y) {
                    setMouseX(x);
                    setMouseY(y);
                } else {
                    // Move to end slot
                    pressKey(10, Key.Type.KEY_8);
                }

            }

            return;
        }

        if (inventoryOpen) {
            inventoryOpen = false;
            pressKey(10, Key.Type.KEY_ESCAPE);
            return;
        }

        pressKey(10, Key.Type.valueOf("KEY_" + (pearlSlot + 1)));
    }

    @Override
    // TODO: ender pearl cooldown
    public boolean shouldExecute() {
        if (clientInstance.getCurrentTick() - lastPearledTick < 20 || !canSeeEnemy())
            return false;

        val inventory = fakePlayer.getInventory();

        if (inventory == null || !inventory.contains(Item.ENDER_PEARL))
            return false;

        val world = fakePlayer.getWorld();

        if (world == null)
            return false;

        for (val entity : world.getEntities())
            if (entity instanceof ClientPlayer enemy
                    && !fakePlayer.getFriendlyEntityUUIDs().contains(entity.getUuid()))
                for (val t : Type.values())
                    if (t.test(fakePlayer, enemy))
                        return true;

        return false;
    }

    private boolean canSeeEnemy() {
        val world = fakePlayer.getWorld();
        return world == null ? false
                : world.getEntities().stream()
                        .anyMatch(entity -> !fakePlayer.getFriendlyEntityUUIDs().contains(entity.getUuid()));
    }

    @Override
    public void onTick() {

        val world = fakePlayer.getWorld();

        if (world == null)
            return;

        val inventory = fakePlayer.getInventory();

        if (inventory == null)
            return;

        for (val entity : world.getEntities()) {
            if (entity instanceof ClientPlayer enemy
                    && !fakePlayer.getFriendlyEntityUUIDs().contains(entity.getUuid())) {
                for (val type : Type.values()) {
                    if (type.test(fakePlayer, enemy)) {
                        val targetX = enemy.getX();
                        val targetY = enemy.getY();
                        val targetZ = enemy.getZ();

                        final CollisionFunction collisionFunction = switch (type) {
                            case FORWARD -> (x1, y1, z1) -> floor(x1) == floor(targetX) && floor(y1) == floor(targetY)
                                    && floor(z1) == floor(targetZ);
                            case RETREAT -> (x1, y1, z1) -> hasHitBlock(world, x1, y1, z1);
                            case SIDE ->
                                (x1, y1, z1) -> hypot(x1 - targetX, z1 - targetZ) > 3 && hasHitBlock(world, x1, y1, z1);
                        };

                        val angles = switch (type) {
                            case FORWARD -> {
                                val x = targetX - fakePlayer.getX();
                                val z = targetZ - fakePlayer.getZ();

                                float yaw;
                                if (z < 0.0D && x < 0.0D)
                                    yaw = (float) (90.0D + toDegrees(fastArcTan(z / x)));
                                else if (z < 0.0D && x > 0.0D)
                                    yaw = (float) (-90.0D + toDegrees(fastArcTan(z / x)));
                                else
                                    yaw = (float) toDegrees(-fastArcTan(z / x));

                                val optimizer = world
                                        .univariateOptimizer(EnderPearlTrajectory.class,
                                                EnderPearlTrajectory::getAirTimeTicks,
                                                1000)
                                        .val(fakePlayer.getX(), fakePlayer.getY() + fakePlayer.getEyeHeight(),
                                                fakePlayer.getZ(), yaw)
                                        .var(-90, 90).val(collisionFunction).build();

                                float pitch = optimizer.minimize().floatValue();

                                yield new float[] { yaw, pitch };
                            }
                            case RETREAT -> {

                                val optimizer = world
                                        .bivariateOptimizer(EnderPearlTrajectory.class,
                                                trajectory -> trajectory.distance3DToSq(targetX, targetY, targetZ),
                                                1000)
                                        .val(fakePlayer.getX(), fakePlayer.getY() + fakePlayer.getEyeHeight(),
                                                fakePlayer.getZ())
                                        .var(-180, 180)
                                        .var(-90, 90).val(collisionFunction).build();

                                Number[] result = optimizer.maximize();
                                float yaw = result[0].floatValue();
                                float pitch = result[1].floatValue();

                                yield new float[] { yaw, pitch };
                            }
                            case SIDE -> {
                                val optimizer = world
                                        .bivariateOptimizer(EnderPearlTrajectory.class,
                                                trajectory -> abs(trajectory.distance2DToSq(targetX, targetZ)
                                                        - 3.5),
                                                1000)
                                        .val(fakePlayer.getX(), fakePlayer.getY() + fakePlayer.getEyeHeight(),
                                                fakePlayer.getZ())
                                        .var(-180, 180)
                                        .var(-90, 90).val(collisionFunction).build();

                                Number[] result = optimizer.minimize();
                                float yaw = result[0].floatValue();
                                float pitch = result[1].floatValue();

                                yield new float[] { yaw, pitch };
                            }
                            default -> new float[] { 0.0F, 0.0F };
                        };

                        setMouseYaw(angles[0]);
                        setMousePitch(angles[1]);

                        val itemStack = inventory.getHeldItemStack();

                        if (itemStack == null || itemStack.getItem().getId() != Item.ENDER_PEARL)
                            switchToPearl();
                        else
                            pressButton(10, MouseButton.Type.RIGHT_CLICK);
                        break;
                    }
                }
                break;
            }
        }

    }

    private boolean hasHitBlock(ClientWorld world, double x, double y, double z) {
        val xTile = floor(x);
        val yTile = floor(y);
        val zTile = floor(z);
        val block = world != null ? world.getBlockAt(xTile, yTile, zTile) : null;

        if (world != null && block != null && block.getId() != Block.AIR) {
            val collisionBoundingBox = block.getCollisionBoundingBox(world,
                    xTile,
                    yTile, zTile);

            if (collisionBoundingBox != null && collisionBoundingBox.isVecInside(x, y, z))
                return true;
        }

        return false;
    }

    @Override
    public boolean onEvent(Event event) {
        return false;
    }

    @Override
    public void onGameLoop() {
    }
}
