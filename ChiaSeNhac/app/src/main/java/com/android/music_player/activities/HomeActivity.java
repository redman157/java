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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.music_player.BaseActivity;
import com.android.music_player.interfaces.OnChangePlayListListener;
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
import com.android.music_player.utils.BundleUtils;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import me.zhanghai.android.materialplaypausedrawable.MaterialPlayPauseButton;

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
    private int currentMedia, mediaMax;
    private String songTitle;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    public LinearLayout mLlPlayMedia;
    public int choosePosition;
    public boolean isContinue;
    private OnChangePlayListListener onChangePlayListListener;
    private BundleUtils bundleUtils;

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
                if(MediaPlayerService.mMediaPlayer!= null && MediaPlayerService.mMediaPlayer.isPlaying()) {
                    mBtnPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_pause_light));
                }
            }
            setSongCurrent(mSongs);
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
                Utils.PauseMediaService(context, mSongManager.getTypeCurrent(), mSongManager.getPositionCurrent() );
            } else {
                Log.d(tag, "PlayActivity --- brIsPlayService:" +false);
                mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
                isPlaying = false;
                Utils.PlayMediaService(context, mSongManager.getTypeCurrent(), mSongManager.getPositionCurrent());
            }
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("XXX", "HomeActivity --- onCreate: Enter");
    }

    @Override
    protected void onStop() {

        super.onStop();
        Log.d("XXX", "HomeActivity --- onCreate: Enter");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(brPlayPauseActivity);
        this.unregisterReceiver(brIsPlayService);
    }


    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(brPlayPauseActivity, new IntentFilter(Constants.ACTION.BROADCAST_PLAY_PAUSE));
        registerReceiver(brIsPlayService, new IntentFilter(Constants.ACTION.IS_PLAY));
        mSongManager = SongManager.getInstance();
        mSongManager.setContext(this);
        mSongs = mSongManager.getCurrentSongs();

        if (MediaPlayerService.mMediaPlayer != null){
            if(MediaPlayerService.mMediaPlayer.isPlaying()){
                mLlPlayMedia.setVisibility(View.VISIBLE);
            }
        }else {
            mLlPlayMedia.setVisibility(View.GONE);
        }

//        setSongCurrent(mSongs);
        choosePosition = mSongManager.getPositionCurrent();
        Log.d("XXX", "HomeActivity --- onStart: "+choosePosition);

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
        mSongs = mSongManager.getCurrentSongs();
        imageUtils = ImageUtils.getInstance(this);

        setupToolbar();
        assignView();
        setSongCurrent(mSongs);
        Log.d("XXX", "HomeActivity --- onCreate: Enter");
    }

    private void setupToolbar() {
        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
    }

    public void setSongCurrent(ArrayList<SongModel> song){

        if (mSongManager.getPositionCurrent() == -1){
            mTextArtist.setText(song.get(0).getArtist());
            mTextTitle.setText(song.get(0).getSongName());
            imageUtils.getSmallImageByPicasso(song.get(0).getAlbumID(), mImgMedia);
        }else {
            mTextArtist.setText(song.get(SongManager.getInstance().getPositionCurrent()).getArtist());
            mTextTitle.setText(song.get(SongManager.getInstance().getPositionCurrent()).getSongName());
            imageUtils.getSmallImageByPicasso(song.get(SongManager.getInstance().getPositionCurrent()).getAlbumID(), mImgMedia);
        }

    }

    private void setupViewPager(ViewPager viewPager){
        mViewPagerAdapter = new ViewPagerAdapter(this,getSupportFragmentManager());
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setOnChangePlayListListener(onChangePlayListListener);

        mViewPagerAdapter.addFragment(homeFragment);
        mViewPagerAdapter.addFragment(new AllSongsFragment());

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

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mViewPager_Home = findViewById(R.id.vp_Home);
        setupViewPager(mViewPager_Home);

        if (MediaPlayerService.mMediaPlayer != null) {
            if (MediaPlayerService.mMediaPlayer.isPlaying()) {
                mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
                mLlPlayMedia.setVisibility(View.VISIBLE);
            } else {
                mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
                mLlPlayMedia.setVisibility(View.GONE);
            }
        }else {
            mLlPlayMedia.setVisibility(View.GONE);
            mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
        }

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
            if (mLlPlayMedia.getVisibility() == View.VISIBLE){
                mLlPlayMedia.setVisibility(View.GONE);
            }
            switch (menuItem.getItemId()){

                case R.id.navigation_home:
                    mViewPager_Home.setCurrentItem(0);
                    return true;
                case R.id.navigation_library:
                    mViewPager_Home.setCurrentItem(1);
                    return true;

            }
            return false;
        }
    };

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



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imbt_Play_media:
                Utils.isPlayMediaService(this, mSongManager.getTypeCurrent(),mSongManager.getPositionCurrent());
                break;
            case R.id.layout_play_media:

                break;
            case R.id.btn_title_media:
                Log.d("XXX", "btn_title_media: "+choosePosition);
                Intent intent = new Intent(HomeActivity.this, PlayActivity.class);
                BundleUtils.Builder builder = new BundleUtils.Builder();
                builder.putString(Constants.INTENT.TYPE, Constants.VALUE.NEW_SONGS);
                builder.putInteger(Constants.INTENT.CHOOSE_POS, choosePosition);

                if (mSongManager.isPlayCurrentSong(choosePosition)) {
                    Log.d("BBB", "HomeActivity --- btn_title_media: true");
                    builder.putBoolean(Constants.INTENT.SONG_CONTINUE, true);
                }else {
                    Log.d("BBB", "HomeActivity --- btn_title_media: false");
                    Utils.PauseMediaService(this,  Constants.VALUE.NEW_SONGS, choosePosition);
                    builder.putBoolean(Constants.INTENT.SONG_CONTINUE, false);
                }
                intent.putExtras(builder.generate().getBundle());
                finish();

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                builder.generate().clear();
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
      /*  if (mLlPlayMedia.getVisibility() == View.VISIBLE){
            mLlPlayMedia.setVisibility(View.GONE);
        }*/
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
