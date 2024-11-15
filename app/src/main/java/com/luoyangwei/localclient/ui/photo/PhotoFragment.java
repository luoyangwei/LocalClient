package com.luoyangwei.localclient.ui.photo;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.jakewharton.disklrucache.DiskLruCache;
import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.databinding.FragmentPhotoViewBinding;
import com.luoyangwei.localclient.ui.preview.PreviewActivity;
import com.luoyangwei.localclient.utils.BitmapUtil;
import com.luoyangwei.localclient.utils.DiskLruCacheUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;

/**
 * 照片 Fragment
 */
public class PhotoFragment extends Fragment implements PhotoRecyclerViewAdapter.OnClickListener {
    private static final String TAG = PhotoFragment.class.getName();
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    private FragmentPhotoViewBinding binding;

    private PhotoRecyclerViewAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPhotoViewBinding.inflate(inflater, container, false);
        StrictMode.enableDefaults();

        // 首次展示 30 张，后面一直追加，直到没有数据
        List<Resource> firstTwentyResources = queryFirstThirty();

        adapter = new PhotoRecyclerViewAdapter(getContext(), firstTwentyResources);
        adapter.setOnClickListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);

        binding.photoRecyclerView.setLayoutManager(gridLayoutManager);
        binding.photoRecyclerView.addItemDecoration(new PhotoRecyclerViewItemDecoration(8));

        binding.photoRecyclerView.setHasFixedSize(true);
        binding.photoRecyclerView.setAdapter(adapter);

        backgroundGenerateThumbnails(30);
        return binding.getRoot();
    }

    @SneakyThrows
    private void backgroundGenerateThumbnails(int startIndex) {
        ContentResolver contentResolver = requireActivity().getContentResolver();
        Cursor cursor = query(contentResolver);
        int i = 0;
        List<Resource> resources = new ArrayList<>();
        while (cursor.moveToNext()) {
            resources.add(new Resource(cursor));
        }
        cursor.close();

        if (resources.size() > startIndex) {
            resources = resources.subList(startIndex, resources.size());
        } else {
            resources = new ArrayList<>();
        }
        for (Resource resource : resources) {
            executor.execute(() -> {
                refreshResource(resource);
                EventBus.getDefault().post(resource);
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveResource(Resource resource) {
        adapter.addItem(resource);
    }

    /**
     * 查询前 30 条数据
     *
     * @return 前 30 条数据
     */
    public List<Resource> queryFirstThirty() {
        ContentResolver contentResolver = requireActivity().getContentResolver();
        Cursor cursor = query(contentResolver);
        List<Resource> resources = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            if (cursor.moveToNext()) {
                resources.add(new Resource(cursor));
            }
        }
        cursor.close();

        // 将前 30 条存到缓存里
        for (Resource resource : resources) {
            refreshResource(resource);
        }
        return resources;
    }

    private void refreshResource(Resource resource) {
        DiskLruCache.Snapshot snapshot = DiskLruCacheUtil.getFile(resource.getId());
        if (snapshot != null) {
            Bitmap bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
            Bitmap thumbnailBitmap = ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
            resource.setBitmap(thumbnailBitmap);
        } else {
            // 不存在就重新缓存
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;

                Bitmap bitmap = BitmapFactory.decodeFile(resource.getFullPath());
                Bitmap thumbnailBitmap = ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
                thumbnailBitmap = BitmapUtil.rotateBitmap(thumbnailBitmap, resource.getOrientation());
                bitmap.recycle();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
                DiskLruCacheUtil.putInputStream(resource.getId(), new ByteArrayInputStream(outputStream.toByteArray()));
                resource.setBitmap(thumbnailBitmap);

                outputStream.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Cursor query(ContentResolver contentResolver) {
        String[] projection = new String[]{
//                MediaStore.Images.Media.TITLE,
//                MediaStore.Images.Media.DISPLAY_NAME,
//                MediaStore.Images.Media.DATA,
//                MediaStore.Images.Media.ORIENTATION
        };
        return contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");
    }

    @Override
    public void onClick(View view, Resource resource) {
        Intent intent = new Intent(getContext(), PreviewActivity.class);
        ImageView imageView = view.findViewById(R.id.photo_imageview);
        imageView.setTransitionName(resource.getName());

        intent.putExtra("resourceId", resource.getId());
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), imageView, resource.getName());
        startActivity(intent, activityOptionsCompat.toBundle());
    }
}
