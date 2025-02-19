package gg.mineral.bot.base.client.math.simulation;

import gg.mineral.bot.api.controls.Key;
import gg.mineral.bot.api.entity.living.player.ClientPlayer;
import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.event.EventHandler;
import gg.mineral.bot.base.lwjgl.input.Keyboard;
import gg.mineral.bot.base.lwjgl.input.Mouse;
import lombok.Getter;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Session;
import net.minecraft.world.World;
import org.eclipse.jdt.annotation.NonNull;

public class PlayerMotionSimulator extends EntityPlayerSP
        implements gg.mineral.bot.api.math.simulation.PlayerMotionSimulator {

    private int millis;
    private final double initialX;
    private final double initialY;
    private final double initialZ;
    private final double initialMotionX;
    private final double initialMotionY;
    private final double initialMotionZ;
    private final float initialYaw;
    private final float initialPitch;
    private final boolean initialOnGround;
    private final AxisAlignedBB initialBoundingBox;

    private static final EventHandler EMPTY_EVENT_HANDLER = new EventHandler() {

        @Override
        public <T extends Event> boolean callEvent(@NonNull T event) {
            return false;
        }

    };

    @Getter
    private final Keyboard keyboard = new Keyboard(EMPTY_EVENT_HANDLER);
    @Getter
    private final Mouse mouse = new Mouse(EMPTY_EVENT_HANDLER);

    public PlayerMotionSimulator(Minecraft mc, ClientPlayer player) {
        super(mc, (World) player.getWorld(), new Session(player.getUsername(), player.getUuid().toString(),
                "0",
                "legacy"), 0);

        val oldBoundingBox = player.getBoundingBox();
        assert oldBoundingBox != null;

        this.posX = player.getX();
        this.posY = player.getHeadY();
        this.posZ = player.getZ();
        this.motionX = player.getMotionX();
        this.motionY = player.getMotionY();
        this.motionZ = player.getMotionZ();
        this.rotationYaw = player.getYaw();
        this.rotationPitch = player.getPitch();
        this.onGround = player.isOnGround();
        this.boundingBox = AxisAlignedBB.getBoundingBox(oldBoundingBox.getMinX(), oldBoundingBox.getMinY(), oldBoundingBox.getMinZ(), oldBoundingBox.getMaxX(), oldBoundingBox.getMaxY(), oldBoundingBox.getMaxZ());

        this.initialX = player.getX();
        this.initialY = player.getHeadY();
        this.initialZ = player.getZ();
        this.initialMotionX = player.getMotionX();
        this.initialMotionY = player.getMotionY();
        this.initialMotionZ = player.getMotionZ();
        this.initialYaw = player.getYaw();
        this.initialPitch = player.getPitch();
        this.initialOnGround = player.isOnGround();
        this.initialBoundingBox = AxisAlignedBB.getBoundingBox(oldBoundingBox.getMinX(), oldBoundingBox.getMinY(), oldBoundingBox.getMinZ(), oldBoundingBox.getMaxX(), oldBoundingBox.getMaxY(), oldBoundingBox.getMaxZ());

        this.movementInput = new MovementInput() {
            @Override
            public void updatePlayerMoveState() {
                this.moveStrafe = 0.0F;
                this.moveForward = 0.0F;
                // TODO: fix this not getting called

                if (keyboard.isKeyDown(Key.Type.KEY_W))
                    ++this.moveForward;

                if (keyboard.isKeyDown(Key.Type.KEY_S))
                    --this.moveForward;

                if (keyboard.isKeyDown(Key.Type.KEY_A))
                    ++this.moveStrafe;

                if (keyboard.isKeyDown(Key.Type.KEY_D))
                    --this.moveStrafe;

                this.jump = keyboard.isKeyDown(Key.Type.KEY_SPACE);
                this.sneak = keyboard.isKeyDown(Key.Type.KEY_LSHIFT);

                if (this.sneak) {
                    this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
                    this.moveForward = (float) ((double) this.moveForward * 0.3D);
                }
            }
        };

    }

    @Override
    public void execute(long time) {
        for (; millis < time; millis++)
            this.runGameLoop();
    }

    public void runGameLoop() {

        if (millis % 50 == 0) {
            this.onUpdate();
        }

        this.mc.mouseHelper.mouseXYChange();
        float var132 = 0.5f * 0.6F + 0.2F;
        float var141 = var132 * var132 * var132 * 8.0F;
        float var15 = (float) this.mouse.getDX() * var141;
        float var16 = (float) this.mouse.getDY() * var141;
        byte var17 = 1;

        this.setAngles(var15, var16 * (float) var17);
    }

    @Override
    public void reset() {
        this.millis = 0;
        this.posX = this.initialX;
        this.posY = this.initialY;
        this.posZ = this.initialZ;
        this.motionX = this.initialMotionX;
        this.motionY = this.initialMotionY;
        this.motionZ = this.initialMotionZ;
        this.rotationYaw = this.initialYaw;
        this.rotationPitch = this.initialPitch;
        this.onGround = this.initialOnGround;
        this.boundingBox = AxisAlignedBB.getBoundingBox(this.initialBoundingBox.minX, this.initialBoundingBox.minY, this.initialBoundingBox.minZ, this.initialBoundingBox.maxX, this.initialBoundingBox.maxY, this.initialBoundingBox.maxZ);
    }
}
