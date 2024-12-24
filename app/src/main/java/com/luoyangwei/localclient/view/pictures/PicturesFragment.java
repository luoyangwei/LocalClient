package com.luoyangwei.localclient.view.pictures;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.AppLoadingCache;
import com.luoyangwei.localclient.data.model.Image;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.data.repository.ImageRepository;
import com.luoyangwei.localclient.databinding.FragmentPicturesBinding;
import com.luoyangwei.localclient.utils.EventUtils;
import com.luoyangwei.localclient.view.preview.PreviewActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 照骗
 */
public class PicturesFragment extends Fragment implements PicturesItemOnClickListener {
    private static final String TAG = PicturesFragment.class.getName();

    /**
     * 照片间距
     */
    private static final int PHOTO_MARGIN_SPACE = 8;

    /**
     * 照片网格排列
     */
    private static final int PHOTO_GRID_SPACE_COUNT = 3;

    private FragmentPicturesBinding binding;
    private PicturesListViewAdapter adapter;

    private List<Resource> resources;

    private Resource clickedResource;
    private ImageView onClickedImageView;

    private int page = 1;
    private static final int PAGE_SIZE = 50;  // 每次加载条数据

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Glide.get(requireContext())
                .setMemoryCategory(MemoryCategory.HIGH);

        binding = FragmentPicturesBinding.inflate(inflater, container, false);

        resources = AppLoadingCache.getInstance(requireContext()).getResources();
        List<Resource> firstPreloadPage = new ArrayList<>();
        resources.stream().limit(PAGE_SIZE * 2).forEach(firstPreloadPage::add);

        adapter = new PicturesListViewAdapter(requireActivity(), firstPreloadPage, this);

        binding.picturesListView.setLayoutManager(new GridLayoutManager(getContext(), PHOTO_GRID_SPACE_COUNT));
        binding.picturesListView.addItemDecoration(new PicturesListViewAdapter.ItemDecoration(PHOTO_MARGIN_SPACE));
        binding.picturesListView.setAdapter(adapter);
        binding.picturesListView.addOnScrollListener(addOnScrollListener());

        doubleClickToolbar();
        prepareSharedElementTransition();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (onClickedImageView != null) {
            onClickedImageView.setTransitionName(clickedResource.getId());
        }
    }

    private void prepareSharedElementTransition() {
        Transition transition = TransitionInflater.from(requireActivity())
                .inflateTransition(R.transition.photo_shared_element_transition);
        requireActivity().getWindow().setSharedElementEnterTransition(transition);
        requireActivity().getWindow().setSharedElementReturnTransition(transition);
    }

    private RecyclerView.OnScrollListener addOnScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = gridLayoutManager.getItemCount();

                // 预加载
                if (lastVisibleItem + 50 >= totalItemCount) {
                    page++;

                    List<Resource> preloadPage = new ArrayList<>();
                    resources.stream()
                            .skip((long) page * PAGE_SIZE)
                            .limit(PAGE_SIZE)
                            .forEach(preloadPage::add);
                    Log.i(TAG, "preload...");
                    requireActivity().runOnUiThread(() -> adapter.addItems(preloadPage));

                }
            }
        };
    }

    @Override
    public void onClick(View v, int position, Resource resource) {
        Intent intent = new Intent(requireContext(), PreviewActivity.class);
        intent.putExtra("resourceId", resource.getId());
        ImageView imageView = v.findViewById(R.id.pictures_item);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), imageView, resource.getId());
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        Glide.get(requireContext()).clearMemory();
//
//        // 清理缓存目录// 测试阶段
//        File cacheDir = requireContext().getExternalCacheDir();
//        if (cacheDir != null) {
//            for (File file : cacheDir.listFiles()) {
//                if (file.isFile()) {
//                    file.delete();
//                }
//            }
//        }
    }

    /**
     * 双击返回顶部
     */
    private void doubleClickToolbar() {
        binding.toolbar.setOnClickListener(
                EventUtils.handleDoubleClickListener(v -> binding.picturesListView.smoothScrollToPosition(0))
        );
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

    private void debugDelete(ImageRepository imageRepository) {
        List<Image> images = imageRepository.find();
        Log.d(TAG, String.format("要删除 %d 条数据", images.size()));
        for (Image image : images) {
            imageRepository.delete(image);
        }
    }
}
