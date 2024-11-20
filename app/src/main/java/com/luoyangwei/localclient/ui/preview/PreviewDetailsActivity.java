package com.luoyangwei.localclient.ui.preview;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.luoyangwei.localclient.databinding.ActivityPreviewDetailsBinding;
import com.luoyangwei.localclient.ui.ApplicationActivity;

public class PreviewDetailsActivity extends ApplicationActivity {
    private static final String TAG = PreviewDetailsActivity.class.getName();
    private ActivityPreviewDetailsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.enableEdgeToEdge();
        binding = ActivityPreviewDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // URL 地址
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        String transitionName = intent.getStringExtra("transitionName");
        binding.previewDetailsImage.setTransitionName(transitionName);

        postponeEnterTransition();

        Glide.with(this)
                .load(path)
                .dontAnimate()
                .into(binding.previewDetailsImage);

        startPostponedEnterTransition();
    }
}
