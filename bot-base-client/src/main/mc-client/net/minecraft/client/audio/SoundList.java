package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;

public class SoundList {
    private final List field_148577_a = Lists.newArrayList();
    private boolean field_148575_b;
    private SoundCategory field_148576_c;

    public List func_148570_a() {
        return this.field_148577_a;
    }

    public boolean func_148574_b() {
        return this.field_148575_b;
    }

    public void func_148572_a(boolean p_148572_1_) {
        this.field_148575_b = p_148572_1_;
    }

    public SoundCategory func_148573_c() {
        return this.field_148576_c;
    }

    public void func_148571_a(SoundCategory p_148571_1_) {
        this.field_148576_c = p_148571_1_;
    }

    public static class SoundEntry {
        private String field_148569_a;
        private float field_148567_b = 1.0F;
        private float field_148568_c = 1.0F;
        private int field_148565_d = 1;
        private SoundList.SoundEntry.Type field_148566_e;
        private boolean field_148564_f;

        public SoundEntry() {
            this.field_148566_e = SoundList.SoundEntry.Type.FILE;
            this.field_148564_f = false;
        }

        public String func_148556_a() {
            return this.field_148569_a;
        }

        public void func_148561_a(String p_148561_1_) {
            this.field_148569_a = p_148561_1_;
        }

        public float func_148558_b() {
            return this.field_148567_b;
        }

        public void func_148553_a(float p_148553_1_) {
            this.field_148567_b = p_148553_1_;
        }

        public float func_148560_c() {
            return this.field_148568_c;
        }

        public void func_148559_b(float p_148559_1_) {
            this.field_148568_c = p_148559_1_;
        }

        public int func_148555_d() {
            return this.field_148565_d;
        }

        public void func_148554_a(int p_148554_1_) {
            this.field_148565_d = p_148554_1_;
        }

        public SoundList.SoundEntry.Type func_148563_e() {
            return this.field_148566_e;
        }

        public void func_148562_a(SoundList.SoundEntry.Type p_148562_1_) {
            this.field_148566_e = p_148562_1_;
        }

        public boolean func_148552_f() {
            return this.field_148564_f;
        }

        public void func_148557_a(boolean p_148557_1_) {
            this.field_148564_f = p_148557_1_;
        }

        public static enum Type {
            FILE("FILE", 0, "file"), SOUND_EVENT("SOUND_EVENT", 1, "event");

            private final String field_148583_c;

            private static final SoundList.SoundEntry.Type[] $VALUES = new SoundList.SoundEntry.Type[] { FILE,
                    SOUND_EVENT };

            private Type(String p_i45109_1_, int p_i45109_2_, String p_i45109_3_) {
                this.field_148583_c = p_i45109_3_;
            }

            public static SoundList.SoundEntry.Type func_148580_a(String p_148580_0_) {
                SoundList.SoundEntry.Type[] var1 = values();
                int var2 = var1.length;

                for (int var3 = 0; var3 < var2; ++var3) {
                    SoundList.SoundEntry.Type var4 = var1[var3];

                    if (var4.field_148583_c.equals(p_148580_0_)) {
                        return var4;
                    }
                }

                return null;
            }
        }
    }
}
