package com.luoyangwei.localclient.ui.preview;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.luoyangwei.localclient.data.model.ImageResource;
import com.luoyangwei.localclient.data.source.local.ImageResourceEntry;
import com.luoyangwei.localclient.databinding.ActivityPreviewBinding;
import com.luoyangwei.localclient.ui.ApplicationActivity;

import java.util.List;

public class PreviewActivity extends ApplicationActivity {
    private static final String TAG = PreviewActivity.class.getName();

    private ActivityPreviewBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.enableEdgeToEdge();

        binding = ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageResourceEntry resource = getResourceEntry();

        postponeEnterTransition();

        List<ImageResourceEntry> resources = resourceService.resources();

        initializePreviewViewpager(resources, resource);

//        initializeThumbnails(resource);
    }

    private ImageResourceEntry getResourceEntry() {
        return getObjectExtra("resource", ImageResourceEntry.class);
    }

    /**
     * 初始化预览 ViewPager
     *
     * @param resources        所有资源
     * @param selectedResource 选中的资源
     */
    public void initializePreviewViewpager(List<ImageResourceEntry> resources,
                                           ImageResourceEntry selectedResource) {
        PreviewViewPagerAdapter fragmentAdapter = new PreviewViewPagerAdapter(this, resources);
        binding.previewViewpager.setAdapter(fragmentAdapter);

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
