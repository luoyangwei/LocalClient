package com.luoyangwei.localclient.utils;


import android.view.View;

import java.util.function.Consumer;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EventUtils {

    public OnDoubleClickListener handleDoubleClickListener(Consumer<View> consumer) {
        return new OnDoubleClickListener(consumer);
    }

    public OnDoubleClickListener handleDoubleClickListener(Consumer<View> consumer, long delimit) {
        return new OnDoubleClickListener(consumer, delimit);
    }

    public static class OnDoubleClickListener implements View.OnClickListener {
        private static final long DOUBLE_CLICK_TIME_DELIMIT = 300;

        /**
         * 定义双击时间间隔，单位为毫秒
         */
        private final long delimit;

        /**
         * 点击事件
         */
        private final Consumer<View> consumer;

        /**
         * 首次点击时间
         */
        private long firstClickTime = 0;


        public OnDoubleClickListener(Consumer<View> consumer) {
            this(consumer, DOUBLE_CLICK_TIME_DELIMIT);
        }

        public OnDoubleClickListener(Consumer<View> consumer, long delimit) {
            this.consumer = consumer;
            this.delimit = delimit;
        }

        @Override
        public void onClick(View v) {
            long currentTime = System.currentTimeMillis();
            if (firstClickTime == 0) {
                firstClickTime = currentTime;
            } else {
                if (currentTime - firstClickTime <= delimit) {
                    consumer.accept(v);
                } else {
                    firstClickTime = currentTime;
                }
            }
        }
    }
}
