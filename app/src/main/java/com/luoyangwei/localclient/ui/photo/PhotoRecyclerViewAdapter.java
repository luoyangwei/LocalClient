package com.luoyangwei.localclient.ui.photo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.Resource;

import java.util.List;

import lombok.Setter;

/**
 * 照片 RecyclerView 适配器
 *
 * @author luoyangwei
 * @date 2024年11月15日10:02:52
 */
public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<PhotoRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = PhotoRecyclerViewAdapter.class.getName();

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
        DrawableCrossFadeFactory crossFadeFactory = new DrawableCrossFadeFactory.Builder()
                .setCrossFadeEnabled(true)
                .build();

        RequestOptions requestOptions = new RequestOptions()
                .sizeMultiplier(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.LOW)
                .centerCrop();
        Glide.with(context)
                .load(resource.getThumbnailPath())
                .transition(DrawableTransitionOptions.with(crossFadeFactory))
                .apply(requestOptions)
                .into(holder.imageView);

        holder.imageView.setTransitionName(resource.getName());
        holder.imageView.setOnClickListener(v -> onClickListener.onClick(v, null));
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
