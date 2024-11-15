package com.luoyangwei.localclient.data.source.local;

import android.content.Context;

import com.luoyangwei.localclient.data.model.Resource;

import java.util.List;

/**
 * 获取资源的Service
 *
 * @author luoyangwei
 * @date 2024年11月15日10:10:38
 */
public class ResourceService {
    private static final String TAG = ResourceService.class.getName();
    private final Context context;

    public ResourceService(Context context) {
        this.context = context;
    }

    public List<Resource> getResources() {
        context.getContentResolver();
        return List.of();
    }

}
