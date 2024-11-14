package com.luoyangwei.localclient.data.source.local;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public class ImageThumbnailGenerator {
    private static final int width = 300;
    private static final int height = 300;

    public Bitmap generation(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 首先只获取图片的基本信息，所以这里 inJustDecodeBounds 设置 true
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = Math.min(beWidth, beHeight);
        if (be <= 0) {
            be = 1;
        }

        options.inSampleSize = be;

        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        return bitmap;
    }
}
