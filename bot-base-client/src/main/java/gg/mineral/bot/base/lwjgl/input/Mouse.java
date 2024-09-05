package gg.mineral.bot.base.lwjgl.input;

import gg.mineral.bot.api.controls.MouseButton.Type;
import gg.mineral.bot.base.lwjgl.opengl.Display;
import gg.mineral.bot.impl.config.BotGlobalConfig;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;

@RequiredArgsConstructor
public class Mouse extends gg.mineral.bot.impl.controls.Mouse {

    private final Minecraft mc;

    @Override
    public boolean next() {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            return super.next();
        return org.lwjgl.input.Mouse.next();
    }

    @Override
    public boolean isButtonDown(int i) {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            return super.next();

        return org.lwjgl.input.Mouse.isButtonDown(i);
    }

    @Override
    public boolean isButtonDown(Type type) {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            return super.isButtonDown(type);

        return org.lwjgl.input.Mouse.isButtonDown(type.getKeyCode());
    }

    @Override
    public Type getEventButtonType() {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            return super.getEventButtonType();

        return Type.fromKeyCode(org.lwjgl.input.Mouse.getEventButton());
    }

    @Override
    public int getEventButton() {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            return super.getEventButton();

        return org.lwjgl.input.Mouse.getEventButton();
    }

    @Override
    public int getEventDWheel() {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            return super.getEventDWheel();

        return org.lwjgl.input.Mouse.getEventDWheel();
    }

    @Override
    public void setX(int x) {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            super.setX(x);
        else
            org.lwjgl.input.Mouse.setCursorPosition(x, getY());
    }

    @Override
    public void setY(int y) {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            super.setY(y);
        else
            org.lwjgl.input.Mouse.setCursorPosition(getX(), y);
    }

    public void setCursorPosition(int x, int y) {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl()) {
            super.setX(x);
            super.setY(y);
        } else
            org.lwjgl.input.Mouse.setCursorPosition(x, y);
    }

    @Override
    public int getDX() {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            return super.getDX();

        return org.lwjgl.input.Mouse.getDX();
    }

    @Override
    public int getDY() {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            return super.getDY();

        return org.lwjgl.input.Mouse.getDY();
    }

    public boolean isCreated() {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            return true;

        return org.lwjgl.input.Mouse.isCreated();
    }

    public boolean getEventButtonState() {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            return super.getEventButtonState();

        return org.lwjgl.input.Mouse.getEventButtonState();
    }

    public void setGrabbed(boolean grab) {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            return;

        org.lwjgl.input.Mouse.setGrabbed(grab);
    }

    @Override
    public void changeYaw(float dYaw) {
        if (this.mc.inGameHasFocus && Display.isActive()) {
            float defaultMouseSense = 0.5f;
            float sensitivity = defaultMouseSense * 0.6F + 0.2F;
            float deltaX = dYaw / (sensitivity * sensitivity * sensitivity * 8.0F);
            this.setDX((int) (deltaX / 0.15));
        }
    }

    @Override
    public void changePitch(float dPitch) {
        if (this.mc.inGameHasFocus && Display.isActive()) {
            float defaultMouseSense = 0.5f;
            float sensitivity = defaultMouseSense * 0.6F + 0.2F;
            float inverted = this.mc.gameSettings.invertMouse ? -1 : 1;
            float deltaY = -dPitch / (sensitivity * sensitivity * sensitivity * 8.0F * inverted);
            this.setDY((int) (deltaY / 0.15));
        }
    }

    @Override
    public void setYaw(float yaw) {
        float rotYaw = this.mc.thePlayer == null ? 0.0f : this.mc.thePlayer.rotationYaw;
        changeYaw(yaw - rotYaw);
    }

    @Override
    public void setPitch(float pitch) {
        float rotPitch = this.mc.thePlayer == null ? 0.0f : this.mc.thePlayer.rotationPitch;
        changePitch(pitch - rotPitch);
    }
}
