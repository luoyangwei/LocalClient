package com.luoyangwei.localclient.ui.gallery;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.luoyangwei.localclient.data.model.Resource;

import java.util.List;

/**
 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
 * sequence.
 */
public class GalleryAdapter extends FragmentStateAdapter {
    private final List<Resource> resources;

    public GalleryAdapter(@NonNull FragmentActivity fragmentActivity, List<Resource> resources) {
        super(fragmentActivity);
        this.resources = resources;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new GalleryFragment(resources.get(position));
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }

}
