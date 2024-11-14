package com.luoyangwei.localclient.ui.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.source.local.ImageResourceEntry;
import com.luoyangwei.localclient.data.source.local.ImageResourceService;

import java.util.List;

import lombok.Setter;

public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<PhotoRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = PhotoRecyclerViewAdapter.class.getName();

    private final Context context;
    private final List<ImageResourceEntry> resources;
    private final ImageResourceService resourceService;

    @Setter
    private OnClickListener onClickListener;

    public PhotoRecyclerViewAdapter(Context context, List<ImageResourceEntry> resources) {
        this.context = context;
        this.resources = resources;
        this.resourceService = ImageResourceService.getInstance(context);
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
        ImageResourceEntry resource = resources.get(position);
        Bitmap bitmap = resourceService.getBitmap(resource.getId());
        holder.imageView.setImageBitmap(bitmap);
        holder.imageView.setTransitionName(resource.getTitle());
        holder.imageView.setOnClickListener(v -> onClickListener.onClick(v, resource));
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }

    public void addItem(ImageResourceEntry entry) {
        resources.add(entry);
        notifyItemInserted(resources.size() - 1);
    }

    interface OnClickListener {
        void onClick(View view, ImageResourceEntry resource);
    }

}
