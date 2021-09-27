package net.cyborgcabbage.mc2.mixin;

import net.minecraft.class_596;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_596.class)
public class MouseFixMixin {
    @Inject(method="method_1971",at=@At("TAIL"))
    public void mouseFix(CallbackInfo ci){
        if(Display.isFullscreen()){
            Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
        }
    }
}
