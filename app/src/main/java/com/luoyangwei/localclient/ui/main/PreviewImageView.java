package com.luoyangwei.localclient.ui.main;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.google.android.material.motion.MotionUtils;
import com.luoyangwei.localclient.R;

@SuppressLint("ClickableViewAccessibility")
public class PreviewImageView extends AppCompatImageView {
    private static final String TAG = PreviewImageView.class.getName();

    private ScaleType matrixScaleType;

    private final Matrix startMatrix = new Matrix();
    private final Matrix endMatrix = new Matrix();
    private final Matrix currentMatrix = new Matrix();

    public PreviewImageView(Context context) {
        super(context);
        init();
    }

    public PreviewImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        handleCustomAttrs(context, attrs);
        init();
    }

    public PreviewImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handleCustomAttrs(context, attrs);
        init();
    }

    /**
     * <!-- Scale using the image matrix when drawing. See
     * {@link android.widget.ImageView#setImageMatrix(Matrix)}. -->
     * <enum name="matrix" value="0" />
     * <!-- Scale the image using {@link android.graphics.Matrix.ScaleToFit#FILL}. -->
     * <enum name="fitXY" value="1" />
     * <!-- Scale the image using {@link android.graphics.Matrix.ScaleToFit#START}. -->
     * <enum name="fitStart" value="2" />
     * <!-- Scale the image using {@link android.graphics.Matrix.ScaleToFit#CENTER}. -->
     * <enum name="fitCenter" value="3" />
     * <!-- Scale the image using {@link android.graphics.Matrix.ScaleToFit#END}. -->
     * <enum name="fitEnd" value="4" />
     * <!-- Center the image in the view, but perform no scaling. -->
     * <enum name="center" value="5" />
     * <!-- Scale the image uniformly (maintain the image's aspect ratio) so both dimensions
     * (width and height) of the image will be equal to or larger than the corresponding
     * dimension of the view (minus padding). The image is then centered in the view. -->
     * <enum name="centerCrop" value="6" />
     * <!-- Scale the image uniformly (maintain the image's aspect ratio) so that both
     * dimensions (width and height) of the image will be equal to or less than the
     * corresponding dimension of the view (minus padding). The image is then centered in
     * the view. -->
     * <enum name="centerInside" value="7" />
     */
    private void handleCustomAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PreviewImageView);
        int matrixScaleType = typedArray.getInt(R.styleable.PreviewImageView_matrixScaleType, 0);
        switch (matrixScaleType) {
            case 0:
                this.matrixScaleType = ScaleType.MATRIX;
                break;
            case 1:
                this.matrixScaleType = ScaleType.FIT_XY;
                break;
            case 2:
                this.matrixScaleType = ScaleType.FIT_START;
                break;
            case 3:
                this.matrixScaleType = ScaleType.FIT_CENTER;
                break;
            case 4:
                this.matrixScaleType = ScaleType.FIT_END;
                break;
            case 5:
                this.matrixScaleType = ScaleType.CENTER;
                break;
            case 6:
                this.matrixScaleType = ScaleType.CENTER_CROP;
                break;
        }
        typedArray.recycle();
    }

    private void init() {
        setScaleType(ScaleType.MATRIX);
        getViewTreeObserver().addOnDrawListener(() -> {
            if (getDrawable() != null) {
                float width = getDrawable().getIntrinsicWidth();
                float height = getDrawable().getIntrinsicHeight();
                if (this.matrixScaleType == ScaleType.FIT_CENTER) {
                    setImageMatrix(fitCenterMatrix(width, height));
                }
                if (this.matrixScaleType == ScaleType.CENTER_CROP) {
                    setImageMatrix(centerCropMatrix(width, height));
                }
                invalidate();
            }
        });
    }

    public void animateScaleTypeTransition(ScaleType scaleType) {
        Drawable drawable = getDrawable();
        if (drawable == null) return;

        // 计算起始和结束的矩阵
        initializeStartMatrix();
        if (scaleType == ScaleType.CENTER_CROP) {
            calculateCenterCropMatrix(drawable);
        }
        if (scaleType == ScaleType.FIT_CENTER) {
            calculateFitCenterMatrix(drawable);
        }

        TimeInterpolator interpolator = MotionUtils.resolveThemeInterpolator(getContext(), com.google.android.material.R.attr.motionEasingStandardInterpolator, new FastOutSlowInInterpolator());

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(300); // 动画持续时间
        animator.setInterpolator(interpolator);
        animator.addUpdateListener(animation -> {
            float fraction = (float) animation.getAnimatedValue();
            currentMatrix.set(interpolateMatrices(startMatrix, endMatrix, fraction));
            setImageMatrix(currentMatrix);
        });
        animator.start();
    }

    private void initializeStartMatrix() {
        startMatrix.reset();
        startMatrix.set(getImageMatrix());
    }

    private void calculateFitCenterMatrix(Drawable drawable) {
        int dWidth = drawable.getIntrinsicWidth();
        int dHeight = drawable.getIntrinsicHeight();
        int vWidth = getWidth();
        int vHeight = getHeight();

        float scale = Math.min((float) vWidth / dWidth, (float) vHeight / dHeight);
        float dx = (vWidth - dWidth * scale) * 0.5f;
        float dy = (vHeight - dHeight * scale) * 0.5f;

        endMatrix.reset();
        endMatrix.setScale(scale, scale);
        endMatrix.postTranslate(dx, dy);
    }

    private void calculateCenterCropMatrix(Drawable drawable) {
        float dWidth = drawable.getIntrinsicWidth();
        float dHeight = drawable.getIntrinsicHeight();
        float vWidth = getWidth();
        float vHeight = getHeight();

        float scale;
        float dx;
        float dy;
        if (dWidth * vHeight > vWidth * dHeight) {
            scale = vHeight / dHeight;
            dx = (vWidth - dWidth * scale) * 0.5f;
            dy = 0f;
        } else {
            scale = vWidth / dWidth;
            dx = 0;
            dy = (vHeight - dHeight * scale) * 0.5f;
        }

        endMatrix.reset();
        endMatrix.setScale(scale, scale);
        endMatrix.postTranslate(dx, dy);
    }

    private Matrix interpolateMatrices(Matrix fromMatrix, Matrix toMatrix, float fraction) {
        float[] fromValues = new float[9];
        float[] toValues = new float[9];
        float[] interpolatedValues = new float[9];

        fromMatrix.getValues(fromValues);
        toMatrix.getValues(toValues);

        for (int i = 0; i < 9; i++) {
            interpolatedValues[i] = fromValues[i] + (toValues[i] - fromValues[i]) * fraction;
        }

        Matrix interpolatedMatrix = new Matrix();
        interpolatedMatrix.setValues(interpolatedValues);
        return interpolatedMatrix;
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
    private Matrix fitCenterMatrix(float drawableWidth, float drawableHeight) {
        float width = getWidth();
        float height = getHeight();

        float widthPercentage = width / drawableWidth;
        float heightPercentage = height / drawableHeight;

        float minPercentage = Math.min(widthPercentage, heightPercentage);

        int targetWidth = Math.round(minPercentage * drawableWidth);
        int targetHeight = Math.round(minPercentage * drawableHeight);

        Matrix matrix = new Matrix();
        matrix.postScale(minPercentage, minPercentage);
        matrix.postTranslate((width - targetWidth) / 2, (height - targetHeight) / 2);
        return matrix;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private Matrix centerCropMatrix(float drawableWidth, float drawableHeight) {
        float width = getWidth();
        float height = getHeight();

        float scale;
        float dx;
        float dy;

        if (drawableWidth * height > width * drawableHeight) {
            scale = height / drawableHeight;
            dx = (width - drawableWidth * scale) * 0.5f;
            dy = 0f;
        } else {
            scale = width / drawableWidth;
            dx = 0;
            dy = (height - drawableHeight * scale) * 0.5f;
        }

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate(dx + 0.5f, dy + 0.5f);
        return matrix;
    }

}
