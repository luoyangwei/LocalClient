package com.luoyangwei.localclient.view.album.viewer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.Bucket;
import com.luoyangwei.localclient.data.model.Image;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.data.repository.ImageRepository;
import com.luoyangwei.localclient.utils.ThumbnailUtil;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
public class ViewerAdapter extends RecyclerView.Adapter<ViewerAdapter.ViewHolder> {
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private Context context;
    private Bucket bucket;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View root;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView;
            imageView = itemView.findViewById(R.id.album_viewer_imageview);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_album_viewer_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Resource resource = bucket.getResources().get(position);

        RequestManager requestManager = Glide.with(context);
        RequestBuilder<Drawable> requestBuilder;

        Image image = supplyAsyncImage(resource);
        if (image == null || !image.isHasThumbnail) {
            requestBuilder = null;
            String outputPath = context.getExternalCacheDir() + File.separator + resource.getId() + ".jpg";
            ThumbnailUtil.generation(context, resource, new File(outputPath), bitmap -> requestBuilder.load(bitmap));
        } else {
            requestBuilder = requestManager.load(image.thumbnailPath);
        }

        requestBuilder.into(holder.imageView);
    }


    @SneakyThrows
    private Image supplyAsyncImage(Resource resource) {
        ImageRepository repository = AppDatabase.getInstance(context).imageRepository();
        return CompletableFuture.supplyAsync(() -> repository.findByResourcesId(resource.getId())).get();
    }

    @Override
    public int getItemCount() {
        return bucket.getResources().size();
    }
}
