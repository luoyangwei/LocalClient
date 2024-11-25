package com.luoyangwei.localclient;

import android.app.Application;

import lombok.SneakyThrows;

public class LocalClientApplication extends Application {
    private static final String TAG = LocalClientApplication.class.getName();

    @SneakyThrows
    @Override
    public void onCreate() {
        super.onCreate();
    }

}
