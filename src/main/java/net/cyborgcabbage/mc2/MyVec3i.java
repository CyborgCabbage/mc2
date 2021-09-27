package net.cyborgcabbage.mc2;

import net.minecraft.util.maths.Vec2i;

public class MyVec3i {
    public final int x;
    public final int y;
    public final int z;

    public MyVec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static int hash(int x, int y, int z) {
        int n = 1 << 10;
        return Math.floorMod(y, n)*n*n | Math.floorMod(x, n)*n | Math.floorMod(z, n);
    }

    public int hashCode() {
        return hash(this.x, this.y, this.z);
    }

    public boolean equals(Object object) {
        MyVec3i other = (MyVec3i)object;
        return other.x == this.x && other.y == this.y && other.z == this.z;
    }
}
