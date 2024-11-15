package com.luoyangwei.localclient;

import android.app.Application;
import android.util.Log;

import com.luoyangwei.localclient.utils.DiskLruCacheUtil;

import lombok.SneakyThrows;

public class LocalClientApplication extends Application {
    private static final String TAG = LocalClientApplication.class.getName();

    @SneakyThrows
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "ImageResourceService 初始化");
        DiskLruCacheUtil.initDiskLruCache(getExternalCacheDir());
        //        ImageResourceService.refreshInstance(getApplicationContext());
    }

}
