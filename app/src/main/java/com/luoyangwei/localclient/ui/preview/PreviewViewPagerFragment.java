package com.luoyangwei.localclient.ui.preview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.transition.platform.MaterialArcMotion;
import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.databinding.FragmentPreviewPagerViewBinding;
import com.luoyangwei.localclient.ui.photo.ImageViewShareAnimation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;

public class PreviewViewPagerFragment extends Fragment {
    private static final String TAG = PreviewViewPagerFragment.class.getName();
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private FragmentPreviewPagerViewBinding binding;

    private final Resource resource;

    public PreviewViewPagerFragment(Resource resource) {
        this.resource = resource;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPreviewPagerViewBinding.inflate(inflater);

        TransitionSet transitionSet = new TransitionSet();
        transitionSet
                .addTransition(new ImageViewShareAnimation())
                .addTransition(new ChangeImageTransform())
                .addTransition(new ChangeBounds())
                .addTransition(new ChangeClipBounds())
                .addTransition(new ChangeTransform())
                .setPathMotion(new MaterialArcMotion());
        transitionSet.setDuration(200L);
        setSharedElementEnterTransition(transitionSet);
        setSharedElementReturnTransition(transitionSet);

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        binding.previewImageView.setTransitionName(resource.getName());
        Glide.with(this)
                .load(resource.getThumbnailPath())
                .dontAnimate()
                .apply(requestOptions)
                .into(binding.previewImageView);
        // 异步加载原图
        executor.execute(this::asyncOriginalImageLoad);
//        GestureDetector gestureDetector = new GestureDetector(getContext(), new PreviewImageGestureDetector(requireActivity(), resource));
//        binding.previewImageView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
        return binding.getRoot();
    }

    @SneakyThrows
    private void asyncOriginalImageLoad() {
        Thread.sleep(300);
        RequestBuilder<Drawable> requestBuilder = Glide.with(this)
                .load(resource.getFullPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(binding.previewImageView.getDrawable());
        requireActivity().runOnUiThread(() -> requestBuilder.into(binding.previewImageView));
    }

    /**
     * 预览图片手势监听器
     */
    private static class PreviewImageGestureDetector implements GestureDetector.OnGestureListener {
        private static final float FLING_VELOCITY_THRESHOLD = 50;
        private final Activity activity;
        private final Resource resource;

        private long pressTime = 0L;
        private float touchDownY;

        public PreviewImageGestureDetector(Activity activity, Resource resource) {
            this.activity = activity;
            this.resource = resource;
        }

        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            pressTime = System.currentTimeMillis();
            touchDownY = e.getY();
            return true;
        }

        @Override
        public void onShowPress(@NonNull MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
            if (distanceY > 0) {
//                float alpha = activity.getWindow().getAttributes().alpha;
//                alpha -= distanceY / activity.getResources().getDisplayMetrics().heightPixels;
//                alpha = Math.max(0f, Math.min(1f, alpha));
//                Window window = activity.getWindow();
//                WindowManager.LayoutParams params = window.getAttributes();
//                params.alpha = alpha;
//                window.setAttributes(params);
//                float touchUpY = e2.getY();
//                float v = Math.abs(touchUpY - touchDownY);
//                Log.i(TAG, String.format("向上滑动 %f", v));

            } else {
                System.out.println("向下滑动");
            }
            return true;
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
        }

        @Override
        public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            float touchUpY = e2.getY();
            if (touchUpY < touchDownY && Math.abs(velocityY) > FLING_VELOCITY_THRESHOLD) {
                // 判断为向上快速滑动（松开后触发，通过起始和结束Y坐标以及速度阈值判断）
                Log.d(TAG, "执行上划松开后的操作");
                Intent intent = new Intent(activity, PreviewDetailsActivity.class);
                intent.putExtra("path", resource.getFullPath());
                intent.putExtra("transitionName", resource.getName());

                ImageView imageView = activity.findViewById(R.id.preview_image_view);
                imageView.setTransitionName(resource.getName());

                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imageView, resource.getName());
                activity.startActivity(intent, activityOptionsCompat.toBundle());
            } else {
                Log.w(TAG, "没达到阈值");
            }
            return true;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
