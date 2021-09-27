package net.cyborgcabbage.mc2.mixin;

import net.minecraft.level.Level;
import net.minecraft.level.source.OverworldLevelSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OverworldLevelSource.class)
public class TestMixin {
    @Inject(method="<init>",at=@At("TAIL"))
    public void printStackTrace(Level level, long seed, CallbackInfo ci){
        try {
            throw new Exception("Exception");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
