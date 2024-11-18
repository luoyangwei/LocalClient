package com.luoyangwei.localclient.data.model;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.Locale;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
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
     * 缩略图路径
     */
    private String thumbnailPath;

    /**
     * 方向
     */
    private Integer orientation;

    /**
     * bucketId 文件夹ID
     */
    private String bucketId;

    /**
     * bucketName 文件夹名
     */
    private String bucketName;

    /**
     * 媒体文件的 MIME 类型，表示文件格式（如 image/heic）。
     */
    private String mimeType;

    /**
     * 媒体文件添加到库中的日期，通常为时间戳（秒）。
     */
    private Long dateAdded;

    @SuppressLint("Range")
    public Resource(Cursor cursor) {
        setId(String.format(Locale.getDefault(), "%d", cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))));
        setName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE)));
        setFullName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
        setFullPath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
        setOrientation(cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION)));
        setBucketId(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)));
        setBucketName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)));
        setMimeType(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)));
        setDateAdded(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(id, resource.id) && Objects.equals(name, resource.name)
                && Objects.equals(fullName, resource.fullName) && Objects.equals(fullPath, resource.fullPath)
                && Objects.equals(thumbnailPath, resource.thumbnailPath) && Objects.equals(orientation, resource.orientation)
                && Objects.equals(bucketId, resource.bucketId) && Objects.equals(bucketName, resource.bucketName)
                && Objects.equals(mimeType, resource.mimeType) && Objects.equals(dateAdded, resource.dateAdded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, fullName, fullPath, thumbnailPath, orientation, bucketId, bucketName, mimeType, dateAdded);
    }

    /**
     * 添加缩略图
     *
     * @param image 图片
     */
    @Deprecated
    public Resource addThumbnail(Image image) {
        return setThumbnailPath(image.thumbnailPath);
    }
}
