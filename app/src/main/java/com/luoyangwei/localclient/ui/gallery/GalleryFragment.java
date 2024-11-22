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
import com.bumptech.glide.request.RequestOptions;
import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.Image;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.data.repository.ImageRepository;
import com.luoyangwei.localclient.databinding.FragmentGalleryBinding;
import com.luoyangwei.localclient.ui.preview.PostponeEnterTransitionHelper;

import java.util.concurrent.CompletableFuture;

import lombok.SneakyThrows;

public class GalleryFragment extends Fragment {
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
     * @see GalleryImageView ImageView
     */
    @SneakyThrows
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        ViewPager2 viewPager2 = requireActivity().findViewById(R.id.viewpager);

        binding.imageView.setTransitionName(resource.getId());
        binding.imageView.setDisplay(requireActivity().getWindowManager().getDefaultDisplay());
        binding.imageView.setOnZoomListener(bool -> viewPager2.setUserInputEnabled(!bool));


        ImageRepository repository = AppDatabase.getInstance(requireContext()).imageRepository();
        Image image = CompletableFuture.supplyAsync(() -> repository.findByResourcesId(resource.getId())).get();

        PostponeEnterTransitionHelper.loadImageWithTransition(
                requireActivity(),
                Glide.with(this)
                        .load(image.thumbnailPath)
                        .dontAnimate()
                        .apply(requestOptions),
                binding.imageView);
        binding.imageView.postDelayed(this::asyncOriginalImageLoad, 300);
        return binding.getRoot();
    }

    /**
     * 异步加载原始图片
     */
    @SneakyThrows
    private void asyncOriginalImageLoad() {
        RequestBuilder<Drawable> requestBuilder = Glide.with(this)
                .load(resource.getFullPath())
                .placeholder(binding.imageView.getDrawable());
        requireActivity().runOnUiThread(() -> requestBuilder.into(binding.imageView));
    }
}
