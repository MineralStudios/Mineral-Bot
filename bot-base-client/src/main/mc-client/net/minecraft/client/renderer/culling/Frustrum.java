package net.minecraft.client.renderer.culling;

import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;

public class Frustrum implements ICamera {
    private final ClippingHelper clippingHelper;
    private double xPosition;
    private double yPosition;
    private double zPosition;

    public Frustrum(Minecraft mc) {
        this.clippingHelper = mc.getClippingHelper();
    }

    public void setPosition(double par1, double par3, double par5) {
        this.xPosition = par1;
        this.yPosition = par3;
        this.zPosition = par5;
    }

    /**
     * Calls the clipping helper. Returns true if the box is inside all 6 clipping
     * planes, otherwise returns false.
     */
    public boolean isBoxInFrustum(double par1, double par3, double par5, double par7, double par9, double par11) {
        return this.clippingHelper.isBoxInFrustum(par1 - this.xPosition, par3 - this.yPosition, par5 - this.zPosition,
                par7 - this.xPosition, par9 - this.yPosition, par11 - this.zPosition);
    }

    /**
     * Returns true if the bounding box is inside all 6 clipping planes, otherwise
     * returns false.
     */
    public boolean isBoundingBoxInFrustum(AxisAlignedBB par1AxisAlignedBB) {
        return this.isBoxInFrustum(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ,
                par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
    }

    public boolean isBoxInFrustumFully(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return this.clippingHelper.isBoxInFrustumFully(minX - this.xPosition, minY - this.yPosition,
                minZ - this.zPosition, maxX - this.xPosition, maxY - this.yPosition, maxZ - this.zPosition);
    }

    public boolean isBoundingBoxInFrustumFully(AxisAlignedBB aab) {
        return this.isBoxInFrustumFully(aab.minX, aab.minY, aab.minZ, aab.maxX, aab.maxY, aab.maxZ);
    }
}
