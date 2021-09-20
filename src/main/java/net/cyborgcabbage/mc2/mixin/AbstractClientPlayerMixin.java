package net.cyborgcabbage.mc2.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.Session;
import net.minecraft.entity.player.AbstractClientPlayer;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends PlayerBase {
    public AbstractClientPlayerMixin(Level arg) {
        super(arg);
    }

    @Inject(method="<init>",at=@At(value="TAIL"))
    private void skinUrlFix(Minecraft minecraft, Level level, Session session, int dimensionId, CallbackInfo ci){
        this.skinUrl  = "http://skins.minecraft.net/MinecraftSkins/" + session.username + ".png";
    }
}
