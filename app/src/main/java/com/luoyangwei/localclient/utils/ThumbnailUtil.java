package com.luoyangwei.localclient.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Size;

import com.luoyangwei.localclient.data.model.Image;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.data.repository.ImageRepository;
import com.luoyangwei.localclient.data.source.local.ResourceService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import lombok.experimental.UtilityClass;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 缩略图工具
 *
 * @author luoyangwei
 * @date 2024年11月18日14:32:29
 */
@UtilityClass
public class ThumbnailUtil {
    private static final String TAG = ThumbnailUtil.class.getName();
    private static final int IGNORE_BY_SIZE = 100;

    /**
     * 生成缩略图
     *
     * @param context    上下文
     * @param targetFile 目标文件
     * @param outputDir  输出目录
     * @param listener   监听结果
     */
    public void generation(Context context, File targetFile, File outputDir, OnCompressListener listener) {
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        Luban.with(context).load(targetFile)
                .ignoreBy(IGNORE_BY_SIZE)
                .setTargetDir(outputDir.getAbsolutePath())
                .setCompressListener(listener)
                .launch();
    }

    public void generation(Context context, Resource resource, File outputDir, Consumer<Bitmap> consumer) {
        ResourceService resourceService = new ResourceService(context);
        Bitmap bitmap = resourceService.loadThumbnail(resource.getUri(), new Size(1000, 1000));
        if (bitmap == null) {
            return;
        }

        consumer.accept(bitmap);
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputDir)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
        } catch (IOException e) {
            Log.e(TAG, "persistenceThumbnail: ", e);
        }

        CompletableFuture.runAsync(() -> {
            // 生成文件加入缓存
            ImageRepository repository = AppDatabase.getInstance(context).imageRepository();
            Image image = repository.findByResourcesId(resource.getId());
            if (image == null) {
                image = Image.getInstance(resource);
                image.isHasThumbnail = true;
                image.thumbnailPath = outputDir.getAbsolutePath();
                repository.insert(image);
            } else {
                image.isHasThumbnail = true;
                image.thumbnailPath = outputDir.getAbsolutePath();
                repository.update(image);
            }
        });
    }


    /**
     * 获取输出缩略图路径
     *
     * @param context 上下文
     * @return 缩略图路径
     */
    public String getOutputThumbnailPath(Context context) {
        return context.getExternalCacheDir() + File.separator + ".thumbnails";
    }

    public abstract class CompressListener implements OnCompressListener {
        public abstract void asyncComplete(File file);

        @Override
        public void onStart() {
            // not implemented
        }

        @Override
        public void onSuccess(File file) {
            new Thread(() -> asyncComplete(file)).start();
        }

        @Override
        public void onError(Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
