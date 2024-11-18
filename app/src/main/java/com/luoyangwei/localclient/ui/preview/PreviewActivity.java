package com.luoyangwei.localclient.ui.preview;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.luoyangwei.localclient.data.model.ImageResource;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.data.repository.ImageRepository;
import com.luoyangwei.localclient.data.source.local.ResourceService;
import com.luoyangwei.localclient.databinding.ActivityPreviewBinding;
import com.luoyangwei.localclient.ui.ApplicationActivity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

        Future<List<Resource>> futureTask = executor.submit(this::asyncResources);
        try {
            List<Resource> resources = futureTask.get();

            String resourceId = getIntent().getStringExtra("resourceId");
            String thumbnailPath = getIntent().getStringExtra("thumbnailPath");

            Resource resource = resources.stream()
                    .filter(r -> r.getId().equals(resourceId))
                    .findFirst()
                    .orElseThrow();

            Log.i(TAG, "resourceId: " + resourceId);

            initializePreviewViewpager(resources, resource);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 启动动画效果
        postponeEnterTransition();
    }

    public List<Resource> asyncResources() {
        ImageRepository repository = AppDatabase.getInstance(this).imageRepository();
        ResourceService resourceService = new ResourceService(this);
        return resourceService.getResources(resource -> repository.findById(Long.parseLong(resource.getId())) != null);
    }


    /**
     * 初始化预览 ViewPager
     *
     * @param resources        所有资源
     * @param selectedResource 选中的资源
     */
    public void initializePreviewViewpager(List<Resource> resources,
                                           Resource selectedResource) {
        PreviewViewPagerAdapter fragmentAdapter = new PreviewViewPagerAdapter(this, resources);
        binding.previewViewpager.setAdapter(fragmentAdapter);

        // 控制选中
        resources.stream().filter(r -> r.getId().equals(selectedResource.getId())).findFirst().ifPresent(r ->
                binding.previewViewpager.setCurrentItem(resources.indexOf(r), false));

        binding.previewViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                startPostponedEnterTransition();
            }
        });
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
