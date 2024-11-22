package com.luoyangwei.localclient.ui.preview;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.luoyangwei.localclient.data.model.Resource;

import java.util.List;

public class PreviewViewPagerAdapter extends FragmentStateAdapter {
    private final List<Resource> resources;
    private final Activity activity;

    public PreviewViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Resource> resources) {
        super(fragmentActivity);
        this.activity = fragmentActivity;
        this.resources = resources;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new PreviewViewPagerFragment(activity, resources.get(position));
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }
}
