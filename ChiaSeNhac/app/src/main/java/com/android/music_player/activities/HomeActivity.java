package com.android.music_player.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.music_player.R;
import com.android.music_player.adapters.ChooseMusicAdapter;
import com.android.music_player.adapters.PlayListAdapter;
import com.android.music_player.fragments.EqualizerFragment;
import com.android.music_player.fragments.HomeFragment;
import com.android.music_player.fragments.LibraryFragment;
import com.android.music_player.interfaces.DialogType;
import com.android.music_player.interfaces.OnConnectMediaId;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.media.BrowserHelper;
import com.android.music_player.media.MediaBrowserListener;
import com.android.music_player.utils.BottomSheetHelper;
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


public class HomeActivity extends BaseActivity implements View.OnClickListener,
        MediaBrowserListener.OnChangeMusicListener, SlidingUpPanelLayout.PanelSlideListener,
        OnConnectMediaId, PlayListAdapter.OnClickItemListener {
    private RelativeLayout mViewControlMedia, mViewMusic;
    private LinearLayout mViewPanelMedia, mLayoutSeeMore, mLayoutControlSong, mLlChangeMusic;
    private View mLayoutMedia, mLayoutState;
    public TextView mTextArtistMedia, mTextTitleMedia;
    public ImageView mImgAlbumArt, mImgChangeMusic, mImgBack;
    private LinearLayout mLinearTop, mBtnHome,mBtnLibrary, mLinearNext;
    private MediaManager mMediaManager;
    public PlayPauseView mBtnPlayPauseMedia , mBtnPlayPausePanel;
    private ImageView mBtnPrev, mBtnRepeat, mBtnNext, mBtnSetTime,
            mBtnSeeMore, mBtnAbout, mBtnEqualizer, mBtnFavorite, mBtnShuffle;
    public SeekBar mSeekBarAudio;
    private TextView mTextLeftTime, mTextRightTime, mTextTitleMusic, mTextArtistMusic , mTextAlbumMusic, mTextHome, mTextLibrary;
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
    private ChooseMusicAdapter mChooseMusicAdapter;
    public final static String FRAGMENT_TAG = "fragment_tag";
    public BottomSheetHelper bottomSheetHelper;

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

    public void setViewMusic(String mediaID, PanelState state){

        this.nameChoose = mediaID;
        MediaMetadataCompat metadataCompat = mMediaManager.getMetadata(this,
                mediaID);
        mTextArtistMedia.setText(metadataCompat.getString(Constants.METADATA.Artist));
        mTextTitleMedia.setText(metadataCompat.getString(Constants.METADATA.Title));
        ImageHelper.getInstance(this).getSmallImageByPicasso(String.valueOf(MusicLibrary.getAlbumRes(mediaID)),
                mImgAlbumArt);
        setPlayMedia(mediaID);

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
        mLayoutSeeMore = mLayoutMedia.findViewById(R.id.linear_see_more);
        mLlChangeMusic = mLayoutMedia.findViewById(R.id.linear_change_music);
        mViewControlMedia = mLayoutMedia.findViewById(R.id.relative_change_state);
        mViewPanelMedia = mLayoutMedia.findViewById(R.id.ll_play_media);
        mLayoutControlSong = mLayoutMedia.findViewById(R.id.linear_control_song);

        // status play media
        mImgAlbumArt = mLayoutMedia.findViewById(R.id.img_albumArt_media);
        mTextTitleMedia = mLayoutMedia.findViewById(R.id.text_title_media);
        mTextArtistMedia = mLayoutMedia.findViewById(R.id.text_artists_media);
        mBtnPlayPauseMedia = mLayoutMedia.findViewById(R.id.imbt_play_media);

        // thông tin bài hát
        mImgBack = mLayoutMedia.findViewById(R.id.image_close_panel);
        mTextTitleMusic = mLayoutMedia.findViewById(R.id.text_title);
        mTextArtistMusic = mLayoutMedia.findViewById(R.id.text_artist);
        mTextAlbumMusic = mLayoutMedia.findViewById(R.id.text_album);
        mImgViewQueue = mLayoutMedia.findViewById(R.id.image_view_queue);
        mImgAddToPlayList = mLayoutMedia.findViewById(R.id.image_add_to_playlist);
        mImgChangeMedia = mLayoutMedia.findViewById(R.id.image_album_art);

        // linear control song

        mLinearNext = mLayoutMedia.findViewById(R.id.linear_next);
        mBtnAbout = mLayoutMedia.findViewById(R.id.image_about);
        mBtnSetTime = mLayoutMedia.findViewById(R.id.image_set_time);
        mBtnEqualizer = mLayoutMedia.findViewById(R.id.image_equalizer);
        mBtnFavorite = mLayoutMedia.findViewById(R.id.image_favorite);
        mImgBack = mLayoutMedia.findViewById(R.id.image_close_panel);

        // linear seekbar time
        mTextLeftTime = mLayoutMedia.findViewById(R.id.text_start);
        mSeekBarAudio = mLayoutMedia.findViewById(R.id.seekbar_timer);
        mTextRightTime = mLayoutMedia.findViewById(R.id.text_end);

        // linear play media
        mBtnRepeat = mLayoutMedia.findViewById(R.id.image_replay);
        mBtnPrev = mLayoutMedia.findViewById(R.id.image_prev);
        mBtnPlayPausePanel = mLayoutMedia.findViewById(R.id.image_play_pause);
        mBtnNext = mLayoutMedia.findViewById(R.id.image_next);
        mBtnSeeMore = mLayoutMedia.findViewById(R.id.image_more);
        mBtnShuffle = mLayoutMedia.findViewById(R.id.image_shuffle);

        // linear button state
        mLayoutState= mLayoutMedia.findViewById(R.id.layout_panel_home);
//        mLinearTop = findViewById(R.id.linear_top);

        mBtnHome = mLayoutMedia.findViewById(R.id.btn_home);
        mTextHome = mLayoutMedia.findViewById(R.id.text_home);
        mBtnLibrary = mLayoutMedia.findViewById(R.id.btn_library);
        mTextLibrary = mLayoutMedia.findViewById(R.id.text_library);

        mBtnHome.setEnabled(false);
        mTextHome.setTextColor(getColor(R.color.red));
        mViewMusic = mLayoutState.findViewById(R.id.relative_info_music);
    }

    private void assignView(){
        mSlidingUpPanelLayout.addPanelSlideListener(this);
        mSlidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        mLayoutSeeMore.setOnClickListener(this);

        mBtnFavorite.setOnClickListener(this);
        mViewMusic.setOnClickListener(this);
        mBtnSetTime.setOnClickListener(this);
        mBtnEqualizer.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mBtnRepeat.setOnClickListener(this);
        mBtnPrev.setOnClickListener(this);

        mLinearNext.setOnClickListener(this);
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

        if (mChooseMusicAdapter == null) {
            mChooseMusicAdapter = new ChooseMusicAdapter(this, getControllerActivity().getQueue());
            mChooseMusicAdapter.setOnConnectMediaIdListener(this);
            mChooseMusicAdapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_home:
                mTextHome.setTextColor(getColor(R.color.red));
                mTextLibrary.setTextColor(getColor(R.color.accentColor));
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
                mTextHome.setTextColor(getColor(R.color.accentColor));
                mTextLibrary.setTextColor(getColor(R.color.red));
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_placeholder ,
                                LibraryFragment.newInstance(),
                                FRAGMENT_TAG)
                        .commit();
                break;
            case R.id.image_about:
                DialogHelper.showAboutMusic(this,
                        MusicLibrary.model.get(getControllerActivity().getMetadata().getString(Constants.METADATA.Title)));
                break;
            case R.id.image_set_time:
                break;
            case R.id.image_equalizer:
                EqualizerFragment fragment = EqualizerFragment.newInstance();
                fragment.show(getSupportFragmentManager(), FRAGMENT_TAG);
                break;
            case R.id.image_favorite:
                bottomSheetHelper =
                        new BottomSheetHelper(DialogType.CHANGE_MUSIC);
//                dialog.changeMusic(mMediaManager.getCurrentMusic());
                bottomSheetHelper.show(getSupportFragmentManager(), FRAGMENT_TAG);
                break;
            case R.id.image_shuffle:
                if (isShuffle) {
                    mBtnShuffle.setImageResource(R.drawable.app_shuffle_unactive);
                    getControllerActivity().getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
                    isShuffle = false;
                } else{
                    mBtnShuffle.setImageResource(R.drawable.app_shuffle_active);
                    getControllerActivity().getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
                    isShuffle = true;
                }
                break;
            case R.id.image_replay:
                if (isReplay){
                    mBtnRepeat.setImageResource(R.drawable.app_repeat_active);
                    getControllerActivity().getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
                    isReplay = false;
                }else {
                    mBtnRepeat.setImageResource(R.drawable.app_repeat_unactive);
                    getControllerActivity().getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
                    isReplay = true;
                }
                break;
            case R.id.image_prev:
                getControllerActivity().getTransportControls().skipToPrevious();

                break;
            case R.id.image_play_pause:
            case R.id.imbt_play_media:
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
            case R.id.image_next:

            case R.id.linear_next:
                getControllerActivity().getTransportControls().skipToNext();
                break;
            case R.id.image_more:
                if (!isMore){
                    mLayoutSeeMore.setAlpha(1);
                    mBtnSeeMore.setImageResource(R.drawable.ic_menu_dot_white);
                    isMore = true;
                    Animation fadeIn = AnimationUtils.loadAnimation(HomeActivity.this,R.anim.fadein);
                    mLayoutSeeMore.setAnimation(fadeIn);
                    mLayoutSeeMore.setVisibility(View.VISIBLE);
                }else {
                    mBtnSeeMore.setImageResource(R.drawable.ic_menu_dot_black);
                    isMore = false;
                    mLayoutSeeMore.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mLayoutSeeMore.setVisibility(View.GONE);
                        }
                    });
                }
                break;
            case R.id.image_view_queue:

                bottomSheetHelper =
                        new BottomSheetHelper(DialogType.CHOOSE_MUSIC,
                                mChooseMusicAdapter);
                bottomSheetHelper.show(getSupportFragmentManager(), FRAGMENT_TAG);
                break;
            case R.id.image_add_to_playlist:
                bottomSheetHelper = new BottomSheetHelper(DialogType.ADD_MUSIC_TO_PLAYLIST,
                        this);
                bottomSheetHelper.show(getSupportFragmentManager(), FRAGMENT_TAG);
                break;
            case R.id.relative_info_music:
                if (mSlidingUpPanelLayout != null &&
                        (mSlidingUpPanelLayout.getPanelState() == PanelState.COLLAPSED)){
                    mSlidingUpPanelLayout.setPanelState(PanelState.EXPANDED);
                }

                break;
            case R.id.image_close_panel:
                if (mSlidingUpPanelLayout != null &&
                        (mSlidingUpPanelLayout.getPanelState() == PanelState.EXPANDED || mSlidingUpPanelLayout.getPanelState() == PanelState.ANCHORED)) {
                    mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
                }
                break;
            case R.id.menu_search:
                Intent iSearch = new Intent(this, SearchActivity.class);
                startActivity(iSearch);
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
//        mLayoutState.setAlpha(alpha);
        if (slideOffset < 0.3f){
            mLayoutState.setVisibility(View.VISIBLE);
        }else {
            mLayoutState.setVisibility(View.GONE);
        }
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
                break;
            case COLLAPSED:

                mLayoutState.setAlpha(1);

                break;
            case DRAGGING:
                if (previousState == PanelState.COLLAPSED){
                    mLayoutState.setAlpha(0);
                    mLayoutState.setVisibility(View.GONE);
                } else if (previousState == PanelState.EXPANDED) {
                    mLayoutState.setAlpha(0);
                    mLayoutState.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

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
    public void onChangeMediaId(String mediaID) {
        setViewMusic(mediaID ,PanelState.EXPANDED );
        mMediaManager.getMediaBrowserConnection().getTransportControls().prepareFromMediaId(mediaID, null);
        if (bottomSheetHelper!= null && bottomSheetHelper.getShowsDialog()) {
            bottomSheetHelper.dismiss();
        }
     /*   setViewMusic(mediaID, PanelState.EXPANDED);
        mMediaManager.getMediaBrowserConnection().getTransportControls().prepareFromMediaId(mediaID, null);*/
        Log.d("XXX","dialog --- onChangeMediaId: "+mediaID);
    }

    @Override
    public void onAddMusicToPlayList(String namePlayList) {
        bottomSheetHelper.dismiss();
        if (mMediaManager.addMusicToPlayList(namePlayList, mMediaManager.getCurrentMusic())){
            Utils.ToastShort(this,"Đã Add Bài: "+ mMediaManager.getCurrentMusic());
        }else {
            Utils.ToastShort(this,"Add Bài: "+ mMediaManager.getCurrentMusic());
        }
        Log.d("SSS", "PlayListAdapter.OnClickItemListener: "+ namePlayList);
    }
}
