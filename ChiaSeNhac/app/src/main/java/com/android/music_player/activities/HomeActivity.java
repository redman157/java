package com.android.music_player.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.music_player.BaseActivity;
import com.android.music_player.R;
import com.android.music_player.fragments.AllMusicFragment;
import com.android.music_player.fragments.MainFragment;
import com.android.music_player.interfaces.OnChangeListener;
import com.android.music_player.interfaces.OnConnectionMedia;
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
        MediaBrowserListener.OnMediaListener, SlidingUpPanelLayout.PanelSlideListener, OnChangeListener {
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
    public Toolbar mToolBar;
    public MediaBrowserHelper mMediaBrowserHelper;
    private MediaBrowserListener mBrowserListener;
    public  FrameLayout mLayoutPlaceHolder;
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
        Log.d("XXX", "Home Activity --- onPause: enter");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("XXX", "Home Activity --- onStop: enter");
        onStopService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("XXX", "Home Activity --- onDestroy: enter");

    }

    @Override
    public void onStartService() {
        mMediaBrowserHelper.onStart();
    }

    @Override
    public void onStopService() {
//        mSeekBarAudio.disconnectController();
//        mMediaBrowserHelper.onStop();
    }

    @Override
    public void initService() {
        Log.d("CCC", "Home Activity --- initService: "+mMusicManager.getCurrentMusic());
        browserConnection = mMusicManager.getMediaBrowserConnection();
        browserConnection.setSeekBarAudio(mSeekBarAudio, mTextLeftTime, mTextRightTime);
        browserConnection.setMediaId(mMusicManager.getCurrentMusic());

        mMediaBrowserHelper = browserConnection;
        mBrowserListener = new MediaBrowserListener();
        mBrowserListener.setOnMediaListener(this);

        mMediaBrowserHelper.registerCallback("HomeActivity", mBrowserListener);

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
    public void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_placeholder, fragment);
        fragmentTransaction.addToBackStack(fragment.getTag());
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        onStartService();
        super.onStart();

        if (mSlidingUpPanelLayout.getPanelState() == PanelState.EXPANDED){
            Log.d("XXX", "Home Activity --- onStart: EXPANDED");
            setViewMusic(mMusicManager.getCurrentMusic(),PanelState.EXPANDED );
        }else if (mSlidingUpPanelLayout.getPanelState() == PanelState.COLLAPSED){
            Log.d("XXX", "Home Activity --- onStart: COLLAPSED");
            setViewMusic(mMusicManager.getCurrentMusic(), PanelState.COLLAPSED);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("XXX", "Home Activity --- onCreate: enter");
        initManager();
        if (savedInstanceState == null){
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fl_placeholder ,
                    AllMusicFragment.newInstance(),
                    "AllMusicFragment");
            fragmentTransaction.add(R.id.fl_placeholder ,MainFragment.newInstance(this),
                    "MainFragment");

            fragmentTransaction.commit();
        }
        setContentView(R.layout.activity_home);
        initView();

        initService();
        setupToolbar();
        assignView();

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

        ImageUtils.getInstance(this).getSmallImageByPicasso(String.valueOf(MusicLibrary.getAlbumRes(songName)),
                mImgChangeMedia);

        mTextRightTime.setText(Utils.formatTime((int) metadataCompat.getLong(Constants.METADATA.Duration)));
        mTextTitleMusic.setText(metadataCompat.getString(Constants.METADATA.Title));
        mTextAlbumMusic.setText(metadataCompat.getString(Constants.METADATA.Album));
        mTextArtistMusic.setText(metadataCompat.getString(Constants.METADATA.Artist));
    }

    public void setViewMusic(String songName, PanelState state){
        if (songName.equals("")){
            return;
        }
        this.nameChoose = songName;
        MediaMetadataCompat metadataCompat = MusicLibrary.getMetadata(this,
                songName);
        mTextArtistMedia.setText(metadataCompat.getString(Constants.METADATA.Artist));
        mTextTitleMedia.setText(metadataCompat.getString(Constants.METADATA.Title));
        ImageUtils.getInstance(this).getSmallImageByPicasso(String.valueOf(MusicLibrary.getAlbumRes(songName)),
                mImgAlbumArt);
        setPlayMedia(songName);

        mSlidingUpPanelLayout.setPanelState(state);
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

        Utils.UpdateButtonPlay(mBtnPlayPauseMedia, isPlaying);
        mSlidingUpPanelLayout.setPanelState(PanelState.HIDDEN);
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
            case android.R.id.home:
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Fragment mainFragment;
                if(manager.findFragmentByTag("MainFragment") != null) {
                    mainFragment = manager.findFragmentByTag("MainFragment");
                } else {
                    mainFragment = MainFragment.newInstance(this);
                }

                transaction.replace(R.id.fl_placeholder, mainFragment);
                transaction.commit();
                break;
            case R.id.action_searchBtn:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.sleep_timer:
                startActivity(new Intent(this, TimerActivity.class));
                break;
            case R.id.sync:
                Intent intent = new Intent(this, SplashActivity.class).putExtra(Constants.VALUE.SYNC,
                        true);
                startActivity(intent);
                break;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.equalizer:
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
            case R.id.icon_next:
                mMediaBrowserHelper.getTransportControls().skipToNext();
                break;
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
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bạn có muốn thoát App không ?");
            builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(1);
                }
            });
            builder.show();
        }
    }

    @Override
    public void onCheckPlay(boolean isPlay, PlaybackStateCompat state) {
        // compare status play don't update button play
        if (isPlaying == isPlay){
            return;
        }
        this.isPlaying = isPlay;

        Utils.UpdateButtonPlay(mBtnPlayPauseMedia, isPlay);
        Utils.UpdateButtonPlay(mBtnPlayPauseMusic, isPlay);
    }

    @Override
    public void onComplete(boolean isNext) {
        if (isNext){
            onMediaMetadata(mMediaBrowserHelper.getMetadata());
        }
    }

    @Override
    public void onMediaMetadata(MediaMetadataCompat mediaMetadata) {
        Log.d("CCC",
                "HomeActivity --- onMediaMetadata: "+mediaMetadata.getString(Constants.METADATA.Title));
        if (mSlidingUpPanelLayout.getPanelState() == PanelState.EXPANDED){
            setViewMusic(mediaMetadata.getString(Constants.METADATA.Title) , PanelState.EXPANDED);
        }else if (mSlidingUpPanelLayout.getPanelState() == PanelState.COLLAPSED){
            setViewMusic(mediaMetadata.getString(Constants.METADATA.Title) , PanelState.COLLAPSED);
        }
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

        switch (newState) {
            case EXPANDED:
                mViewPanelMedia.setVisibility(View.GONE);
                break;
            case COLLAPSED:
                mViewPanelMedia.setAlpha(1);
                mViewPanelMedia.setVisibility(View.VISIBLE);
                break;
        }
    }
    private OnConnectionMedia onConnectionMedia;
    @Override
    public void onNameMusic(String nameMusic) {
        setViewMusic(nameMusic, PanelState.EXPANDED);
        browserConnection.setAutoPlay(nameMusic, true);
    }


}
