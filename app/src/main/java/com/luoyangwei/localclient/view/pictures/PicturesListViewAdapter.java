package com.luoyangwei.localclient.view.pictures;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.utils.GlideUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PicturesListViewAdapter extends RecyclerView.Adapter<PicturesListViewAdapter.ViewHolder> {
    private static final String TAG = "PicturesListViewAdapter";
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    private final Activity activity;
    private final List<Resource> resources;
    private final Context context;
    private final PicturesItemOnClickListener itemOnClick;

    public PicturesListViewAdapter(Activity activity, List<Resource> resources, PicturesItemOnClickListener itemOnClick) {
        this.activity = activity;
        this.resources = resources;
        this.context = activity.getApplicationContext();
        this.itemOnClick = itemOnClick;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pictures_item);
        }
    }

    public static class ItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public ItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect,
                                   @NonNull View view,
                                   @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.top = space;
            outRect.bottom = space;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycler_pictures, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Resource resource = resources.get(position);
        Glide.with(context)
                .load(resource.getFullPath())
                .apply(GlideUtil.defaultOptions())
                .override(300)
                .into(holder.imageView);

        String resourceId = resource.getId();

        holder.imageView.setTransitionName(resourceId);
        holder.imageView.setOnClickListener(view -> itemOnClick.onClick(view, position, resource));
    }

    public void addItems(List<Resource> preloadResources) {
        resources.addAll(preloadResources);
        notifyItemRangeInserted(resources.size() - preloadResources.size(), preloadResources.size());
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }
}
