package com.luoyangwei.localclient.utils;

import com.jakewharton.disklrucache.DiskLruCache;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DiskLruCacheUtil {
    private static DiskLruCache diskLruCache;

    public DiskLruCache initDiskLruCache(File file) {
        try {
            diskLruCache = DiskLruCache.open(file, 1, 1, 10 * 1024 * 1024);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return diskLruCache;
    }


    public File mkdirs(File base) {
        String dir = base + File.separator + "disk_lru";
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 将key进行加密
     *
     * @param key key
     * @return 加密后的key
     */
    private String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public void putFile(String key, File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            putInputStream(key, inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void putInputStream(String key, InputStream inputStream) {
        try {
            DiskLruCache.Editor editor = diskLruCache.edit(hashKeyForDisk(key));
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                IOUtils.copy(inputStream, outputStream);
                editor.commit();
                diskLruCache.flush();
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 判断是否存在key
     *
     * @param diskLruCache DiskLruCache
     * @param key          key
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        try (DiskLruCache.Snapshot snapshot = diskLruCache.get(hashKeyForDisk(key))) {
            return snapshot != null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DiskLruCache.Snapshot getFile(String key) {
        try {
            return diskLruCache.get(hashKeyForDisk(key));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
