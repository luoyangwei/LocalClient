package com.luoyangwei.localclient.ui.gallery;

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
import com.bumptech.glide.request.RequestOptions;
import com.luoyangwei.localclient.data.model.Image;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.data.repository.ImageRepository;
import com.luoyangwei.localclient.databinding.FragmentGalleryBinding;
import com.luoyangwei.localclient.ui.preview.PostponeEnterTransitionHelper;

import java.util.concurrent.CompletableFuture;

import lombok.SneakyThrows;

public class GalleryFragment extends Fragment {
    private final RequestOptions requestOptions = new RequestOptions();
    private FragmentGalleryBinding binding;
    private final Resource resource;

    public GalleryFragment(Resource resource) {
        this.resource = resource;
    }

    @SneakyThrows
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        binding.imageView.setTransitionName(resource.getId());

        ImageRepository repository = AppDatabase.getInstance(requireContext()).imageRepository();
        Image image = CompletableFuture.supplyAsync(() -> repository.findByResourcesId(resource.getId())).get();

        PostponeEnterTransitionHelper.loadImageWithTransition(
                requireActivity(),
                Glide.with(this)
                        .load(image.thumbnailPath)
                        .dontAnimate()
                        .apply(requestOptions),
                binding.imageView);
        binding.imageView.postDelayed(this::asyncOriginalImageLoad, 200);
        return binding.getRoot();
    }

    @SneakyThrows
    private void asyncOriginalImageLoad() {
        RequestBuilder<Drawable> requestBuilder = Glide.with(this)
                .load(resource.getFullPath())
                .placeholder(binding.imageView.getDrawable());
        requireActivity().runOnUiThread(() -> requestBuilder.into(binding.imageView));
    }
}
