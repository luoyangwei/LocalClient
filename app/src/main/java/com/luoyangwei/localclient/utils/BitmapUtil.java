package com.luoyangwei.localclient.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BitmapUtil {

    public Bitmap drawableToBitmap(Drawable drawable) {
        // 获取 Drawable 的宽度和高度
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        // 如果宽度和高度无效，返回空 Bitmap
        if (width <= 0 || height <= 0) {
            width = 1;
            height = 1;
        }

        // 创建一个空的 Bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // 创建一个 Canvas 将其绑定到 Bitmap 上
        Canvas canvas = new Canvas(bitmap);

        // 设置 Drawable 的边界
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());

        // 绘制 Drawable 到 Canvas 上
        drawable.draw(canvas);

        // 返回生成的 Bitmap
        return bitmap;
    }

    public Bitmap rotateBitmap(Bitmap bitmap, Integer angle) {
        if (angle == null) {
            return bitmap;
        }

        // 创建一个矩阵对象
        Matrix matrix = new Matrix();
        // 设置旋转角度
        matrix.postRotate(angle);

        // 使用 matrix 创建一个新的旋转过的 Bitmap
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
