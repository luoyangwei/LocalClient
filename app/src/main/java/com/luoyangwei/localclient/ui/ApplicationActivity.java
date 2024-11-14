package com.luoyangwei.localclient.ui;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.transition.platform.MaterialArcMotion;
import com.google.gson.Gson;
import com.luoyangwei.localclient.data.source.local.ImageResourceService;

public class ApplicationActivity extends AppCompatActivity {
    protected ImageResourceService resourceService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeMaterialArcMotion();
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        resourceService = ImageResourceService.getInstance(getApplicationContext());
    }

    public <T> T getObjectExtra(String name, Class<T> clazz) {
        return new Gson().fromJson(getIntent().getStringExtra(name), clazz);
    }

    /**
     * 初始化 Material Arc Motion
     */
    private void initializeMaterialArcMotion() {
        TransitionSet transitionSet = new TransitionSet();
        transitionSet.addTransition(new ChangeImageTransform())
                .addTransition(new ChangeBounds())
                .addTransition(new ChangeClipBounds())
                .addTransition(new ChangeTransform())
                .setPathMotion(new MaterialArcMotion());
        transitionSet.setDuration(200L);
        getWindow().setSharedElementEnterTransition(transitionSet);
        getWindow().setSharedElementReturnTransition(transitionSet);
    }

    protected void enableEdgeToEdge() {
        EdgeToEdge.enable(this);
    }
}
