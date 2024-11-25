package com.luoyangwei.localclient.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.source.local.ResourceService;

import java.util.List;

/**
 * 储存已经加载好的缩略图数据
 */
public class AppLoadingCache {
    private static final String TAG = AppLoadingCache.class.getName();
    private static AppLoadingCache instance;
    private static List<Resource> resources;
    private static LoadingCache<String, Resource> cache;

    /**
     * 单例的
     *
     * @return AppLoadingCache
     */
    public static AppLoadingCache getInstance(Context context) {
        if (instance == null) {
            instance = new AppLoadingCache();
            loadCache(context);
        }
        return instance;
    }

    public static void loadCache(Context context) {
        // 预存数据
        ResourceService resourceService = new ResourceService(context);
        resources = resourceService.getResources();
        cache = CacheBuilder.newBuilder()
                // 设置并发级别为当前处理器核心数
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                // 初始缓存容量
                .initialCapacity(resources.size())
                // 设置最大容量
                .maximumSize(resources.size() * 10L).build(new CacheLoader<>() {
                    @NonNull
                    @Override
                    public Resource load(@NonNull String key) throws Exception {
                        Log.w(TAG, "缓存中没有该数据，key：" + key);
                        return resourceService.getResource(key);
                    }
                });
    }

    public List<Resource> getResources() {
        return resources;
    }

    public Resource get(String key) {
        try {
            return cache.get(key);
        } catch (Exception e) {
            Log.e(TAG, "获取缓存数据失败", e);
            return null;
        }
    }

    public void put(String key, Resource value) {
        cache.put(key, value);
    }
}
