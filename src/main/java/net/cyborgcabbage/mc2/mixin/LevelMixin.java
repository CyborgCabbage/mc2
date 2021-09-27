package net.cyborgcabbage.mc2.mixin;

import net.cyborgcabbage.mc2.GetLayeredChunkIO;
import net.cyborgcabbage.mc2.LayeredChunkIO;
import net.cyborgcabbage.mc2.LayeredLevelSource;
import net.cyborgcabbage.mc2.LayeredServerChunkCache;
import net.minecraft.level.Level;
import net.minecraft.level.chunk.ChunkIO;
import net.minecraft.level.chunk.LevelChunkLoader;
import net.minecraft.level.dimension.Dimension;
import net.minecraft.level.dimension.DimensionData;
import net.minecraft.level.dimension.McRegionDimensionFile;
import net.minecraft.level.source.LevelSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class LevelMixin {
    @Shadow @Final protected DimensionData dimensionData;

    @Shadow @Final public Dimension dimension;

    @Inject(method="getChunkCache",at=@At("HEAD"),cancellable = true)
    public void insertLayeredServerChunkCache(CallbackInfoReturnable<LevelSource> cir){
        if(this.dimensionData instanceof GetLayeredChunkIO) {
            System.out.println("Using layered chunks for "+this.dimensionData+"!!!");
            LayeredChunkIO layeredChunkIO = ((GetLayeredChunkIO)this.dimensionData).getLayeredChunkIO();
            cir.setReturnValue(new LayeredServerChunkCache((Level) (Object) this, layeredChunkIO, (LayeredLevelSource)this.dimension.createLevelSource()));
        }else{
            System.out.println("Not using layered chunks for "+this.dimensionData+" :(");
        }
    }
}
