package com.luoyangwei.localclient.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.luoyangwei.localclient.R;

import java.util.function.Function;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GlideUtil {

    public class ResourceRequestListener<T, R> implements RequestListener<T> {
        private final R r;
        private final Function<Target<T>, Boolean> failedCallback;
        private final Function<T, Boolean> readyCallback;

        public ResourceRequestListener(R r, Function<Target<T>, Boolean> failedCallback, Function<T, Boolean> readyCallback) {
            this.r = r;
            this.failedCallback = failedCallback;
            this.readyCallback = readyCallback;
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, @NonNull Target<T> target, boolean isFirstResource) {
            return failedCallback.apply(target);
        }

        @Override
        public boolean onResourceReady(@NonNull T resource, @NonNull Object model, Target<T> target, @NonNull DataSource dataSource, boolean isFirstResource) {
            return readyCallback.apply(resource);
        }
    }

    public <R> ResourceRequestListener<Drawable, R> drawableRequestListener(R data,
                                                                            Function<Drawable, Boolean> readyCallback) {
        return new ResourceRequestListener<>(data, (t) -> false, readyCallback);
    }


    public RequestOptions defaultOptions() {
        return RequestOptions
                .downsampleOf(DownsampleStrategy.AT_LEAST)
                .dontTransform()
                .dontAnimate()
                .encodeQuality(10)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .encodeFormat(Bitmap.CompressFormat.WEBP)
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.placeholder);
    }

//    public <R> ResourceRequestListener<Bitmap, R> bitmapRequestListener(R data, Function<R, Boolean> failedCallback, Function<R, Boolean> readyCallback) {
//        return new ResourceRequestListener<>(data, failedCallback, readyCallback);
//    }
}
