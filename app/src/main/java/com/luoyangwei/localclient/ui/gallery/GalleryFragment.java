package com.luoyangwei.localclient.ui.gallery;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.Image;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.data.repository.ImageRepository;
import com.luoyangwei.localclient.databinding.FragmentGalleryBinding;

import java.util.concurrent.CompletableFuture;

import lombok.SneakyThrows;

public class GalleryFragment extends Fragment implements RequestListener<Drawable>, View.OnTouchListener {
    private static final String TAG = GalleryFragment.class.getName();
    private final RequestOptions requestOptions = new RequestOptions();
    private FragmentGalleryBinding binding;
    private final Resource resource;
    private GestureDetector gestureDetector;
    private ViewPager2 viewPager;

    public GalleryFragment(Resource resource) {
        this.resource = resource;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     * @see com.github.chrisbanes.photoview.PhotoView ImageView
     */
    @SuppressLint("ClickableViewAccessibility")
    @SneakyThrows
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        viewPager = requireActivity().findViewById(R.id.viewpager);

        binding.imageView.setTransitionName(resource.getId());
        binding.imageView.setMaximumScale(5f);
        binding.imageView.setMediumScale(3f);
        binding.imageView.setMinimumScale(1f);
        binding.imageView.setOnScaleChangeListener((scaleFactor, focusX, focusY) -> viewPager.setUserInputEnabled(scaleFactor == 1f));

//        gestureDetector = new GestureDetector(requireContext(), new ImageViewGestureListener(binding.imageView));
        binding.imageView.setOnTouchListener(this);

        ImageRepository repository = AppDatabase.getInstance(requireContext()).imageRepository();
        Image image = CompletableFuture.supplyAsync(() -> repository.findByResourcesId(resource.getId())).get();

        Glide.with(this)
                .load(image.thumbnailPath)
                .dontAnimate()
                .apply(requestOptions)
                .listener(this)
                .into(binding.imageView);
        return binding.getRoot();
    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model,
                                @NonNull Target<Drawable> target, boolean isFirstResource) {
        requireActivity().startPostponedEnterTransition();
        asyncOriginalImageLoad();
        return false;
    }

    @Override
    public boolean onResourceReady(@NonNull Drawable resource,
                                   @NonNull Object model, Target<Drawable> target,
                                   @NonNull DataSource dataSource, boolean isFirstResource) {
        requireActivity().startPostponedEnterTransition();
        asyncOriginalImageLoad();
        return false;
    }

    /**
     * 异步加载原始图片
     */
    @SneakyThrows
    private void asyncOriginalImageLoad() {
        binding.imageView.postDelayed(() -> {
            RequestBuilder<Drawable> requestBuilder = Glide.with(this)
                    .load(resource.getFullPath())
                    .placeholder(binding.imageView.getDrawable());
            requireActivity().runOnUiThread(() -> requestBuilder.into(binding.imageView));
        }, 300);

    }

    private float startY = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        PhotoViewAttacher attacher = binding.imageView.getAttacher();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                viewPager.setUserInputEnabled(false);
                break;

            case MotionEvent.ACTION_MOVE:
                float currentY = event.getY();
                float deltaY = currentY - startY;
                // 计算缩放比例，缩放范围 [0.5, 1.0]
                float scale = Math.max(0.5f, v.getScaleX() - (deltaY / binding.imageView.getHeight()));

                if (attacher.getScale() <= attacher.getMinimumScale()) {
                    // 仅当未缩放时，处理下滑逻辑
                    if (deltaY > 0) {
                        v.setTranslationY(v.getTranslationY() + deltaY);
                        v.setScaleX(scale);
                        v.setScaleY(scale);
                        // TODO 支持根据下滑幅度设置透明度
                        return true; // 拦截事件
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouch: " + v.getTranslationY());
                if (v.getTranslationY() >= 800) {
                    requireActivity().getOnBackPressedDispatcher().onBackPressed(); // 返回逻辑
                } else {
                    v.animate()
                            .translationY(0)
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(200)
                            .start(); // 回弹
                }
                viewPager.setUserInputEnabled(true);
                break;
        }
        return binding.imageView.getAttacher().onTouch(v, event); // 始终将事件传递给 PhotoView
    }

    private static class ImageViewGestureListener extends GestureDetector.SimpleOnGestureListener {
        private final PhotoView target;
        private float startY = 0;

        public ImageViewGestureListener(PhotoView target) {
            this.target = target;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            startY = e.getY();
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceY) > Math.abs(distanceX) && distanceY < 0) {
                Log.i(TAG, "distanceY: " + distanceY);
                target.setTranslationY(target.getTranslationY() - distanceY);
            }
            return false;
        }
    }
}
