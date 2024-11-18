package com.luoyangwei.localclient.ui.preview;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.transition.platform.MaterialArcMotion;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.databinding.FragmentPreviewPagerViewBinding;

public class PreviewViewPagerFragment extends Fragment {
    private static final String TAG = PreviewViewPagerFragment.class.getName();
    private FragmentPreviewPagerViewBinding binding;

    private final Resource resource;

    public PreviewViewPagerFragment(Resource resource) {
        this.resource = resource;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPreviewPagerViewBinding.inflate(inflater);

        TransitionSet transitionSet = new TransitionSet();
        transitionSet.addTransition(new ChangeImageTransform())
                .addTransition(new ChangeBounds())
                .addTransition(new ChangeClipBounds())
                .addTransition(new ChangeTransform())
                .setPathMotion(new MaterialArcMotion());
        transitionSet.setDuration(200L);
        setSharedElementEnterTransition(transitionSet);
        setSharedElementReturnTransition(transitionSet);

        binding.previewImageView.setTransitionName(resource.getName());
        Glide.with(this)
                .load(resource.getFullPath())
                .thumbnail(Glide.with(this).load(resource.getThumbnailPath()))
                .into(binding.previewImageView);
        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
