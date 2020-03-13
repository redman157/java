package com.droidheat.musicplayer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.adapters.ChangeMusicPagerAdapter;
import com.droidheat.musicplayer.fragments.ChangeMusicFragment;
import com.droidheat.musicplayer.manager.SongsManager;

public class PlayActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private ChangeMusicPagerAdapter mAdapter;
    private SongsManager mSongManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        mViewPager = findViewById(R.id.vp_change_music);
        initDataVP();
        mSongManager = SongsManager.getInstance();
        mSongManager.setContext(this);
        setupViewPager();
    }
    private void initDataVP(){

        mAdapter = new ChangeMusicPagerAdapter(getSupportFragmentManager());
        for (int idx = 0 ; idx < mSongManager.allSongs().size(); idx ++){
            mAdapter.addData(new ChangeMusicFragment(), mSongManager.allSongs().get(idx) );
        }
    }
    private void setupViewPager(){
        mViewPager.setAdapter(mAdapter);
    }
}
