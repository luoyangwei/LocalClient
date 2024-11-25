package com.luoyangwei.localclient.ui.album;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.luoyangwei.localclient.data.AppLoadingCache;
import com.luoyangwei.localclient.data.model.Bucket;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.databinding.FragmentAlbumViewBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.SneakyThrows;

/**
 * 相册 Fragment
 */
public class AlbumFragment extends Fragment {
    private static final String TAG = AlbumFragment.class.getName();
    private FragmentAlbumViewBinding binding;

    /**
     * 照片间距
     */
    private static final int ALBUM_MARGIN_SPACE = 16;

    /**
     * 照片网格排列
     */
    private static final int ALBUM_GRID_SPACE_COUNT = 3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAlbumViewBinding.inflate(inflater, container, false);
        List<Bucket> buckets = buckets();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), ALBUM_GRID_SPACE_COUNT);
        AlbumBucketAdapter adapter = new AlbumBucketAdapter(requireContext(), buckets);

        binding.albumRecyclerView.setLayoutManager(gridLayoutManager);
        binding.albumRecyclerView.setAdapter(adapter);
        binding.albumRecyclerView.addItemDecoration(new AlbumBucketAdapter.AlbumItemDecoration(ALBUM_MARGIN_SPACE));
        return binding.getRoot();
    }

    @SneakyThrows
    private List<Bucket> buckets() {
        List<Resource> resources = AppLoadingCache.getInstance(requireContext()).getResources();

        // 取出重复项
        Map<String, String> bucketMap = new HashMap<>();
        resources.forEach(resource ->
                bucketMap.put(resource.getBucketId(), resource.getBucketName()));

        List<Bucket> buckets = new ArrayList<>();
        for (String bucketId : bucketMap.keySet()) {
            List<Resource> bucketResources = resources.stream().filter(resource ->
                    resource.getBucketId().equals(bucketId)).collect(Collectors.toList());
            Resource resource = bucketResources.stream().findFirst().orElseThrow();
            Bucket bucket = new Bucket();
            bucket.setId(bucketId)
                    .setName(bucketMap.get(bucketId));
            bucket.setResources(bucketResources);
            bucket.setCount(bucketResources.size());
            bucket.setMostRecentResourceId(resource.getId());
            buckets.add(bucket);
        }
        return buckets;
    }
}
