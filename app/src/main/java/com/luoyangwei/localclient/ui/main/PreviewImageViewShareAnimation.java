package com.luoyangwei.localclient.ui.main;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.transition.Transition;
import androidx.transition.TransitionValues;

public class PreviewImageViewShareAnimation extends Transition {
    private static final String TAG = PreviewImageViewShareAnimation.class.getName();

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        Log.i(TAG, "captureStartValues");
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        Log.i(TAG, "captureEndValues");
    }
}
