package com.luoyangwei.localclient.ui.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.luoyangwei.localclient.utils.BitmapUtil;
import com.luoyangwei.localclient.utils.DiskLruCacheUtil;

import lombok.Getter;

public class CacheImageView extends AppCompatImageView {
//    private final ImageResourceService imageResourceService;

    @Getter
    private String resourceId;

    public CacheImageView(@NonNull Context context) {
        super(context);
//        imageResourceService = ImageResourceService.getInstance(context);
    }

    public CacheImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        imageResourceService = ImageResourceService.getInstance(context);
    }

    public CacheImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        imageResourceService = ImageResourceService.getInstance(context);
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
        // 检查是否有缓存
        ImageResourceService imageResourceService = ImageResourceService.getInstance(getContext());
//        DiskLruCache diskLruCache = imageResourceService.initDiskLruCache(getContext());
        if (DiskLruCacheUtil.hasKey(resourceId)) {
            // 如果有缓存，就显示缓存
//            InputStream inputStream = DiskLruCacheUtil.getFile(resourceId);
//            Bitmap bm = BitmapFactory.decodeStream(inputStream);
//            super.setImageBitmap(bm);
        } else {
            Drawable drawable = getDrawable();
            if (drawable != null) {
                Bitmap bitmap = BitmapUtil.drawableToBitmap(drawable);
                super.setImageBitmap(bitmap);
//                DiskLruCacheUtil.putFile(diskLruCache, resourceId, bitmap);
            }
        }
    }
}
