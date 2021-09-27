package net.cyborgcabbage.mc2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_255;
import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;
import net.minecraft.level.source.LevelSource;
import net.minecraft.util.ProgressListener;

import java.util.*;

public class LayeredServerChunkCache implements LevelSource {
    private final Set<Integer> dropSet = new HashSet<>();
    private final Chunk field_1226;
    private final LayeredLevelSource levelSource;
    private final LayeredChunkIO chunkIO;
    private final Map<Integer, Chunk> serverChunkCache = new HashMap<>();
    private final List<Chunk> field_1230 = new ArrayList<>();
    private final Level level;

    public LayeredServerChunkCache(Level level, LayeredChunkIO chunkIO, LayeredLevelSource levelSource) {
        this.field_1226 = new class_255(level, new byte['\u8000'], 0, 0);
        this.level = level;
        this.chunkIO = chunkIO;
        this.levelSource = levelSource;
    }

    public boolean isChunkLoaded(int chunkX, int chunkY, int chunkZ) {
        return this.serverChunkCache.containsKey(MyVec3i.hash(chunkX, chunkY, chunkZ));
    }

    public Chunk loadChunk(int chunkX, int chunkY, int chunkZ) {
        int hashedChunkPos = MyVec3i.hash(chunkX, chunkY, chunkZ);
        this.dropSet.remove(hashedChunkPos);
        Chunk chunk = this.serverChunkCache.get(hashedChunkPos);
        if (chunk == null) {
            chunk = this.method_1051(chunkX, chunkY, chunkZ);
            if (chunk == null) {
                if (this.levelSource == null) {
                    chunk = this.field_1226;
                } else {
                    chunk = this.levelSource.getChunk(chunkX, chunkY, chunkZ);
                }
            }

            this.serverChunkCache.put(hashedChunkPos, chunk);
            this.field_1230.add(chunk);
            if (chunk != null) {
                chunk.method_878();
                chunk.method_881();
                if (!chunk.decorated && this.isChunkLoaded(chunkX + 1, chunkY, chunkZ + 1) && this.isChunkLoaded(chunkX, chunkY, chunkZ + 1) && this.isChunkLoaded(chunkX + 1, chunkY, chunkZ)) {
                    this.decorate(this, chunkX, chunkY, chunkZ);
                }
            }

            if (this.isChunkLoaded(chunkX - 1, chunkY, chunkZ) && !this.getChunk(chunkX - 1, chunkY, chunkZ).decorated && this.isChunkLoaded(chunkX - 1, chunkY, chunkZ + 1) && this.isChunkLoaded(chunkX, chunkY, chunkZ + 1) && this.isChunkLoaded(chunkX - 1, chunkY, chunkZ)) {
                this.decorate(this, chunkX - 1, chunkY, chunkZ);
            }

            if (this.isChunkLoaded(chunkX, chunkY, chunkZ - 1) && !this.getChunk(chunkX, chunkY, chunkZ - 1).decorated && this.isChunkLoaded(chunkX + 1, chunkY, chunkZ - 1) && this.isChunkLoaded(chunkX, chunkY, chunkZ - 1) && this.isChunkLoaded(chunkX + 1, chunkY, chunkZ)) {
                this.decorate(this, chunkX, chunkY, chunkZ - 1);
            }

            if (this.isChunkLoaded(chunkX - 1, chunkY, chunkZ - 1) && !this.getChunk(chunkX - 1, chunkY, chunkZ - 1).decorated && this.isChunkLoaded(chunkX - 1, chunkY, chunkZ - 1) && this.isChunkLoaded(chunkX, chunkY, chunkZ - 1) && this.isChunkLoaded(chunkX - 1, chunkY, chunkZ)) {
                this.decorate(this, chunkX - 1, chunkY, chunkZ - 1);
            }
        }

        return chunk;
    }

    public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {
        Chunk chunk = this.serverChunkCache.get(MyVec3i.hash(chunkX, chunkY, chunkZ));
        return chunk == null ? this.loadChunk(chunkX, chunkY, chunkZ) : chunk;
    }

    private Chunk method_1051(int x, int y, int z) {
        if (this.chunkIO == null) {
            return null;
        } else {
            try {
                Chunk chunk = this.chunkIO.getChunk(this.level, x, y, z);
                if (chunk != null) {
                    chunk.lastUpdate = this.level.getLevelTime();
                }

                return chunk;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private void method_1049(Chunk chunk) {
        if (this.chunkIO != null) {
            try {
                this.chunkIO.iDoNothingToo(this.level, chunk);
            } catch (Exception var3) {
                var3.printStackTrace();
            }

        }
    }

    private void method_1050(Chunk chunk) {
        if (this.chunkIO != null) {
            //try {
            chunk.lastUpdate = this.level.getLevelTime();
            this.chunkIO.saveChunk(this.level, chunk);
            //} catch (IOException var3) {
            //    var3.printStackTrace();
            //}
        }
    }

    public void decorate(LevelSource levelSource, int chunkX, int chunkY, int chunkZ) {
        Chunk chunk = this.getChunk(chunkX, chunkY, chunkZ);
        if (!chunk.decorated) {
            chunk.decorated = true;
            if (this.levelSource != null) {
                this.levelSource.decorate(levelSource, chunkX, chunkY, chunkZ);
                chunk.method_885();
            }
        }

    }

    @Override
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return this.isChunkLoaded(chunkX,0, chunkZ);
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ) {

        return this.getChunk(chunkX,0, chunkZ);
    }

    @Override
    public Chunk loadChunk(int chunkX, int chunkZ) {
        return this.loadChunk(chunkX,0, chunkZ);
    }

    @Override
    public void decorate(LevelSource levelSource, int chunkX, int chunkZ) {
        this.decorate(levelSource, chunkX,0, chunkZ);
    }

    public boolean deleteCacheCauseClientCantHandleThis(boolean iDontKnowWhy, ProgressListener listener) {
        int var3 = 0;

        for (Chunk chunk : this.field_1230) { // Iterate chunks
            if (iDontKnowWhy && !chunk.field_968) {
                this.method_1049(chunk);
            }

            if (chunk.method_871(iDontKnowWhy)) {
                this.method_1050(chunk);
                chunk.field_967 = false;
                ++var3;
                if (var3 == 24 && !iDontKnowWhy) {
                    return false;
                }
            }
        }

        if (iDontKnowWhy) {
            if (this.chunkIO == null) {
                return true;
            }

            this.chunkIO.iAmActuallyUseless();
        }

        return true;
    }

    public boolean method_1801() {
        for(int i = 0; i < 100; ++i) {
            if (!this.dropSet.isEmpty()) {
                Integer hashChunkPos = this.dropSet.iterator().next();
                Chunk chunk = this.serverChunkCache.get(hashChunkPos);
                chunk.method_883();
                this.method_1050(chunk);
                this.method_1049(chunk);
                this.dropSet.remove(hashChunkPos);
                this.serverChunkCache.remove(hashChunkPos);
                this.field_1230.remove(chunk);
            }
        }

        if (this.chunkIO != null) {
            this.chunkIO.iAmUseless();
        }

        return this.levelSource.method_1801();
    }

    public boolean method_1805() {
        return true;
    }

    @Environment(EnvType.CLIENT)
    public String toString() {
        return "LayeredServerChunkCache: " + this.serverChunkCache.size() + " Drop: " + this.dropSet.size();
    }
}
