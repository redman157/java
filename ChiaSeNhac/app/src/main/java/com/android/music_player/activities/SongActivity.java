package com.android.music_player.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.android.music_player.IconView;
import com.android.music_player.R;
import com.android.music_player.adapters.ViewPagerAdapter;
import com.android.music_player.fragments.ListMusicFragment;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.managers.MusicManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.M)
public class SongActivity extends AppCompatActivity implements
        TabLayout.OnTabSelectedListener,ViewPager.OnPageChangeListener,
        View.OnClickListener{
    public LinearLayout mLl_Play_Media;
    public IconView mImgAlbumId;

    private SharedPrefsUtils mSharedPrefsUtils;
    public TextView mTextArtist, mTextTitle;
    private Toolbar mToolBar;
    private Button mBtnTitle;

    public int chooseSong;
    private ImageButton mBtnPlayPause;
    public ImageView mImgMedia;
    private MusicManager mMusicManager;
    private ActionBar actionBar;
    private boolean receiverRegistered;
    private View collapsingProfileHeaderView;
    private boolean isPlaying;
    public ImageView profileImage;
    public TextView profileName, profileArtist, profileAlbum;
    private TabLayout mTabLayoutSong;
    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPagerSong;
    @Override
    protected void onStart() {
        super.onStart();
        mMusicManager = MusicManager.getInstance();
        mMusicManager.setContext(this);
        setViewMusic();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        mMusicManager = MusicManager.getInstance();
        mMusicManager.setContext(this);
        initView();
        mSharedPrefsUtils = new SharedPrefsUtils(this);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        assignView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setViewMusic(){

        MediaMetadataCompat metadataCompat = MusicLibrary.getMetadata(this,
                mMusicManager.getCurrentMusic());

        mTextArtist.setText(metadataCompat.getString(Constants.METADATA.Artist));
        mTextTitle.setText(metadataCompat.getString(Constants.METADATA.Title));
        mImgMedia.setImageBitmap(metadataCompat.getBitmap(Constants.METADATA.AlbumID));

    }


    public void setSongCurrent(ArrayList<SongModel> mSongs, int position){
        SongModel songModel = mSongs.get(position);
        mMusicManager.setPosition(position);
        mMusicManager.setAlbumID(mSongs.get(position).getAlbumID());

        mTextArtist.setText(songModel.getArtist());
        mTextTitle.setText(songModel.getSongName());

        ImageUtils.getInstance(SongActivity.this).getSmallImageByPicasso(
                songModel.getAlbumID(),
                profileImage);

        ImageUtils.getInstance(SongActivity.this).getSmallImageByPicasso(
                songModel.getAlbumID(),
                mImgMedia);

        ImageUtils.getInstance(this).getSmallImageByPicasso(
                songModel.getAlbumID(),
                mImgAlbumId);

        profileName.setText(songModel.getSongName());
        profileArtist.setText(songModel.getArtist());
        profileAlbum.setText(songModel.getAlbum());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent iBackMusic = new Intent(this, HomeActivity.class);
                finish();
                startActivity(iBackMusic);
                break;
            case R.id.action_searchBtn:
                finish();
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.sleep_timer:
                finish();
                startActivity(new Intent(this, TimerActivity.class));
                break;
            case R.id.sync:
                finish();
                Intent intent = new Intent(this, SplashActivity.class).putExtra(Constants.VALUE.SYNC,
                        true);
                startActivity(intent);
                break;
            case R.id.settings:
                finish();
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.equalizer:
                finish();
                startActivity(new Intent(this, EqualizerActivity.class));
                break;
            case R.id.changeTheme:
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_choose_accent_color);
                dialog.findViewById(R.id.orange).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(
                                Constants.PREFERENCES.ACCENT_COLOR, Constants.COLOR.ORANGE);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.cyan).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.CYAN);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.green).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.GREEN);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.yellow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.YELLOW);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.pink).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.PINK);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.purple).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.PURPLE);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.grey).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.GREY);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.red).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.RED);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        collapsingProfileHeaderView = findViewById(R.id.collapseActionView);
        mViewPagerSong = findViewById(R.id.vp_AllSong);
        mTabLayoutSong = findViewById(R.id.tab_SongActivity);
        profileAlbum = collapsingProfileHeaderView.findViewById(R.id.profileMisc);
        profileImage = collapsingProfileHeaderView.findViewById(R.id.profileImage);
        profileName = collapsingProfileHeaderView.findViewById(R.id.profileName);
        profileArtist = collapsingProfileHeaderView.findViewById(R.id.profileSubtitle);
        mToolBar = findViewById(R.id.tb_SongActivity);
        mTextArtist = findViewById(R.id.text_artists_media);
        mTextTitle = findViewById(R.id.text_title_media);
        mImgMedia = findViewById(R.id.img_albumArt_media);
        mBtnPlayPause = findViewById(R.id.imbt_Play_media);
        mBtnTitle = findViewById(R.id.btn_title_media);
        mLl_Play_Media = findViewById(R.id.ll_play_media);
        mImgAlbumId = findViewById(R.id.img_AlbumId);
    }

    private void assignView() {
        mBtnTitle.setOnClickListener(this);
        mBtnPlayPause.setOnClickListener(this);
        mLl_Play_Media.setOnClickListener(this);
        setupViewPager(mViewPagerSong);
    }

    private void setupViewPager(ViewPager viewPager){
        mViewPagerAdapter = new ViewPagerAdapter(this,getSupportFragmentManager());
        mViewPagerAdapter.addFragment(new ListMusicFragment(mMusicManager.allSortSongs()));
        mViewPagerAdapter.addFragment(new ListMusicFragment(mMusicManager.allSortSongs()));
        mViewPagerAdapter.addFragment(new ListMusicFragment(mMusicManager.allSortSongs()));
        mViewPagerAdapter.addFragment(new ListMusicFragment(mMusicManager.allSortSongs()));

        viewPager.setAdapter(mViewPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOnClickListener(this);

        mTabLayoutSong.setupWithViewPager(viewPager);

        for (int i = 0; i < mTabLayoutSong.getTabCount(); i++) {
            mTabLayoutSong.getTabAt(i).setCustomView(mViewPagerAdapter.getTabSong(i));
        }
        mTabLayoutSong.addOnTabSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_title_media:
                break;
            case R.id.imbt_Play_media:
//                Utils.isPlayMediaService(this, mMusicManager.getType(), mMusicManager.getPosition());
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        for (int i = 0; i < mTabLayoutSong.getTabCount(); i ++){
            View view = mTabLayoutSong.getTabAt(i).getCustomView();
            TextView title = view.findViewById(R.id.item_tl_text_home);
            int color = (i == tab.getPosition()) ? getResources().getColor(R.color.red) :
                    getResources().getColor(R.color.white);
            title.setTextColor(color);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}


