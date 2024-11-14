package com.luoyangwei.localclient.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.RequiredArgsConstructor;

@Entity(tableName = "thumbnail")
@RequiredArgsConstructor
public class Thumbnail {

    /**
     * 自增ID
     */
    @PrimaryKey
    public Integer id;

    /**
     * 资源ID
     */
    @ColumnInfo(name = "resource_id")
    public String resourceId;

}
