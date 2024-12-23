package com.luoyangwei.localclient.view.preview;

import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;

import androidx.annotation.Nullable;

import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.AppLoadingCache;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.databinding.ActivityPreviewBinding;
import com.luoyangwei.localclient.view.ApplicationActivity;

import java.util.List;

public class PreviewActivity extends ApplicationActivity {
    private static final String TAG = "PreviewActivity";

    private ActivityPreviewBinding binding;
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

        PreviewScreenSlidePagerAdapter adapter = new PreviewScreenSlidePagerAdapter(this, this, resources);
        binding.viewpager.setAdapter(adapter);

        postponeEnterTransition();

        int indexOf = resources.indexOf(resource);
        binding.viewpager.setCurrentItem(indexOf, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareSharedElementTransition();
        postponeEnterTransition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void prepareSharedElementTransition() {
        Transition transition = TransitionInflater.from(getApplicationContext())
                .inflateTransition(R.transition.photo_shared_element_transition);
        getWindow().setSharedElementEnterTransition(transition);
        getWindow().setSharedElementReturnTransition(transition);
    }
}
