package com.luoyangwei.localclient.ui.photo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;
import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.source.local.ImageResourceEntry;
import com.luoyangwei.localclient.data.source.local.ImageResourceService;
import com.luoyangwei.localclient.databinding.FragmentPhotoViewBinding;
import com.luoyangwei.localclient.ui.preview.PreviewActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * 照片 Fragment
 */
public class PhotoFragment extends Fragment implements PhotoRecyclerViewAdapter.OnClickListener {
    private static final String TAG = PhotoFragment.class.getName();

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPhotoViewBinding.inflate(inflater, container, false);

        ImageResourceService imageResourceService = ImageResourceService.getInstance(getContext());
        List<ImageResourceEntry> resources = imageResourceService.resources();

        adapter = new PhotoRecyclerViewAdapter(getContext(), resources);
        adapter.setOnClickListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);

        binding.photoRecyclerView.setLayoutManager(gridLayoutManager);
        binding.photoRecyclerView.addItemDecoration(new PhotoRecyclerViewItemDecoration(8));

        binding.photoRecyclerView.setHasFixedSize(true);
        binding.photoRecyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceCallback(ImageResourceEntry entry) {
        adapter.addItem(entry);
    }

    @Override
    public void onClick(View view, ImageResourceEntry resource) {
        Intent intent = new Intent(getContext(), PreviewActivity.class);
        ImageView imageView = view.findViewById(R.id.photo_imageview);
        imageView.setTransitionName(resource.getTitle());

        intent.putExtra("resource", new Gson().toJson(resource));
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),
                imageView, resource.getTitle());
        startActivity(intent, activityOptionsCompat.toBundle());
    }
}
