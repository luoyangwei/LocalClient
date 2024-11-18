package com.luoyangwei.localclient.data.repository;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.luoyangwei.localclient.data.model.Image;
import com.luoyangwei.localclient.data.model.Thumbnail;

import lombok.Getter;

@Database(entities = {Thumbnail.class, Image.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    @Getter
    public static final String databaseName = "local_client";

    /**
     * 获取缩略图仓库
     *
     * @return 缩略图仓库
     */
    public abstract ThumbnailRepository thumbnailRepository();

    /**
     * 获取图片仓库
     *
     * @return 图片仓库
     */
    public abstract ImageRepository imageRepository();

    /**
     * 获取数据库实例
     *
     * @param context 上下文
     * @return 数据库实例
     */
    public static AppDatabase getInstance(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                        AppDatabase.getDatabaseName())
                .fallbackToDestructiveMigration()
                .build();
    }
}
