package com.luoyangwei.localclient.ui.preview;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lombok.AllArgsConstructor;
import lombok.Data;

public class PreviewThumbnailsSpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final ThumbnailViewRect thumbnailViewRect;

    public PreviewThumbnailsSpacesItemDecoration(ThumbnailViewRect thumbnailViewRect) {
        this.thumbnailViewRect = thumbnailViewRect;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (thumbnailViewRect.getSpace() != 0) {
            outRect.left = thumbnailViewRect.getSpace();
            outRect.right = thumbnailViewRect.getSpace();
            outRect.bottom = thumbnailViewRect.getSpace();
            outRect.top = thumbnailViewRect.getSpace();
        }
        if (thumbnailViewRect.getWidth() != 0 && thumbnailViewRect.getHeight() != 0) {
            outRect.right = thumbnailViewRect.getWidth();
            outRect.bottom = thumbnailViewRect.getHeight();
        }
    }

    @Data
    @AllArgsConstructor
    public static class ThumbnailViewRect {
        private int width;
        private int height;
        private int space;
    }
}
