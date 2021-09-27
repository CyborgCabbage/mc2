package net.cyborgcabbage.mc2;

import net.minecraft.level.Level;
import net.minecraft.level.LevelProperties;
import net.minecraft.level.chunk.Chunk;
import net.minecraft.util.io.AbstractTag;
import net.minecraft.util.io.CompoundTag;
import net.minecraft.util.io.NBTIO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;

public class LayeredChunkIO {
    private final File file;

    public LayeredChunkIO(File file) {
        this.file = file;
    }

    public Chunk getChunk(Level level, int xPos, int yPos, int zPos) {
        DataInputStream var4 = LayeredRegionLoader.method_1215(this.file, xPos, yPos, zPos);
        if (var4 != null) {
            CompoundTag var5 = NBTIO.readTag(var4);
            if (!var5.containsKey("Level")) {
                System.out.println("Chunk file at " + xPos + "," + yPos + "," + zPos + " is missing level data, skipping");
                return null;
            } else if (!var5.getCompoundTag("Level").containsKey("Blocks")) {
                System.out.println("Chunk file at " + xPos + "," + yPos + "," + zPos + " is missing block data, skipping");
                return null;
            } else {
                Chunk chunk = LayeredLevelManager.method_1479(level, var5.getCompoundTag("Level"));
                if (!chunk.isSameXAndZ(xPos, zPos)) {
                    System.out.println("Chunk file at " + xPos + "," + yPos + "," + zPos + " is in the wrong location; relocating. (Expected " + xPos + ", " + zPos + ", got " + chunk.x + ", " + chunk.z + ")");
                    var5.put("xPos", xPos);
                    var5.put("yPos", yPos);
                    var5.put("zPos", zPos);
                    chunk = LayeredLevelManager.method_1479(level, var5.getCompoundTag("Level"));
                }

                chunk.method_890();
                return chunk;
            }
        } else {
            return null;
        }
    }

    public void saveChunk(Level level, Chunk chunkWrapper) {
        level.checkSessionLock();

        try {
            DataOutputStream var3 = LayeredRegionLoader.method_1216(this.file, chunkWrapper.x, ((ValueY)chunkWrapper).getY(), chunkWrapper.z);
            CompoundTag var4 = new CompoundTag();
            CompoundTag var5 = new CompoundTag();
            var4.put("Level", (AbstractTag)var5);
            LayeredLevelManager.method_1480(chunkWrapper, level, var5);
            NBTIO.writeTag(var4, var3);
            var3.close();
            LevelProperties var6 = level.getProperties();
            var6.setSizeOnDisk(var6.getSizeOnDisk() + (long)LayeredRegionLoader.method_1214(this.file, chunkWrapper.x, ((ValueY)chunkWrapper).getY(), chunkWrapper.z));
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }

    public void iDoNothingToo(Level level, Chunk chunk) {
    }

    public void iAmUseless() {
    }

    public void iAmActuallyUseless() {
    }
}
