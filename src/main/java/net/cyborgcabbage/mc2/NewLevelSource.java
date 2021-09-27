/*package net.cyborgcabbage.mc2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockBase;
import net.minecraft.block.Sand;
import net.minecraft.block.material.Material;
import net.minecraft.level.Level;
import net.minecraft.level.biome.Biome;
import net.minecraft.level.chunk.Chunk;
import net.minecraft.level.gen.Cave;
import net.minecraft.level.gen.OverworldCave;
import net.minecraft.level.source.LevelSource;
import net.minecraft.level.structure.*;
import net.minecraft.structure.Ore;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.noise.PerlinOctaveNoise;

import java.util.Random;

public class NewLevelSource {
    private Random rand;
    private PerlinOctaveNoise upperInterpolationNoise;
    private PerlinOctaveNoise lowerInterpolationNoise;
    private PerlinOctaveNoise interpolationNoise;
    private PerlinOctaveNoise beachNoise;
    private PerlinOctaveNoise surfaceDepthNoise;
    public PerlinOctaveNoise biomeNoise;
    public PerlinOctaveNoise depthNoise;
    public PerlinOctaveNoise treeNoise;
    private Level level;
    private double[] noises;
    private double[] sandNoises = new double[256];
    private double[] gravelNoises = new double[256];
    private double[] surfaceDepthNoises = new double[256];
    private Cave cave = new OverworldCave();
    private Biome[] biomes;
    double[] interpolationNoises;
    double[] upperInterpolationNoises;
    double[] lowerInterpolationNoises;
    double[] biomeNoises;
    double[] depthNoises;
    int[][] unusedVals = new int[32][32];
    private double[] temperatureNoises;

    public NewLevelSource(Level level, long seed) {
        this.level = level;
        this.rand = new Random(seed);
        this.upperInterpolationNoise = new PerlinOctaveNoise(this.rand, 16);
        this.lowerInterpolationNoise = new PerlinOctaveNoise(this.rand, 16);
        this.interpolationNoise = new PerlinOctaveNoise(this.rand, 8);
        this.beachNoise = new PerlinOctaveNoise(this.rand, 4);
        this.surfaceDepthNoise = new PerlinOctaveNoise(this.rand, 4);
        this.biomeNoise = new PerlinOctaveNoise(this.rand, 10);
        this.depthNoise = new PerlinOctaveNoise(this.rand, 16);
        this.treeNoise = new PerlinOctaveNoise(this.rand, 8);
    }

    public void shapeChunk(int chunkX, int chunkZ, byte[] tiles, Biome[] biomes, double[] temperatures) {
        byte noiseValuesWidth = 4;
        byte noiseValuesHeight = 16;
        byte seaLevel = 64;
        int noiseValuesX = noiseValuesWidth + 1;
        int noiseValuesY = noiseValuesHeight + 1;
        int noiseValuesZ = noiseValuesWidth + 1;
        this.noises = this.calculateNoise(this.noises, chunkX * noiseValuesWidth, 0, chunkZ * noiseValuesWidth, noiseValuesX, noiseValuesY, noiseValuesZ);

        for(int x = 0; x < noiseValuesWidth; ++x) {
            for(int z = 0; z < noiseValuesWidth; ++z) {
                for(int y = 0; y < noiseValuesHeight; ++y) {
                    double yStepFraction = 0.125D;
                    double yProgress00 = this.noises[((x + 0) * noiseValuesZ + z + 0) * noiseValuesY + y + 0];
                    double yProgress01 = this.noises[((x + 0) * noiseValuesZ + z + 1) * noiseValuesY + y + 0];
                    double yProgress10 = this.noises[((x + 1) * noiseValuesZ + z + 0) * noiseValuesY + y + 0];
                    double yProgress11 = this.noises[((x + 1) * noiseValuesZ + z + 1) * noiseValuesY + y + 0];
                    double yStep00 = (this.noises[((x + 0) * noiseValuesZ + z + 0) * noiseValuesY + y + 1] - yProgress00) * yStepFraction;
                    double yStep01 = (this.noises[((x + 0) * noiseValuesZ + z + 1) * noiseValuesY + y + 1] - yProgress01) * yStepFraction;
                    double yStep10 = (this.noises[((x + 1) * noiseValuesZ + z + 0) * noiseValuesY + y + 1] - yProgress10) * yStepFraction;
                    double yStep11 = (this.noises[((x + 1) * noiseValuesZ + z + 1) * noiseValuesY + y + 1] - yProgress11) * yStepFraction;

                    for(int y2 = 0; y2 < 8; ++y2) {
                        double xStepFraction = 0.25D;
                        double xProgress0 = yProgress00;
                        double xProgress1 = yProgress01;
                        double xStep0 = (yProgress10 - yProgress00) * xStepFraction;
                        double xStep1 = (yProgress11 - yProgress01) * xStepFraction;

                        for(int x2 = 0; x2 < 4; ++x2) {
                            int tileIndex = x2 + x * 4 << 11 | 0 + z * 4 << 7 | y * 8 + y2;
                            double zStepFraction = 0.25D;
                            double zProgress = xProgress0;
                            double zStep = (xProgress1 - xProgress0) * zStepFraction;

                            for(int z2 = 0; z2 < 4; ++z2) {
                                double temperature = temperatures[(x * 4 + x2) * 16 + z * 4 + z2];
                                int tileId = 0;
                                if (y * 8 + y2 < seaLevel) {
                                    if (temperature < 0.5D && y * 8 + y2 >= seaLevel - 1) {
                                        tileId = BlockBase.ICE.id;
                                    } else {
                                        tileId = BlockBase.STILL_WATER.id;
                                    }
                                }

                                if (zProgress > 0.0D) {
                                    tileId = BlockBase.STONE.id;
                                }

                                tiles[tileIndex] = (byte)tileId;
                                tileIndex += 1 << 7;
                                zProgress += zStep;
                            }

                            xProgress0 += xStep0;
                            xProgress1 += xStep1;
                        }

                        yProgress00 += yStep00;
                        yProgress01 += yStep01;
                        yProgress10 += yStep10;
                        yProgress11 += yStep11;
                    }
                }
            }
        }

    }

    public void buildSurface(int chunkX, int chunkZ, byte[] tiles, Biome[] biomes) {
        byte var5 = 64;
        double var6 = 1.0/32.0;
        this.sandNoises = this.beachNoise.sample(this.sandNoises, chunkX * 16, chunkZ * 16, 0.0D, 16, 16, 1, var6, var6, 1.0D);
        this.gravelNoises = this.beachNoise.sample(this.gravelNoises, chunkX * 16, 109.0134D, chunkZ * 16, 16, 1, 16, var6, 1.0D, var6);
        this.surfaceDepthNoises = this.surfaceDepthNoise.sample(this.surfaceDepthNoises, chunkX * 16, chunkZ * 16, 0.0D, 16, 16, 1, var6 * 2.0D, var6 * 2.0D, var6 * 2.0D);

        for(int var8 = 0; var8 < 16; ++var8) {
            for(int var9 = 0; var9 < 16; ++var9) {
                Biome var10 = biomes[var8 + var9 * 16];
                boolean var11 = this.sandNoises[var8 + var9 * 16] + this.rand.nextDouble() * 0.2D > 0.0D;
                boolean var12 = this.gravelNoises[var8 + var9 * 16] + this.rand.nextDouble() * 0.2D > 3.0D;
                int var13 = (int)(this.surfaceDepthNoises[var8 + var9 * 16] / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
                int var14 = -1;
                byte var15 = var10.topTileId;
                byte var16 = var10.underTileId;

                for(int var17 = 127; var17 >= 0; --var17) {
                    int var18 = (var9 * 16 + var8) * 128 + var17;
                    if (var17 <= 0 + this.rand.nextInt(5)) {
                        tiles[var18] = (byte)BlockBase.BEDROCK.id;
                    } else {
                        byte var19 = tiles[var18];
                        if (var19 == 0) {
                            var14 = -1;
                        } else if (var19 == BlockBase.STONE.id) {
                            if (var14 == -1) {
                                if (var13 <= 0) {
                                    var15 = 0;
                                    var16 = (byte)BlockBase.STONE.id;
                                } else if (var17 >= var5 - 4 && var17 <= var5 + 1) {
                                    var15 = var10.topTileId;
                                    var16 = var10.underTileId;
                                    if (var12) {
                                        var15 = 0;
                                    }

                                    if (var12) {
                                        var16 = (byte)BlockBase.GRAVEL.id;
                                    }

                                    if (var11) {
                                        var15 = (byte)BlockBase.SAND.id;
                                    }

                                    if (var11) {
                                        var16 = (byte)BlockBase.SAND.id;
                                    }
                                }

                                if (var17 < var5 && var15 == 0) {
                                    var15 = (byte)BlockBase.STILL_WATER.id;
                                }

                                var14 = var13;
                                if (var17 >= var5 - 1) {
                                    tiles[var18] = var15;
                                } else {
                                    tiles[var18] = var16;
                                }
                            } else if (var14 > 0) {
                                --var14;
                                tiles[var18] = var16;
                                if (var14 == 0 && var16 == BlockBase.SAND.id) {
                                    var14 = this.rand.nextInt(4);
                                    var16 = (byte)BlockBase.SANDSTONE.id;
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public Chunk loadChunk(int chunkX, int chunkY, int chunkZ) {
        return this.getChunk(chunkX, chunkY, chunkZ);
    }

    public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {
        this.rand.setSeed((long)chunkX * 341873128712L + (long)chunkY * 22801763489L + (long)chunkZ * 132897987541L);
        byte[] var3 = new byte['\u8000'];
        Chunk var4 = new Chunk(this.level, var3, chunkX, chunkZ);
        this.biomes = this.level.getBiomeSource().getBiomes(this.biomes, chunkX * 16, chunkZ * 16, 16, 16);
        double[] var5 = this.level.getBiomeSource().temperatureNoises;
        this.shapeChunk(chunkX, chunkZ, var3, this.biomes, var5);
        this.buildSurface(chunkX, chunkZ, var3, this.biomes);
        this.cave.generate(this, this.level, chunkX, chunkZ, var3);
        var4.generateHeightmap();
        return var4;
    }

    private double[] calculateNoise(double[] noises, int chunkX, int chunkY, int chunkZ, int noiseResolutionX, int noiseResolutionY, int noiseResolutionZ) {
        if (noises == null) {
            noises = new double[noiseResolutionX * noiseResolutionY * noiseResolutionZ];
        }

        double var8 = 684.412D;
        double var10 = 684.412D;
        double[] var12 = this.level.getBiomeSource().temperatureNoises;
        double[] var13 = this.level.getBiomeSource().rainfallNoises;
        this.biomeNoises = this.biomeNoise.sample(this.biomeNoises, chunkX, chunkZ, noiseResolutionX, noiseResolutionZ, 1.121D, 1.121D, 0.5D);
        this.depthNoises = this.depthNoise.sample(this.depthNoises, chunkX, chunkZ, noiseResolutionX, noiseResolutionZ, 200.0D, 200.0D, 0.5D);
        this.interpolationNoises = this.interpolationNoise.sample(this.interpolationNoises, (double)chunkX, (double)chunkY, (double)chunkZ, noiseResolutionX, noiseResolutionY, noiseResolutionZ, var8 / 80.0D, var10 / 160.0D, var8 / 80.0D);
        this.upperInterpolationNoises = this.upperInterpolationNoise.sample(this.upperInterpolationNoises, (double)chunkX, (double)chunkY, (double)chunkZ, noiseResolutionX, noiseResolutionY, noiseResolutionZ, var8, var10, var8);
        this.lowerInterpolationNoises = this.lowerInterpolationNoise.sample(this.lowerInterpolationNoises, (double)chunkX, (double)chunkY, (double)chunkZ, noiseResolutionX, noiseResolutionY, noiseResolutionZ, var8, var10, var8);
        int var14 = 0;
        int var15 = 0;
        int var16 = 16 / noiseResolutionX;

        for(int var17 = 0; var17 < noiseResolutionX; ++var17) {
            int var18 = var17 * var16 + var16 / 2;

            for(int var19 = 0; var19 < noiseResolutionZ; ++var19) {
                int var20 = var19 * var16 + var16 / 2;
                double var21 = var12[var18 * 16 + var20];
                double var23 = var13[var18 * 16 + var20] * var21;
                double var25 = 1.0D - var23;
                var25 = var25 * var25;
                var25 = var25 * var25;
                var25 = 1.0D - var25;
                double var27 = (this.biomeNoises[var15] + 256.0D) / 512.0D;
                var27 = var27 * var25;
                if (var27 > 1.0D) {
                    var27 = 1.0D;
                }

                double var29 = this.depthNoises[var15] / 8000.0D;
                if (var29 < 0.0D) {
                    var29 = -var29 * 0.3D;
                }

                var29 = var29 * 3.0D - 2.0D;
                if (var29 < 0.0D) {
                    var29 = var29 / 2.0D;
                    if (var29 < -1.0D) {
                        var29 = -1.0D;
                    }

                    var29 = var29 / 1.4D;
                    var29 = var29 / 2.0D;
                    var27 = 0.0D;
                } else {
                    if (var29 > 1.0D) {
                        var29 = 1.0D;
                    }

                    var29 = var29 / 8.0D;
                }

                if (var27 < 0.0D) {
                    var27 = 0.0D;
                }

                var27 = var27 + 0.5D;
                var29 = var29 * (double)noiseResolutionY / 16.0D;
                double var31 = (double)noiseResolutionY / 2.0D + var29 * 4.0D;
                ++var15;

                for(int var33 = 0; var33 < noiseResolutionY; ++var33) {
                    double var34 = 0.0D;
                    double var36 = ((double)var33 - var31) * 12.0D / var27;
                    if (var36 < 0.0D) {
                        var36 *= 4.0D;
                    }

                    double var38 = this.upperInterpolationNoises[var14] / 512.0D;
                    double var40 = this.lowerInterpolationNoises[var14] / 512.0D;
                    double var42 = (this.interpolationNoises[var14] / 10.0D + 1.0D) / 2.0D;
                    if (var42 < 0.0D) {
                        var34 = var38;
                    } else if (var42 > 1.0D) {
                        var34 = var40;
                    } else {
                        var34 = var38 + (var40 - var38) * var42;
                    }

                    var34 = var34 - var36;
                    if (var33 > noiseResolutionY - 4) {
                        double var44 = (double)((float)(var33 - (noiseResolutionY - 4)) / 3.0F);
                        var34 = var34 * (1.0D - var44) + -10.0D * var44;
                    }

                    noises[var14] = var34;
                    ++var14;
                }
            }
        }

        return noises;
    }

    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return true;
    }

    public void decorate(LevelSource levelSource, int chunkX, int chunkZ) {
        Sand.fallInstantly = true;
        int var4 = chunkX * 16;
        int var5 = chunkZ * 16;
        Biome var6 = this.level.getBiomeSource().getBiome(var4 + 16, var5 + 16);
        this.rand.setSeed(this.level.getSeed());
        long var7 = this.rand.nextLong() / 2L * 2L + 1L;
        long var9 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long)chunkX * var7 + (long)chunkZ * var9 ^ this.level.getSeed());
        double var11 = 0.25D;
        if (this.rand.nextInt(4) == 0) {
            int var13 = var4 + this.rand.nextInt(16) + 8;
            int var14 = this.rand.nextInt(128);
            int var15 = var5 + this.rand.nextInt(16) + 8;
            (new Lake(BlockBase.STILL_WATER.id)).generate(this.level, this.rand, var13, var14, var15);
        }

        if (this.rand.nextInt(8) == 0) {
            int var26 = var4 + this.rand.nextInt(16) + 8;
            int var38 = this.rand.nextInt(this.rand.nextInt(120) + 8);
            int var50 = var5 + this.rand.nextInt(16) + 8;
            if (var38 < 64 || this.rand.nextInt(10) == 0) {
                (new Lake(BlockBase.STILL_LAVA.id)).generate(this.level, this.rand, var26, var38, var50);
            }
        }

        for(int var27 = 0; var27 < 8; ++var27) {
            int var39 = var4 + this.rand.nextInt(16) + 8;
            int var51 = this.rand.nextInt(128);
            int var16 = var5 + this.rand.nextInt(16) + 8;
            (new Dungeon()).generate(this.level, this.rand, var39, var51, var16);
        }

        for(int var28 = 0; var28 < 10; ++var28) {
            int var40 = var4 + this.rand.nextInt(16);
            int var52 = this.rand.nextInt(128);
            int var63 = var5 + this.rand.nextInt(16);
            (new ClayDeposit(32)).generate(this.level, this.rand, var40, var52, var63);
        }

        for(int var29 = 0; var29 < 20; ++var29) {
            int var41 = var4 + this.rand.nextInt(16);
            int var53 = this.rand.nextInt(128);
            int var64 = var5 + this.rand.nextInt(16);
            (new Ore(BlockBase.DIRT.id, 32)).generate(this.level, this.rand, var41, var53, var64);
        }

        for(int var30 = 0; var30 < 10; ++var30) {
            int var42 = var4 + this.rand.nextInt(16);
            int var54 = this.rand.nextInt(128);
            int var65 = var5 + this.rand.nextInt(16);
            (new Ore(BlockBase.GRAVEL.id, 32)).generate(this.level, this.rand, var42, var54, var65);
        }

        for(int var31 = 0; var31 < 20; ++var31) {
            int var43 = var4 + this.rand.nextInt(16);
            int var55 = this.rand.nextInt(128);
            int var66 = var5 + this.rand.nextInt(16);
            (new Ore(BlockBase.COAL_ORE.id, 16)).generate(this.level, this.rand, var43, var55, var66);
        }

        for(int var32 = 0; var32 < 20; ++var32) {
            int var44 = var4 + this.rand.nextInt(16);
            int var56 = this.rand.nextInt(64);
            int var67 = var5 + this.rand.nextInt(16);
            (new Ore(BlockBase.IRON_ORE.id, 8)).generate(this.level, this.rand, var44, var56, var67);
        }

        for(int var33 = 0; var33 < 2; ++var33) {
            int var45 = var4 + this.rand.nextInt(16);
            int var57 = this.rand.nextInt(32);
            int var68 = var5 + this.rand.nextInt(16);
            (new Ore(BlockBase.GOLD_ORE.id, 8)).generate(this.level, this.rand, var45, var57, var68);
        }

        for(int var34 = 0; var34 < 8; ++var34) {
            int var46 = var4 + this.rand.nextInt(16);
            int var58 = this.rand.nextInt(16);
            int var69 = var5 + this.rand.nextInt(16);
            (new Ore(BlockBase.REDSTONE_ORE.id, 7)).generate(this.level, this.rand, var46, var58, var69);
        }

        for(int var35 = 0; var35 < 1; ++var35) {
            int var47 = var4 + this.rand.nextInt(16);
            int var59 = this.rand.nextInt(16);
            int var70 = var5 + this.rand.nextInt(16);
            (new Ore(BlockBase.DIAMOND_ORE.id, 7)).generate(this.level, this.rand, var47, var59, var70);
        }

        for(int var36 = 0; var36 < 1; ++var36) {
            int var48 = var4 + this.rand.nextInt(16);
            int var60 = this.rand.nextInt(16) + this.rand.nextInt(16);
            int var71 = var5 + this.rand.nextInt(16);
            (new Ore(BlockBase.LAPIS_LAZULI_ORE.id, 6)).generate(this.level, this.rand, var48, var60, var71);
        }

        var11 = 0.5D;
        int var37 = (int)((this.treeNoise.sample((double)var4 * var11, (double)var5 * var11) / 8.0D + this.rand.nextDouble() * 4.0D + 4.0D) / 3.0D);
        int var49 = 0;
        if (this.rand.nextInt(10) == 0) {
            ++var49;
        }

        if (var6 == Biome.FOREST) {
            var49 += var37 + 5;
        }

        if (var6 == Biome.RAINFOREST) {
            var49 += var37 + 5;
        }

        if (var6 == Biome.SEASONAL_FOREST) {
            var49 += var37 + 2;
        }

        if (var6 == Biome.TAIGA) {
            var49 += var37 + 5;
        }

        if (var6 == Biome.DESERT) {
            var49 -= 20;
        }

        if (var6 == Biome.TUNDRA) {
            var49 -= 20;
        }

        if (var6 == Biome.PLAINS) {
            var49 -= 20;
        }

        for(int var61 = 0; var61 < var49; ++var61) {
            int var72 = var4 + this.rand.nextInt(16) + 8;
            int var17 = var5 + this.rand.nextInt(16) + 8;
            Structure var18 = var6.getTree(this.rand);
            var18.method_1143(1.0D, 1.0D, 1.0D);
            var18.generate(this.level, this.rand, var72, this.level.getHeight(var72, var17), var17);
        }

        byte var62 = 0;
        if (var6 == Biome.FOREST) {
            var62 = 2;
        }

        if (var6 == Biome.SEASONAL_FOREST) {
            var62 = 4;
        }

        if (var6 == Biome.TAIGA) {
            var62 = 2;
        }

        if (var6 == Biome.PLAINS) {
            var62 = 3;
        }

        for(int var73 = 0; var73 < var62; ++var73) {
            int var76 = var4 + this.rand.nextInt(16) + 8;
            int var85 = this.rand.nextInt(128);
            int var19 = var5 + this.rand.nextInt(16) + 8;
            (new Mushroom(BlockBase.DANDELION.id)).generate(this.level, this.rand, var76, var85, var19);
        }

        byte var74 = 0;
        if (var6 == Biome.FOREST) {
            var74 = 2;
        }

        if (var6 == Biome.RAINFOREST) {
            var74 = 10;
        }

        if (var6 == Biome.SEASONAL_FOREST) {
            var74 = 2;
        }

        if (var6 == Biome.TAIGA) {
            var74 = 1;
        }

        if (var6 == Biome.PLAINS) {
            var74 = 10;
        }

        for(int var77 = 0; var77 < var74; ++var77) {
            byte var86 = 1;
            if (var6 == Biome.RAINFOREST && this.rand.nextInt(3) != 0) {
                var86 = 2;
            }

            int var97 = var4 + this.rand.nextInt(16) + 8;
            int var20 = this.rand.nextInt(128);
            int var21 = var5 + this.rand.nextInt(16) + 8;
            (new TallGrass(BlockBase.TALLGRASS.id, var86)).generate(this.level, this.rand, var97, var20, var21);
        }

        var74 = 0;
        if (var6 == Biome.DESERT) {
            var74 = 2;
        }

        for(int var78 = 0; var78 < var74; ++var78) {
            int var87 = var4 + this.rand.nextInt(16) + 8;
            int var98 = this.rand.nextInt(128);
            int var108 = var5 + this.rand.nextInt(16) + 8;
            (new Deadbush(BlockBase.DEADBUSH.id)).generate(this.level, this.rand, var87, var98, var108);
        }

        if (this.rand.nextInt(2) == 0) {
            int var79 = var4 + this.rand.nextInt(16) + 8;
            int var88 = this.rand.nextInt(128);
            int var99 = var5 + this.rand.nextInt(16) + 8;
            (new Mushroom(BlockBase.ROSE.id)).generate(this.level, this.rand, var79, var88, var99);
        }

        if (this.rand.nextInt(4) == 0) {
            int var80 = var4 + this.rand.nextInt(16) + 8;
            int var89 = this.rand.nextInt(128);
            int var100 = var5 + this.rand.nextInt(16) + 8;
            (new Mushroom(BlockBase.BROWN_MUSHROOM.id)).generate(this.level, this.rand, var80, var89, var100);
        }

        if (this.rand.nextInt(8) == 0) {
            int var81 = var4 + this.rand.nextInt(16) + 8;
            int var90 = this.rand.nextInt(128);
            int var101 = var5 + this.rand.nextInt(16) + 8;
            (new Mushroom(BlockBase.RED_MUSHROOM.id)).generate(this.level, this.rand, var81, var90, var101);
        }

        for(int var82 = 0; var82 < 10; ++var82) {
            int var91 = var4 + this.rand.nextInt(16) + 8;
            int var102 = this.rand.nextInt(128);
            int var109 = var5 + this.rand.nextInt(16) + 8;
            (new SugarCane()).generate(this.level, this.rand, var91, var102, var109);
        }

        if (this.rand.nextInt(32) == 0) {
            int var83 = var4 + this.rand.nextInt(16) + 8;
            int var92 = this.rand.nextInt(128);
            int var103 = var5 + this.rand.nextInt(16) + 8;
            (new PumpkinPatch()).generate(this.level, this.rand, var83, var92, var103);
        }

        int var84 = 0;
        if (var6 == Biome.DESERT) {
            var84 += 10;
        }

        for(int var93 = 0; var93 < var84; ++var93) {
            int var104 = var4 + this.rand.nextInt(16) + 8;
            int var110 = this.rand.nextInt(128);
            int var114 = var5 + this.rand.nextInt(16) + 8;
            (new Cactus()).generate(this.level, this.rand, var104, var110, var114);
        }

        for(int var94 = 0; var94 < 50; ++var94) {
            int var105 = var4 + this.rand.nextInt(16) + 8;
            int var111 = this.rand.nextInt(this.rand.nextInt(120) + 8);
            int var115 = var5 + this.rand.nextInt(16) + 8;
            (new Spring(BlockBase.FLOWING_WATER.id)).generate(this.level, this.rand, var105, var111, var115);
        }

        for(int var95 = 0; var95 < 20; ++var95) {
            int var106 = var4 + this.rand.nextInt(16) + 8;
            int var112 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(112) + 8) + 8);
            int var116 = var5 + this.rand.nextInt(16) + 8;
            (new Spring(BlockBase.FLOWING_LAVA.id)).generate(this.level, this.rand, var106, var112, var116);
        }

        this.temperatureNoises = this.level.getBiomeSource().getTemperatures(this.temperatureNoises, var4 + 8, var5 + 8, 16, 16);

        for(int var96 = var4 + 8; var96 < var4 + 8 + 16; ++var96) {
            for(int var107 = var5 + 8; var107 < var5 + 8 + 16; ++var107) {
                int var113 = var96 - (var4 + 8);
                int var117 = var107 - (var5 + 8);
                int var22 = this.level.method_228(var96, var107);
                double var23 = this.temperatureNoises[var113 * 16 + var117] - (double)(var22 - 64) / 64.0D * 0.3D;
                if (var23 < 0.5D && var22 > 0 && var22 < 128 && this.level.isAir(var96, var22, var107) && this.level.getMaterial(var96, var22 - 1, var107).blocksMovement() && this.level.getMaterial(var96, var22 - 1, var107) != Material.ICE) {
                    this.level.setTile(var96, var22, var107, BlockBase.SNOW.id);
                }
            }
        }

        Sand.fallInstantly = false;
    }

    public boolean deleteCacheCauseClientCantHandleThis(boolean iDontKnowWhy, ProgressListener listener) {
        return true;
    }

    public boolean method_1801() {
        return false;
    }

    public boolean method_1805() {
        return true;
    }

    @Environment(EnvType.CLIENT)
    public String toString() {
        return "RandomLevelSource";
    }
}
*/