package net.cyborgcabbage.mc2.mixin;

import net.cyborgcabbage.mc2.LayeredLevelSource;
import net.minecraft.level.Level;
import net.minecraft.level.dimension.Dimension;
import net.minecraft.level.source.LevelSource;
import net.minecraft.level.source.OverworldLevelSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(Dimension.class)
public class DimensionMixin {
    @Shadow public Level level;

    @Inject(method="createLevelSource",at=@At("HEAD"),cancellable = true)
    public void insertLayeredLevelSource(CallbackInfoReturnable<LevelSource> cir){
        cir.setReturnValue(new LayeredLevelSource(this.level, this.level.getSeed()));
    }
}
