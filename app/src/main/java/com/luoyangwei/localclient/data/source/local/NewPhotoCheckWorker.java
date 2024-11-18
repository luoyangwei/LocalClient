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

import java.util.List;

public class NewPhotoCheckWorker extends Worker {
    private static final String TAG = NewPhotoCheckWorker.class.getName();
    private final ImageRepository imageRepository;

    public NewPhotoCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.imageRepository = AppDatabase.getInstance(context).imageRepository();
    }

    @NonNull
    @Override
    public Result doWork() {
        ResourceService resourceService = new ResourceService(getApplicationContext());
        List<Resource> resources = resourceService.getResources(r -> imageRepository.findById(Long.parseLong(r.getId())) == null);
        Log.i(TAG, "doWork: resources.size() = " + resources.size());
        resources.subList(0, Math.min(resources.size(), 30));
        for (Resource resource : resources) {
            Image image = Image.getInstance(resource);
            new Thread(() -> {
                if (imageRepository.findById(image.id) == null) {
                    imageRepository.insert(image);
                }
            }).start();
        }
        return Result.retry();
    }

}
