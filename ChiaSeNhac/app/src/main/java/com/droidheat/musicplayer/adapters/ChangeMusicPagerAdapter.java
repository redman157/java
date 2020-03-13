package com.droidheat.musicplayer.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;

public class ChangeMusicPagerAdapter extends FragmentPagerAdapter {
    private int index;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<SongModel> songModels = new ArrayList<>();

    public interface SongModelFragment {
        Fragment getModel(SongModel song);
    }
    private SongModelFragment songModelFragment;
    public void setFragments(SongModelFragment songModelFragment){
        this.songModelFragment = songModelFragment;
    }
    public ChangeMusicPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);

    }

    public void addData(Fragment fragment, SongModel songModel){
        fragments.add(fragment);
        songModels.add(songModel);
        songModelFragment.getModel(songModel);
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
