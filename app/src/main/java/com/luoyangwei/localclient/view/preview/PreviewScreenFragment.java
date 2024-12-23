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

import com.bumptech.glide.Glide;
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

        binding.imageView.setTransitionName(resource.getId());
        binding.imageView.setMaximumScale(5f);
        binding.imageView.setMediumScale(4f);
        binding.imageView.setMinimumScale(1f);

        Glide.with(activity.getApplicationContext())
                .load(resource.getFullPath())
//                .override(500)
                .sizeMultiplier(0.5f)
                .dontTransform()
                .dontAnimate()
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
