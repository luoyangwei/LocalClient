package com.luoyangwei.localclient.ui.preview;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.motion.MotionUtils;
import com.luoyangwei.localclient.data.AppLoadingCache;
import com.luoyangwei.localclient.data.model.Image;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.data.repository.ImageRepository;
import com.luoyangwei.localclient.databinding.ActivityPreviewBinding;
import com.luoyangwei.localclient.ui.ApplicationActivity;
import com.luoyangwei.localclient.ui.photo.TransitionNameChangedEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;

public class PreviewActivity extends ApplicationActivity {
    private static final String TAG = PreviewActivity.class.getName();
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    private ActivityPreviewBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.enableEdgeToEdge();
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        binding = ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TransitionSet transitionSet = new TransitionSet();
        transitionSet.addTransition(new ChangeBounds());
        transitionSet.addTransition(new ChangeTransform());
        transitionSet.addTransition(new ChangeImageTransform());
//        transitionSet.setDuration(300);
        transitionSet.setInterpolator(MotionUtils.resolveThemeInterpolator(getApplicationContext(),
                com.google.android.material.R.attr.motionEasingStandardInterpolator, new FastOutSlowInInterpolator()));
        getWindow().setSharedElementEnterTransition(transitionSet);
        getWindow().setSharedElementExitTransition(transitionSet);

        // 进入页面后直接展示缩略图
        String resourceId = getIntent().getStringExtra("resourceId");
        AppLoadingCache loadingCache = AppLoadingCache.getInstance(getApplicationContext());
        initializePreviewViewpager(loadingCache.getResources(), loadingCache.get(resourceId));

        // 启动动画效果
        postponeEnterTransition();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化预览 ViewPager
     *
     * @param resources        所有资源
     * @param selectedResource 选中的资源
     */
    @SneakyThrows
    public void initializePreviewViewpager(List<Resource> resources, Resource selectedResource) {
        ImageRepository repository = AppDatabase.getInstance(getApplicationContext()).imageRepository();
        Image image = CompletableFuture.supplyAsync(() -> repository.findByResourcesId(selectedResource.getId())).get();

        PreviewViewPagerAdapter fragmentAdapter = new PreviewViewPagerAdapter(this, resources);
        binding.previewViewpager.setAdapter(fragmentAdapter);

        // 控制选中
        Resource resource = resources.stream()
                .filter(r -> r.getId().equals(selectedResource.getId()))
                .findFirst()
                .orElseThrow();
        resource.setThumbnailPath(image.thumbnailPath);
        binding.previewViewpager.setCurrentItem(resources.indexOf(resource), false);

        binding.previewViewpager.setOffscreenPageLimit(2);
        binding.previewViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Resource selectedResource = resources.get(position);
                TransitionNameChangedEvent transitionNameChangedEvent = TransitionNameChangedEvent.builder()
                        .resourceId(selectedResource.getId())
                        .name(selectedResource.getName())
                        .build();
                EventBus.getDefault().post(transitionNameChangedEvent);
            }
        });
        binding.previewViewpager.post(this::startPostponedEnterTransition);
    }
}
