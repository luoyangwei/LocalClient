package com.luoyangwei.localclient.ui.photo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.model.Thumbnail;
import com.luoyangwei.localclient.data.repository.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        Log.i(TAG, String.format("View image info [width: %d, height:%d, size: %d]",
                resource.getBitmap().getWidth(), resource.getBitmap().getHeight(), resource.getBitmap().getByteCount()));
        holder.imageView.setImageBitmap(resource.getBitmap());
        holder.imageView.setTransitionName(resource.getName());
        holder.imageView.setOnClickListener(v -> onClickListener.onClick(v, resource));
    }

    private void insertThumbnail(Thumbnail thumbnail) {
        AppDatabase.getInstance(context).thumbnailRepository().insert(thumbnail);
    }


    private Thumbnail getThumbnail(String resourceId) {
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

}
