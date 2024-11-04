package gg.mineral.bot.base.lwjgl.input;

import gg.mineral.bot.api.controls.MouseButton.Type;
import gg.mineral.bot.api.event.EventHandler;
import gg.mineral.bot.impl.config.BotGlobalConfig;

public class Mouse extends gg.mineral.bot.impl.controls.Mouse {

    public Mouse(EventHandler eventHandler) {
        super(eventHandler);
    }

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
}
