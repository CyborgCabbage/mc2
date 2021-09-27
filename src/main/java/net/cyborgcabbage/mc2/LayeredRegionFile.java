/*package net.cyborgcabbage.mc2;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class LayeredRegionFile {
    private static final byte[] emptySector = new byte[4096];
    private final File fileName;
    private RandomAccessFile file;
    private final int[] offsets;
    private final int[] timestamps;
    private ArrayList<Boolean> sectorFree;
    private int sizeDelta;
    private long lastModified = 0L;

    public LayeredRegionFile(File file) {
        this.offsets = new int[1024];
        this.timestamps = new int[1024];
        this.fileName = file;
        this.debugln("REGION LOAD " + this.fileName);
        this.sizeDelta = 0;

        try {
            if (file.exists()) {
                this.lastModified = file.lastModified();
            }

            this.file = new RandomAccessFile(file, "rw");
            if (this.file.length() < 4096L) {
                for(int var2 = 0; var2 < 1024; ++var2) {
                    this.file.writeInt(0);
                }

                for(int var7 = 0; var7 < 1024; ++var7) {
                    this.file.writeInt(0);
                }

                this.sizeDelta += 8192;
            }

            if ((this.file.length() & 4095L) != 0L) {
                for(int var8 = 0; (long)var8 < (this.file.length() & 4095L); ++var8) {
                    this.file.write(0);
                }
            }

            int var9 = (int)this.file.length() / 4096;
            this.sectorFree = new ArrayList<Boolean>(var9);

            for(int var3 = 0; var3 < var9; ++var3) {
                this.sectorFree.add(true);
            }

            this.sectorFree.set(0, false);
            this.sectorFree.set(1, false);
            this.file.seek(0L);

            for(int offsetIndex = 0; offsetIndex < 1024; ++offsetIndex) {
                int offset = this.file.readInt();
                this.offsets[offsetIndex] = offset;
                if (offset != 0 && (offset >> 8) + (offset & 255) <= this.sectorFree.size()) {
                    for(int var5 = 0; var5 < (offset & 255); ++var5) {
                        this.sectorFree.set((offset >> 8) + var5, false);
                    }
                }
            }

            for(int timestampIndex = 0; timestampIndex < 1024; ++timestampIndex) {
                int timestamp = this.file.readInt();
                this.timestamps[timestampIndex] = timestamp;
            }
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    public synchronized int getSizeDelta() {
        int sizeDelta = this.sizeDelta;
        this.sizeDelta = 0;
        return sizeDelta;
    }

    private void debug(String in) {
    }

    private void debugln(String in) {
        this.debug(in + "\n");
    }

    private void debug(String mode, int x, int y, int z, String in) {
        this.debug("REGION " + mode + " " + this.fileName.getName() + "[" + x + "," + y + "," + z +  "] = " + in);
    }

    private void debug(String mode, int x, int y, int z, int count, String in) {
        this.debug("REGION " + mode + " " + this.fileName.getName() + "[" + x + "," + y + "," + z +  "] " + count + "B = " + in);
    }

    private void debugln(String mode, int x, int y, int z, String in) {
        this.debug(mode, x, y, z, in + "\n");
    }

    public synchronized DataInputStream getChunkDataInputStream(int x, int y, int z) {
        if (this.outOfBounds(x, y, z)) {
            this.debugln("READ", x, y, z, "out of bounds");
            return null;
        } else {
            try {
                int var3 = this.getOffset(x, z);
                if (var3 == 0) {
                    return null;
                } else {
                    int var4 = var3 >> 8;
                    int var5 = var3 & 255;
                    if (var4 + var5 > this.sectorFree.size()) {
                        this.debugln("READ", x, z, "invalid sector");
                        return null;
                    } else {
                        this.file.seek(var4 * 4096);
                        int var6 = this.file.readInt();
                        if (var6 > 4096 * var5) {
                            this.debugln("READ", x, z, "invalid length: " + var6 + " > 4096 * " + var5);
                            return null;
                        } else {
                            byte var7 = this.file.readByte();
                            if (var7 == 1) {
                                byte[] var11 = new byte[var6 - 1];
                                this.file.read(var11);
                                return new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(var11)));
                            } else if (var7 == 2) {
                                byte[] var8 = new byte[var6 - 1];
                                this.file.read(var8);
                                return new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(var8)));
                            } else {
                                this.debugln("READ", x, z, "unknown version " + var7);
                                return null;
                            }
                        }
                    }
                }
            } catch (IOException var10) {
                this.debugln("READ", x, z, "exception");
                return null;
            }
        }
    }

    public DataOutputStream getChunkDataOutputStream(int x, int z) {
        return this.outOfBounds(x, z) ? null : new DataOutputStream(new DeflaterOutputStream(new LayeredRegionFile.ChunkBuffer(x, z)));
    }

    protected synchronized void write(int x, int z, byte[] data, int length) {
        try {
            int var5 = this.getOffset(x, z);
            int var6 = var5 >> 8;
            int var7 = var5 & 255;
            int var8 = (length + 5) / 4096 + 1;
            if (var8 >= 256) {
                return;
            }

            if (var6 != 0 && var7 == var8) {
                this.debug("SAVE", x, z, length, "rewrite");
                this.write(var6, data, length);
            } else {
                for(int var9 = 0; var9 < var7; ++var9) {
                    this.sectorFree.set(var6 + var9, true);
                }

                int var15 = this.sectorFree.indexOf(true);
                int var10 = 0;
                if (var15 != -1) {
                    for(int var11 = var15; var11 < this.sectorFree.size(); ++var11) {
                        if (var10 != 0) {
                            if (this.sectorFree.get(var11)) {
                                ++var10;
                            } else {
                                var10 = 0;
                            }
                        } else if (this.sectorFree.get(var11)) {
                            var15 = var11;
                            var10 = 1;
                        }

                        if (var10 >= var8) {
                            break;
                        }
                    }
                }

                if (var10 >= var8) {
                    this.debug("SAVE", x, z, length, "reuse");
                    var6 = var15;
                    this.setOffset(x, z, var15 << 8 | var8);

                    for(int var17 = 0; var17 < var8; ++var17) {
                        this.sectorFree.set(var6 + var17, false);
                    }

                    this.write(var6, data, length);
                } else {
                    this.debug("SAVE", x, z, length, "grow");
                    this.file.seek(this.file.length());
                    var6 = this.sectorFree.size();

                    for(int var16 = 0; var16 < var8; ++var16) {
                        this.file.write(emptySector);
                        this.sectorFree.add(false);
                    }

                    this.sizeDelta += 4096 * var8;
                    this.write(var6, data, length);
                    this.setOffset(x, z, var6 << 8 | var8);
                }
            }

            this.writeTimestamp(x, z, (int)(System.currentTimeMillis() / 1000L));
        } catch (IOException var12) {
            var12.printStackTrace();
        }

    }

    private void write(int sector, byte[] data, int length) {
        this.debugln(" " + sector);
        this.file.seek(sector * 4096);
        this.file.writeInt(length + 1);
        this.file.writeByte(2);
        this.file.write(data, 0, length);
    }

    private boolean outOfBounds(int x, int y, int z) {
        return x < 0 || x >= 32 || z < 0 || z >= 32;
    }

    private int getOffset(int x, int z) {
        return this.offsets[x + z * 32];
    }

    public boolean offsetHasData(int i, int j) {
        return this.getOffset(i, j) != 0;
    }

    private void setOffset(int x, int z, int offset) {
        this.offsets[x + z * 32] = offset;
        this.file.seek((x + z * 32) * 4);
        this.file.writeInt(offset);
    }

    private void writeTimestamp(int x, int z, int timestamp) {
        this.timestamps[x + z * 32] = timestamp;
        this.file.seek(4096 + (x + z * 32) * 4);
        this.file.writeInt(timestamp);
    }

    public void close() {
        this.file.close();
    }

    class ChunkBuffer extends ByteArrayOutputStream {
        private final int x;
        private final int y;
        private final int z;

        public ChunkBuffer(int x, int y, int z) {
            super(8096);
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void close() {
            LayeredRegionFile.this.write(this.x, this.y, this.z, this.buf, this.count);
        }
    }
}
*/