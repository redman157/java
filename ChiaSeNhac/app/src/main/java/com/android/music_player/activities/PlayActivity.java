package com.android.music_player.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.music_player.R;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.managers.MusicManager;
import com.android.music_player.media.MediaBrowserConnection;
import com.android.music_player.media.MediaBrowserHelper;
import com.android.music_player.media.MediaBrowserListener;
import com.android.music_player.media.MediaSeekBar;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PlayActivity extends AppCompatActivity implements
        View.OnClickListener, MediaBrowserListener.OnPlayPause {
    private MusicManager mMusicManager;
    public MediaSeekBar mSeekBarAudio;
    public ImageButton mBtnPlayPause;

    private ImageButton mBtnPrev, mBtnRepeat, mBtnNext,
    mBtnSeeMore, mBtnAbout, mBtnEqualizer;
    private TextView mTextLeftTime, mTextRightTime, mTextTitle, mTextArtist, mTextAlbum;
    private ImageView mImgViewQueue, mImgAddToPlayList, mImgChangeMusic;
    private ArrayList<SongModel> mSongs = new ArrayList<>();
    private ArrayList<SongModel> mSongShuffle = new ArrayList<>();
    private LinearLayout ll_vp_change_music;
    private LinearLayout mLinearSeeMore;
    private boolean mIsPlaying;
    private boolean isMore = false;
    private boolean isShuffle = false;
    private List<MediaBrowserCompat.MediaItem> mediaItemList;
    private Utils mUtils;
    private Toolbar mToolBar;
    private String tag = "BBB";
    private MediaMetadataCompat currentMetadata;
    private int step = 0;
    private MediaBrowserHelper mMediaBrowserHelper;
    private MediaBrowserListener mBrowserListener;

    public interface OnMediaID {
        void onMedia(String mediaId);
    }
    private OnMediaID onMediaID;

    public void setOnMedia(OnMediaID onMediaID){
        this.onMediaID = onMediaID;
    }
    @Override
    protected void onStart() {
        super.onStart();

        MusicManager.getInstance().setContext(this);
        MediaBrowserConnection browserConnection =
                MusicManager.getInstance().getMediaBrowserConnection();
        browserConnection.setSeekBarAudio(mSeekBarAudio, mTextLeftTime, mTextRightTime);
        browserConnection.setMediaId(mMusicManager.getMediaId());
        mMediaBrowserHelper = browserConnection;

        mBrowserListener = new MediaBrowserListener();
        mBrowserListener.setOnPlayPause(this);
        mMediaBrowserHelper.registerCallback("PlayActivity", mBrowserListener);

        Log.d("JJJ", "PlayActivity onStart: "+ mMusicManager.getMediaId());

        mMediaBrowserHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("JJJ", "PlayActivity onStop: "+ mMusicManager.getMediaId());
        mSeekBarAudio.disconnectController();
        mMediaBrowserHelper.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mMusicManager = MusicManager.getInstance();
        mMusicManager.setContext(this);

        initView();
        setupToolBar();
        assignView();
    }

    private void setupToolBar() {
        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_close_black_24dp));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent iBackMusic = new Intent(this, SongActivity.class);
                startActivity(iBackMusic);
//                finish();
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                break;
            case R.id.action_drive_mode:
                break;
            case R.id.goto_album:
                break;
            case R.id.goto_artist:
                break;
            case R.id.add_to_playlist:
                break;
            case R.id.info:
                break;
            case R.id.equalizer:
                finish();
                startActivity(new Intent(this, EqualizerActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }

    private void initView() {
        mToolBar = findViewById(R.id.tb_PlayActivity);
        mBtnEqualizer = findViewById(R.id.icon_equalizer);
        mLinearSeeMore = findViewById(R.id.ll_see_more);
        mTextLeftTime = findViewById(R.id.text_leftTime);
        mTextRightTime = findViewById(R.id.text_rightTime);
        mBtnPlayPause = findViewById(R.id.icon_play);

        mBtnPrev = findViewById(R.id.icon_prev);
        mBtnRepeat = findViewById(R.id.icon_change_mode);
        mBtnNext = findViewById(R.id.icon_next);
        mBtnSeeMore = findViewById(R.id.icon_image_More);
        ll_vp_change_music = findViewById(R.id.ll_vp_change_music);
        mSeekBarAudio = findViewById(R.id.sb_Time);


        mTextTitle = findViewById(R.id.item_text_title);
        mTextArtist = findViewById(R.id.item_text_artist);
        mTextAlbum = findViewById(R.id.item_text_album);
        mImgViewQueue = findViewById(R.id.item_img_viewQueue);
        mImgAddToPlayList = findViewById(R.id.item_img_addToPlayListImageView);
        mImgChangeMusic = findViewById(R.id.item_img_ChangeMusic);

        mBtnAbout = findViewById(R.id.icon_about);
    }

    private void assignView(){
        mBtnEqualizer.setOnClickListener(this);
        mBtnPlayPause.setOnClickListener(this);
        mBtnPrev.setOnClickListener(this);
        mBtnRepeat.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mBtnSeeMore.setOnClickListener(this);
        mBtnAbout.setOnClickListener(this);
        mImgViewQueue.setOnClickListener(this);
        mImgAddToPlayList.setOnClickListener(this);
    }

    private void assignData(String mediaId){
        Log.d("UUU","assignData: "+mediaId);
        MediaMetadataCompat metadataCompat = MusicLibrary.getMetadata(PlayActivity.this, mediaId);
        mTextTitle.setText(metadataCompat.getString(Constants.METADATA.Title));
        mTextArtist.setText(metadataCompat.getString(Constants.METADATA.Artist));
        mTextAlbum.setText(metadataCompat.getString(Constants.METADATA.Album));
        ImageUtils.getInstance(this).getImageByPicassoAnimation(String.valueOf(MusicLibrary.getAlbumRes(mediaId)),
                mImgChangeMusic);
    }

    /**
     * Convenience class to collect the click listeners together.
     * <p>
     * In a larger app it's better to split the listeners out or to use your favorite
     * library.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.icon_play:
                if (mIsPlaying) {
//                    Utils.UpdateButtonPlay(mBtnPlayPause, true);
                    mMediaBrowserHelper.getTransportControls().pause();
                } else {
//                    Utils.UpdateButtonPlay(mBtnPlayPause, false);
                    mMediaBrowserHelper.getTransportControls().playFromMediaId(mMusicManager.getMediaId(), null);
                }
                break;
            case R.id.icon_next:
                mMediaBrowserHelper.getTransportControls().skipToNext();
                break;
            case R.id.icon_prev:
                mMediaBrowserHelper.getTransportControls().skipToPrevious();
                break;
            case R.id.icon_change_mode:
                step = step + 1;
                if (step == 4){
                    step = 0;
                }
                setMode(step);
                break;
            case R.id.icon_image_More:
                if (!isMore){
                    mLinearSeeMore.setAlpha(1);
                    mBtnSeeMore.setImageResource(R.drawable.ic_menu_dot_black);
                    isMore = true;
                    Animation fadeIn = AnimationUtils.loadAnimation(PlayActivity.this,R.anim.fadein);
                    mLinearSeeMore.setAnimation(fadeIn);
                    mLinearSeeMore.setVisibility(View.VISIBLE);
                }else {
                    mBtnSeeMore.setImageResource(R.drawable.ic_menu_dot_white);

                    isMore = false;
                    mLinearSeeMore.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mLinearSeeMore.setVisibility(View.GONE);
                        }
                    });
                }
                break;

            case R.id.icon_equalizer:
                startActivity(new Intent(this, EqualizerActivity.class));
                break;

            case R.id.icon_about:
//                DialogUtils.showSongsInfo(PlayActivity.this, mSongs.get(position));
                break;

        }
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

    @Override
    public void onCheck(boolean isPlay, PlaybackStateCompat state) {
        // compare status play don't update button play
        if (mIsPlaying == isPlay){
            return;
        }
        this.mIsPlaying = isPlay;
        Utils.UpdateButtonPlay(mBtnPlayPause, isPlay);

    }

    @Override
    public void onNext(boolean isNext) {
        if (isNext){
            mMediaBrowserHelper.getTransportControls().skipToNext();
        }
    }

    @Override
    public void onMediaMetadata(MediaMetadataCompat mediaMetadata) {
        assignData(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
    }
}
