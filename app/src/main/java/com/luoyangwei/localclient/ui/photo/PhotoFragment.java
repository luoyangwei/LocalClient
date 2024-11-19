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
import com.luoyangwei.localclient.data.AppLoadingCache;
import com.luoyangwei.localclient.data.model.Image;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.data.repository.ImageRepository;
import com.luoyangwei.localclient.databinding.FragmentPhotoViewBinding;
import com.luoyangwei.localclient.ui.preview.PreviewActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        EventBus.getDefault().register(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPhotoViewBinding.inflate(inflater, container, false);
        initializeToolbarMenu();

        List<Resource> resources = AppLoadingCache.getInstance(requireContext()).getResources();
        adapter = new PhotoRecyclerViewAdapter(requireActivity(), requireContext(), resources);
        adapter.setOnClickListener(this);
        requireActivity().runOnUiThread(() -> {
            binding.photoRecyclerView.setAdapter(adapter);
        });


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);

        binding.photoRecyclerView.setLayoutManager(gridLayoutManager);
        binding.photoRecyclerView.addItemDecoration(new PhotoRecyclerViewItemDecoration(8));

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventbusSubscriber(TransitionNameChangedEvent transitionNameChangedEvent) {
        Log.i(TAG, "TransitionNameChangedEvent" + transitionNameChangedEvent.getName());
        List<Resource> resources = AppLoadingCache.getInstance(requireContext()).getResources();
        for (int i = 0; i < resources.size(); i++) {
            // TODO: 好像没有作用，不是主要功能，可作为优化后续升级
            if (resources.get(i).getId().equals(transitionNameChangedEvent.getResourceId())) {
                // 这里是 position
//                binding.photoRecyclerView.scrollToPosition(i);
            }
        }
    }

    @Override
    public void onClick(View view, Resource resource) {
        Intent intent = new Intent(getContext(), PreviewActivity.class);
        ImageView imageView = view.findViewById(R.id.photo_imageview);
        imageView.setTransitionName(resource.getName());

        intent.putExtra("resourceId", resource.getId());
        intent.putExtra("position", (int) view.getTag());
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), imageView, resource.getName());
        startActivity(intent, activityOptionsCompat.toBundle());
    }
}
