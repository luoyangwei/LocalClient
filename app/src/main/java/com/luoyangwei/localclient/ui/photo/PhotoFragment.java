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
import androidx.work.WorkManager;

import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.Image;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.data.repository.ImageRepository;
import com.luoyangwei.localclient.data.source.local.ResourceService;
import com.luoyangwei.localclient.databinding.FragmentPhotoViewBinding;
import com.luoyangwei.localclient.ui.preview.PreviewActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 照片 Fragment
 */
public class PhotoFragment extends Fragment implements PhotoRecyclerViewAdapter.OnClickListener {
    private static final String TAG = PhotoFragment.class.getName();
    private static final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(3);
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

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

//        // 第一个线程：负责加载数据，加载完成后，将数据加入到List，让后显示到页面
//        scheduledThreadPoolExecutor.scheduleWithFixedDelay(this::dataLoad, 3, 1, java.util.concurrent.TimeUnit.SECONDS);
//        // 第二个线程：负责读取 List 的数据，然后生成缩略图，生成完成后，将数据更新到 List 和数据库
//        scheduledThreadPoolExecutor.scheduleWithFixedDelay(this::generateThumbnails, 4, 1, java.util.concurrent.TimeUnit.SECONDS);
//        // 第三个线程：负责接受文件的变化，如果文件有变化，更新 List


//        PeriodicWorkRequest workRequest = new PeriodicWorkRequest
//                .Builder(NewPhotoCheckWorker.class, 1, TimeUnit.MINUTES).build();
//        WorkManager.getInstance(requireContext()).enqueue(workRequest);
//        scheduledThreadPoolExecutor.scheduleWithFixedDelay(this::generateThumbnails, 3, 1, TimeUnit.SECONDS);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        WorkManager.getInstance(requireContext()).cancelAllWork();
        scheduledThreadPoolExecutor.getQueue().clear();
    }

    private void debugDelete(ImageRepository imageRepository) {
        List<Image> images = imageRepository.find();
        Log.d(TAG, String.format("要删除 %d 条数据", images.size()));
        for (Image image : images) {
            imageRepository.delete(image);
        }
    }

    private List<Image> loadImages(int page) {
        ImageRepository repository = AppDatabase.getInstance(requireContext()).imageRepository();
        return repository.findPaging(page, 100);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPhotoViewBinding.inflate(inflater, container, false);
        initializeToolbarMenu();

        CompletableFuture<List<Resource>> completableFuture = CompletableFuture.supplyAsync(() -> {
            ResourceService resourceService = new ResourceService(requireContext());
            return resourceService.getResources();
        });
        completableFuture.thenAccept(resources -> {
            adapter = new PhotoRecyclerViewAdapter(requireActivity(), requireContext(), resources);
            adapter.setOnClickListener(this);
            requireActivity().runOnUiThread(() -> {
                binding.photoRecyclerView.setAdapter(adapter);
            });
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);

        binding.photoRecyclerView.setLayoutManager(gridLayoutManager);
        binding.photoRecyclerView.addItemDecoration(new PhotoRecyclerViewItemDecoration(8));

//        binding.photoRecyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
//            if (scrollY > oldScrollY) {
//                Log.d(TAG, "Scroll Down");
//            } else {
//                Log.d(TAG, "Scroll Up");
//            }
//        });

        binding.photoRecyclerView.setHasFixedSize(true);
        binding.photoRecyclerView.setAdapter(adapter);

        // 双击返回顶部
        binding.toolbar.setOnClickListener(this::doubleClickSmoothScrollTop);
        return binding.getRoot();
    }

    private void initializeToolbarMenu() {
        Map<Integer, Runnable> actions = new HashMap<>();
        actions.put(R.id.toolbar_photo_delete_item, this::handleDeleteClick);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            Runnable action = actions.get(item.getItemId());
            if (action != null) {
                action.run();
                return true;
            }
            return false;
        });
    }

    private void handleDeleteClick() {
        Log.d(TAG, "Delete images table");
        ImageRepository imageRepository = AppDatabase.getInstance(requireContext()).imageRepository();
        new Thread(() -> debugDelete(imageRepository)).start();
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
        intent.putExtra("thumbnailPath", resource.getThumbnailPath());
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), imageView, resource.getName());
        startActivity(intent, activityOptionsCompat.toBundle());
    }
}
