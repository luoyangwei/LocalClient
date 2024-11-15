package com.luoyangwei.localclient.data.source.local;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import com.jakewharton.disklrucache.DiskLruCache;
import com.luoyangwei.localclient.utils.DiskLruCacheUtil;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class ImageResourceService {
    private static final String TAG = ImageResourceService.class.getName();
    private static ImageResourceService instance;
    private final static long MAX_SIZE = 10 * 1024 * 1024;
    private final static int APP_VERSION = 1;
    private final static int VALUE_COUNT = 1;

//    /**
//     * 加载状态
//     *
//     * @see Status
//     */
//    @Getter
//    private Status status;

    /**
     * 缩略图生成器
     */
    private final ImageThumbnailGenerator imageThumbnailGenerator = new ImageThumbnailGenerator();

    @Getter
    private DiskLruCache diskLruCache;
    private final List<ImageResourceEntry> entry = new ArrayList<>();

    public static ImageResourceService getInstance(Context context) {
        if (instance == null) {
            instance = new ImageResourceService(context);
        }
        return instance;
    }

    public static ImageResourceService refreshInstance(Context context) {
        instance = new ImageResourceService(context);
        return instance;
    }

    public DiskLruCache initDiskLruCache(Context context) {
        File file = DiskLruCacheUtil.mkdirs(context.getExternalCacheDir());
        try {
            diskLruCache = DiskLruCache.open(file, APP_VERSION, VALUE_COUNT, MAX_SIZE);
        } catch (Exception e) {
//            status = Status.FAILED;
            throw new RuntimeException(e);
        }
        return diskLruCache;
    }

    public ImageResourceService(Context context) {
//        status = Status.LOADING;
        // 初始化
        initDiskLruCache(context);

//        new Thread(() -> {
//            Cursor cursor = query(context.getContentResolver());
//            if (cursor != null) {
//                while (cursor.moveToNext()) {
//                    CursorResolverConverter cursorResolverConverter = new CursorResolverConverter(cursor);
//                    ImageResource entity = cursorResolverConverter.convertFileEntity();
//                    File imageFile = new File(entity.getData());
//                    ImageResourceEntry imageResourceEntry = new ImageResourceEntry(
//                            entity.getIdString(), entity.getTitle(), entity.getData(), entity);
//                    entry.add(imageResourceEntry);
//
//                    if (DiskLruCacheUtil.hasKey(diskLruCache, entity.getIdString())) {
//                        continue;
//                    }
//
//                    // 这里存缩略图，原图已经保存在手机里了
//                    Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imageFile.getAbsolutePath()), 200, 200);
//                    bitmap = BitmapUtil.rotateBitmap(bitmap, entity.getOrientation());
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
//
//                    DiskLruCacheUtil.putInputStream(diskLruCache, entity.getIdString(), new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
//                    Log.i(TAG, "Put file: " + entity.getIdString());
//
//                    EventBus.getDefault().post(imageResourceEntry);
//                }
//            }
//            status = Status.SUCCESS;
//        }).start();
    }

    /**
     * 获取 InputStream
     *
     * @param id 文件 id
     * @return InputStream
     */
    public InputStream getInputStream(String id) {
        return DiskLruCacheUtil.getFile(id);
    }

    /**
     * 获取 Bitmap
     *
     * @param id 文件 id
     * @return Bitmap
     */
    public Bitmap getBitmap(String id) {
        InputStream inputStream = getInputStream(id);
        return BitmapFactory.decodeStream(inputStream);
    }

    /**
     * 获取资源
     *
     * @return 资源列表
     */
    public List<ImageResourceEntry> resources() {
        return this.entry;
    }

//
//    /**
//     * 等待成功
//     * <br/>
//     * 会等待所有图片加载完成后放行
//     */
////    public void await() {
////        while (this.status.equals(Status.LOADING)) ;
////    }

    private Cursor query(ContentResolver contentResolver) {
        return contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Images.Media.DATE_ADDED + " DESC");
    }

    public enum Status {
        LOADING,
        SUCCESS,
        FAILED
    }
}
