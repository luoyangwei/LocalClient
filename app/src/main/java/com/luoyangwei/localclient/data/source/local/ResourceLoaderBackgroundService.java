package com.luoyangwei.localclient.data.source.local;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;

public class ResourceLoaderBackgroundService extends Service {
    private static final String TAG = ResourceLoaderBackgroundService.class.getName();
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public ResourceLoaderBackgroundService() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    @SneakyThrows
    public void onCreate() {
        super.onCreate();
//        Cursor cursor = query(getApplicationContext().getContentResolver());
//        ThumbnailRepository repository = AppDatabase.getInstance(getApplicationContext()).thumbnailRepository();
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                Resource resource = new Resource(cursor);
//                // 加载图片，然后生成缩略图，保存到 DiskLruCache
//                executor.execute(() -> {
//                    Thumbnail thumbnail = repository.queryByResourceId(resource.getId());
//                    if (thumbnail == null) {
//
//                        Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(resource.getFullPath()), 200, 200);
//                        bitmap = BitmapUtil.rotateBitmap(bitmap, resource.getOrientation());
//                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
//                        DiskLruCacheUtil.putInputStream(resource.getId(), new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
//
//                        // 加入到数据库
//                        thumbnail = new Thumbnail();
//                        thumbnail.resourceId = resource.getId();
//                        repository.insert(thumbnail);
//
//                        Log.i(TAG, "已加载图片：" + resource.getFullPath());
//                    }
//                });
//            }
//        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SneakyThrows
    private void run() {
        Log.i(TAG, "BackgroundResourceLoaderService 已启动");
        for (int i = 0; i < 1000; i++) {
            Thread.sleep(1000);
            Log.i(TAG, "ResourceLoaderBackgroundService 正在运行");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        new Thread(this::run).start();
        return START_STICKY;
    }
}