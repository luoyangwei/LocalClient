package com.luoyangwei.localclient.ui.photo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageViewShareAnimation extends Transition {
    private static final String PROPNAME_MATRIX = "matrixTransition:matrix";
    private static final String PROPNAME_BOUNDS = "matrixTransition:bounds";

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    private void captureValues(TransitionValues transitionValues) {
        View view = transitionValues.view;
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            Matrix matrix = imageView.getImageMatrix();
            Rect bounds = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());

            transitionValues.values.put(PROPNAME_MATRIX, new Matrix(matrix));
            transitionValues.values.put(PROPNAME_BOUNDS, bounds);
        }
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }

        ImageView imageView = (ImageView) endValues.view;

        Matrix startMatrix = (Matrix) startValues.values.get(PROPNAME_MATRIX);
        Matrix endMatrix = (Matrix) endValues.values.get(PROPNAME_MATRIX);

        Rect startBounds = (Rect) startValues.values.get(PROPNAME_BOUNDS);
        Rect endBounds = (Rect) endValues.values.get(PROPNAME_BOUNDS);

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(animation -> {
            float fraction = animation.getAnimatedFraction();

            Matrix currentMatrix = new Matrix();
            float[] startValuesArr = new float[9];
            float[] endValuesArr = new float[9];

            startMatrix.getValues(startValuesArr);
            endMatrix.getValues(endValuesArr);

            for (int i = 0; i < 9; i++) {
                startValuesArr[i] = startValuesArr[i] + (endValuesArr[i] - startValuesArr[i]) * fraction;
            }

            currentMatrix.setValues(startValuesArr);
            imageView.setImageMatrix(currentMatrix);

            // Optionally, adjust bounds
            int left = (int) (startBounds.left + (endBounds.left - startBounds.left) * fraction);
            int top = (int) (startBounds.top + (endBounds.top - startBounds.top) * fraction);
            int right = (int) (startBounds.right + (endBounds.right - startBounds.right) * fraction);
            int bottom = (int) (startBounds.bottom + (endBounds.bottom - startBounds.bottom) * fraction);

            imageView.layout(left, top, right, bottom);
        });

        return animator;
    }
}
