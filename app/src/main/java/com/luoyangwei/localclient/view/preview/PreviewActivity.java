package com.luoyangwei.localclient.view.preview;

import android.app.SharedElementCallback;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.AppLoadingCache;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.databinding.ActivityPreviewBinding;
import com.luoyangwei.localclient.view.ApplicationActivity;

import java.util.List;
import java.util.Map;

public class PreviewActivity extends ApplicationActivity {
    private static final String TAG = "PreviewActivity";

    private ActivityPreviewBinding binding;
    private Resource resource;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();

        binding = ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<Resource> resources = AppLoadingCache.getInstance(getApplicationContext()).getResources();
        String resourceId = getIntent().getStringExtra("resourceId");
        resource = AppLoadingCache.getInstance(getApplicationContext()).get(resourceId);

        PreviewScreenSlidePagerAdapter adapter = new PreviewScreenSlidePagerAdapter(this, this, resources);
        binding.viewpager.setAdapter(adapter);
        int indexOf = resources.indexOf(resource);
        binding.viewpager.setCurrentItem(indexOf, false);

        prepareSharedElementTransition();
        postponeEnterTransition();
        Log.i(TAG, "onCreated");
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareSharedElementTransition();
        postponeEnterTransition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void prepareSharedElementTransition() {
        Transition transition = TransitionInflater.from(getApplicationContext())
                .inflateTransition(R.transition.photo_shared_element_transition);
        getWindow().setSharedElementEnterTransition(transition);
        getWindow().setSharedElementReturnTransition(transition);
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);
            }

            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
            }

            @Override
            public void onRejectSharedElements(List<View> rejectedSharedElements) {
                super.onRejectSharedElements(rejectedSharedElements);
            }

            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
            }

            @Override
            public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
                return super.onCaptureSharedElementSnapshot(sharedElement, viewToGlobalMatrix, screenBounds);
            }

            @Override
            public View onCreateSnapshotView(Context context, Parcelable snapshot) {
                return super.onCreateSnapshotView(context, snapshot);
            }

            @Override
            public void onSharedElementsArrived(List<String> sharedElementNames, List<View> sharedElements, OnSharedElementsReadyListener listener) {
                super.onSharedElementsArrived(sharedElementNames, sharedElements, listener);
            }
        });
    }
}
