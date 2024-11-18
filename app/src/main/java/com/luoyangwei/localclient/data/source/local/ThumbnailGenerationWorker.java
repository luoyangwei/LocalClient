package com.luoyangwei.localclient.data.source.local;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.luoyangwei.localclient.data.model.Image;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.data.repository.ImageRepository;
import com.luoyangwei.localclient.utils.ThumbnailUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ThumbnailGenerationWorker extends Worker {
    private static final String TAG = ThumbnailGenerationWorker.class.getName();
    private final ImageRepository imageRepository;

    /**
     * 资源列表
     */
    private final List<Resource> resources = new ArrayList<>();

    /**
     * 文件储存位置
     */
    private final String outputThumbnailPath;

    public ThumbnailGenerationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.imageRepository = AppDatabase.getInstance(context).imageRepository();
        this.outputThumbnailPath = ThumbnailUtils.getOutputThumbnailPath(context);
        this.resources.addAll(loadImages(context));
        saveImages(resources);
    }

    private List<Resource> loadImages(Context context) {
        ResourceService resourceService = new ResourceService(context);
        return resourceService.getResources(r -> true);
    }

    private void saveImages(List<Resource> resources) {
        for (Resource resource : resources) {
            Long id = Long.parseLong(resource.getId());
            Image image = imageRepository.findById(id);
            if (Objects.isNull(image)) {
                image = Image.getInstance(resource);
                imageRepository.insert(image);
            }
        }
    }


    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "work running ...");
        List<Image> images = imageRepository.findNotHasThumbnail();
        for (Image image : images) {
            try {
                Resource resource = resources.stream()
                        .filter(r -> r.getId().equals(String.valueOf(image.id)))
                        .findFirst()
                        .orElseThrow();
                String thumbnailDirectory = outputThumbnailPath + File.separator + resource.getBucketName();
                ThumbnailUtils.generation(getApplicationContext(),
                        new File(image.fullPath),
                        new File(thumbnailDirectory),
                        new ThumbnailUtils.CompressListener() {
                            @Override
                            public void asyncComplete(File file) {
                                image.isHasThumbnail = true;
                                image.thumbnailPath = file.getAbsolutePath();
                                imageRepository.update(image);
                                EventBus.getDefault().post(image);
                            }
                        });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return Result.retry();
    }
}
