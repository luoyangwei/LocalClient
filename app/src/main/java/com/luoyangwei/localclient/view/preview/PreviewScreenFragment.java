package com.luoyangwei.localclient.view.preview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.databinding.FragmentPreviewScreenBinding;
import com.luoyangwei.localclient.utils.GlideUtil;

public class PreviewScreenFragment extends Fragment {
    private static final String TAG = "PreviewScreenFragment";

    private FragmentPreviewScreenBinding binding;

    private final Activity activity;
    private final Resource resource;
    private final int position;

    public PreviewScreenFragment(Activity activity, Resource resource, int position) {
        this.activity = activity;
        this.resource = resource;
        this.position = position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPreviewScreenBinding.inflate(inflater, container, false);
        ViewPager2 viewPager = activity.findViewById(R.id.viewpager);

        binding.imageView.setTransitionName(resource.getId());
        binding.imageView.setMaximumScale(4f);
        binding.imageView.setMinimumScale(1f);
        PhotoViewAttacher attacher = binding.imageView.getAttacher();

        // 检查照片是原尺寸就可以滑动
        attacher.setOnScaleChangeListener((scaleFactor, focusX, focusY) ->
                viewPager.setUserInputEnabled(Math.round(attacher.getScale()) == attacher.getMinimumScale()));

        Glide.with(activity.getApplicationContext())
                .load(resource.getFullPath())
                .sizeMultiplier(0.5f)
                .apply(GlideUtil.defaultOptions())
                .listener(GlideUtil.drawableRequestListener(resource, drawable -> {
                    activity.startPostponedEnterTransition();
                    binding.imageView.postDelayed(() ->
                                    Glide.with(activity.getApplicationContext())
                                            .load(resource.getFullPath())
                                            .dontTransform()
                                            .dontAnimate()
                                            .placeholder(drawable)
                                            .into(binding.imageView)
                            , 260);
                    return false;
                }))
                .into(binding.imageView);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
    }
}
