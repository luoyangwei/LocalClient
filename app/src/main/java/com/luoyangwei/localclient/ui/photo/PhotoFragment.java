package com.luoyangwei.localclient.ui.photo;

import android.content.Intent;
import android.graphics.Rect;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.AppLoadingCache;
import com.luoyangwei.localclient.data.model.Image;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.data.repository.ImageRepository;
import com.luoyangwei.localclient.databinding.FragmentPhotoViewBinding;
import com.luoyangwei.localclient.ui.gallery.GalleryActivity;
import com.luoyangwei.localclient.utils.EventUtils;

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
     * 照片间距
     */
    private static final int PHOTO_MARGIN_SPACE = 8;

    /**
     * 照片网格排列
     */
    private static final int PHOTO_GRID_SPACE_COUNT = 4;

    private FragmentPhotoViewBinding binding;
    private PhotoRecyclerViewAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPhotoViewBinding.inflate(inflater, container, false);
        initializeToolbarMenu();

        List<Resource> resources = AppLoadingCache.getInstance(requireContext()).getResources();
        adapter = new PhotoRecyclerViewAdapter(requireActivity(), requireContext(), resources);
        adapter.setOnClickListener(this);
        requireActivity().runOnUiThread(() -> binding.photoRecyclerView.setAdapter(adapter));


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), PHOTO_GRID_SPACE_COUNT);

        binding.photoRecyclerView.setLayoutManager(gridLayoutManager);
        binding.photoRecyclerView.addItemDecoration(new PhotoRecyclerViewItemDecoration(PHOTO_MARGIN_SPACE));

        binding.photoRecyclerView.setHasFixedSize(true);
        binding.photoRecyclerView.setAdapter(adapter);

        // 双击返回顶部
        binding.toolbar.setOnClickListener(
                EventUtils.handleDoubleClickListener(v -> binding.photoRecyclerView.smoothScrollToPosition(0))
        );
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

    private static class PhotoRecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public PhotoRecyclerViewItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.top = space;
            outRect.bottom = space;
        }
    }

    @Override
    public void onClick(View view, Resource resource) {
        Intent intent = new Intent(getContext(), GalleryActivity.class);
        ImageView imageView = view.findViewById(R.id.photo_imageview);

        String transitionName = resource.getId();
        imageView.setTransitionName(transitionName);
        intent.putExtra("resourceId", transitionName);

        // 预加载
        executor.execute(() -> Glide.with(this).load(resource.getFullPath()).preload());

        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), imageView, transitionName);
        startActivity(intent, activityOptionsCompat.toBundle());
    }
}
