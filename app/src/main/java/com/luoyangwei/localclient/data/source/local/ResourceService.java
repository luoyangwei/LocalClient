package com.luoyangwei.localclient.data.source.local;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.luoyangwei.localclient.data.model.Resource;

import java.util.ArrayList;
import java.util.List;

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
    public List<Resource> getResources() {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = query(contentResolver);
        List<Resource> resources = new ArrayList<>();
        while (cursor.moveToNext()) {
            resources.add(new Resource(cursor));
        }
        cursor.close();
        return resources;
    }


    private Cursor query(ContentResolver contentResolver) {
        String[] projection = new String[]{
//                MediaStore.Images.Media.TITLE,
//                MediaStore.Images.Media.DISPLAY_NAME,
//                MediaStore.Images.Media.DATA,
//                MediaStore.Images.Media.ORIENTATION
        };
        return contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");
    }
}
