package com.luoyangwei.localclient.data.model;


import lombok.Data;

@Data
public class ImageResource {

    /**
     * 资源ID
     */
    private Long id;

    /**
     * 服务器生成的ID
     */
    private String serverId;

    /**
     * 表示该媒体文件是否被标记为收藏。0 表示未收藏，1 表示已收藏。
     */
    private Boolean isFavorite;

    /**
     * 媒体文件所在的存储卷的名称，比如外部存储或内部存储。
     */
    private String volumeName;

    /**
     * 媒体文件的标题或名称。
     */
    private String title;

    /**
     * 媒体文件的分辨率，以宽×高格式表示。
     */
    private String resolution;

    /**
     * 媒体文件的大小（以字节为单位）。
     */
    private Long size;

    /**
     * 媒体文件拍摄的日期，通常为时间戳（毫秒）。
     */
    private Long dateTaken;

    /**
     * 表示添加该媒体文件的版本号或生成编号。
     */
    private Long generationAdded;

    /**
     * 表示该媒体文件是否被标记为删除（垃圾箱）。0 表示未删除，1 表示已删除。
     */
    private Boolean isTrashed;

    /**
     * 存储的 XMP 元数据字符串，可能为空，表示没有相关的 XMP 数据。
     */
    private String xmp;

    /**
     * 表示最后修改该媒体文件的版本号或生成编号。
     */
    private Long generationModified;

    /**
     * 媒体文件的高度（以像素为单位）。
     */
    private Integer height;

    /**
     * 媒体文件的宽度（以像素为单位）。
     */
    private Integer weight;

    /**
     * 该媒体文件所属的文件夹（或相册）的唯一标识符。
     */
    private String bucketId;

    /**
     * 该媒体文件所属文件夹（或相册）的可视名称。
     */
    private String bucketDisplayName;

    /**
     * 媒体文件的实际存储路径。
     * <br/>
     * alias: _data
     */
    private String data;

    /**
     * 媒体文件的方向，通常用于表示图像的旋转角度。
     */
    private Integer orientation;

    /**
     * 媒体文件的显示名称，包括扩展名。
     * <br/>
     * alias: _display_name
     */
    private String displayName;

//    /**
//     * 表示该媒体文件是否受数字版权管理（DRM）保护。0 表示未受保护，1 表示受保护。
//     */
//    private Boolean isDram;

    /**
     * 媒体文件添加到库中的日期，通常为时间戳（秒）。
     */
    private Long dateAdded;

    /**
     * 媒体文件最后修改的日期，通常为时间戳（秒）。
     */
    private Long dateModified;

    /**
     * 媒体文件的 MIME 类型，表示文件格式（如 image/heic）。
     */
    private String mimeType;

    /**
     * 媒体文件的相对路径，相对于某个基路径。
     */
    private String relativePath;

    /**
     * 媒体文件的缩略图的路径。
     */
    private String thumbnailPath;

    /**
     * 表示该媒体文件是否正在处理中。0 表示未处理，1 表示正在处理。
     */
    private Boolean isPending;

    /**
     * 是否上传到云
     */
    private Boolean isUploaded;

    public String getIdString() {
        return getId().toString();
    }
}
