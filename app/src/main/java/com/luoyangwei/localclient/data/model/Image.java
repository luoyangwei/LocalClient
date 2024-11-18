package com.luoyangwei.localclient.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Entity(tableName = "images", indices = {@Index("resource_id")})
@RequiredArgsConstructor
public class Image {

    /**
     * 主键
     */
    @PrimaryKey(autoGenerate = true)
    public Long id;

    /**
     * 资源ID
     */
    @ColumnInfo(name = "resource_id")
    public String resourceId;

    /**
     * 媒体文件的标题或名称。
     */
    @ColumnInfo(name = "name")
    public String name;

    /**
     * 服务器生成的ID
     */
    @ColumnInfo(name = "server_id")
    public String serverId;

    /**
     * 储存路径
     */
    @ColumnInfo(name = "full_path")
    public String fullPath;

    /**
     * 媒体文件的 MIME 类型，表示文件格式（如 image/heic）。
     */
    @ColumnInfo(name = "mime_type")
    public String mimeType;

    /**
     * 是否有缩略图
     */
    @ColumnInfo(name = "is_has_thumbnail")
    public Boolean isHasThumbnail;

    /**
     * 缩略图路径
     */
    @ColumnInfo(name = "thumbnail_path")
    public String thumbnailPath;

    /**
     * 媒体文件添加到库中的日期，通常为时间戳（秒）。
     */
    @ColumnInfo(name = "date_added")
    public Long dateAdded;


    public static Image getInstance(Resource resource) {
        Image image = new Image();
//        image.id = Long.parseLong(resource.getId());
        image.resourceId = resource.getId();
        image.name = resource.getName();
        image.fullPath = resource.getFullPath();
        image.mimeType = resource.getMimeType();
        image.isHasThumbnail = false;
        image.dateAdded = resource.getDateAdded();
        return image;
    }

}
