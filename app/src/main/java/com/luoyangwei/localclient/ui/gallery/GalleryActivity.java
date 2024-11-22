package com.luoyangwei.localclient.ui.gallery;

import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;

import androidx.annotation.Nullable;

import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.AppLoadingCache;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.databinding.ActivityGalleryBinding;
import com.luoyangwei.localclient.ui.ApplicationActivity;

import java.util.List;

import lombok.SneakyThrows;

public class GalleryActivity extends ApplicationActivity {
    private ActivityGalleryBinding binding;

    @Override
    @SneakyThrows
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.enableEdgeToEdge();
        binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeViewPager();
        scrollToPosition();
        prepareSharedElementTransition();
        postponeEnterTransition();
    }

    private void initializeViewPager() {
        List<Resource> resources = AppLoadingCache.getInstance(getApplicationContext()).getResources();
        GalleryAdapter adapter = new GalleryAdapter(this, resources);
        binding.viewpager.setAdapter(adapter);
        binding.viewpager.setOffscreenPageLimit(1);
    }

    private void scrollToPosition() {
        String resourceId = getIntent().getStringExtra("resourceId");
        List<Resource> resources = AppLoadingCache.getInstance(getApplicationContext()).getResources();
        Resource resource = resources.stream()
                .filter(r -> r.getId().equals(resourceId))
                .findFirst()
                .orElseThrow();
        binding.viewpager.setCurrentItem(resources.indexOf(resource), false);
    }

    private void prepareSharedElementTransition() {
        Transition transition = TransitionInflater.from(getApplicationContext())
                .inflateTransition(R.transition.photo_shared_element_transition);
        getWindow().setSharedElementEnterTransition(transition);
        getWindow().setSharedElementReturnTransition(transition);
    }
}
