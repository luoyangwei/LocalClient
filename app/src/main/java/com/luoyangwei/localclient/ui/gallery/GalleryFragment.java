package com.luoyangwei.localclient.ui.gallery;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.Image;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.data.repository.ImageRepository;
import com.luoyangwei.localclient.databinding.FragmentGalleryBinding;

import java.util.concurrent.CompletableFuture;

import lombok.SneakyThrows;

public class GalleryFragment extends Fragment implements RequestListener<Drawable> {
    private static final String TAG = GalleryFragment.class.getName();
    private final RequestOptions requestOptions = new RequestOptions();
    private FragmentGalleryBinding binding;
    private final Resource resource;

    public GalleryFragment(Resource resource) {
        this.resource = resource;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     * @see com.github.chrisbanes.photoview.PhotoView ImageView
     */
    @SneakyThrows
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        ViewPager2 viewPager = requireActivity().findViewById(R.id.viewpager);
        binding.imageView.setTransitionName(resource.getId());
        binding.imageView.setMaximumScale(5f);
        binding.imageView.setMinimumScale(1f);
        binding.imageView.setOnScaleChangeListener((scaleFactor, focusX, focusY) -> viewPager.setUserInputEnabled(scaleFactor == 1f));

        ImageRepository repository = AppDatabase.getInstance(requireContext()).imageRepository();
        Image image = CompletableFuture.supplyAsync(() -> repository.findByResourcesId(resource.getId())).get();

        Glide.with(this)
                .load(image.thumbnailPath)
                .dontAnimate()
                .apply(requestOptions)
                .listener(this)
                .into(binding.imageView);
        return binding.getRoot();
    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model,
                                @NonNull Target<Drawable> target, boolean isFirstResource) {
        requireActivity().startPostponedEnterTransition();
        asyncOriginalImageLoad();
        return false;
    }

    @Override
    public boolean onResourceReady(@NonNull Drawable resource,
                                   @NonNull Object model, Target<Drawable> target,
                                   @NonNull DataSource dataSource, boolean isFirstResource) {
        requireActivity().startPostponedEnterTransition();
        asyncOriginalImageLoad();
        return false;
    }

    /**
     * 异步加载原始图片
     */
    @SneakyThrows
    private void asyncOriginalImageLoad() {
        binding.imageView.postDelayed(() -> {
            RequestBuilder<Drawable> requestBuilder = Glide.with(this)
                    .load(resource.getFullPath())
                    .placeholder(binding.imageView.getDrawable());
            requireActivity().runOnUiThread(() -> requestBuilder.into(binding.imageView));
        }, 300);

    }
}
