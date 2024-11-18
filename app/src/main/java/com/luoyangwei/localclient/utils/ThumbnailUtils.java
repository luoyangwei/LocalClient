package com.luoyangwei.localclient.utils;

import android.content.Context;

import java.io.File;

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
public class ThumbnailUtils {
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
