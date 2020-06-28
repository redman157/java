package com.android.music_player.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.music_player.R;
import com.android.music_player.adapters.MusicDialogAdapter;
import com.android.music_player.fragments.HomeFragment;
import com.android.music_player.fragments.LibraryFragment;
import com.android.music_player.interfaces.OnChangeListener;
import com.android.music_player.interfaces.OnMediaItem;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.media.BrowserHelper;
import com.android.music_player.media.MediaBrowserListener;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.DialogHelper;
import com.android.music_player.utils.ImageHelper;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.utils.Utils;
import com.android.music_player.view.PlayPauseView;
import com.google.android.material.appbar.AppBarLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;


public class HomeActivity extends BaseActivity implements View.OnClickListener, OnMediaItem,
        MediaBrowserListener.OnChangeMusicListener, SlidingUpPanelLayout.PanelSlideListener, OnChangeListener {
    private RelativeLayout mViewControlMedia, mViewMusic;
    private LinearLayout mViewPanelMedia, mLayoutSeeMore, mLayoutControlSong, mLlChangeMusic;
    private View mLayoutMedia, mLayoutState;

    public ImageView mImgAlbumArt, mImgChangeMusic, mImgBack;
    private LinearLayout mLinearTop;
    public Button mBtnHome, mBtnLibrary;
    private MediaManager mMediaManager;
    public PlayPauseView mBtnPlayPauseMedia , mBtnPlayPausePanel;
    private ImageButton mBtnPrev, mBtnRepeat, mBtnNext, mBtnSetTime,
            mBtnSeeMore, mBtnAbout, mBtnEqualizer, mBtnFavorite, mBtnShuffle;
    public SeekBar mSeekBarAudio;
    private TextView mTextLeftTime, mTextRightTime, mTextTitleMedia, mTextArtistMedia
            , mTextTitleMusic, mTextArtistMusic , mTextAlbumMusic;
    private ImageView mImgViewQueue, mImgAddToPlayList, mImgChangeMedia;
    public SlidingUpPanelLayout mSlidingUpPanelLayout;
    public Toolbar mToolBar;
    public  FrameLayout mLayoutPlaceHolder;
    private AppBarLayout mAppBarLayout;
    private DrawerLayout mDrawerLayout;
    private boolean isPlaying = false , isReplay = false, isShuffle = false;
    public String type;
    public BrowserHelper mBrowserHelper;
    private SharedPrefsUtils mSharedPrefsUtils;
    private int step = 0;
    private float alpha = 0 ;
    private boolean isMore;
    private String nameChoose;
    private STATE state;
    private MusicDialogAdapter mMusicDialogAdapter;
    public final String FRAGMENT_TAG = "fragment_tag";


    @Override
    public void initManager() {
        mSharedPrefsUtils = getSharedPrefsUtils();
        mMediaManager = getMediaManager();
        ImageHelper.getInstance(this);
    }

    @Override
    public void onBackPressed() {
        if (mSlidingUpPanelLayout.getPanelState() == PanelState.EXPANDED){
            mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
        }else {
            baseBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSlidingUpPanelLayout.getPanelState() == PanelState.EXPANDED){
            setViewMusic(mMediaManager.getCurrentMusic(),PanelState.EXPANDED );
        }else if (mSlidingUpPanelLayout.getPanelState() == PanelState.COLLAPSED){
            setViewMusic(mMediaManager.getCurrentMusic(), PanelState.COLLAPSED);
        }
        setMediaChange(this.getClass().getSimpleName(), this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initManager();
        setContentView(R.layout.activity_home);
        initializeToolbar();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.app_name);
        }
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_placeholder ,
                            HomeFragment.newInstance(this),
                            FRAGMENT_TAG)
                    .commit();
        }
        initView();
        assignView();
        Log.d("III","State hiện tại: "+(mSlidingUpPanelLayout.getPanelState()));
    }

    
    public void setPlayMedia(String songName){
        MediaMetadataCompat metadataCompat = mMediaManager.getMetadata(this,
                songName);
        ImageHelper.getInstance(this).getSmallImageByPicasso(String.valueOf(MusicLibrary.getAlbumRes(songName)),
                mImgChangeMedia);

        mTextRightTime.setText(Utils.formatTime((int) metadataCompat.getLong(Constants.METADATA.Duration)));
        mTextTitleMusic.setText(metadataCompat.getString(Constants.METADATA.Title));
        mTextAlbumMusic.setText(metadataCompat.getString(Constants.METADATA.Album));
        mTextArtistMusic.setText(metadataCompat.getString(Constants.METADATA.Artist));
    }

    public void setViewMusic(String songName, PanelState state){
        if (songName.equals("")){
            songName = (String) MusicLibrary.music.keySet().toArray()[0];
        }
        this.nameChoose = songName;
        MediaMetadataCompat metadataCompat = mMediaManager.getMetadata(this,
                songName);
        mTextArtistMedia.setText(metadataCompat.getString(Constants.METADATA.Artist));
        mTextTitleMedia.setText(metadataCompat.getString(Constants.METADATA.Title));
        ImageHelper.getInstance(this).getSmallImageByPicasso(String.valueOf(MusicLibrary.getAlbumRes(songName)),
                mImgAlbumArt);
        setPlayMedia(songName);

        mSlidingUpPanelLayout.setPanelState(state);
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        mSlidingUpPanelLayout = findViewById(R.id.activity_main);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        mToolBar = findViewById(R.id.toolbar);
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
        mImgBack = mLayoutMedia.findViewById(R.id.img_close_panel);
        mTextTitleMusic = mLayoutMedia.findViewById(R.id.text_title);
        mTextArtistMusic = mLayoutMedia.findViewById(R.id.text_artist);
        mTextAlbumMusic = mLayoutMedia.findViewById(R.id.text_album);
        mImgViewQueue = mLayoutMedia.findViewById(R.id.item_img_viewQueue);
        mImgAddToPlayList = mLayoutMedia.findViewById(R.id.item_img_addToPlayListImageView);
        mImgChangeMedia = mLayoutMedia.findViewById(R.id.img_album_art);

        // linear control song
        mBtnAbout = mLayoutMedia.findViewById(R.id.icon_about);
        mBtnSetTime = mLayoutMedia.findViewById(R.id.icon_set_time);
        mBtnEqualizer = mLayoutMedia.findViewById(R.id.icon_equalizer);
        mBtnFavorite = mLayoutMedia.findViewById(R.id.icon_favorite);
        mImgBack = mLayoutMedia.findViewById(R.id.img_close_panel);

        // linear seekbar time
        mTextLeftTime = mLayoutMedia.findViewById(R.id.text_leftTime);
        mSeekBarAudio = mLayoutMedia.findViewById(R.id.sb_Time);
        mTextRightTime = mLayoutMedia.findViewById(R.id.text_rightTime);

        // linear play media
        mBtnRepeat = mLayoutMedia.findViewById(R.id.icon_replay);
        mBtnPrev = mLayoutMedia.findViewById(R.id.icon_prev);
        mBtnPlayPausePanel = mLayoutMedia.findViewById(R.id.icon_play);
        mBtnNext = mLayoutMedia.findViewById(R.id.icon_next);
        mBtnSeeMore = mLayoutMedia.findViewById(R.id.icon_image_More);
        mBtnShuffle = mLayoutMedia.findViewById(R.id.icon_shuffle);

        // linear button state
        mLayoutState= mLayoutMedia.findViewById(R.id.layout_panel_home);
        mLinearTop = findViewById(R.id.ll_top);


        mBtnHome = mLayoutMedia.findViewById(R.id.btn_home);
        mBtnLibrary = mLayoutMedia.findViewById(R.id.btn_library);
        if (Build.VERSION.SDK_INT > 23) {
            mBtnHome.setTextColor(R.color.red);
        }else {
            mBtnHome.setTextColor(getResources().getColor(R.color.red));
        }
        mViewMusic = mLayoutState.findViewById(R.id.rl_info_music);
    }

    private void assignView(){
        mSlidingUpPanelLayout.addPanelSlideListener(this);
        mLayoutSeeMore.setOnClickListener(this);

        mBtnFavorite.setOnClickListener(this);
        mViewMusic.setOnClickListener(this);
        mBtnSetTime.setOnClickListener(this);
        mBtnEqualizer.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mBtnRepeat.setOnClickListener(this);
        mBtnPrev.setOnClickListener(this);

        mBtnNext.setOnClickListener(this);
        mBtnSeeMore.setOnClickListener(this);
        mBtnAbout.setOnClickListener(this);
        mBtnHome.setOnClickListener(this);
        mBtnLibrary.setOnClickListener(this);
        mBtnShuffle.setOnClickListener(this);

        mBtnPlayPauseMedia.setOnClickListener(this);
        mBtnPlayPausePanel.setOnClickListener(this);

        mBtnPlayPauseMedia.Pause();
        mBtnPlayPausePanel.Pause();

        mImgViewQueue.setOnClickListener(this);
        mImgAddToPlayList.setOnClickListener(this);

        mSeekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                getControllerActivity().getTransportControls().seekTo(seekBar.getProgress());
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_home:
                mBtnLibrary.setTextColor(R.color.black);
                mBtnHome.setTextColor(R.color.red);
                mBtnHome.setEnabled(false);
                mBtnLibrary.setEnabled(true);


                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_placeholder ,
                                HomeFragment.newInstance(this),
                                FRAGMENT_TAG)
                        .commit();
                break;
            case R.id.btn_library:
                mBtnHome.setEnabled(true);
                mBtnLibrary.setEnabled(false);
                mBtnHome.setTextColor(R.color.black);
                mBtnLibrary.setTextColor(R.color.red);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_placeholder ,
                                LibraryFragment.newInstance(),
                                FRAGMENT_TAG)
                        .commit();
                break;
            case R.id.icon_about:
                break;
            case R.id.icon_set_time:
                break;
            case R.id.icon_equalizer:
                startActivity(new Intent(this, EqualizerActivity.class));
                break;
            case R.id.icon_favorite:
                break;
            case R.id.icon_shuffle:
                if (isShuffle) {
                    mBtnShuffle.setImageResource(R.drawable.app_shuffle_white);
                    getControllerActivity().getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
                    isShuffle = false;
                } else{
                    mBtnShuffle.setImageResource(R.drawable.app_shuffle_black);
                    getControllerActivity().getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
                    isShuffle = true;
                }
                break;
            case R.id.icon_replay:
                if (isReplay){
                    mBtnRepeat.setImageResource(R.drawable.app_repeat_active);
                    getControllerActivity().getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
                    isReplay = false;
                }else {
                    mBtnRepeat.setImageResource(R.drawable.app_repeat);
                    getControllerActivity().getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
                    isReplay = true;
                }
                break;
            case R.id.icon_prev:
                getControllerActivity().getTransportControls().skipToPrevious();

                break;
            case R.id.icon_play:

            case R.id.imbt_Play_media:
                if (isPlaying){
                    mBtnPlayPauseMedia.Pause();
                    mBtnPlayPausePanel.Pause();
                    getControllerActivity().getTransportControls().pause();
                    stopSeekBarUpdate();
                }else {
                    mBtnPlayPauseMedia.Play();
                    mBtnPlayPausePanel.Play();

                    getControllerActivity().getTransportControls().playFromMediaId(nameChoose, null);
                }
                break;
            case R.id.icon_next:
                getControllerActivity().getTransportControls().skipToNext();

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
            case R.id.item_img_viewQueue:
                Dialog mDlOptionMusic = new Dialog(this);
                mMusicDialogAdapter = new MusicDialogAdapter(this, mDlOptionMusic,
                        getControllerActivity().getQueue());
                mMusicDialogAdapter.setOnClickItemListener(this);
                mMusicDialogAdapter.notifyDataSetChanged();
              /*  for (int i = 0; i < mSongModels.size(); i++) {
                    if (mSongModels.get(i).getSongName().equals(musicMain.get(pos).getSongName())) {
                        mMusicDialogAdapter.setPosition(i);
                        mMusicDialogAdapter.setOnClickItemListener(onClickItemListener);
                        DialogHelper.showSelectSong(this, mMusicDialogAdapter, i);
                    }
                }*/
              DialogHelper.showSelectSong(this, mMusicDialogAdapter);
                break;
            case R.id.item_img_addToPlayListImageView:
                break;
            case R.id.rl_info_music:
                if (mSlidingUpPanelLayout != null &&
                        (mSlidingUpPanelLayout.getPanelState() == PanelState.COLLAPSED)){
                    mSlidingUpPanelLayout.setPanelState(PanelState.EXPANDED);
                }
                break;
            case R.id.img_close_panel:
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
    public void onStateChange(boolean isPlay, PlaybackStateCompat state) {
        // compare status play don't update button play
        if (isPlaying != isPlay){
            if (isPlay){
                mBtnPlayPauseMedia.Play();
                mBtnPlayPausePanel.Play();
            }else {
                mBtnPlayPauseMedia.Pause();
                mBtnPlayPausePanel.Pause();
            }
        }

        this.isPlaying = isPlay;
        mLastPlaybackState = state;
        // setup seekbar timer
        if (isPlay && (state.getState() == PlaybackStateCompat.STATE_PLAYING || state.getState() == PlaybackStateCompat.STATE_BUFFERING)) {
            scheduleSeekBarUpdate();
        }
    }

    @Override
    public void onMediaMetadata(MediaMetadataCompat mediaMetadata) {
        updateDuration(mediaMetadata);
        if (mSlidingUpPanelLayout.getPanelState() == PanelState.EXPANDED){
            setViewMusic(mediaMetadata.getString(Constants.METADATA.Title) , PanelState.EXPANDED);
        }else if (mSlidingUpPanelLayout.getPanelState() == PanelState.COLLAPSED){
            setViewMusic(mediaMetadata.getString(Constants.METADATA.Title) , PanelState.COLLAPSED);
        }
    }

    @Override
    public void onComplete(boolean isComplete) {
        if (isComplete){
            onMediaMetadata(mBrowserHelper.getMetadata());
        }
    }

    @Override
    public void OnStateChange(STATE state) {
        // thay trang thái
        switch (state){
            case DONE:
                if (this.state == STATE.OPEN){
                    mSlidingUpPanelLayout.setPanelState(PanelState.HIDDEN);
                }else if (this.state == STATE.CLOSE){
                    mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
                }
                mSlidingUpPanelLayout.setTouchEnabled(true);
                break;
            case DRAWING:
                mSlidingUpPanelLayout.setTouchEnabled(false);
                break;
            case CONTROL:
                break;
            case PROCESS:
                break;
        }
    }

    @Override
    public void IsClose(STATE state) {
        this.state = state;
        if (state == STATE.OPEN){
            mSlidingUpPanelLayout.setPanelState(PanelState.HIDDEN);

        }else if (state == STATE.CLOSE){
            mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
        }

    }

    // Panel change state
    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        Log.d("UUU", "onPanelSlide : "+slideOffset);
        alpha = slideOffset;
        mLayoutState.setAlpha(alpha);
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
                mLayoutState.setAlpha(0);
                mLayoutState.setVisibility(View.GONE);
//                getDrawerToggle().onDrawerClosed();
                break;
            case COLLAPSED:
                mLayoutState.setAlpha(1);
                mLayoutState.setVisibility(View.VISIBLE);
                break;
            case DRAGGING:
                mLayoutState.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onMusicID(String nameMusic) {
        setViewMusic(nameMusic, PanelState.EXPANDED);
        mMediaManager.getMediaBrowserConnection().setAutoPlay(nameMusic, true);
    }


//    @Override
//    public void onController(MediaControllerCompat mediaController) {
//        if (mBrowserHelper.isConnect()){
//            Log.d("ZZZ", mediaController == null ? "null" : "khac null" );
//        }
//    }

    /**
     * Setup seekbar for mediaplayer controler
     * */
    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    private ScheduledFuture<?> mScheduleFuture;
    private PlaybackStateCompat mLastPlaybackState;
    private final Handler mHandler = new Handler();
    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private void scheduleSeekBarUpdate() {

        stopSeekBarUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    private void stopSeekBarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    private void updateProgress() {
        if (mLastPlaybackState == null) {
            return;
        }

        long currentPosition = mLastPlaybackState.getPosition();
        Log.d("ZZZ","updateProgress: "+currentPosition);
        if (currentPosition > mSeekBarAudio.getMax()) {
            return;
        }
        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING
                ||mLastPlaybackState.getState() == PlaybackStateCompat.STATE_BUFFERING) {
            // Calculate the elapsed time between the last position update and now and unless
            // paused, we can assume (delta * speed) + current position is approximately the
            // latest position. This ensure that we do not repeatedly call the getPlaybackState()
            // on MediaControllerCompat.
            long timeDelta = SystemClock.elapsedRealtime() -
                    mLastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
        }
        mTextLeftTime.setText(Utils.formatTime((int) currentPosition));
        mSeekBarAudio.setProgress((int) currentPosition);
    }

    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        mSeekBarAudio.setMax(duration);
        mTextRightTime.setText(Utils.formatTime(duration));
    }

    @Override
    public void onChooseMedia(String mediaID) {
        Log.d("VVV","onChooseMedia: "+mediaID);
        setViewMusic(mediaID ,PanelState.EXPANDED );
    }

}
