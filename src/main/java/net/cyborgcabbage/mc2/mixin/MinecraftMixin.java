package net.cyborgcabbage.mc2.mixin;

import net.minecraft.client.Minecraft;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Redirect(method="init",at=@At(value="INVOKE",target="Lorg/lwjgl/opengl/Display;create()V"))
    private void bitDepthFix() throws LWJGLException {
        Display.create(new PixelFormat().withDepthBits(24));
    }
}
