package com.android.music_player.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.music_player.BaseActivity;
import com.android.music_player.R;
import com.android.music_player.fragments.MainFragment;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.managers.MusicManager;
import com.android.music_player.media.MediaBrowserConnection;
import com.android.music_player.media.MediaBrowserHelper;
import com.android.music_player.media.MediaBrowserListener;
import com.android.music_player.media.MediaSeekBar;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.utils.SwipeTouchUtils;
import com.android.music_player.utils.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;


public class HomeActivity extends BaseActivity implements View.OnClickListener,
        MediaBrowserListener.OnPlayPause, SlidingUpPanelLayout.PanelSlideListener{
    private String tag = "BBB";
    private RelativeLayout mViewControlMedia;
    private LinearLayout mViewPanelMedia, mLayoutSeeMore, mLayoutControlSong, mLlChangeMusic;
    private View mLayoutMedia;
    public ImageView mImgAlbumArt, mImgChangeMusic, mImgBack;
    public Button mBtnTitle;
    private ArrayList<SongModel> mSongs;
    private MusicManager mMusicManager;
    public ImageButton mBtnPlayPauseMedia , mBtnPlayPauseMusic;
    private ImageButton mBtnPrev, mBtnRepeat, mBtnNext, mBtnSetTime,
            mBtnSeeMore, mBtnAbout, mBtnEqualizer, mBtnFavorite;
    public MediaSeekBar mSeekBarAudio;
    private TextView mTextLeftTime, mTextRightTime, mTextTitleMedia, mTextArtistMedia
            , mTextTitleMusic, mTextArtistMusic , mTextAlbumMusic;
    private ImageView mImgViewQueue, mImgAddToPlayList, mImgChangeMedia;

    public SlidingUpPanelLayout mSlidingUpPanelLayout;
    private Toolbar mToolBar;
    private MediaBrowserHelper mMediaBrowserHelper;
    private MediaBrowserListener mBrowserListener;
    private FrameLayout mLayoutPlaceHolder;
    private AppBarLayout mAppBarLayout;

    private boolean isPlaying = false;
    public String type;
    public MediaBrowserConnection browserConnection;
    private SharedPrefsUtils mSharedPrefsUtils;
    private int step = 0;
    private float alpha = 0 ;
    private boolean isMore, isSetup;
    private String nameChoose;


    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        onStopService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onStartService() {
        mMediaBrowserHelper.onStart();
    }

    @Override
    public void onStopService() {
        mSeekBarAudio.disconnectController();
        mMediaBrowserHelper.onStop();
    }

    @Override
    public void initService() {
        browserConnection = mMusicManager.getMediaBrowserConnection();
        browserConnection.setSeekBarAudio(mSeekBarAudio, mTextLeftTime, mTextRightTime);
        browserConnection.setMediaId(mMusicManager.getCurrentMusic().getSongName(), false);
        mMediaBrowserHelper = browserConnection;

        mBrowserListener = new MediaBrowserListener();
        mBrowserListener.setOnPlayPause(this);
        mMediaBrowserHelper.registerCallback("PlayActivity", mBrowserListener);
    }

    @Override
    public void initManager() {
        mSharedPrefsUtils = new SharedPrefsUtils(this);
        mMusicManager = MusicManager.getInstance();
        mMusicManager.setContext(this);
        mSongs = mMusicManager.getListSong();
        ImageUtils.getInstance(this);
    }

    @Override
    public void changeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_placeholder, fragment);
//        fragmentTransaction1.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initManager();
        initService();
        onStartService();

        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("MainFragment");
        changeFragment(mainFragment);
        setViewMusic(mMusicManager.getCurrentMusic().getSongName(), false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null){
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fl_placeholder ,MainFragment.newInstance(),"MainFragment");
            fragmentTransaction.commit();
        }
        setContentView(R.layout.activity_home);
        initView();
        setupToolbar();
        assignView();
        Utils.UpdateButtonPlay(mBtnPlayPauseMedia, isPlaying);
    }

    private void setMode(int repeat) {
        switch (repeat){
            case 0:
                mBtnRepeat.setImageResource(R.drawable.app_repeat_active);
                mMediaBrowserHelper.getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
                break;
            case 1:
                mBtnRepeat.setImageResource(R.drawable.app_repeat);
                mMediaBrowserHelper.getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
                break;
            case 2:
                mBtnRepeat.setImageResource(R.drawable.app_shuffle_white);
                mMediaBrowserHelper.getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
                break;
            case 3:
                mBtnRepeat.setImageResource(R.drawable.app_shuffle_black);

                mMediaBrowserHelper.getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
                break;
        }
    }

    private void setupToolbar() {
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    public void setPlayMedia(String songName){
        MediaMetadataCompat metadataCompat = MusicLibrary.getMetadata(this,
                songName);

        ImageUtils.getInstance(this).getSmallImageByPicasso(
                String.valueOf(MusicLibrary.getAlbumRes(songName)),mImgChangeMedia
        );

        mTextTitleMusic.setText(metadataCompat.getString(Constants.METADATA.Title));
        mTextAlbumMusic.setText(metadataCompat.getString(Constants.METADATA.Album));
        mTextArtistMusic.setText(metadataCompat.getString(Constants.METADATA.Artist));

    }

    public boolean setHiddenSliding(SongModel songModel){
        if (songModel == null){
            Log.d("CCC", "HomeActivity --- setViewMusic: songmodel null");
            mSlidingUpPanelLayout.setPanelState(PanelState.HIDDEN);
//            mMusicManager.setMediaId("");
            return false;
        }else {
            Log.d("CCC", "HomeActivity --- setViewMusic: songmodel khác null");
            mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
//            mMusicManager.setMediaId(mMusicManager.getCurrentMusic().getSongName());
            return true;
        }
    }

    public void setViewMusic(String songName, boolean isSetup){

        this.nameChoose = songName;
        MediaMetadataCompat metadataCompat = MusicLibrary.getMetadata(this,
                songName);

        mTextArtistMedia.setText(metadataCompat.getString(Constants.METADATA.Artist));
        mTextTitleMedia.setText(metadataCompat.getString(Constants.METADATA.Title));
        ImageUtils.getInstance(this).getSmallImageByPicasso(
                String.valueOf(MusicLibrary.getAlbumRes(metadataCompat.getString(Constants.METADATA.Title))),
                mImgAlbumArt);


        setPlayMedia(songName);

     /*   if (isSetup){
            mSlidingUpPanelLayout.setPanelState(PanelState.EXPANDED);
        }else {
            mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
        }*/
    }

    private void initView() {
        mSlidingUpPanelLayout = findViewById(R.id.activity_main);
        mToolBar = findViewById(R.id.tb_HomeActivity);
        mLayoutPlaceHolder = findViewById(R.id.fl_placeholder);
        mAppBarLayout = findViewById(R.id.appBarLayout);
        mLayoutMedia = findViewById(R.id.layout_main_media);

        // view group mLayoutMedia
        mLayoutSeeMore = mLayoutMedia.findViewById(R.id.ll_see_more);
        mLlChangeMusic = mLayoutMedia.findViewById(R.id.ll_change_music);
        mViewControlMedia = mLayoutMedia.findViewById(R.id.rl_change_state);
        mViewPanelMedia = mLayoutMedia.findViewById(R.id.ll_play_media);
        mLayoutControlSong = mLayoutMedia.findViewById(R.id.ll_control_song);

        // status play media
        mImgAlbumArt = mLayoutMedia.findViewById(R.id.img_albumArt_media);
        mTextTitleMedia = mLayoutMedia.findViewById(R.id.text_title_media);
        mTextArtistMedia = mLayoutMedia.findViewById(R.id.text_artists_media);
        mBtnPlayPauseMedia = mLayoutMedia.findViewById(R.id.imbt_Play_media);

        // thông tin bài hát
        mImgBack = mLayoutMedia.findViewById(R.id.img_change_state);
        mTextTitleMusic = mLayoutMedia.findViewById(R.id.text_title);
        mTextArtistMusic = mLayoutMedia.findViewById(R.id.text_artist);
        mTextAlbumMusic = mLayoutMedia.findViewById(R.id.text_album);
        mImgViewQueue = mLayoutMedia.findViewById(R.id.item_img_viewQueue);
        mImgAddToPlayList = mLayoutMedia.findViewById(R.id.item_img_addToPlayListImageView);
        mImgChangeMedia = mLayoutMedia.findViewById(R.id.img_album_art);

        // linear control song
        mBtnAbout = mLayoutMedia.findViewById(R.id.icon_about);
        mBtnTitle = mLayoutMedia.findViewById(R.id.btn_title_media);
        mBtnSetTime = mLayoutMedia.findViewById(R.id.icon_set_time);
        mBtnEqualizer = mLayoutMedia.findViewById(R.id.icon_equalizer);
        mBtnFavorite = mLayoutMedia.findViewById(R.id.icon_favorite);
        mImgBack = mLayoutMedia.findViewById(R.id.img_change_state);

        // linear seekbar time
        mTextLeftTime = mLayoutMedia.findViewById(R.id.text_leftTime);
        mSeekBarAudio = mLayoutMedia.findViewById(R.id.sb_Time);
        mTextRightTime = mLayoutMedia.findViewById(R.id.text_rightTime);

        // linear play media
        mBtnRepeat = mLayoutMedia.findViewById(R.id.icon_change_mode);
        mBtnPrev = mLayoutMedia.findViewById(R.id.icon_prev);
        mBtnPlayPauseMusic = mLayoutMedia.findViewById(R.id.icon_play);
        mBtnNext = mLayoutMedia.findViewById(R.id.icon_next);
        mBtnSeeMore = mLayoutMedia.findViewById(R.id.icon_image_More);
    }

    private void assignView(){
        mSlidingUpPanelLayout.addPanelSlideListener(this);

        mLayoutSeeMore.setOnClickListener(this);


        mBtnPlayPauseMedia.setOnClickListener(this);
        mBtnTitle.setOnClickListener(this);
        mBtnFavorite.setOnClickListener(this);
        mBtnTitle.setOnClickListener(this);
        mBtnSetTime.setOnClickListener(this);
        mBtnEqualizer.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mBtnRepeat.setOnClickListener(this);
        mBtnPrev.setOnClickListener(this);
        mBtnPlayPauseMusic.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mBtnSeeMore.setOnClickListener(this);
        mBtnAbout.setOnClickListener(this);

        mViewControlMedia.setOnTouchListener(new SwipeTouchUtils(this));
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

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.icon_about:
                break;
            case R.id.icon_set_time:
                break;
            case R.id.icon_equalizer:
                startActivity(new Intent(this, EqualizerActivity.class));
                break;
            case R.id.icon_favorite:
                break;
            case R.id.icon_change_mode:
                step = step + 1;
                if (step == 4){
                    step = 0;
                }
                setMode(step);
                break;
            case R.id.icon_prev:
                mMediaBrowserHelper.getTransportControls().skipToPrevious();
                break;
            case R.id.icon_play:

            case R.id.imbt_Play_media:

                if (isPlaying){
                    mMediaBrowserHelper.getTransportControls().pause();
                }else {
                    mMediaBrowserHelper.getTransportControls().playFromMediaId(nameChoose, null);
                }
                break;
            /*case R.id.icon_next:

                break;*/
            case R.id.icon_image_More:
                if (!isMore){
                    mLayoutSeeMore.setAlpha(1);
                    mBtnSeeMore.setImageResource(R.drawable.ic_menu_dot_black);
                    isMore = true;
                    Animation fadeIn = AnimationUtils.loadAnimation(HomeActivity.this,R.anim.fadein);
                    mLayoutSeeMore.setAnimation(fadeIn);
                    mLayoutSeeMore.setVisibility(View.VISIBLE);
                }else {
                    mBtnSeeMore.setImageResource(R.drawable.ic_menu_dot_white);

                    isMore = false;
                    mLayoutSeeMore.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mLayoutSeeMore.setVisibility(View.GONE);
                        }
                    });
                }
                break;
            case R.id.btn_title_media:
                if (mSlidingUpPanelLayout != null &&
                        (mSlidingUpPanelLayout.getPanelState() == PanelState.COLLAPSED)){
                    mSlidingUpPanelLayout.setPanelState(PanelState.EXPANDED);

                }
                break;
            case R.id.img_change_state:
                if (mSlidingUpPanelLayout != null &&
                        (mSlidingUpPanelLayout.getPanelState() == PanelState.EXPANDED || mSlidingUpPanelLayout.getPanelState() == PanelState.ANCHORED)) {
                    mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
                }
                Log.d("CCC","hehe");
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
        if (mSlidingUpPanelLayout != null &&
                (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }


    @Override
    public void onCheck(boolean isPlay, PlaybackStateCompat state) {
        // compare status play don't update button play
        if (isPlaying == isPlay){
            return;
        }
        this.isPlaying = isPlay;

        Utils.UpdateButtonPlay(mBtnPlayPauseMedia, isPlay);
        Utils.UpdateButtonPlay(mBtnPlayPauseMusic, isPlay);
    }

    @Override
    public void onNext(boolean isNext) {
        if (isNext){
            mMediaBrowserHelper.getTransportControls().skipToNext();
        }
    }

    @Override
    public void onMediaMetadata(MediaMetadataCompat mediaMetadata) {
        setViewMusic(mediaMetadata.getString(Constants.METADATA.Title), true);
    }

    // Panel change state
    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        Log.d("UUU", "onPanelSlide : "+slideOffset);
        alpha = slideOffset;
        if (slideOffset > 0.3){
            mViewPanelMedia.setVisibility(View.GONE);
        }else {
            mViewPanelMedia.setAlpha(slideOffset);
        }
        mViewControlMedia.setAlpha(alpha);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
        /**
         *  EXPANDED vuốt thành công,
         *  COLLAPSED trạng thái ban đầu chưa vuốt,
         *  ANCHORED vuốt sẽ dừng lại ở mức set cứng,
         *  HIDDEN ẩn thanh sliding,
         *  DRAGGING đang vuốt
         * **/
        Log.d("III", "onPanelStateChanged previousState: " + previousState.toString() + " " +
                "---newState: " + newState.toString());

        panel.findViewById(R.id.icon_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaBrowserHelper.getTransportControls().skipToNext();

            }
        });
        switch (newState) {
            case EXPANDED:
                mViewPanelMedia.setVisibility(View.GONE);
//                mSlidingUpPanelLayout.setTouchEnabled(false);

                break;
            case COLLAPSED:
                mViewPanelMedia.setAlpha(1);
                mViewPanelMedia.setVisibility(View.VISIBLE);

//                mSlidingUpPanelLayout.setTouchEnabled(true);
                break;
        }


    }

}
