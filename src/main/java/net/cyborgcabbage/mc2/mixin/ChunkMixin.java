package net.cyborgcabbage.mc2.mixin;

import net.cyborgcabbage.mc2.ValueY;
import net.minecraft.level.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Chunk.class)
public class ChunkMixin implements ValueY {
    int y = 0;

    public int getY() {
        return y;
    }

    public void setY(int value) {
        y = value;
    }

}
