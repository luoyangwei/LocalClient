package com.luoyangwei.localclient.ui.preview;

import android.graphics.Bitmap;
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
        binding.previewImageView.setTransitionName(resource.getName());

        TransitionSet transitionSet = new TransitionSet();
        transitionSet.addTransition(new ChangeImageTransform())
                .addTransition(new ChangeBounds())
                .addTransition(new ChangeClipBounds())
                .addTransition(new ChangeTransform())
                .setPathMotion(new MaterialArcMotion());
        transitionSet.setDuration(200L);
        setSharedElementEnterTransition(transitionSet);
        setSharedElementReturnTransition(transitionSet);


//        ImageResourceService imageResourceService = ImageResourceService.getInstance(getContext());
//        binding.previewImageView.setImageBitmap(imageResourceService.getBitmap(resource.getId()));

//        binding.previewImageView.setImageBitmap(loadImageBitmap(10));
//        backgroundLoadImageResource();
        return binding.getRoot();
    }

    private Bitmap loadImageBitmap(int sampleSize) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = sampleSize;
//        Bitmap bitmap = BitmapFactory.decodeFile(new File(resource.getData()).getAbsolutePath(), options);
//        return BitmapUtil.rotateBitmap(bitmap, resource.getOrientation());
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

//    public void backgroundLoadImageResource() {
//        Activity activity = requireActivity();
//        new Thread(() -> {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                Log.e(TAG, "Thread sleep error", e);
//            }
//            Bitmap bitmap = loadImageBitmap(1);
//            activity.runOnUiThread(() -> binding.previewImageView.setImageBitmap(bitmap));
//        }).start();
//    }

}
