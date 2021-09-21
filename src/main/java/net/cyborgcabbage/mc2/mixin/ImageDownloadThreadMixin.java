package net.cyborgcabbage.mc2.mixin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.ImageProcessor;
import net.minecraft.util.ImageDownloadThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

@Mixin(ImageDownloadThread.class)
public class ImageDownloadThreadMixin {
    @Shadow public BufferedImage image;

    @Inject(method="<init>",at=@At("TAIL"),cancellable = true)
    public void skinFix(String string, ImageProcessor imageProcessor, CallbackInfo ci){
        boolean isCape = string.contains("MinecraftCloaks");
        boolean isSkin = string.contains("MinecraftSkins");
        if(isCape || isSkin){
            (new Thread(() -> {
                HttpURLConnection apiUuid = null;
                HttpURLConnection apiProfile = null;
                HttpURLConnection apiTexture = null;
                String username;
                if(isSkin) username = string.replace("http://s3.amazonaws.com/MinecraftSkins/","").replace(".png","");
                else username = string.replace("http://s3.amazonaws.com/MinecraftCloaks/","").replace(".png","");
                try {
                    //Username to UUID
                    URL usernameToUuid = new URL("https://api.mojang.com/users/profiles/minecraft/"+username);
                    apiUuid = (HttpURLConnection)usernameToUuid.openConnection();
                    apiUuid.setDoInput(true);
                    apiUuid.setDoOutput(false);
                    apiUuid.connect();
                    if (apiUuid.getResponseCode() == 200) {
                        Scanner apiUuidScanner = new Scanner(apiUuid.getInputStream()).useDelimiter("\\A");
                        String uuidJsonString = apiUuidScanner.hasNext() ? apiUuidScanner.next() : "";
                        JsonObject uuidJsonObject = new JsonParser().parse(uuidJsonString).getAsJsonObject();

                        //UUID to skin
                        URL uuidToProfile = new URL("https://sessionserver.mojang.com/session/minecraft/profile/"+uuidJsonObject.get("id").getAsString());
                        apiProfile = (HttpURLConnection)uuidToProfile.openConnection();
                        apiProfile.setDoInput(true);
                        apiProfile.setDoOutput(false);
                        apiProfile.connect();
                        if (apiProfile.getResponseCode() == 200) {
                            Scanner apiProfileScanner = new Scanner(apiProfile.getInputStream()).useDelimiter("\\A");
                            String profileString = apiProfileScanner.hasNext() ? apiProfileScanner.next() : "";
                            JsonObject profileJsonObject = new JsonParser().parse(profileString).getAsJsonObject();
                            String textureString = profileJsonObject.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
                            textureString = new String(Base64.getDecoder().decode(textureString), StandardCharsets.UTF_8);
                            JsonObject textureJsonObject = new JsonParser().parse(textureString).getAsJsonObject();

                            //Get Texture
                            URL texture;
                            if(isSkin) texture = new URL(textureJsonObject.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString());
                            else texture = new URL(textureJsonObject.get("textures").getAsJsonObject().get("CAPE").getAsJsonObject().get("url").getAsString());
                            apiTexture = (HttpURLConnection)texture.openConnection();
                            apiTexture.setDoInput(true);
                            apiTexture.setDoOutput(false);
                            apiTexture.connect();
                            if (apiTexture.getResponseCode() == 200) {
                                if (imageProcessor == null) {
                                    this.image = ImageIO.read(apiTexture.getInputStream());
                                } else {
                                    this.image = imageProcessor.process(ImageIO.read(apiTexture.getInputStream()));
                                }
                            }
                        }
                    }
                } catch (Exception var6) {
                    var6.printStackTrace();
                } finally {
                    if (apiUuid != null) {
                        apiUuid.disconnect();
                    }
                    if (apiProfile != null) {
                        apiProfile.disconnect();
                    }
                    if (apiTexture != null) {
                        apiTexture.disconnect();
                    }
                }
            })).start();
        }

    }
}
