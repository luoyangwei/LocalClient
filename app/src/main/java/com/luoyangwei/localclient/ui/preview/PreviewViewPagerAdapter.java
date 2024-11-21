package com.luoyangwei.localclient.ui.preview;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.luoyangwei.localclient.R;
import com.luoyangwei.localclient.data.model.Resource;

import java.util.List;

public class PreviewViewPagerAdapter extends FragmentStateAdapter {
    private final List<Resource> resources;
    private final FragmentManager fragmentManager;

    public PreviewViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Resource> resources) {
        super(fragmentActivity);
        fragmentManager = fragmentActivity.getSupportFragmentManager();
        this.resources = resources;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        PreviewViewPagerFragment previewViewPagerFragment = new PreviewViewPagerFragment(resources.get(position));
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, previewViewPagerFragment)
                .commit();
        return previewViewPagerFragment;
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }
}
