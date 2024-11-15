package com.luoyangwei.localclient.data.source.local;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.data.repository.ThumbnailRepository;

import java.io.File;
import java.util.List;

/**
 * 生成缩略图的服务
 *
 * @author luoyangwei
 * @date 2024年11月15日18:55:01
 */
public class ThumbnailGenerationService extends Service {
    private static final String TAG = ThumbnailGenerationService.class.getName();
    private ResourceService resourceService;
    private Thread thread;
    private ThumbnailRepository thumbnailRepository;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        resourceService = new ResourceService(this);
        thread = new Thread(this::run);
        AppDatabase.getInstance(getApplicationContext()).thumbnailRepository();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        thread.start();
        return START_STICKY;
    }

    public void run() {
        List<Resource> resources = resourceService.getResources();
        for (Resource resource : resources) {
            createFolderIfNotExists(resource.getBucketName());
            // 压缩图片

//            Compressor.INSTANCE.compress(getApplicationContext(), new File(resource.getFullPath()), ( options) -> {
//                options.resolution(1280, 720);
//                options.quality(80);
//                options.format(Bitmap.CompressFormat.WEBP);
//                options.size(2_097_152);  // 2 MB
//                return null;
//            });
            // 存入数据库
        }
        Log.i(TAG, "资源数量：" + resources.size());
    }

    /**
     * 文件夹不存在就创建
     */
    public void createFolderIfNotExists(String name) {
        File thumbnailsFile = new File(getApplicationContext().getExternalCacheDir() + File.separator + "thumbnails" + File.separator + name);
        if (!thumbnailsFile.exists()) {
            thumbnailsFile.mkdirs();
        }
    }
}
