package com.luoyangwei.localclient.ui.album;

import android.content.Context;
import android.graphics.Outline;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.AppLoadingCache;
import com.luoyangwei.localclient.data.model.Bucket;
import com.luoyangwei.localclient.data.model.Resource;

import java.util.List;
import java.util.Locale;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AlbumBucketAdapter extends RecyclerView.Adapter<AlbumBucketAdapter.ViewHolder> {
    private static final int DEFAULT_ITEM_WIDTH = 500;
    private static final int DEFAULT_ITEM_HEIGHT = 500;
    private static final float DEFAULT_ITEM_RADIUS = 56;
    private Context context;
    private List<Bucket> data;

    /**
     * ViewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView bucketName;
        private final TextView bucketCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.album_imageview);
            bucketName = itemView.findViewById(R.id.album_bucket_name);
            bucketCount = itemView.findViewById(R.id.album_bucket_count);
        }
    }

    /**
     * RecyclerView 间距
     */
    public static class AlbumItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public AlbumItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.top = space;
            outRect.bottom = space;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_album_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bucket bucket = data.get(position);
        Resource resource = AppLoadingCache.getInstance(context).get(bucket.getMostRecentResourceId());

        Glide.with(context)
                .load(resource.getFullPath())
                .override(DEFAULT_ITEM_WIDTH, DEFAULT_ITEM_HEIGHT)
                .centerCrop()
                .into(holder.imageView);
        imageViewClipToOutline(holder.imageView, DEFAULT_ITEM_RADIUS);

        holder.bucketName.setText(bucket.getName());
        holder.bucketCount.setText(String.format(Locale.getDefault(), "%d", bucket.getCount()));
    }

    private void imageViewClipToOutline(ImageView imageView, float radius) {
        imageView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        });
        imageView.setClipToOutline(true);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
