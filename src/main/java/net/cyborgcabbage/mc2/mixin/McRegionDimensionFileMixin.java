package net.cyborgcabbage.mc2.mixin;

import net.cyborgcabbage.mc2.GetLayeredChunkIO;
import net.cyborgcabbage.mc2.LayeredChunkIO;
import net.minecraft.level.chunk.ChunkIO;
import net.minecraft.level.chunk.LevelChunkLoader;
import net.minecraft.level.dimension.Dimension;
import net.minecraft.level.dimension.DimensionFile;
import net.minecraft.level.dimension.McRegionDimensionFile;
import net.minecraft.level.dimension.Nether;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(McRegionDimensionFile.class)
public class McRegionDimensionFileMixin extends DimensionFile implements GetLayeredChunkIO {
    public McRegionDimensionFileMixin(File file, String worldName, boolean mkdirs) {
        super(file, worldName, mkdirs);
    }

    @Override
    public LayeredChunkIO getLayeredChunkIO() {
        return new LayeredChunkIO(this.getParentFolder());
    }
}
