package com.luoyangwei.localclient.data.repository;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.luoyangwei.localclient.data.model.Image;

import lombok.Getter;

@Database(entities = {Image.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    @Getter
    public static final String databaseName = "local_client";

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
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                            AppDatabase.getDatabaseName())
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
