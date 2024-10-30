package net.minecraft.client.settings;

import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class KeyBinding implements Comparable<KeyBinding> {

    private static final Set<String> keybindSet = new ObjectOpenHashSet<>();
    @Getter
    private final String keyDescription;
    private final int keyCodeDefault;
    private final String keyCategory;
    @Setter
    @Getter
    private int keyCode;

    /** because _303 wanted me to call it that(Caironater) */
    private boolean pressed;
    private int presses;

    public static void onTick(Minecraft mc, int p_74507_0_) {
        if (p_74507_0_ != 0) {
            KeyBinding var1 = mc.getKeyBindHash().get(p_74507_0_);

            if (var1 != null)
                ++var1.presses;
        }
    }

    public static void setKeyBindState(Minecraft mc, int p_74510_0_, boolean p_74510_1_) {
        if (p_74510_0_ != 0) {
            KeyBinding var2 = mc.getKeyBindHash().get(p_74510_0_);

            if (var2 != null)
                var2.pressed = p_74510_1_;
        }
    }

    public static void unPressAllKeys(Minecraft mc) {
        val var0 = mc.getKeybindArray().iterator();

        while (var0.hasNext()) {
            KeyBinding var1 = (KeyBinding) var0.next();
            var1.unpressKey();
        }
    }

    public static void resetKeyBindingArrayAndHash(Minecraft mc) {
        mc.getKeyBindHash().clear();
        val var0 = mc.getKeybindArray().iterator();

        while (var0.hasNext()) {
            KeyBinding var1 = (KeyBinding) var0.next();
            mc.getKeyBindHash().put(var1.keyCode, var1);
        }
    }

    public static Set<String> func_151467_c() {
        return keybindSet;
    }

    public KeyBinding(Minecraft mc, String p_i45001_1_, int p_i45001_2_, String p_i45001_3_) {
        this.keyDescription = p_i45001_1_;
        this.keyCode = p_i45001_2_;
        this.keyCodeDefault = p_i45001_2_;
        this.keyCategory = p_i45001_3_;
        mc.getKeybindArray().add(this);
        mc.getKeyBindHash().put(p_i45001_2_, this);
        keybindSet.add(p_i45001_3_);
    }

    public boolean getIsKeyPressed() {
        return this.pressed;
    }

    public String getKeyCategory() {
        return this.keyCategory;
    }

    public boolean isPressed() {
        if (this.presses == 0) {
            return false;
        } else {
            --this.presses;
            return true;
        }
    }

    private void unpressKey() {
        this.presses = 0;
        this.pressed = false;
    }

    public int getKeyCodeDefault() {
        return this.keyCodeDefault;
    }

    public int compareTo(KeyBinding p_compareTo_1_) {
        int var2 = I18n.format(this.keyCategory, new Object[0])
                .compareTo(I18n.format(p_compareTo_1_.keyCategory, new Object[0]));

        if (var2 == 0)
            var2 = I18n.format(this.keyDescription, new Object[0])
                    .compareTo(I18n.format(p_compareTo_1_.keyDescription, new Object[0]));

        return var2;
    }

}
