package com.droidheat.musicplayer.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.droidheat.musicplayer.fragments.ChangeMusicFragment;
import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;

public class ChangeMusicPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<ChangeMusicFragment> mFragments = new ArrayList<>();

    public ChangeMusicPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public void addData(ChangeMusicFragment fragment, SongModel songModel) {
        fragment.setSongModel(songModel);
        mFragments.add(fragment);

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
