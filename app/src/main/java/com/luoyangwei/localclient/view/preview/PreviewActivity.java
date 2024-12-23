package com.luoyangwei.localclient.view.preview;

import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;

import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.AppLoadingCache;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.databinding.ActivityPreviewBinding;
import com.luoyangwei.localclient.view.ApplicationActivity;

import java.util.List;
import java.util.Map;

public class PreviewActivity extends ApplicationActivity {
    private static final String TAG = "PreviewActivity";

    private ActivityPreviewBinding binding;
    private PreviewScreenSlidePagerAdapter adapter;
    private Resource resource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        prepareSharedElementTransition();

        binding = ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<Resource> resources = AppLoadingCache.getInstance(getApplicationContext()).getResources();
        String resourceId = getIntent().getStringExtra("resourceId");
        resource = AppLoadingCache.getInstance(getApplicationContext()).get(resourceId);

        adapter = new PreviewScreenSlidePagerAdapter(this, this, resources);
        binding.viewpager.setAdapter(adapter);
        binding.viewpager.setCurrentItem(resources.indexOf(resource), false);

        postponeEnterTransition();
    }

    private void prepareSharedElementTransition() {
        Transition transition = TransitionInflater.from(getApplicationContext())
                .inflateTransition(R.transition.photo_shared_element_transition);
        getWindow().setSharedElementEnterTransition(transition);
        getWindow().setSharedElementReturnTransition(transition);
        setEnterSharedElementCallback(new SharedElementCallback() {

            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
            }
        });
    }
}
