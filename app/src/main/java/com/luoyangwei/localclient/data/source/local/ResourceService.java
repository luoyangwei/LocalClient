package com.luoyangwei.localclient.data.source.local;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.luoyangwei.localclient.data.model.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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

    public Resource getResource(Long id) {
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

    private Cursor queryById(ContentResolver contentResolver, Long id) {
        String[] projection = new String[]{};
        String selection = MediaStore.Images.Media._ID + " = ?";
        String[] selectionArgs = {id.toString()};
        return contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, MediaStore.Images.Media.DATE_ADDED + " DESC");
    }


    private Cursor query(ContentResolver contentResolver) {
        String[] projection = new String[]{};
        return contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");
    }
}
