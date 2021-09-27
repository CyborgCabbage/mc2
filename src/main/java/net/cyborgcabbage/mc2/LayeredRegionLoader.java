package net.cyborgcabbage.mc2;

import net.minecraft.level.storage.RegionFile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LayeredRegionLoader {
    private static final Map<File, SoftReference<RegionFile>> regionCache = new HashMap<File, SoftReference<RegionFile>>();

    private LayeredRegionLoader() {
    }

    public static synchronized RegionFile getRegion(File levelDir, int x, int y, int z) {
        File regionFolder = new File(levelDir, "region");
        File regionFile;
        if(y == 0) {
            regionFile = new File(regionFolder, "r." + (x >> 5) + "." + (z >> 5) + ".mcr");
        }else{
            regionFile = new File(regionFolder, "r." + (x >> 5) + "." + (z >> 5) + "." + y + ".mcr");
        }
        Reference<RegionFile> var5 = regionCache.get(regionFile);
        if (var5 != null) {
            RegionFile var6 = var5.get();
            if (var6 != null) {
                return var6;
            }
        }

        if (!regionFolder.exists()) {
            regionFolder.mkdirs();
        }

        if (regionCache.size() >= 256) {
            clearCache();
        }

        RegionFile var7 = new RegionFile(regionFile);
        regionCache.put(regionFile, new SoftReference<>(var7));
        return var7;
    }

    public static synchronized void clearCache() {
        for (SoftReference<RegionFile> regionFileSoftReference : regionCache.values()) {
            //try {
            RegionFile regionFile = regionFileSoftReference.get();
            if (regionFile != null) {
                regionFile.close();
            }
            //} catch (IOException var3) {
            //    var3.printStackTrace();
            //}
        }
        regionCache.clear();
    }

    public static int method_1214(File file, int x, int y, int z) {
        RegionFile regionFile = getRegion(file, x, y, z);
        return regionFile.getSizeDelta();
    }

    public static DataInputStream method_1215(File file, int x, int y, int z) {
        RegionFile regionFile = getRegion(file, x, y, z);
        return regionFile.getChunkDataInputStream(x&31,z&31);
    }

    public static DataOutputStream method_1216(File file, int x, int y, int z) {
        RegionFile regionFile = getRegion(file, x, y, z);
        return regionFile.getChunkDataOutputStream(x&31,z&31);
    }
}

