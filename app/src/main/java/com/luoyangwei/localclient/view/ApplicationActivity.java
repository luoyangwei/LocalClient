package com.luoyangwei.localclient.view;

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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationActivity extends AppCompatActivity {

    /**
     * 默认动画时长
     */
    protected static final int DEFAULT_ANIMATION_DURATION = 300;

    /**
     * 默认动画延迟时长
     */
    protected static final int DEFAULT_ANIMATION_DELAY_DURATION = 400;

    /**
     * 默认固定线程池数量
     */
    protected static final int DEFAULT_FIXED_THREAD_POOL_COUNT = 10;

    /**
     * 默认线程池
     */
    protected static final ExecutorService executor = Executors.newFixedThreadPool(DEFAULT_FIXED_THREAD_POOL_COUNT);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeMaterialArcMotion();
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
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
//        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.md_theme_background));
    }
}
