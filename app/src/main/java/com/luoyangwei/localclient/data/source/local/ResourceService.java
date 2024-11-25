package com.luoyangwei.localclient.data.source.local;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import com.luoyangwei.localclient.data.model.Bucket;
import com.luoyangwei.localclient.data.model.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 获取资源的Service
 *
 * @author luoyangwei
 * @date 2024年11月15日10:10:38
 */
public class ResourceService {
    private static final String TAG = ResourceService.class.getName();
    private final Context context;

    public ResourceService(Context context) {
        this.context = context;
    }

    public List<Resource> getResources() {
        return getResources(r -> true);
    }

    /**
     * 查询所有
     *
     * @return resources
     */
    public List<Resource> getResources(Function<Resource, Boolean> filter) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = query(contentResolver);
        List<Resource> resources = new ArrayList<>();
        while (cursor.moveToNext()) {
            Resource resource = new Resource(cursor);
            if (filter != null && filter.apply(resource)) {
                resources.add(resource);
            }
        }
        cursor.close();
        return resources;
    }

    /**
     * 加载缩略图
     *
     * @param uri  图片uri
     * @param size 缩略图大小
     * @return 缩略图
     */
    public Bitmap loadThumbnail(Uri uri, Size size) {
        ContentResolver contentResolver = context.getContentResolver();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return contentResolver.loadThumbnail(uri, size, null);
            } else {
                return MediaStore.Images.Thumbnails.getThumbnail(contentResolver, Long.parseLong(uri.getLastPathSegment()),
                        MediaStore.Images.Thumbnails.MINI_KIND, null);
            }
        } catch (IOException e) {
            Log.e(TAG, "loadThumbnail: ", e);
        }
        return null;
    }

    public Resource getResource(String id) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = queryById(contentResolver, id);
        Resource resource = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                resource = new Resource(cursor);
            }
            cursor.close();
        }
        return resource;
    }

    /**
     * 获取资源 Bucket
     *
     * @return buckets
     */
    public List<Bucket> getBuckets() {
        List<Resource> resources = getResources();

        // 取出重复项
        Map<String, String> bucketMap = new HashMap<>();
        resources.forEach(resource ->
                bucketMap.put(resource.getBucketId(), resource.getBucketName()));

        List<Bucket> buckets = new ArrayList<>();
        for (String bucketId : bucketMap.keySet()) {
            List<Resource> bucketResources = resources.stream().filter(resource -> resource.getBucketId().equals(bucketId))
                    .collect(Collectors.toList());
            Resource resource = bucketResources.get(bucketResources.size() - 1);
            Bucket bucket = new Bucket();
            bucket.setId(bucketId)
                    .setName(bucketMap.get(bucketId));
            bucket.setResources(bucketResources);
            bucket.setCount(bucketResources.size());
            bucket.setMostRecentResourceId(resource.getId());
            buckets.add(bucket);
        }
        return buckets.stream()
                .sorted(Comparator.comparingInt(Bucket::getCount).reversed())
                .collect(Collectors.toList());
    }

    private Cursor queryById(ContentResolver contentResolver, String id) {
        String[] projection = new String[]{};
        String selection = MediaStore.Images.Media._ID + " = ?";
        String[] selectionArgs = {id};
        return contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, MediaStore.Images.Media.DATE_ADDED + " DESC");
    }


    private Cursor query(ContentResolver contentResolver) {
        String[] projection = new String[]{};
        return contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");
    }
}
