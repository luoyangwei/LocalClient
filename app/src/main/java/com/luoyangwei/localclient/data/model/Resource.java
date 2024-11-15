package com.luoyangwei.localclient.data.model;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class Resource {

    /**
     * id
     */
    private String id;

    /**
     * 全名（不带后缀）
     */
    private String name;

    /**
     * 全名（带后缀）
     * a.png
     */
    private String fullName;

    /**
     * 文件全路径
     */
    private String fullPath;

    /**
     * 方向
     */
    private Integer orientation;

    @SuppressLint("Range")
    public Resource(Cursor cursor) {
        setId(String.format(Locale.getDefault(), "%d", cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))));
        setName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE)));
        setFullName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
        setFullPath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
        setOrientation(cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION)));
    }

}
