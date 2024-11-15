package com.luoyangwei.localclient.ui.photo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.Thumbnail;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.utils.DiskLruCacheUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import lombok.Data;
import lombok.Setter;

/**
 * 照片 RecyclerView 适配器
 *
 * @author luoyangwei
 * @date 2024年11月15日10:02:52
 */
public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<PhotoRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = PhotoRecyclerViewAdapter.class.getName();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    private final Context context;
    private final List<Resource> resources;

    @Setter
    private OnClickListener onClickListener;

    public PhotoRecyclerViewAdapter(Context context, List<Resource> resources) {
        this.context = context;
        this.resources = resources;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photo_imageview);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_photo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Resource resource = resources.get(position);
        Bitmap bitmap;

        // 检查是否有生成缩略图
        Thumbnail thumbnail = getThumbnail(resource.getId());
        if (thumbnail == null) {
            Bitmap sourceBitmap = BitmapFactory.decodeFile(resource.getFullPath());
            Bitmap thumbnailBitmap = ThumbnailUtils.extractThumbnail(sourceBitmap, 300, 300);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            thumbnailBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            DiskLruCacheUtil.putInputStream(resource.getId(), new ByteArrayInputStream(outputStream.toByteArray()));
            bitmap = thumbnailBitmap;
        } else {
            InputStream inputStream = DiskLruCacheUtil.getFile(resource.getId());
            bitmap = BitmapFactory.decodeStream(inputStream);
        }

        holder.imageView.setImageBitmap(bitmap);
        holder.imageView.setTransitionName(resource.getName());
        holder.imageView.setOnClickListener(v -> onClickListener.onClick(v, resource));
    }


    public Thumbnail getThumbnail(String resourceId) {
        Thumbnail thumbnail;
        try {
            Future<Thumbnail> future = executor.submit(() ->
                    AppDatabase.getInstance(context).thumbnailRepository().queryByResourceId(resourceId));
            thumbnail = future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return thumbnail;
    }


    @Override
    public int getItemCount() {
        return resources.size();
    }

    public void addItem(Resource entry) {
        resources.add(entry);
        notifyItemInserted(resources.size() - 1);
    }

    interface OnClickListener {
        void onClick(View view, Resource resource);
    }

    @Data
    public static class Resource {

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

}
