package net.cyborgcabbage.mc2;

import net.minecraft.level.Level;
import net.minecraft.level.chunk.Chunk;
import net.minecraft.level.source.LevelSource;
import net.minecraft.level.source.OverworldLevelSource;
import net.minecraft.level.source.SkylandsLevelSource;
import net.minecraft.util.ProgressListener;

public class LayeredLevelSource implements LevelSource {
    private final LevelSource overworld;

    public LayeredLevelSource(Level level, long seed) {
        System.out.println("LayeredLevelSource");
        overworld = new OverworldLevelSource(level,seed);
        //overworld = new SkylandsLevelSource(level,seed);
    }

    @Override
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        System.out.println("isChunkLoaded");
        return overworld.isChunkLoaded(chunkX, chunkZ);
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ) {
        //System.out.println(Thread.currentThread().getStackTrace());
        System.out.println("getChunk");
        return overworld.getChunk(chunkX, chunkZ);

    }

    @Override
    public Chunk loadChunk(int chunkX, int chunkZ) {
        System.out.println("loadChunk");
        return overworld.loadChunk(chunkX, chunkZ);
    }

    @Override
    public void decorate(LevelSource levelSource, int chunkX, int chunkZ) {
        System.out.println("decorate");
        try {
            throw new Exception("Exception");
        }catch(Exception e){
            e.printStackTrace();
        }
        overworld.decorate(levelSource, chunkX, chunkZ);
    }

    @Override
    public boolean deleteCacheCauseClientCantHandleThis(boolean iDontKnowWhy, ProgressListener listener) {
        System.out.println("deleteCacheCauseClientCantHandleThis");
        return overworld.deleteCacheCauseClientCantHandleThis(iDontKnowWhy, listener);
    }

    @Override
    public boolean method_1801() {
        //System.out.println("method_1801");
        return overworld.method_1801();
    }

    @Override
    public boolean method_1805() {
        System.out.println("method_1805");
        return overworld.method_1805();
    }

    public boolean isChunkLoaded(int chunkX, int chunkY, int chunkZ) {
        return overworld.isChunkLoaded(chunkX, chunkZ);
    }

    public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {
        try{
            throw new Exception("FART");
        }catch(Exception e){
            e.printStackTrace();
        }
        return overworld.getChunk(chunkX, chunkZ);
    }

    public Chunk loadChunk(int chunkX, int chunkY, int chunkZ) {
        return overworld.loadChunk(chunkX, chunkZ);
    }

    public void decorate(LevelSource levelSource, int chunkX, int chunkY, int chunkZ) {
        overworld.decorate(levelSource, chunkX, chunkZ);
    }
}
