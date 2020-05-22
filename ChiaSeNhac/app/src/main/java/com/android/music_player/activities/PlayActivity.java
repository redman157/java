package com.android.music_player.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.android.music_player.CustomViewPager;
import com.android.music_player.R;
import com.android.music_player.adapters.ChangeSongPagerAdapter;
import com.android.music_player.fragments.ChangeSongFragment;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.managers.MusicManager;
import com.android.music_player.media.MediaBrowserConnection;
import com.android.music_player.media.MediaBrowserHelper;
import com.android.music_player.media.MediaBrowserListener;
import com.android.music_player.media.MediaSeekBar;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.DialogUtils;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PlayActivity extends AppCompatActivity
        implements ViewPager.OnPageChangeListener,
        View.OnClickListener, MediaBrowserListener.OnPlayPause, MediaBrowserConnection.OnMediaItem {
    public CustomViewPager mVpMusic;
    private ChangeSongPagerAdapter mAdapter;
    private MusicManager mMusicManager;
    private MediaSeekBar mSeekBarAudio;

    public ImageButton mBtnPlayPause;
    private ImageButton mBtnPrev, mBtnRepeat, mBtnNext,
    mBtnSeeMore, mBtnAbout, mBtnShuffle, mBtnEqualizer;
    private SharedPrefsUtils mSharedPrefsUtils;
    private TextView mTextLeftTime, mTextRightTime;
    private String type, songName;
    private int position;
    private ArrayList<SongModel> mSongs = new ArrayList<>();
    private ArrayList<SongModel> mSongShuffle = new ArrayList<>();
    private boolean receiverRegistered;
    public Intent iSeekBar;
    private Intent iPlayPause;
    public int seekPos, currentMedia , mediaMax;
    private LinearLayout ll_vp_change_music;
    private LinearLayout mLinearSeeMore;
    private boolean isChange, isPlaying, mIsPlaying;
    private boolean isRepeat = false;
    private boolean isMore = false;
    public boolean isShuffle = false;
    private List<MediaBrowserCompat.MediaItem> mediaItemList;
    private Utils mUtils;
    private Toolbar mToolBar;
    private String tag = "BBB";
    private MediaMetadataCompat currentMetadata;
    private MediaBrowserHelper mMediaBrowserHelper;
    private MediaBrowserListener mBrowserListener;
    @Override
    protected void onStart() {

        super.onStart();
        Log.d("MMM", "PlayActivity onStart: enter");
        mMediaBrowserHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSeekBarAudio.disconnectController();
        mMediaBrowserHelper.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mMusicManager = MusicManager.getInstance();
        mMusicManager.setContext(this);
        mSharedPrefsUtils = new SharedPrefsUtils(this);

        initView();
        getBundle();
        //save current song
        setupToolBar();
        assignView();


        MediaBrowserConnection browserConnection = new MediaBrowserConnection(this);
        browserConnection.setSeekBarAudio(mSeekBarAudio);
        browserConnection.setOnMedia(this);
        mMediaBrowserHelper = browserConnection;

        mBrowserListener = new MediaBrowserListener();
        mBrowserListener.setOnPlayPause(this);
        mMediaBrowserHelper.registerCallback(mBrowserListener);

    }

    private void getBundle() {
        Intent intent = getIntent();
        mUtils = new Utils(intent);

        type = mUtils.getString(Constants.INTENT.TYPE,"");
        songName = mUtils.getString(Constants.INTENT.SONG_NAME, "");
        position = mUtils.getInteger(Constants.INTENT.CHOOSE_POS, -1);


        currentMetadata = MusicLibrary.getCurrentMusic(songName);
        mMusicManager.setType(type);
        mSongs = mMusicManager.getListSong(type);
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
                iBackMusic.putExtra(Constants.INTENT.TYPE_MUSIC, type);
                finish();
                startActivity(iBackMusic);
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
        mBtnRepeat = findViewById(R.id.icon_repeat);
        mBtnNext = findViewById(R.id.icon_next);
        mBtnSeeMore = findViewById(R.id.icon_image_More);
        ll_vp_change_music = findViewById(R.id.ll_vp_change_music);
        mSeekBarAudio = findViewById(R.id.sb_Time);
        mSeekBarAudio.setText(mTextLeftTime, mTextRightTime);
        mBtnShuffle = findViewById(R.id.icon_shuffle);
        mBtnAbout = findViewById(R.id.icon_about);
        mVpMusic = findViewById(R.id.vp_change_music);
    }

    private void assignView(){
        mBtnEqualizer.setOnClickListener(this);
        mBtnPlayPause.setOnClickListener(this);
        mBtnPrev.setOnClickListener(this);
        mBtnRepeat.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mBtnSeeMore.setOnClickListener(this);
        mBtnAbout.setOnClickListener(this);
        mBtnShuffle.setOnClickListener(this);

        setupViewPager(songName);
    }


    private void setupViewPager(String songName){
        mVpMusic.setPageTransformer(true, new ChangeSongPagerAdapter.ZoomOutPageTransformer());
        mVpMusic.setPageTransformer(false, new ChangeSongPagerAdapter.DepthPageTransformer());
        mVpMusic.setTranslationX(-1 * mVpMusic.getWidth());

        mAdapter = new ChangeSongPagerAdapter(this,getSupportFragmentManager());
        mAdapter.addData(songName);

        mVpMusic.setAdapter(mAdapter);
        mVpMusic.setCurrentItem(0);
        mVpMusic.addOnPageChangeListener(this);
    }

 /*   private void initData(String songName){
        mAdapter = new ChangeSongPagerAdapter(this,getSupportFragmentManager());
        mAdapter.addData(songName);
    }*/

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset == 0.0 && positionOffsetPixels == 0) {
            mMusicManager.increase(mSongs.get(position));
            Log.d(tag, "PlayActivity --- onPageScrolled: "+position);

            mSharedPrefsUtils.setString(Constants.PREFERENCES.SAVE_ALBUM_ID,
                    mSongs.get(this.position).getAlbumID());
        }
    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
        Log.d(tag, "Play Activity --- onPageSelected: "+position);
        mTextLeftTime.setText("00 : 00");
        mTextRightTime.setText(Utils.formatTime(mSongs.get(position).getTime()));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
                // khi bấm play check play trước đã, trong broad cast check play sẽ play video
                Log.d(tag, "PlayActivity --- type: "+type);

                if (mIsPlaying) {
                    mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
                    mMediaBrowserHelper.getTransportControls().pause();
                } else {
                    mBtnPlayPause.setPressed(true);
                    mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
                    mMediaBrowserHelper.getTransportControls().play();
                }
                break;
            case R.id.icon_next:
                // khi nhấn next truyền intent xuống service -> do bundle là getString về null
               /* mBtnNext.setClickable(true);
                mBtnNext.setImageDrawable(getResources().getDrawable(R.drawable.ic_next_white));

                Log.d(tag, "PlayActivity --- icon_next: " + (position));
                Utils.NextMediaService(PlayActivity.this, type, position);*/
                mMediaBrowserHelper.getTransportControls().skipToNext();
                break;
            case R.id.icon_prev:
               /* mBtnPrev.setClickable(true);
                mBtnPrev.setImageResource(R.drawable.ic_previous_white);

                Log.d(tag, "PlayActivity --- icon_prev: " + (MusicManager.getInstance().getPosition() - 1));
                Utils.PreviousMediaService(PlayActivity.this, type, position);*/

                mMediaBrowserHelper.getTransportControls().skipToPrevious();
                break;
            case R.id.icon_repeat:
                try {
                    if (!isRepeat) {
                        isRepeat = true;
                        setRepeat(true);
//                        Utils.RepeatMediaService(PlayActivity.this, true, type, position);
                        Utils.ToastShort(this, "Turn On Repeat Music");
                    } else {
                        isRepeat = false;
                        setRepeat(false);
                        mBtnRepeat.setImageResource(R.drawable.ic_repeat_white);

                        Utils.ToastShort(PlayActivity.this, "Turn Off Repeat Music");
                    }
                }finally {
                    Utils.RepeatMediaService(PlayActivity.this, isRepeat, type, position);
                }
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
                DialogUtils.showSongsInfo(PlayActivity.this, mSongs.get(position));
                break;
            case R.id.icon_shuffle:


              /*  if (!isShuffle){
                    isShuffle = true;
                    setShuffle(true);
                    Toast.makeText(this, "Turn On Shuffle Music", Toast.LENGTH_SHORT).show();
                    Utils.ShuffleMediaService(PlayActivity.this,true,type, position);
                }else {
                    isShuffle = false;
                    setShuffle(false);
                    Utils.ToastShort(PlayActivity.this,"Turn Off Shuffle Music");
                    Utils.ShuffleMediaService(PlayActivity.this,false, mMusicManager.getType(), position);
                }*/
                break;
        }
    }

    private void setRepeat(boolean isRepeat) {
        if (isRepeat) {
//            MusicManager.getInstance().setPosition(position);
            mBtnRepeat.setImageResource(R.drawable.ic_repeat_blue);
            mBtnShuffle.setEnabled(false);

        }else {
            mBtnRepeat.setImageResource(R.drawable.ic_repeat_white);
            mBtnShuffle.setEnabled(true);
        }
    }

    private void setShuffle(boolean isShuffle) {
        if (isShuffle) {
            mBtnShuffle.setImageResource(R.drawable.app_shuffle_blue);
            // set type hiện tại là trộn bài
            type = Constants.VALUE.SHUFFLE;
            // set mảng trộn
            mSongShuffle = MusicManager.getInstance().shuffleSongs(mSongs);
            mMusicManager.setShuffleSongs(mSongShuffle);
            ChangeSongFragment.newInstance(mSongShuffle);

            mAdapter.addData(mSongShuffle);
            mAdapter.notifyDataSetChanged();
        }else {
            mBtnShuffle.setImageResource(R.drawable.app_shuffle_white);
            // set type hiện tại là trở về ban đầu
            type = mMusicManager.getType();
            mSongs = mMusicManager.getListSong();
            ChangeSongFragment.newInstance(null);
            mAdapter.addData(mSongs);
            mAdapter.notifyDataSetChanged();
        }
        mSeekBarAudio.setProgress(0);
        mTextLeftTime.setText("00 : 00");
    }

    @Override
    public void onCheck(boolean isPlay) {
        this.mIsPlaying = isPlay;
        Utils.UpdateButtonPlay(mBtnPlayPause, isPlay);
    }

    @Override
    public void onMedia(List<MediaBrowserCompat.MediaItem> children) {
        mediaItemList = children;
    }
}
