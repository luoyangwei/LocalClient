package com.luoyangwei.localclient.ui.preview;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.luoyangwei.localclient.data.AppLoadingCache;
import com.luoyangwei.localclient.data.model.Image;
import com.luoyangwei.localclient.data.model.ImageResource;
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

        binding = ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
    public void initializePreviewViewpager(List<Resource> resources,
                                           Resource selectedResource) {
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

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                startPostponedEnterTransition();
            }
        });
    }

    @SneakyThrows
    private void preload(int position) {
        List<Resource> resources = AppLoadingCache.getInstance(getApplicationContext()).getResources();
        ImageRepository repository = AppDatabase.getInstance(getApplicationContext()).imageRepository();
        if (position > 0) {
            Resource previousResource = resources.get(position - 1);
            Image image = CompletableFuture.supplyAsync(() -> repository.findByResourcesId(previousResource.getId())).get();
            previousResource.setThumbnailPath(image.thumbnailPath);

            executor.execute(() -> Glide.with(getApplicationContext()).load(previousResource.getThumbnailPath())
                    .preload());
            executor.execute(() -> Glide.with(getApplicationContext()).load(previousResource.getFullPath())
                    .preload());
        }
        if (position < resources.size() - 1) {
            Resource nextResource = resources.get(position + 1);
            Image image = CompletableFuture.supplyAsync(() -> repository.findByResourcesId(nextResource.getId())).get();
            nextResource.setThumbnailPath(image.thumbnailPath);

            executor.execute(() -> Glide.with(getApplicationContext()).load(nextResource.getThumbnailPath())
                    .preload());
            executor.execute(() -> Glide.with(getApplicationContext()).load(nextResource.getFullPath())
                    .preload());
        }
    }

    private void initializeThumbnails(ImageResource resource) {
//        new Thread(() -> {
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//            binding.previewRecyclerView.setLayoutManager(linearLayoutManager);
//            ImageResourceDao imageResourceDao = ImageResourceDao.getInstance();
//
//            // DEBUG: 只显示 30 个
//            List<ImageResource> resources = imageResourceDao.resources(1, 30);
//            binding.previewRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//                @Override
//                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//                    super.getItemOffsets(outRect, view, parent, state);
//                }
//            });
//
//            runOnUiThread(() -> {
//                // 设置初始状态
//                binding.previewBottomLayout.setAlpha(0f);
//                binding.previewBottomLayout.setTranslationY(30f);
//
//                // 设置列表数据
//                binding.previewRecyclerView.setAdapter(
//                        new PreviewThumbnailsRecyclerViewAdapter(this, resource.getId(), resources));
//
//                // 更新为新状态
//                binding.previewBottomLayout.animate()
//                        .alpha(1)
//                        .translationY(0f)
//                        .setDuration(400)
//                        .start();
//            });
//        }).start();
    }

}
