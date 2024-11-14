package com.luoyangwei.localclient.data.source.local;

import android.database.Cursor;
import android.provider.MediaStore;

import com.luoyangwei.localclient.data.model.ImageResource;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CursorResolverConverter {
    private static final String TAG = CursorResolverConverter.class.getName();
    private final Cursor cursor;

    public CursorResolverConverter(Cursor cursor) {
        this.cursor = cursor;
    }

    private Map<String, Object> getterValue(String... keys) {
        Map<String, Object> values = new HashMap<>();
        for (String key : keys) {
            int keyIndex = this.cursor.getColumnIndex(key);
            // FIELD_TYPE_NULL = 0
            // FIELD_TYPE_INTEGER = 1
            // FIELD_TYPE_FLOAT = 2
            // FIELD_TYPE_STRING = 3
            // FIELD_TYPE_BLOB = 4
            int type = this.cursor.getType(keyIndex);
            if (type == Cursor.FIELD_TYPE_STRING) {
                values.put(key, this.cursor.getString(keyIndex));
            }
            if (type == Cursor.FIELD_TYPE_BLOB) {
                values.put(key, new String(this.cursor.getBlob(keyIndex), StandardCharsets.UTF_8));
            }
            if (type == Cursor.FIELD_TYPE_FLOAT) {
                values.put(key, this.cursor.getFloat(keyIndex));
            }
            if (type == Cursor.FIELD_TYPE_INTEGER) {
                values.put(key, this.cursor.getInt(keyIndex));
            }
        }
        return values;
    }

    private Long objectToLong(Object o) {
        if (o == null) return null;
        return Long.parseLong(String.valueOf(o));
    }

    private Boolean objectToBoolean(Object o) {
        if (o == null) return null;
        return (Integer) o == 1;
    }

    private Integer objectToInteger(Object o) {
        if (o == null) return null;
        return (Integer) o;
    }

    public ImageResource convertFileEntity() {
        Map<String, Object> values = toMap();
        ImageResource entity = new ImageResource();
        entity.setId(objectToLong(values.get(MediaStore.Images.Media._ID)));
        entity.setData(String.valueOf(values.get(MediaStore.Images.Media.DATA)));
        entity.setSize(objectToLong(values.get(MediaStore.Images.Media.SIZE)));
        entity.setDisplayName(String.valueOf(values.get(MediaStore.Images.Media.DISPLAY_NAME)));
        entity.setDateAdded(objectToLong(values.get(MediaStore.Images.Media.DATE_ADDED)));
        entity.setDateModified(objectToLong(values.get(MediaStore.Images.Media.DATE_MODIFIED)));
        entity.setDateAdded(objectToLong(values.get(MediaStore.Images.Media.DATE_MODIFIED)));
        entity.setDateTaken(objectToLong(values.get(MediaStore.Images.Media.DATE_TAKEN)));
        entity.setMimeType(String.valueOf(values.get(MediaStore.Images.Media.MIME_TYPE)));
//        entity.setIsDram(objectToBoolean(values.get(MediaStore.Images.Media.IS_DRM)));
        entity.setIsPending(objectToBoolean(values.get(MediaStore.Images.Media.IS_PENDING)));
        entity.setIsTrashed(objectToBoolean(values.get(MediaStore.Images.Media.IS_TRASHED)));
        entity.setWeight(objectToInteger(values.get(MediaStore.Images.Media.WIDTH)));
        entity.setHeight(objectToInteger(values.get(MediaStore.Images.Media.HEIGHT)));
        entity.setResolution((String) values.get(MediaStore.Images.Media.RESOLUTION));
        entity.setVolumeName((String) values.get(MediaStore.Images.Media.VOLUME_NAME));
        entity.setRelativePath((String) values.get(MediaStore.Images.Media.RELATIVE_PATH));
        entity.setBucketId((String) values.get(MediaStore.Images.Media.BUCKET_ID));
        entity.setBucketDisplayName((String) values.get(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
        entity.setOrientation(objectToInteger(values.get(MediaStore.Images.Media.ORIENTATION)));
        entity.setIsFavorite(objectToBoolean(values.get(MediaStore.Images.Media.IS_FAVORITE)));
//        entity.setIsDownload(objectToBoolean(values.get(MediaStore.Images.Media.IS_DOWNLOAD)));
        entity.setGenerationAdded(objectToLong(values.get(MediaStore.Images.Media.GENERATION_ADDED)));
        entity.setGenerationModified(objectToLong(values.get(MediaStore.Images.Media.GENERATION_MODIFIED)));
        entity.setXmp((String) values.get(MediaStore.Images.Media.XMP));
        entity.setTitle((String) values.get(MediaStore.Images.Media.TITLE));
        return entity;
    }

    private Map<String, Object> toMap() {
        return getterValue(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media._COUNT,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.IS_DRM,
                MediaStore.Images.Media.IS_PENDING,
                MediaStore.Images.Media.IS_TRASHED,
                MediaStore.Images.Media.DATE_EXPIRES,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.RESOLUTION,
                MediaStore.Images.Media.OWNER_PACKAGE_NAME,
                MediaStore.Images.Media.VOLUME_NAME,
                MediaStore.Images.Media.RELATIVE_PATH,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DOCUMENT_ID,
                MediaStore.Images.Media.INSTANCE_ID,
                MediaStore.Images.Media.ORIGINAL_DOCUMENT_ID,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Media.IS_FAVORITE,
                MediaStore.Images.Media.IS_DOWNLOAD,
                MediaStore.Images.Media.GENERATION_ADDED,
                MediaStore.Images.Media.GENERATION_MODIFIED,
                MediaStore.Images.Media.XMP,
                MediaStore.Images.Media.CD_TRACK_NUMBER,
                MediaStore.Images.Media.ALBUM,
                MediaStore.Images.Media.ARTIST,
                MediaStore.Images.Media.AUTHOR,
                MediaStore.Images.Media.COMPOSER,
                MediaStore.Images.Media.GENRE,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.YEAR,
                MediaStore.Images.Media.DURATION,
                MediaStore.Images.Media.NUM_TRACKS,
                MediaStore.Images.Media.WRITER,
                MediaStore.Images.Media.ALBUM_ARTIST,
                MediaStore.Images.Media.DISC_NUMBER,
                MediaStore.Images.Media.COMPILATION,
                MediaStore.Images.Media.BITRATE,
                MediaStore.Images.Media.CAPTURE_FRAMERATE
        );
    }
}
