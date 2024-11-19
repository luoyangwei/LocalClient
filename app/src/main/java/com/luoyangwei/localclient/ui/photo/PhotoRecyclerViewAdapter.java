package com.luoyangwei.localclient.ui.photo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.Image;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.data.repository.ImageRepository;
import com.luoyangwei.localclient.data.source.local.ResourceService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Setter;

/**
 * 照片 RecyclerView 适配器
 *
 * @author luoyangwei
 * @date 2024年11月15日10:02:52
 */
public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<PhotoRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = PhotoRecyclerViewAdapter.class.getName();
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private static final Size THUMBNAIL_SIZE = new Size(500, 500);

    private final Activity activity;
    private final Context context;
    private final List<Resource> resources;

    @Setter
    private OnClickListener onClickListener;

    public PhotoRecyclerViewAdapter(Activity activity, Context context, List<Resource> resources) {
        this.activity = activity;
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

        DrawableCrossFadeFactory crossFadeFactory = new DrawableCrossFadeFactory.Builder()
                .setCrossFadeEnabled(true)
                .build();

        RequestOptions requestOptions = new RequestOptions()
                .sizeMultiplier(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.LOW)
                .centerCrop();

        ResourceService resourceService = new ResourceService(context);

        // 创建线程去生成文件
        // 已经有缩略图的情况下就不需要再调用 resourceService.loadThumbnail 来生成缩略图, 虽然 resourceService.loadThumbnail 已经很快了。
        CompletableFuture.supplyAsync(() -> {
            ImageRepository repository = AppDatabase.getInstance(context).imageRepository();
            return repository.findByResourcesId(resource.getId());
        }).thenAccept(image -> {

            RequestManager requestManager = Glide.with(context);
            RequestBuilder<Drawable> requestBuilder;

            // 没有查询到，说明没有生成过缩略图，这里就要生成缩略图了
            if (image == null || !image.isHasThumbnail) {
                Bitmap bitmap = resourceService.loadThumbnail(resource.getUri(), THUMBNAIL_SIZE);
                executor.execute(() -> persistenceThumbnail(bitmap, resource, image));
                requestBuilder = requestManager.load(bitmap);
            } else {
                Log.i(TAG, "onBindViewHolder: " + image.thumbnailPath);
                requestBuilder = requestManager.load(image.thumbnailPath);
            }

            activity.runOnUiThread(() -> requestBuilder
                    .transition(DrawableTransitionOptions.with(crossFadeFactory))
                    .apply(requestOptions)
                    .into(holder.imageView));
        });

        holder.imageView.setTransitionName(resource.getName());
        holder.imageView.setOnClickListener(v -> onClickListener.onClick(v, resource));
    }

    private void persistenceThumbnail(Bitmap bitmap, Resource resource, Image image) {
        if (bitmap == null) {
            return;
        }

        String outputPath = context.getExternalCacheDir() + File.separator + resource.getId() + ".jpg";
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputPath)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
            fileOutputStream.flush();
        } catch (IOException e) {
            Log.e(TAG, "persistenceThumbnail: ", e);
        }

        // 生成文件加入缓存
        ImageRepository repository = AppDatabase.getInstance(context).imageRepository();
        synchronized (repository) {
            if (image == null) {
                image = Image.getInstance(resource);
                image.isHasThumbnail = true;
                image.thumbnailPath = outputPath;
                repository.insert(image);
            } else {
                image.isHasThumbnail = true;
                image.thumbnailPath = outputPath;
                repository.update(image);
            }
        }
    }


    @Override
    public int getItemCount() {
        return resources.size();
    }

    @Deprecated
    public void addItem(Resource entry) {
        resources.add(entry);
        notifyItemInserted(resources.size() - 1);
    }

    interface OnClickListener {
        void onClick(View view, Resource resource);
    }

}
