package com.android.music_player.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.music_player.BaseActivity;
import com.android.music_player.OnChangePlayList;
import com.android.music_player.R;
import com.android.music_player.adapters.ViewPagerAdapter;
import com.android.music_player.fragments.AlbumGridFragment;
import com.android.music_player.fragments.AllSongsFragment;
import com.android.music_player.fragments.ArtistGridFragment;
import com.android.music_player.fragments.HomeFragment;
import com.android.music_player.fragments.PlaylistFragment;
import com.android.music_player.managers.SongManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.services.MediaPlayerService;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import me.zhanghai.android.materialplaypausedrawable.MaterialPlayPauseButton;
import me.zhanghai.android.materialplaypausedrawable.MaterialPlayPauseDrawable;

public class HomeActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener{
    private ViewPager mViewPager_Home;

    private String tag = "BBB";
    private BottomNavigationView mNavigationView;
    private ViewPagerAdapter mViewPagerAdapter;
    private ImageUtils imageUtils;
    private View mViewPlayMedia;
    public ImageView mImgMedia;
    public TextView mTextTitle, mTextArtist;
    public Button mBtnTitle;
    private SharedPrefsUtils mSharedPrefsUtils;
    private ArrayList<SongModel> mSongs;
    private SongManager mSongManager;
    public MaterialPlayPauseButton mBtnPlayPause;
    private boolean isPlaying;
    private String type;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    public LinearLayout mLlPlayMedia;
    private OnChangePlayList onChangePlayList;

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("XXX", "Home Activity : onPause");
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(brPlayPauseActivity);
        this.unregisterReceiver(brIsPlayService);
    }

    private BroadcastReceiver brPlayPauseActivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            // activity gửi broadcast từ service >> activity
            boolean isPlayingNoti =
                    serviceIntent.getBooleanExtra(Constants.INTENT.IS_PLAY_MEDIA_NOTIFICATION,
                            false);
            if (isPlayingNoti) {
                mBtnPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_play_light));
            } else {
                mBtnPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_pause_light));
            }
        }
    };

    private BroadcastReceiver brIsPlayService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // activity gửi broadcast xuống service
            boolean isPlayingMedia = intent.getBooleanExtra(Constants.INTENT.IS_PLAY_MEDIA_SERVICE, false);

            if (isPlayingMedia) {
                Log.d(tag, "PlayActivity --- brIsPlayService:" +true);
                mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
                isPlaying = true;
                mLlPlayMedia.setVisibility(View.VISIBLE);

            } else {
                Log.d(tag, "PlayActivity --- brIsPlayService:" +false);
                mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
                isPlaying = false;
                mLlPlayMedia.setVisibility(View.GONE);
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        mSongManager = SongManager.getInstance();
        mSongManager.setContext(this);
        registerReceiver(brPlayPauseActivity, new IntentFilter(Constants.ACTION.BROADCAST_PLAY_PAUSE));
        registerReceiver(brIsPlayService, new IntentFilter(Constants.ACTION.IS_PLAY));

        type = mSharedPrefsUtils.getString(Constants.PREFERENCES.TYPE, "");
        mSongs = mSongManager.setType(type);
        Utils.ChangeSongService(this, false,mSongs);

        /*if (MediaPlayerService.mMediaPlayer != null) {
            if (MediaPlayerService.mMediaPlayer.isPlaying()) {

                mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
            } else {
                mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
            }
        }else {
            mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // khởi tạo màn hình chính là home ta cần check position để gán sẵn vị trí luôn
        initView();
        mSharedPrefsUtils = new SharedPrefsUtils(this);
        mSongManager = SongManager.getInstance();
        mSongManager.setContext(this);

        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);

        imageUtils = ImageUtils.getInstance(this);
        assignView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
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

    private void setupViewPager(ViewPager viewPager){

        mViewPagerAdapter = new ViewPagerAdapter(this,getSupportFragmentManager());
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setOnChangePlayList(onChangePlayList);

        mViewPagerAdapter.addFragment(homeFragment);
        mViewPagerAdapter.addFragment(new AllSongsFragment());
        mViewPagerAdapter.addFragment(new AlbumGridFragment());
        mViewPagerAdapter.addFragment(new ArtistGridFragment());
        mViewPagerAdapter.addFragment(new PlaylistFragment());

        viewPager.setAdapter(mViewPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOnClickListener(this);
    }

    private void initView() {
        mLlPlayMedia = findViewById(R.id.ll_play_media);
        mToolBar = findViewById(R.id.tb_HomeActivity);
        mViewPlayMedia = findViewById(R.id.layout_play_media);
        mTextTitle = mViewPlayMedia.findViewById(R.id.text_title_media);
        mTextArtist = mViewPlayMedia.findViewById(R.id.text_artists_media);
        mBtnPlayPause = mViewPlayMedia.findViewById(R.id.imbt_Play_media);
        mBtnTitle = mViewPlayMedia.findViewById(R.id.btn_title_media);
        mImgMedia = mViewPlayMedia.findViewById(R.id.img_albumArt_media);

        if (MediaPlayerService.mMediaPlayer != null) {
            if (MediaPlayerService.mMediaPlayer.isPlaying()) {
                mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
            } else {
                mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
            }
        }else {
            mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
        }
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mViewPager_Home = findViewById(R.id.vp_Home);
        setupViewPager(mViewPager_Home);
    }

    private void assignView(){
        mBtnPlayPause.setOnClickListener(this);
        mBtnPlayPause.setAnimationDuration(1500);
        mBtnTitle.setOnClickListener(this);
        mViewPlayMedia.setOnClickListener(this);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.navigation_home:
                    if (mLlPlayMedia.getVisibility() == View.VISIBLE){
                        mLlPlayMedia.setVisibility(View.GONE);
                    }
                    mViewPager_Home.setCurrentItem(0);
                    return true;
                case R.id.navigation_songs:
                    if (mLlPlayMedia.getVisibility() == View.VISIBLE){
                        mLlPlayMedia.setVisibility(View.GONE);
                    }
                    mViewPager_Home.setCurrentItem(1);
                    return true;
                case R.id.navigation_albums:
                    if (mLlPlayMedia.getVisibility() == View.VISIBLE){
                        mLlPlayMedia.setVisibility(View.GONE);
                    }
                    mViewPager_Home.setCurrentItem(2);
                    return true;
                case R.id.navigation_artists:
                    if (mLlPlayMedia.getVisibility() == View.VISIBLE){
                        mLlPlayMedia.setVisibility(View.GONE);
                    }
                    mViewPager_Home.setCurrentItem(3);
                    return true;
                case R.id.navigation_playlists:
                    if (mLlPlayMedia.getVisibility() == View.VISIBLE){
                        mLlPlayMedia.setVisibility(View.GONE);
                    }
                    mViewPager_Home.setCurrentItem(4);
                    return true;
            }
            return false;
        }
    };

    private void setTypeSong(String type){
        Log.d("PPP", "setTypeSong: "+SongManager.getInstance().getCurrentMusic());
        if (mSongManager.getAllPlaylistDB().searchPlayList(type)) {
            mSongs = mSongManager.getAllSongToPlayList(type);
        }else {
            if (type.equals(Constants.VALUE.NEW_SONGS) || type.equals(Constants.VALUE.ALL_NEW_SONGS)) {
                mSongs = SongManager.getInstance().newSongs();
            } else if (type.equals(Constants.VALUE.ALL_SONGS)) {
                mSongs = SongManager.getInstance().allSortSongs();
            } else if (type.equals("")) {
                mSongs = SongManager.getInstance().newSongs();
            }
        }
        processEndOfList(SongManager.getInstance().getCurrentMusic());
        Log.d("PPP", mSongs.get(SongManager.getInstance().getCurrentMusic()).getSongName());
        mTextArtist.setText(mSongs.get(SongManager.getInstance().getCurrentMusic()).getArtist());
        mTextTitle.setText(mSongs.get(SongManager.getInstance().getCurrentMusic()).getSongName());
        imageUtils.getSmallImageByPicasso(mSongs.get(SongManager.getInstance().getCurrentMusic()).getAlbumID(), mImgMedia);

    }

    private void processEndOfList(int position){
        Log.d("CCC", "processEndOfList: "+position);
        int size = mSongs.size() - 1;
        if (position == size || position > size){
            SongManager.getInstance().setCurrentMusic(0);

        }else if (position < 0){
            SongManager.getInstance().setCurrentMusic(69);
        }else {
            SongManager.getInstance().setCurrentMusic(position);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imbt_Play_media:

                if(MediaPlayerService.mMediaPlayer != null){
                    if (MediaPlayerService.mMediaPlayer.isPlaying()){
                        mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
                        mBtnPlayPause.setState(MaterialPlayPauseDrawable.State.Pause);
                        Intent iPause = new Intent(this, MediaPlayerService.class);
                        iPause.setAction(Constants.ACTION.PAUSE);
                        iPause.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, false);
                        startService(iPause);
                    }else {
                        mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
                        mBtnPlayPause.setState(MaterialPlayPauseDrawable.State.Play);
                        Intent iPlay = new Intent(this, MediaPlayerService.class);
                        iPlay.setAction(Constants.ACTION.PLAY);
                        iPlay.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, false);
                        startService(iPlay);
                    }
                }
                Toast.makeText(this, "test thôi chưa xài", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layout_play_media:

                break;
            case R.id.btn_title_media:
                Intent intent = new Intent(HomeActivity.this, PlayActivity.class);
                intent.putExtra(Constants.INTENT.TYPE, Constants.VALUE.NEW_SONGS);
                if (MediaPlayerService.mMediaPlayer.isPlaying()) {
                    intent.putExtra(Constants.INTENT.SONG_CONTINUE, true);
                }else {
                    intent.putExtra(Constants.INTENT.SONG_CONTINUE, false);
                }
                intent.putExtra(Constants.INTENT.POSITION,
                        mSharedPrefsUtils.getInteger(Constants.PREFERENCES.POSITION, 0));

                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                break;
            case R.id.menu_search:
                Intent iSearch = new Intent(this, SearchActivity.class);

                startActivity(iSearch);
                break;
            case R.id.vp_Home:
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private int currentViewPagerPosition = 0;
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mLlPlayMedia.getVisibility() == View.VISIBLE){
            mLlPlayMedia.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mNavigationView.getMenu().getItem(currentViewPagerPosition).setChecked(false);
        mNavigationView.getMenu().getItem(position).setChecked(true);
        currentViewPagerPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


}