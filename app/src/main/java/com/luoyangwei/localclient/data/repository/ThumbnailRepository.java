package com.luoyangwei.localclient.data.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.luoyangwei.localclient.data.model.Thumbnail;


@Dao
public interface ThumbnailRepository {

    @Insert
    void insert(Thumbnail thumbnail);

    @Query("SELECT * FROM thumbnail WHERE resource_id = :resourceId")
    Thumbnail queryByResourceId(String resourceId);
}
