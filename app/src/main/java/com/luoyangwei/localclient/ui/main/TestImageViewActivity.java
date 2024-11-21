package com.luoyangwei.localclient.ui.main;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.luoyangwei.localclient.data.AppLoadingCache;
import com.luoyangwei.localclient.data.model.Resource;
import com.luoyangwei.localclient.databinding.ActivityTestImageviewBinding;
import com.luoyangwei.localclient.utils.BitmapUtil;

import java.util.List;

@SuppressLint("ClickableViewAccessibility")
public class TestImageViewActivity extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = TestImageViewActivity.class.getName();
    private ActivityTestImageviewBinding binding;

    private boolean isCenterCrop = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestImageviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Resource resource = getResource();
        binding.imageView.setImageBitmap(getImageBitmap(resource));

        binding.button.setOnClickListener(v -> {
            if (!isCenterCrop) {
                binding.imageView.animateScaleTypeTransition(ImageView.ScaleType.CENTER_CROP);
            } else {
                binding.imageView.animateScaleTypeTransition(ImageView.ScaleType.FIT_CENTER);
            }
            isCenterCrop = !isCenterCrop;
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }


    private Bitmap getImageBitmap(Resource resource) {
        Bitmap bitmap = BitmapFactory.decodeFile(resource.getFullPath());
        return BitmapUtil.rotateBitmap(bitmap, resource.getOrientation());
    }

    private Resource getResource() {
        List<Resource> resources = AppLoadingCache.getInstance(getApplicationContext()).getResources();
        return resources.get(resources.size() - 1);
    }
}
