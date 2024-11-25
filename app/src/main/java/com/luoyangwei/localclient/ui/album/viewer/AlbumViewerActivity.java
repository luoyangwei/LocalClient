package com.luoyangwei.localclient.ui.album.viewer;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.luoyangwei.localclient.data.model.Bucket;
import com.luoyangwei.localclient.data.source.local.ResourceService;
import com.luoyangwei.localclient.databinding.ActivityAlbumViewerBinding;
import com.luoyangwei.localclient.ui.ApplicationActivity;

public class AlbumViewerActivity extends ApplicationActivity {
    private static final String TAG = AlbumViewerActivity.class.getName();
    private ActivityAlbumViewerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.enableEdgeToEdge();
        binding = ActivityAlbumViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the bucket id
        String bucketId = getIntent().getStringExtra("bucketId");

        // Get the bucket
        ResourceService resourceService = new ResourceService(getApplicationContext());
        Bucket bucket = resourceService.getBuckets().stream().filter(b -> b.getId().equals(bucketId))
                .findFirst()
                .orElseThrow();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
        ViewerAdapter adapter = new ViewerAdapter(getApplicationContext(), bucket);

        // Set up the toolbar
        binding.toolbar.setNavigationOnClickListener(this::navigationOnClick);
        binding.recyclerView.setLayoutManager(gridLayoutManager);
        binding.recyclerView.setAdapter(adapter);
    }

    // On click to Activity back pressed
    public void navigationOnClick(View v) {
        getOnBackPressedDispatcher().onBackPressed();
    }
}
