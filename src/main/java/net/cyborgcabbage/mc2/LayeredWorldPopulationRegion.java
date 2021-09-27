package net.cyborgcabbage.mc2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockBase;
import net.minecraft.block.material.Material;
import net.minecraft.level.Level;
import net.minecraft.level.WorldPopulationRegion;
import net.minecraft.level.chunk.Chunk;
import net.minecraft.level.gen.BiomeSource;
import net.minecraft.tileentity.TileEntityBase;

public class LayeredWorldPopulationRegion extends WorldPopulationRegion {
    private int originChunkX;
    private int originChunkY;
    private int originChunkZ;
    private Chunk[][][] chunks;
    private Level level;

    public LayeredWorldPopulationRegion(Level level, int originX, int originY, int originZ, int endX, int endY, int endZ) {
        super(level, originX, originY, originZ, endX, endY, endZ);
        this.level = level;
        this.originChunkX = originX >> 4;
        this.originChunkY = originY >> 7;
        this.originChunkZ = originZ >> 4;
        int endChunkX = endX >> 4;
        int endChunkY = endY >> 7;
        int endChunkZ = endZ >> 4;
        this.chunks = new Chunk[endChunkX - this.originChunkX + 1][endChunkY - this.originChunkY + 1][endChunkZ - this.originChunkZ + 1];

        for(int cx = this.originChunkX; cx <= endChunkX; ++cx) {
            for (int cy = this.originChunkY; cy <= endChunkY; ++cy) {
                for (int cz = this.originChunkZ; cz <= endChunkZ; ++cz) {
                    this.chunks[cx - this.originChunkX][cy - this.originChunkY][cz - this.originChunkZ] = ((LayeredServerChunkCache)level.getCache()).getChunk(cx,cy,cz);
                }
            }
        }
    }

    public int getTileId(int x, int y, int z) {
        int relativeChunkX = (x >> 4) - this.originChunkX;
        int relativeChunkY = (y >> 7) - this.originChunkY;
        int relativeChunkZ = (z >> 4) - this.originChunkZ;
        if (
                relativeChunkX >= 0 && relativeChunkX < this.chunks.length &&
                relativeChunkY >= 0 && relativeChunkY < this.chunks[relativeChunkX].length &&
                relativeChunkZ >= 0 && relativeChunkZ < this.chunks[relativeChunkX][relativeChunkY].length
        ) {
            Chunk chunk = this.chunks[relativeChunkX][relativeChunkY][relativeChunkZ];
            return chunk == null ? 0 : chunk.getTileId(x & 15, y & 127, z & 15);
        } else {
            return 0;
        }
    }

    public TileEntityBase getTileEntity(int x, int y, int z) {
        int chunkX = (x >> 4) - this.originChunkX;
        int chunkY = (y >> 7) - this.originChunkY;
        int chunkZ = (z >> 4) - this.originChunkZ;
        return this.chunks[chunkX][chunkY][chunkZ].method_882(x & 15, y & 127, z & 15);
    }

    @Environment(EnvType.CLIENT)
    public float method_1784(int x, int y, int z, int i1) {
        int var5 = this.method_143(x, y, z);
        if (var5 < i1) {
            var5 = i1;
        }

        return this.level.dimension.lightTable[var5];
    }

    @Environment(EnvType.CLIENT)
    public float getBrightness(int x, int y, int z) {
        return this.level.dimension.lightTable[this.method_143(x, y, z)];
    }

    @Environment(EnvType.CLIENT)
    public int method_143(int x, int y, int z) {
        return this.method_142(x, y, z, true);
    }

    @Environment(EnvType.CLIENT)
    public int method_142(int x, int y, int z, boolean flag) { //Get sky light for block
        if (x >= -32000000 && z >= -32000000 && x < 32000000 && z <= 32000000) {
            if (flag) {
                int tileId = this.getTileId(x, y, z);
                if (tileId == BlockBase.STONE_SLAB.id || tileId == BlockBase.FARMLAND.id || tileId == BlockBase.WOOD_STAIRS.id || tileId == BlockBase.COBBLESTONE_STAIRS.id) {
                    int highest = this.method_142(x, y + 1, z, false);
                    int xPos = this.method_142(x + 1, y, z, false);
                    int xNeg = this.method_142(x - 1, y, z, false);
                    int zPos = this.method_142(x, y, z + 1, false);
                    int zNeg = this.method_142(x, y, z - 1, false);
                    if (xPos > highest) {
                        highest = xPos;
                    }

                    if (xNeg > highest) {
                        highest = xNeg;
                    }

                    if (zPos > highest) {
                        highest = zPos;
                    }

                    if (zNeg > highest) {
                        highest = zNeg;
                    }

                    return highest;
                }
            }
            int chunkX = (x >> 4) - this.originChunkX;
            int chunkY = (y >> 7) - this.originChunkY;
            int chunkZ = (z >> 4) - this.originChunkZ;
            return this.chunks[chunkX][chunkY][chunkZ].method_880(x & 15, y & 127, z & 15, this.level.field_202);
        } else {
            return 15;
        }
    }

    public int getTileMeta(int x, int y, int z) {
        int chunkX = (x >> 4) - this.originChunkX;
        int chunkY = (y >> 7) - this.originChunkY;
        int chunkZ = (z >> 4) - this.originChunkZ;
        return this.chunks[chunkX][chunkY][chunkZ].method_875(x & 15, y & 127, z & 15);
    }

    public Material getMaterial(int x, int y, int z) {
        int tileId = this.getTileId(x, y, z);
        return tileId == 0 ? Material.AIR : BlockBase.BY_ID[tileId].material;
    }

    @Environment(EnvType.CLIENT)
    public BiomeSource getBiomeSource() {
        return this.level.getBiomeSource();
    }

    @Environment(EnvType.CLIENT)
    public boolean isFullOpaque(int x, int y, int z) {
        BlockBase blockBase = BlockBase.BY_ID[this.getTileId(x, y, z)];
        return blockBase != null && blockBase.isFullOpaque();
    }

    public boolean canSuffocate(int x, int y, int z) {
        BlockBase blockBase = BlockBase.BY_ID[this.getTileId(x, y, z)];
        if (blockBase == null) {
            return false;
        } else {
            return blockBase.material.blocksMovement() && blockBase.isFullCube();
        }
    }
}
