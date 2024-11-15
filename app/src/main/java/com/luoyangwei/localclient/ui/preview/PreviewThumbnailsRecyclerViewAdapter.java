package com.luoyangwei.localclient.ui.preview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.ImageResource;

import java.util.List;

/**
 * 预览图时下方的缩略图列表适配器
 *
 * @author luoyangwei
 * @date 2024年11月13日10:54:48
 */
public class PreviewThumbnailsRecyclerViewAdapter extends RecyclerView.Adapter<PreviewThumbnailsRecyclerViewAdapter.ViewHolder> {
    private final Context context;
    private final Long currentResourceId;
    private final List<ImageResource> resources;

    public PreviewThumbnailsRecyclerViewAdapter(Context context, Long currentResourceId, List<ImageResource> resources) {
        this.context = context;
        this.currentResourceId = currentResourceId;
        this.resources = resources;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.thumbnail_imageview);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_preview_thumbnail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageResource resource = resources.get(position);
//        Picasso.get()
//                .load(new File(resource.getData()))
//                .resize(200, 200)
//                .placeholder(R.drawable.ic_image_load_placeholder)
//                .centerCrop()
//                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }

}
