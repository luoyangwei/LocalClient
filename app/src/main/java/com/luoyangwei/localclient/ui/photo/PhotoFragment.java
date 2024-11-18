package com.luoyangwei.localclient.ui.photo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.Image;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.data.repository.ImageRepository;
import com.luoyangwei.localclient.data.source.local.ResourceService;
import com.luoyangwei.localclient.data.source.local.ThumbnailGenerationWorker;
import com.luoyangwei.localclient.databinding.FragmentPhotoViewBinding;
import com.luoyangwei.localclient.ui.preview.PreviewActivity;
import com.luoyangwei.localclient.utils.ThumbnailUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import top.zibin.luban.Luban;

/**
 * 照片 Fragment
 */
public class PhotoFragment extends Fragment implements PhotoRecyclerViewAdapter.OnClickListener {
    private static final String TAG = PhotoFragment.class.getName();
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    /**
     * 预加载数量
     */
    private static final int PRELOADED_COUNT = 10;

    /**
     * 首次点击时间
     */
    private long firstToolbarClickTime = 0;

    /**
     * 定义双击时间间隔，单位为毫秒
     */
    private static final long DOUBLE_CLICK_TIME_DELIMIT = 300;

    private FragmentPhotoViewBinding binding;

    private PhotoRecyclerViewAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void debugDelete(ImageRepository imageRepository) {
        List<Image> images = imageRepository.find();
        for (Image image : images) {
            imageRepository.delete(image);
        }
    }

    /**
     * 预加载
     */
    private List<Resource> preloaded() throws IOException {
        List<Resource> results = new ArrayList<>();
        ImageRepository imageRepository = AppDatabase.getInstance(requireContext()).imageRepository();

        // TODO debug
        debugDelete(imageRepository);

        ResourceService resourceService = new ResourceService(requireContext());
        List<Resource> resources = resourceService.getResources(resource -> {
            Image image = imageRepository.findById(Long.parseLong(resource.getId()));
            return image == null; // 没有加入过数据库纳入到预加载
        });

        List<Image> images = imageRepository.findHasThumbnail();
        if (images.isEmpty()) {
            results.addAll(resources.subList(0, Math.min(resources.size(), PRELOADED_COUNT)));
        } else {
            for (Image image : images) {
                resources.stream().filter(resource -> resource.getId().equals(String.valueOf(image.id)))
                        .map(resource -> resource.addThumbnail(image))
                        .findFirst().ifPresent(results::add);
            }
        }

        for (Resource resource : results) {
            Long id = Long.parseLong(resource.getId());
            Image image = imageRepository.findById(id);

            // 如果是空的，补一条数据
            if (Objects.isNull(image)) {
                image = Image.getInstance(resource);
                imageRepository.insert(image);
            }

            // 生成缩略图
            String thumbnailDirectory = ThumbnailUtils.getOutputThumbnailPath(requireContext()) +
                    File.separator + resource.getBucketName();
            List<File> paths = Luban.with(requireContext()).load(new File(image.fullPath))
                    .ignoreBy(50)
                    .setTargetDir(thumbnailDirectory)
                    .get();
            File thumbnailPath = paths.stream().findFirst().orElseThrow();

            // 更新表
            image.thumbnailPath = thumbnailPath.getAbsolutePath();
            image.isHasThumbnail = true;
            imageRepository.update(image);

            // 更新资源
            resource.setThumbnailPath(image.thumbnailPath);
        }

        return results;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPhotoViewBinding.inflate(inflater, container, false);

        // 预加载
        Future<List<Resource>> futurePreloadedResources = executor.submit(this::preloaded);
        try {
            List<Resource> preloadedResources = futurePreloadedResources.get();
            adapter = new PhotoRecyclerViewAdapter(getContext(), preloadedResources);
            adapter.setOnClickListener(this);

            PeriodicWorkRequest workRequest = new PeriodicWorkRequest
                    .Builder(ThumbnailGenerationWorker.class, 5, TimeUnit.SECONDS)
                    .build();
            WorkManager.getInstance(requireContext()).enqueue(workRequest);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);

        binding.photoRecyclerView.setLayoutManager(gridLayoutManager);
        binding.photoRecyclerView.addItemDecoration(new PhotoRecyclerViewItemDecoration(8));

        binding.photoRecyclerView.setHasFixedSize(true);
        binding.photoRecyclerView.setAdapter(adapter);

        // 双击返回顶部
        binding.toolbar.setOnClickListener(this::doubleClickSmoothScrollTop);
        return binding.getRoot();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void resourceChange(Image image) {
        ResourceService resourceService = new ResourceService(requireContext());
        Resource resource = resourceService.getResource(image.id);
        resource.addThumbnail(image);
        adapter.addItem(resource);
        Log.i(TAG, "resourceChange" + resource.getId());
    }

    /**
     * 双击返回顶部
     *
     * @param v 点击的视图
     */
    public void doubleClickSmoothScrollTop(View v) {
        long currentTime = System.currentTimeMillis();
        if (firstToolbarClickTime == 0) {
            firstToolbarClickTime = currentTime;
        } else {
            if (currentTime - firstToolbarClickTime <= DOUBLE_CLICK_TIME_DELIMIT) {
                binding.photoRecyclerView.smoothScrollToPosition(0);
            } else {
                firstToolbarClickTime = currentTime;
            }
        }
    }


    @Override
    public void onClick(View view, Resource resource) {
        Intent intent = new Intent(getContext(), PreviewActivity.class);
        ImageView imageView = view.findViewById(R.id.photo_imageview);
        imageView.setTransitionName(resource.getName());

        intent.putExtra("resourceId", resource.getId());
        ActivityOptionsCompat activityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), imageView, resource.getName());
        startActivity(intent, activityOptionsCompat.toBundle());
    }
}
