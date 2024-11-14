package com.luoyangwei.localclient.data.source.local;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import com.luoyangwei.localclient.data.model.Thumbnail;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.data.repository.ThumbnailRepository;
import com.luoyangwei.localclient.utils.BitmapUtil;
import com.luoyangwei.localclient.utils.DiskLruCacheUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Data;

public class BackgroundResourceLoaderService extends Service {
    private static final String TAG = BackgroundResourceLoaderService.class.getName();
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public BackgroundResourceLoaderService() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Cursor cursor = query(getApplicationContext().getContentResolver());
        ThumbnailRepository repository = AppDatabase.getInstance(getApplicationContext()).thumbnailRepository();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Resource resource = new Resource(cursor);
                // 加载图片，然后生成缩略图，保存到 DiskLruCache
                executor.execute(() -> {
                    Thumbnail thumbnail = repository.queryByResourceId(resource.getId());
                    if (thumbnail == null) {

                        Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(resource.getFullPath()), 200, 200);
                        bitmap = BitmapUtil.rotateBitmap(bitmap, resource.getOrientation());
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
                        DiskLruCacheUtil.putInputStream(resource.getId(), new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

                        // 加入到数据库
                        thumbnail = new Thumbnail();
                        thumbnail.resourceId = resource.getId();
                        repository.insert(thumbnail);

                        Log.i(TAG, "已加载图片：" + resource.getFullPath());
                    }
                });
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "BackgroundResourceLoaderService 已启动");

        return START_STICKY;
    }

    @Data
    public static class Resource {

        /**
         * id
         */
        private String id;

        /**
         * 全名（不带后缀）
         */
        private String name;

        /**
         * 全名（带后缀）
         * a.png
         */
        private String fullName;

        /**
         * 文件全路径
         */
        private String fullPath;

        private Integer orientation;

        @SuppressLint("Range")
        public Resource(Cursor cursor) {
            setId(String.format(Locale.getDefault(), "%d", cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))));
            setName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE)));
            setFullName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
            setFullPath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            setOrientation(cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION)));
        }
    }

    private Cursor query(ContentResolver contentResolver) {
        return contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Images.Media.DATE_ADDED + " DESC");
    }
}