package com.luoyangwei.localclient.ui.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.luoyangwei.localclient.utils.TransitionUtil;

import java.util.function.Consumer;

import lombok.Setter;

/**
 * 自定义的 ImageView
 */
public class GalleryImageView extends AppCompatImageView {
    private static final String TAG = GalleryImageView.class.getName();
    private TouchGestureAction action;

    public GalleryImageView(@NonNull Context context) {
        super(context);
        initializeTouchEvent(context);
    }

    public GalleryImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeTouchEvent(context);
    }

    public GalleryImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeTouchEvent(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeTouchEvent(Context context) {
        action = new TouchGestureAction(this);
        TouchGestureListener listener = new TouchGestureListener(action);
        GestureDetector gestureDetector = new GestureDetector(context, listener);
        setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
    }

    public void setOnZoomListener(Consumer<Boolean> onZoomListener) {
        action.setOnZoomListener(onZoomListener);
    }

    /**
     * 设置显示器信息
     *
     * @param display 显示器
     */
    public void setDisplay(Display display) {
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        action.setDisplayMetrics(metrics);
    }

    /**
     * 触摸手势动作
     */
    private static final class TouchGestureAction {
        private static final String TAG = TouchGestureAction.class.getName();
        private static final int ZOOM_DURATION = 200;
        private static final float ZOOM_SCALE = 2.8f;

        private final ImageView target;

        @Setter
        private DisplayMetrics displayMetrics;
        @Setter
        private Consumer<Boolean> onZoomListener;

        private boolean isZooming = false;
        private static float offsetX;
        private static float offsetY;

        public TouchGestureAction(ImageView target) {
            this.target = target;
        }

        /**
         * 放大跟缩小
         *
         * @param e 事件
         */
        public void zoom(MotionEvent e) {
            ViewPropertyAnimator animator;
            if (!isZooming) {
                animator = target.animate();
                animator.scaleY(ZOOM_SCALE).scaleX(ZOOM_SCALE);

                // 图片的宽高
                float imageWidth = target.getMeasuredWidth() * ZOOM_SCALE;
                float imageHeight = target.getMeasuredHeight() * ZOOM_SCALE;

                // 容器的尺寸
                float containerWidth = displayMetrics.widthPixels;
                float containerHeight = displayMetrics.heightPixels;

                // 事件坐标
                float clickX = e.getX();
                float clickY = e.getY();

                // 将点击位置转换为相对中心点坐标
                float relativeClickX = clickX - containerWidth / 2f;
                float relativeClickY = clickY - containerHeight / 2f;

                // 图片中心可移动的最大范围
                float maxOffsetX = (imageWidth - containerWidth) / 2f;
                float maxOffsetY = (imageHeight - containerHeight) / 2f;

                offsetX = -relativeClickX;
                offsetY = -relativeClickY;

                // 限制偏移范围，防止图片超出边界
                offsetX = Math.max(-maxOffsetX, Math.min(maxOffsetX, offsetX));
                offsetY = Math.max(-maxOffsetY, Math.min(maxOffsetY, offsetY));

                animator.translationX(offsetX).translationY(offsetY);
            } else {
                animator = target.animate().scaleY(1f)
                        .translationX(0)
                        .translationY(0)
                        .scaleX(1f);
            }
            animator.setDuration(ZOOM_DURATION)
                    .setInterpolator(TransitionUtil.getInterpolator(target.getContext()))
                    .start();

            isZooming = !isZooming;
            onZoomListener.accept(isZooming);
        }

        /**
         * 移动
         *
         * @param e1        按下事件
         * @param e2        移动事件
         * @param distanceX X轴移动距离
         * @param distanceY Y轴移动距离
         */
        public void move(MotionEvent e1,
                         MotionEvent e2,
                         float distanceX,
                         float distanceY) {
//            Log.i(TAG, String.format("Move distanceX: %f, distanceY: %f", distanceX, distanceY));
            if (distanceX > 0) {
                Log.i(TAG, "向左滑");
            } else {
                Log.i(TAG, "向右滑");
            }
        }
    }

    /**
     * 触摸手势监听器
     */
    private static final class TouchGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String TAG = TouchGestureListener.class.getName();
        private final TouchGestureAction touchGestureAction;

        /**
         * 双击按下
         */
        private static final int ACTION_DOUBLE_CLICK_DOWN = 0;

        /**
         * 双击抬起
         */
        private static final int ACTION_DOUBLE_CLICK_UP = 1;

        public TouchGestureListener(TouchGestureAction touchGestureAction) {
            this.touchGestureAction = touchGestureAction;
        }

        /**
         * 每次按下屏幕立即出发，
         *
         * @param e The down motion event.
         * @return true if the event is consumed, else false
         */
        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent e) {
            Log.i(TAG, "onSingleTapUp: " + e.getAction());
            return true;
        }

        /**
         * 滑动事件
         *
         * @param e1        The first down motion event that started the scrolling. A {@code null} event
         *                  indicates an incomplete event stream or error state.
         * @param e2        The move motion event that triggered the current onScroll.
         * @param distanceX The distance along the X axis that has been scrolled since the last
         *                  call to onScroll. This is NOT the distance between {@code e1}
         *                  and {@code e2}.
         * @param distanceY The distance along the Y axis that has been scrolled since the last
         *                  call to onScroll. This is NOT the distance between {@code e1}
         *                  and {@code e2}.
         * @return true if the event is consumed, else false
         */
        @Override
        public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
            touchGestureAction.move(e1, e2, distanceX, distanceY);
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(@NonNull MotionEvent e) {
            switch (e.getAction()) {
                case ACTION_DOUBLE_CLICK_DOWN:
                    break;
                case ACTION_DOUBLE_CLICK_UP:
                    touchGestureAction.zoom(e);
                    break;
            }
            return true;
        }
    }
}
