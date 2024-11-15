package com.luoyangwei.localclient.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.transition.platform.MaterialArcMotion;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.hjq.permissions.Permission;
import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.source.local.ResourceLoaderBackgroundService;
import com.luoyangwei.localclient.data.source.local.ThumbnailGenerationService;
import com.luoyangwei.localclient.databinding.ActivityMainBinding;
import com.luoyangwei.localclient.ui.album.AlbumFragment;
import com.luoyangwei.localclient.ui.cloud.CloudFragment;
import com.luoyangwei.localclient.ui.photo.PhotoFragment;
import com.luoyangwei.localclient.utils.PermissionUtil;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 简单的权限授予动作
        PermissionUtil.request(this, Permission.READ_MEDIA_IMAGES, Permission.READ_MEDIA_VIDEO,
                Permission.READ_MEDIA_VISUAL_USER_SELECTED);

        initializeBottomNavigationView();
        initializeDefaultFragment();
//        startupBackgroundResourceLoaderService();

        Intent intent = new Intent(this, ThumbnailGenerationService.class);
        startService(intent);
    }

    private MaterialContainerTransform materialContainerTransformBuilder() {
        MaterialContainerTransform transform = new MaterialContainerTransform();
        transform.setDuration(30);
        transform.addTarget(android.R.id.content);
        transform.setPathMotion(new MaterialArcMotion());
        return transform;
    }

    private void initializeDefaultFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PhotoFragment())
                .commit();
    }

    private void initializeBottomNavigationView() {
        Map<Integer, Runnable> actions = new HashMap<>();
        actions.put(R.id.photo_item, this::handlePhotoClick);
        actions.put(R.id.album_item, this::handleAlbumClick);
        actions.put(R.id.cloud_item, this::handleCloudClick);

        binding.bottomNavigation.bottomNavigationView.setOnItemSelectedListener(item -> {
            Runnable action = actions.get(item.getItemId());
            if (action != null) {
                action.run();
                return true;
            }
            return false;
        });
    }

    /**
     * Bottom navigation bar photo item click handler
     */
    private void handlePhotoClick() {
        Log.i(TAG, "handlePhotoClick");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PhotoFragment())
                .commit();
    }

    /**
     * Bottom navigation bar album item click handler
     */
    private void handleAlbumClick() {
        Log.i(TAG, "handleAlbumClick");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AlbumFragment())
                .commit();
    }

    /**
     * Bottom navigation bar cloud item click handler
     */
    private void handleCloudClick() {
        Log.i(TAG, "handleCloudClick");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new CloudFragment())
                .commit();
    }

    public void startupBackgroundResourceLoaderService() {
        Intent intent = new Intent(this, ResourceLoaderBackgroundService.class);
        startService(intent);
        Log.i(TAG, "BackgroundResourceLoaderService 启动");
    }
}