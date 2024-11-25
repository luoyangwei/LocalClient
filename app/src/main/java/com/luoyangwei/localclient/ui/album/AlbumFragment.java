package com.luoyangwei.localclient.ui.album;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.luoyangwei.localclient.data.model.Bucket;
import com.luoyangwei.localclient.data.source.local.ResourceService;
import com.luoyangwei.localclient.databinding.FragmentAlbumViewBinding;

import java.util.List;

/**
 * 相册 Fragment
 */
public class AlbumFragment extends Fragment {
    private static final String TAG = AlbumFragment.class.getName();
    private FragmentAlbumViewBinding binding;

    /**
     * 照片间距
     */
    private static final int ALBUM_MARGIN_SPACE = 8;

    /**
     * 照片网格排列
     */
    private static final int ALBUM_GRID_SPACE_COUNT = 3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAlbumViewBinding.inflate(inflater, container, false);

        ResourceService resourceService = new ResourceService(requireContext());
        List<Bucket> buckets = resourceService.getBuckets();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), ALBUM_GRID_SPACE_COUNT);
        BucketAdapter adapter = new BucketAdapter(requireContext(), buckets);

        binding.albumRecyclerView.setLayoutManager(gridLayoutManager);
        binding.albumRecyclerView.setAdapter(adapter);
        binding.albumRecyclerView.addItemDecoration(new BucketAdapter.AlbumItemDecoration(ALBUM_MARGIN_SPACE));
        return binding.getRoot();
    }
}
