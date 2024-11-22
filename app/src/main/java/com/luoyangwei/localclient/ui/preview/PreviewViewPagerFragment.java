package com.luoyangwei.localclient.ui.preview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.databinding.FragmentPreviewPagerViewBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;

public class PreviewViewPagerFragment extends Fragment {
    private static final String TAG = PreviewViewPagerFragment.class.getName();
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private FragmentPreviewPagerViewBinding binding;

    private final Resource resource;
    private final Activity activity;

    public PreviewViewPagerFragment(Activity activity, Resource resource) {
        this.activity = activity;
        this.resource = resource;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPreviewPagerViewBinding.inflate(inflater);

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        binding.previewImageView.setTransitionName(resource.getName());

        PostponeEnterTransitionHelper.loadImageWithTransition(
                activity,
                Glide.with(this)
                        .load(resource.getThumbnailPath())
                        .dontAnimate()
                        .apply(requestOptions),
                binding.previewImageView);
        // 异步加载原图
//        executor.execute(this::asyncOriginalImageLoad);
        return binding.getRoot();
    }

    @SneakyThrows
    private void asyncOriginalImageLoad() {
        Thread.sleep(500);
        RequestBuilder<Drawable> requestBuilder = Glide.with(this)
                .load(resource.getFullPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(binding.previewImageView.getDrawable());
        requireActivity().runOnUiThread(() -> requestBuilder.into(binding.previewImageView));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
