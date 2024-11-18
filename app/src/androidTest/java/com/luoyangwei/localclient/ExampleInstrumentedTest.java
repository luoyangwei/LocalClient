package com.luoyangwei.localclient;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.luoyangwei.localclient.data.model.Image;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.data.repository.AppDatabase;
import com.luoyangwei.localclient.data.repository.ImageRepository;
import com.luoyangwei.localclient.data.source.local.ResourceService;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.luoyangwei.localclient", appContext.getPackageName());
    }

    @Test
    public void deleteThumbnails() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ImageRepository repository = AppDatabase.getInstance(appContext).imageRepository();
//        List<Image> images = repository.findNotHasThumbnail();
//        for (Image image : images) {
//            repository.delete(image);
//        }
    }

    @Test
    public void deleteAll() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ImageRepository repository = AppDatabase.getInstance(appContext).imageRepository();
        List<Image> images = repository.find();
        images.forEach(repository::delete);
    }

    @Test
    public void thumbnails() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ResourceService resourceService = new ResourceService(appContext);
        List<Resource> resources = resourceService.getResources(null);
        for (Resource resource : resources) {
//            try {
//                Thumbnails.of(new File(resource.getFullPath()))
//                        .size(300, 300)
//                        .toFile(new File("thumbnail.jpg"));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
        }
    }
}