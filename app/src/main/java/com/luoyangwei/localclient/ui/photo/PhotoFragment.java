package com.luoyangwei.localclient.ui.photo;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.luoyangwei.localclient.databinding.FragmentPhotoViewBinding;
import com.luoyangwei.localclient.ui.preview.PreviewActivity;

import java.util.ArrayList;
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPhotoViewBinding.inflate(inflater, container, false);
        List<PhotoRecyclerViewAdapter.Resource> resources = contentResolverQuery();

        adapter = new PhotoRecyclerViewAdapter(getContext(), resources);
        adapter.setOnClickListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);

        binding.photoRecyclerView.setLayoutManager(gridLayoutManager);
        binding.photoRecyclerView.addItemDecoration(new PhotoRecyclerViewItemDecoration(8));

        binding.photoRecyclerView.setHasFixedSize(true);
        binding.photoRecyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    public List<PhotoRecyclerViewAdapter.Resource> contentResolverQuery() {
        ContentResolver contentResolver = requireActivity().getContentResolver();
        Cursor cursor = query(contentResolver);
        List<PhotoRecyclerViewAdapter.Resource> resources = new ArrayList<>();
        while (cursor.moveToNext()) {
            resources.add(new PhotoRecyclerViewAdapter.Resource(cursor));
        }
        return resources;
    }

    private Cursor query(ContentResolver contentResolver) {
        return contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Images.Media.DATE_ADDED + " DESC");
    }

    @Override
    public void onClick(View view, PhotoRecyclerViewAdapter.Resource resource) {
        Intent intent = new Intent(getContext(), PreviewActivity.class);
        ImageView imageView = view.findViewById(R.id.photo_imageview);
        imageView.setTransitionName(resource.getName());

        intent.putExtra("resource", new Gson().toJson(resource));
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),
                imageView, resource.getName());
        startActivity(intent, activityOptionsCompat.toBundle());
    }
}
