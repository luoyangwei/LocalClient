package com.luoyangwei.localclient.ui.main;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.google.android.material.motion.MotionUtils;
import com.luoyangwei.localclient.data.AppLoadingCache;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.databinding.ActivityMatrixImageBinding;
import com.luoyangwei.localclient.ui.ApplicationActivity;
import com.luoyangwei.localclient.utils.BitmapUtil;

import java.util.List;

public class MatrixImageActivity extends ApplicationActivity implements View.OnClickListener {
    private ActivityMatrixImageBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMatrixImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        List<Resource> resources = AppLoadingCache.getInstance(getApplicationContext()).getResources();
        Resource resource = resources.get(1);

        Bitmap bitmap = BitmapFactory.decodeFile(resource.getFullPath());
        bitmap = BitmapUtil.rotateBitmap(bitmap, resource.getOrientation());
        binding.imageView.setImageBitmap(bitmap);

        binding.button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        DisplayMetrics displayMetrics = binding.imageView.getResources().getDisplayMetrics();
        float width = binding.imageView.getWidth();
        float height = binding.imageView.getHeight();

        float drawableWidth = binding.imageView.getDrawable().getIntrinsicWidth();
        float drawableHeight = binding.imageView.getDrawable().getIntrinsicHeight();

        binding.imageView.setScaleType(ImageView.ScaleType.MATRIX);
        Matrix matrix = new Matrix();

        TimeInterpolator interpolator = MotionUtils.resolveThemeInterpolator(getApplicationContext(),
                com.google.android.material.R.attr.motionEasingStandardInterpolator,
                new FastOutSlowInInterpolator());

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 0.5f);
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setInterpolator(interpolator);

        valueAnimator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
//            matrix.setTranslate((width - drawableWidth) * 0.5f, (height - drawableHeight) * 0.5f);
            matrix.setTranslate((width - drawableWidth) * animatedValue, (height - drawableHeight) * animatedValue);
            binding.imageView.setImageMatrix(matrix);
        });
        valueAnimator.start();
    }
}
