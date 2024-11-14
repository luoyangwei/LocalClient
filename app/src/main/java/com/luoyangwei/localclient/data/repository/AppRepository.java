package com.luoyangwei.localclient.data.repository;

import android.content.Context;

import androidx.room.Room;

public class AppRepository {
    private final ThumbnailRepository thumbnailRepository;

    public AppRepository(Context context) {
        AppDatabase db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                        AppDatabase.getDatabaseName())
                .build();
        thumbnailRepository = db.thumbnailRepository();
    }
}
