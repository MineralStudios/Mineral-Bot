package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AnimationMetadataSection implements IMetadataSection {
    private final List animationFrames;
    private final int frameWidth;
    private final int frameHeight;
    private final int frameTime;

    public AnimationMetadataSection(List p_i1309_1_, int p_i1309_2_, int p_i1309_3_, int p_i1309_4_) {
        this.animationFrames = p_i1309_1_;
        this.frameWidth = p_i1309_2_;
        this.frameHeight = p_i1309_3_;
        this.frameTime = p_i1309_4_;
    }

    public int getFrameHeight() {
        return this.frameHeight;
    }

    public int getFrameWidth() {
        return this.frameWidth;
    }

    public int getFrameCount() {
        return this.animationFrames.size();
    }

    public int getFrameTime() {
        return this.frameTime;
    }

    private AnimationFrame getAnimationFrame(int p_130072_1_) {
        return (AnimationFrame) this.animationFrames.get(p_130072_1_);
    }

    public int getFrameTimeSingle(int p_110472_1_) {
        AnimationFrame var2 = this.getAnimationFrame(p_110472_1_);
        return var2.hasNoTime() ? this.frameTime : var2.getFrameTime();
    }

    public boolean frameHasTime(int p_110470_1_) {
        return !((AnimationFrame) this.animationFrames.get(p_110470_1_)).hasNoTime();
    }

    public int getFrameIndex(int p_110468_1_) {
        return ((AnimationFrame) this.animationFrames.get(p_110468_1_)).getFrameIndex();
    }

    public Set getFrameIndexSet() {
        HashSet var1 = Sets.newHashSet();
        Iterator var2 = this.animationFrames.iterator();

        while (var2.hasNext()) {
            AnimationFrame var3 = (AnimationFrame) var2.next();
            var1.add(Integer.valueOf(var3.getFrameIndex()));
        }

        return var1;
    }
}
