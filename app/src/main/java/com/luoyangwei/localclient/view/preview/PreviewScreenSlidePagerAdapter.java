package com.luoyangwei.localclient.view.preview;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.luoyangwei.localclient.data.model.Resource;

import java.util.List;

public class PreviewScreenSlidePagerAdapter extends FragmentStateAdapter {
    private final List<Resource> resources;
    private final Activity activity;

    public PreviewScreenSlidePagerAdapter(@NonNull FragmentActivity fragmentActivity, Activity activity, List<Resource> resources) {
        super(fragmentActivity);
        this.resources = resources;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new PreviewScreenFragment(activity, resources.get(position), position);
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }
}
