package com.luoyangwei.localclient.ui.main;

import android.os.Bundle;
import android.util.Log;

import com.hjq.permissions.Permission;
import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.source.local.ResourceService;
import com.luoyangwei.localclient.databinding.ActivityMainBinding;
import com.luoyangwei.localclient.ui.ApplicationActivity;
import com.luoyangwei.localclient.ui.album.AlbumFragment;
import com.luoyangwei.localclient.ui.cloud.CloudFragment;
import com.luoyangwei.localclient.ui.photo.PhotoFragment;
import com.luoyangwei.localclient.utils.PermissionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends ApplicationActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.enableEdgeToEdge();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 简单的权限授予动作
        PermissionUtil.request(this,
                Permission.READ_MEDIA_IMAGES,
                Permission.READ_MEDIA_VIDEO,
                Permission.READ_MEDIA_VISUAL_USER_SELECTED);

        // 初始化存入数据库
        initializeResources();

        // 测试
        initializeBottomNavigationView();
        initializeDefaultFragment();
    }

    private void initializeResources() {
        ResourceService resourceService = new ResourceService(getApplicationContext());
        List<Resource> resources = resourceService.getResources();
        Log.i(TAG, "Insert " + resources.size() + " images into database");

        executor.execute(() -> {
            long currentTimeMillis = System.currentTimeMillis();
            Log.i(TAG, "Thread (" + Thread.currentThread().getId() + ") insert " + resources.size() +
                    " images, cost " + (System.currentTimeMillis() - currentTimeMillis) + "ms");
        });
    }

    private void initializeDefaultFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PhotoFragment()).commit();
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
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PhotoFragment()).commit();
    }

    /**
     * Bottom navigation bar album item click handler
     */
    private void handleAlbumClick() {
        Log.i(TAG, "handleAlbumClick");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AlbumFragment()).commit();
    }

    /**
     * Bottom navigation bar cloud item click handler
     */
    private void handleCloudClick() {
        Log.i(TAG, "handleCloudClick");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CloudFragment()).commit();
    }
}