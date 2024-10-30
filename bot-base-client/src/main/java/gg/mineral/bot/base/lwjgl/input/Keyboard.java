package gg.mineral.bot.base.lwjgl.input;

import gg.mineral.bot.api.controls.Key;
import gg.mineral.bot.api.controls.Key.Type;
import gg.mineral.bot.impl.config.BotGlobalConfig;
import lombok.val;

public class Keyboard extends gg.mineral.bot.impl.controls.Keyboard {

    boolean repeatEvents = false;

    @Override
    public boolean next() {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            return super.next();
        return org.lwjgl.input.Keyboard.next();
    }

    @Override
    public boolean isKeyDown(Key.Type type) {
        assert type != null;
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            return super.isKeyDown(type);
        return org.lwjgl.input.Keyboard.isKeyDown(type.getKeyCode());
    }

    public boolean isKeyDown(int keyCode) {
        Key.Type type = Key.Type.fromKeyCode(keyCode);
        if (type == null)
            return false;
        return isKeyDown(type);
    }

    @Override
    public Type getEventKeyType() {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            return super.getEventKeyType();
        return Key.Type.fromKeyCode(org.lwjgl.input.Keyboard.getEventKey());
    }

    @Override
    public int getEventKey() {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            return super.getEventKey();
        return org.lwjgl.input.Keyboard.getEventKey();
    }

    @Override
    public boolean getEventKeyState() {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            return super.getEventKeyState();
        return org.lwjgl.input.Keyboard.getEventKeyState();
    }

    public boolean isRepeatEvent() {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            return repeatEvents;
        return org.lwjgl.input.Keyboard.isRepeatEvent();
    }

    public void enableRepeatEvents(boolean enable) {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            repeatEvents = enable;
        else
            org.lwjgl.input.Keyboard.enableRepeatEvents(enable);
    }

    public boolean isCreated() {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl())
            return true;

        return org.lwjgl.input.Keyboard.isCreated();
    }

    public char getEventCharacter() {
        if (BotGlobalConfig.isHeadless() || BotGlobalConfig.isControl()) {
            val type = getEventKeyType();

            if (type == null)
                return '\0';

            return type.getCharacter();
        }

        return org.lwjgl.input.Keyboard.getEventCharacter();
    }
}
