package com.luoyangwei.localclient.view.preview;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.luoyangwei.localclient.data.AppLoadingCache;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.databinding.ActivityPreviewBinding;
import com.luoyangwei.localclient.view.ApplicationActivity;

import java.util.List;

public class PreviewActivity extends ApplicationActivity {
    private static final String TAG = "PreviewActivity";

    private ActivityPreviewBinding binding;
    private PreviewScreenSlidePagerAdapter adapter;
    private Resource resource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();

        binding = ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setOnApplyWindowInsetsListener(binding.getRoot(), (insets) -> {
            binding.operationAppbar.setPadding(0, insets.top, 0, 0);
            binding.operationBottomNavigation.setPadding(0, 0, 0, insets.bottom);
        });

        // 设置共享元素动画
        prepareSharedElementTransition();

        List<Resource> resources = AppLoadingCache.getInstance(getApplicationContext()).getResources();
        String resourceId = getIntent().getStringExtra("resourceId");
        resource = AppLoadingCache.getInstance(getApplicationContext()).get(resourceId);

        adapter = new PreviewScreenSlidePagerAdapter(this, this, resources);
        binding.viewpager.setAdapter(adapter);
        binding.viewpager.setOffscreenPageLimit(1);
        binding.viewpager.setCurrentItem(resources.indexOf(resource), false);

        // 暂停动画
        postponeEnterTransition();
    }
}
