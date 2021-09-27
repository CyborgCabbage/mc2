package net.cyborgcabbage.mc2;

import net.minecraft.class_257;
import net.minecraft.entity.EntityBase;
import net.minecraft.entity.EntityRegistry;
import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;
import net.minecraft.tileentity.TileEntityBase;
import net.minecraft.util.io.CompoundTag;
import net.minecraft.util.io.ListTag;

import java.io.File;
import java.util.Iterator;

public class LayeredLevelManager {

    public LayeredLevelManager(File file, boolean flag) {
    }

    /*private File getLevelDat(int x, int y, int z) {
        String fileName = "c." + Integer.toString(x, 36) + "." + Integer.toString(z, 36) + ".dat";
        String var4 = Integer.toString(x & 63, 36);
        String var5 = Integer.toString(z & 63, 36);
        File var6 = new File(this.levelFile, var4);
        if (!var6.exists()) {
            if (!this.field_1702) {
                return null;
            }

            var6.mkdir();
        }

        var6 = new File(var6, var5);
        if (!var6.exists()) {
            if (!this.field_1702) {
                return null;
            }

            var6.mkdir();
        }

        var6 = new File(var6, fileName);
        return !var6.exists() && !this.field_1702 ? null : var6;
    }*/

    /*public Chunk getChunk(Level level, int xPos, int yPos, int zPos) {
        File var4 = this.getLevelDat(xPos, yPos, zPos);
        if (var4 != null && var4.exists()) {
            try {
                FileInputStream var5 = new FileInputStream(var4);
                CompoundTag var6 = NBTIO.readGzipped(var5);
                if (!var6.containsKey("Level")) {
                    System.out.println("Chunk file at " + xPos + "," + yPos + "," + zPos + " is missing level data, skipping");
                    return null;
                }

                if (!var6.getCompoundTag("Level").containsKey("Blocks")) {
                    System.out.println("Chunk file at " + xPos + "," + yPos + "," + zPos + " is missing block data, skipping");
                    return null;
                }

                Chunk var7 = method_1479(level, var6.getCompoundTag("Level"));
                if (!var7.isSameXAndZ(xPos, zPos)) {
                    System.out.println("Chunk file at " + xPos + "," + zPos + " is in the wrong location; relocating. (Expected " + xPos + ", " + zPos + ", got " + var7.x + ", " + var7.z + ")");
                    var6.put("xPos", xPos);
                    var6.put("zPos", zPos);
                    var7 = method_1479(level, var6.getCompoundTag("Level"));
                }

                var7.method_890();
                return var7;
            } catch (Exception var8) {
                var8.printStackTrace();
            }
        }

        return null;
    }*/

    /*public void saveChunk(Level level, ChunkWrapper chunk) {
        level.checkSessionLock();
        File var3 = this.getLevelDat(chunk.x, chunk.y, chunk.z);
        if (var3.exists()) {
            LevelProperties var4 = level.getProperties();
            var4.setSizeOnDisk(var4.getSizeOnDisk() - var3.length());
        }

        try {
            File var10 = new File(this.levelFile, "tmp_chunk.dat");
            FileOutputStream var5 = new FileOutputStream(var10);
            CompoundTag var6 = new CompoundTag();
            CompoundTag var7 = new CompoundTag();
            var6.put("Level", var7);
            method_1480(chunk, level, var7);
            NBTIO.writeGzipped(var6, var5);
            var5.close();
            if (var3.exists()) {
                var3.delete();
            }

            var10.renameTo(var3);
            LevelProperties var8 = level.getProperties();
            var8.setSizeOnDisk(var8.getSizeOnDisk() + var3.length());
        } catch (Exception var9) {
            var9.printStackTrace();
        }

    }*/

    public static void method_1480(Chunk chunk, Level level, CompoundTag tag) {
        level.checkSessionLock();
        tag.put("xPos", chunk.x);
        tag.put("yPos", chunk.x);
        tag.put("zPos", chunk.z);
        tag.put("LastUpdate", level.getLevelTime());
        tag.put("Blocks", chunk.tiles);
        tag.put("Data", chunk.field_957.field_2103);
        tag.put("SkyLight", chunk.field_958.field_2103);
        tag.put("BlockLight", chunk.field_959.field_2103);
        tag.put("HeightMap", chunk.heightmap);
        tag.put("TerrainPopulated", chunk.decorated);
        chunk.field_969 = false;
        ListTag entitiesListTag = new ListTag();

        Iterator iterator;
        CompoundTag compoundTag;
        for(int var4 = 0; var4 < chunk.entities.length; ++var4) {
            iterator = chunk.entities[var4].iterator();

            while(iterator.hasNext()) {
                EntityBase entity = (EntityBase)iterator.next();
                chunk.field_969 = true;
                compoundTag = new CompoundTag();
                if (entity.method_1343(compoundTag)) {
                    entitiesListTag.add(compoundTag);
                }
            }
        }

        tag.put("Entities", entitiesListTag);
        ListTag tileEntitiesListTag = new ListTag();
        iterator = chunk.field_964.values().iterator();

        while(iterator.hasNext()) {
            TileEntityBase tileEntity = (TileEntityBase)iterator.next();
            compoundTag = new CompoundTag();
            tileEntity.writeIdentifyingData(compoundTag);
            tileEntitiesListTag.add(compoundTag);
        }

        tag.put("TileEntities", tileEntitiesListTag);
    }

    public static Chunk method_1479(Level level, CompoundTag tag) {
        int xPos = tag.getInt("xPos");
        int yPos = tag.getInt("yPos");
        int zPos = tag.getInt("zPos");
        Chunk chunk = new Chunk(level, xPos, zPos);
        ((ValueY)chunk).setY(yPos);
        chunk.tiles = tag.getByteArray("Blocks");
        chunk.field_957 = new class_257(tag.getByteArray("Data"));
        chunk.field_958 = new class_257(tag.getByteArray("SkyLight"));
        chunk.field_959 = new class_257(tag.getByteArray("BlockLight"));
        chunk.heightmap = tag.getByteArray("HeightMap");
        chunk.decorated = tag.getBoolean("TerrainPopulated");
        if (!chunk.field_957.method_1702()) {
            chunk.field_957 = new class_257(chunk.tiles.length);
        }

        if (chunk.heightmap == null || !chunk.field_958.method_1702()) {
            chunk.heightmap = new byte[256];
            chunk.field_958 = new class_257(chunk.tiles.length);
            chunk.generateHeightmap();
        }

        if (!chunk.field_959.method_1702()) {
            chunk.field_959 = new class_257(chunk.tiles.length);
            chunk.method_857();
        }

        ListTag entitiesListTag = tag.getListTag("Entities");
        if (entitiesListTag != null) {
            for(int i = 0; i < entitiesListTag.size(); ++i) {
                CompoundTag entityTag = (CompoundTag)entitiesListTag.get(i);
                EntityBase entity = EntityRegistry.create(entityTag, level);
                chunk.field_969 = true;
                if (entity != null) {
                    chunk.addEntity(entity);
                }
            }
        }

        ListTag tileEntitiesListTag = tag.getListTag("TileEntities");
        if (tileEntitiesListTag != null) {
            for(int i = 0; i < tileEntitiesListTag.size(); ++i) {
                CompoundTag tileEntityTag = (CompoundTag)tileEntitiesListTag.get(i);
                TileEntityBase tileEntity = TileEntityBase.tileEntityFromNBT(tileEntityTag);
                if (tileEntity != null) {
                    chunk.method_867(tileEntity);
                }
            }
        }

        return chunk;
    }

    /*public void iAmUseless() {
    }

    public void iAmActuallyUseless() {
    }

    public void iDoNothingToo(Level level, Chunk chunk) {
    }*/
}