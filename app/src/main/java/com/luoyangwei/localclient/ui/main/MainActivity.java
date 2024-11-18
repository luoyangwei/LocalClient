package com.luoyangwei.localclient.ui.main;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.hjq.permissions.Permission;
import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.source.local.ResourceService;
import com.luoyangwei.localclient.databinding.ActivityMainBinding;
import com.luoyangwei.localclient.ui.SimpleInterestResources;
import com.luoyangwei.localclient.ui.album.AlbumFragment;
import com.luoyangwei.localclient.ui.cloud.CloudFragment;
import com.luoyangwei.localclient.ui.photo.PhotoFragment;
import com.luoyangwei.localclient.utils.PermissionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private final SimpleInterestResources simpleInterestResources = SimpleInterestResources.getInstance();

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

        // 初始化存入数据库
        new Thread(this::initializeImages).start();

        // 测试
        initializeBottomNavigationView();
        initializeDefaultFragment();
    }

    private void initializeImages() {
        ResourceService resourceService = new ResourceService(this);
        List<Resource> resources = resourceService.getResources(r -> true);
        simpleInterestResources.setResources(resources);
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
}