package com.luoyangwei.localclient.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;

@SuppressLint("ClickableViewAccessibility")
public class PreviewImageView extends AppCompatImageView {
    private static final String TAG = PreviewImageView.class.getName();

    public PreviewImageView(Context context) {
        super(context);
        addOnGlobalLayoutListener();
    }

    public PreviewImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addOnGlobalLayoutListener();
    }

    public PreviewImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addOnGlobalLayoutListener();
    }

    private void addOnGlobalLayoutListener() {
        getViewTreeObserver().addOnPreDrawListener(this::onPreDrawListener);
    }

    private boolean onPreDrawListener() {
        toFitCenter();
        return true;
    }

    private void toFixXY() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            throw new RuntimeException("drawable is null");
        }

        float drawableWidth = drawable.getIntrinsicWidth();
        float drawableHeight = drawable.getIntrinsicHeight();

        float widthPercentage = ((float) getWidth()) / drawableWidth;
        float heightPercentage = ((float) getHeight()) / drawableHeight;
        Matrix matrix = new Matrix();
        matrix.setScale(widthPercentage, heightPercentage);
        setImageMatrix(matrix);
    }

    /**
     * 打开后模拟 fit center 效果
     */
    private void toFitCenter() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            throw new RuntimeException("drawable is null");
        }

        float width = getWidth();
        float height = getHeight();

        float drawableWidth = drawable.getIntrinsicWidth();
        float drawableHeight = drawable.getIntrinsicHeight();

        float widthPercentage = width / drawableWidth;
        float heightPercentage = height / drawableHeight;

        float minPercentage = Math.min(widthPercentage, heightPercentage);

        int targetWidth = Math.round(minPercentage * drawableWidth);
        int targetHeight = Math.round(minPercentage * drawableHeight);

        Matrix matrix = new Matrix();
        matrix.postScale(minPercentage, minPercentage);
        matrix.postTranslate((width - targetWidth) / 2, (height - targetHeight) / 2);
        setImageMatrix(matrix);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, String.format("Action: %d", event.getAction()));
        if (event.getAction() == MotionEvent.ACTION_UP) {
            click(event);
        }
        return true;
    }

    private void click(MotionEvent event) {
    }
}
