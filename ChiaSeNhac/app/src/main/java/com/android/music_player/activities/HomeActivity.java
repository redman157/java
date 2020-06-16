package com.android.music_player.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
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

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.music_player.BaseActivity;
import com.android.music_player.R;
import com.android.music_player.fragments.AllMusicFragment;
import com.android.music_player.fragments.HomeFragment;
import com.android.music_player.interfaces.OnChangeListener;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.media.MediaBrowserConnection;
import com.android.music_player.media.MediaBrowserHelper;
import com.android.music_player.media.MediaBrowserListener;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageHelper;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.utils.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;


public class HomeActivity extends BaseActivity implements MediaBrowserConnection.OnMediaController,View.OnClickListener,
        MediaBrowserListener.OnChangeMusicListener, SlidingUpPanelLayout.PanelSlideListener, OnChangeListener {
    private String tag = "BBB";
    private RelativeLayout mViewControlMedia;
    private LinearLayout mViewPanelMedia, mLayoutSeeMore, mLayoutControlSong, mLlChangeMusic;
    private View mLayoutMedia, mLayoutState;
    public ImageView mImgAlbumArt, mImgChangeMusic, mImgBack;
    public Button mBtnTitle, mBtnHome, mBtnLibrary;
    private MediaManager mMediaManager;
    public ImageButton mBtnPlayPauseMedia , mBtnPlayPauseMusic;
    private ImageButton mBtnPrev, mBtnRepeat, mBtnNext, mBtnSetTime,
            mBtnSeeMore, mBtnAbout, mBtnEqualizer, mBtnFavorite;
    public SeekBar mSeekBarAudio;
    private TextView mTextLeftTime, mTextRightTime, mTextTitleMedia, mTextArtistMedia
            , mTextTitleMusic, mTextArtistMusic , mTextAlbumMusic;
    private ImageView mImgViewQueue, mImgAddToPlayList, mImgChangeMedia;

    public SlidingUpPanelLayout mSlidingUpPanelLayout;
    public Toolbar mToolBar;

    public  FrameLayout mLayoutPlaceHolder;
    private AppBarLayout mAppBarLayout;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean isPlaying = false;
    public String type;
    public MediaBrowserConnection browserConnection;
    public MediaBrowserHelper mMediaBrowserHelper;
    private MediaBrowserListener mBrowserListener;
    private SharedPrefsUtils mSharedPrefsUtils;
    private int step = 0;
    private float alpha = 0 ;
    private boolean isMore, isSetup;
    private String nameChoose;

    private int mItemToOpenWhenDrawerCloses = -1;
    public  final String HOME_FRAGMENT_TAG = "fragment_home";
    public  final String ALL_MUSIC_FRAGMENT_TAG = "fragment_all_music";
    @Override
    public void initManager() {
        mSharedPrefsUtils = new SharedPrefsUtils(this);
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(this);
        ImageHelper.getInstance(this);
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
        initializeFromParams(savedInstanceState, getIntent());
        initView();

//        setupToolbar();
        assignView();
    }

    protected void initializeFromParams(Bundle savedInstanceState, Intent intent) {
        String mediaId = null;
        // check if we were started from a "Play XYZ" voice search. If so, we save the extras
        // (which contain the query details) in a parameter, so we can reuse it later, when the
        // MediaSession is connected.

        if (savedInstanceState == null) {
            // If there is a saved media ID, use it

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_placeholder ,
                            AllMusicFragment.newInstance(),
                            HOME_FRAGMENT_TAG)
                    .add(R.id.fl_placeholder ,
                            HomeFragment.newInstance(this),
                            HOME_FRAGMENT_TAG)
                    .setCustomAnimations(   R.animator.slide_in_from_right, R.animator.slide_out_to_left,
                            R.animator.slide_in_from_left, R.animator.slide_out_to_right)
                    .commit();
        }else {
//            mediaId = savedInstanceState.getString(SAVED_MEDIA_ID);
        }

//        navigateToBrowser(mediaId);
    }

    private void setMode(int repeat) {
        switch (repeat){
            case 0:
                mBtnRepeat.setImageResource(R.drawable.app_repeat_active);
                this.getController().getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
                break;
            case 1:
                mBtnRepeat.setImageResource(R.drawable.app_repeat);
                this.getController().getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
                break;
            case 2:
                mBtnRepeat.setImageResource(R.drawable.app_shuffle_white);
                this.getController().getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
                break;
            case 3:
                mBtnRepeat.setImageResource(R.drawable.app_shuffle_black);
                this.getController().getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
                break;
        }
    }
    
    public void setPlayMedia(String songName){
        MediaMetadataCompat metadataCompat = MusicLibrary.getMetadata(this,
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
            return;
        }
        this.nameChoose = songName;
        MediaMetadataCompat metadataCompat = MusicLibrary.getMetadata(this,
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

        // linear button state

        mLayoutState= mLayoutMedia.findViewById(R.id.layout_state_home);
        mBtnHome = mLayoutMedia.findViewById(R.id.btn_home);
        mBtnHome.setTextColor(R.color.red);
        mBtnLibrary = mLayoutMedia.findViewById(R.id.btn_library);
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
        mBtnHome.setOnClickListener(this);
        mBtnLibrary.setOnClickListener(this);
        Utils.UpdateButtonPlay(mBtnPlayPauseMedia, isPlaying);

        mSeekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getController().getTransportControls().seekTo(seekBar.getProgress());
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
                                HOME_FRAGMENT_TAG)

                        .setCustomAnimations(   R.animator.slide_in_from_right, R.animator.slide_out_to_left,
                                R.animator.slide_in_from_left, R.animator.slide_out_to_right)
                        .commit();
                break;
            case R.id.btn_library:
                mBtnHome.setEnabled(true);
                mBtnLibrary.setEnabled(false);
                mBtnHome.setTextColor(R.color.black);
                mBtnLibrary.setTextColor(R.color.red);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_placeholder ,
                                AllMusicFragment.newInstance(),
                                HOME_FRAGMENT_TAG)

                        .setCustomAnimations(   R.animator.slide_in_from_right, R.animator.slide_out_to_left,
                                R.animator.slide_in_from_left, R.animator.slide_out_to_right)
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
            case R.id.icon_change_mode:
                step = step + 1;
                if (step == 4){
                    step = 0;
                }
                setMode(step);
                break;
            case R.id.icon_prev:
                this.getController().getTransportControls().skipToPrevious();

                break;
            case R.id.icon_play:

            case R.id.imbt_Play_media:
                if (isPlaying){
                    this.getController().getTransportControls().pause();
                    stopSeekBarUpdate();
                }else {
                    this.getController().getTransportControls().playFromMediaId(nameChoose, null);
                }
                break;
            case R.id.icon_next:
                this.getController().getTransportControls().skipToNext();

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

    /*@Override
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
    }*/

    @Override
    public void onStateChange(boolean isPlay, PlaybackStateCompat state) {
        // compare status play don't update button play
//        Log.d("CCC", "HomeActivity --- PlaybackStateCompat: "+state.getPosition()+ " --- isplay: "+isPlay);
        if (isPlaying != isPlay){
            Utils.UpdateButtonPlay(mBtnPlayPauseMedia, isPlay);
            Utils.UpdateButtonPlay(mBtnPlayPauseMusic, isPlay);
        }

        this.isPlaying = isPlay;
        mLastPlaybackState = state;
        if (isPlay &&
                (state.getState() == PlaybackStateCompat.STATE_PLAYING || state.getState() == PlaybackStateCompat.STATE_BUFFERING)) {
            Log.d("CCC", "HomeActivity --- PlaybackStateCompat: "+state.getPosition()+ " --- isplay: "+isPlay);
            scheduleSeekBarUpdate();
        }

    }

    @Override
    public void onMediaMetadata(MediaMetadataCompat mediaMetadata) {
        Log.d("CCC",
                "HomeActivity --- onMediaMetadata: "+mediaMetadata.getString(Constants.METADATA.Title));
        updateDuration(mediaMetadata);
        if (mSlidingUpPanelLayout.getPanelState() == PanelState.EXPANDED){
            setViewMusic(mediaMetadata.getString(Constants.METADATA.Title) , PanelState.EXPANDED);
        }else if (mSlidingUpPanelLayout.getPanelState() == PanelState.COLLAPSED){
            setViewMusic(mediaMetadata.getString(Constants.METADATA.Title) , PanelState.COLLAPSED);
        }

    }

    @Override
    public void onComplete(boolean isNext) {
        if (isNext){
            onMediaMetadata(mMediaBrowserHelper.getMetadata());

        }
    }

    // Panel change state
    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        Log.d("UUU", "onPanelSlide : "+slideOffset);
        alpha = slideOffset;
        if (slideOffset > 0.3){
            mLayoutState.setVisibility(View.GONE);
        }else {
            mLayoutState.setAlpha(slideOffset);
        }
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
                mLayoutState.setVisibility(View.VISIBLE);
                break;
            case COLLAPSED:
                mLayoutState.setAlpha(1);
                mLayoutState.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onMusicID(String nameMusic) {
        setViewMusic(nameMusic, PanelState.EXPANDED);
        mMediaManager.getMediaBrowserConnection().setAutoPlay(nameMusic, true);
    }


    @Override
    public void onController(MediaControllerCompat mediaController) {
        if (mMediaBrowserHelper.isConnect()){
            Log.d("ZZZ", mediaController == null ? "null" : "khac null" );
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
}
