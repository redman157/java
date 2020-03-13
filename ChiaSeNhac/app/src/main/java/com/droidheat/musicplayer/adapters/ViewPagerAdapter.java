package com.droidheat.musicplayer.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;
import java.util.Map;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager manager) {
        super(manager);
    }

    public void addFragment(Fragment fragment) {
        fragments.add(fragment);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
