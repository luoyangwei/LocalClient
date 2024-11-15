package com.luoyangwei.localclient.ui.photo;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.source.local.ResourceService;
import com.luoyangwei.localclient.databinding.FragmentPhotoViewBinding;
import com.luoyangwei.localclient.ui.preview.PreviewActivity;

import java.util.List;

/**
 * 照片 Fragment
 */
public class PhotoFragment extends Fragment implements PhotoRecyclerViewAdapter.OnClickListener {
    private static final String TAG = PhotoFragment.class.getName();

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
//        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPhotoViewBinding.inflate(inflater, container, false);
        StrictMode.enableDefaults();
        ResourceService resourceService = new ResourceService(getContext());

        // 首次展示 30 张，后面一直追加，直到没有数据
        List<Resource> resources = resourceService.getResources();
        adapter = new PhotoRecyclerViewAdapter(getContext(), resources);
        adapter.setOnClickListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);

        binding.photoRecyclerView.setLayoutManager(gridLayoutManager);
        binding.photoRecyclerView.addItemDecoration(new PhotoRecyclerViewItemDecoration(8));

        binding.photoRecyclerView.setHasFixedSize(true);
        binding.photoRecyclerView.setAdapter(adapter);

        // 双击返回顶部
        binding.toolbar.setOnClickListener(this::doubleClickSmoothScrollTop);
        return binding.getRoot();
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
