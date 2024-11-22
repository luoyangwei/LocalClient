package com.luoyangwei.localclient.ui.preview;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PostponeEnterTransitionHelper {
    public void loadImageWithTransition(Activity activity, RequestBuilder<Drawable> builder, ImageView imageView) {
        builder.listener(new RequestListener<>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, @Nullable Target<Drawable> target, boolean isFirstResource) {
                activity.startPostponedEnterTransition();
                return false;
            }

            @Override
            public boolean onResourceReady(@Nullable Drawable resource, @Nullable Object model, Target<Drawable> target, @Nullable DataSource dataSource, boolean isFirstResource) {
                activity.startPostponedEnterTransition();
                return false;
            }
        }).into(imageView);
    }

    public void loadImageWithTransition(Activity activity, String url, ImageView imageView) {
        Glide.with(activity.getApplicationContext())
                .load(url)
                .dontAnimate() // 防止 Glide 默认动画干扰
                .listener(new RequestListener<>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, @Nullable Target<Drawable> target, boolean isFirstResource) {
                        activity.startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@Nullable Drawable resource, @Nullable Object model, Target<Drawable> target, @Nullable DataSource dataSource, boolean isFirstResource) {
                        activity.startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(imageView);
    }
}
