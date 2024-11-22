package com.luoyangwei.localclient.utils;

import android.animation.TimeInterpolator;
import android.content.Context;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.google.android.material.motion.MotionUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TransitionUtil {

    /**
     * 获取默认的 Interpolator
     *
     * @param context 上下文
     * @return Interpolator
     */
    public TimeInterpolator getInterpolator(Context context) {
        return MotionUtils.resolveThemeInterpolator(context,
                com.google.android.material.R.attr.motionEasingStandardInterpolator, new FastOutSlowInInterpolator());
    }
}
