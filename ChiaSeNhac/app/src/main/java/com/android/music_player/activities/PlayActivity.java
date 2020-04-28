package com.android.music_player.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.android.music_player.BaseActivity;
import com.android.music_player.CustomViewPager;
import com.android.music_player.R;
import com.android.music_player.adapters.ChangeMusicPagerAdapter;
import com.android.music_player.fragments.ChangeMusicFragment;
import com.android.music_player.managers.SongManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.services.MediaPlayerService;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.DialogUtils;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.utils.Utils;

import java.util.ArrayList;

public class PlayActivity extends BaseActivity
        implements ViewPager.OnPageChangeListener, View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {
    public CustomViewPager mVpMusic;
    private ChangeMusicPagerAdapter mAdapter;
    private SongManager mSongManager;
    private SeekBar mSeekBarTime;

    public ImageButton mBtnPlayPause;
    private ImageButton mBtnPrev, mBtnRepeat, mBtnNext,
    mBtnSeeMore, mBtnAbout, mBtnShuffle, mBtnEqualizer;
    private SharedPrefsUtils mSharedPrefsUtils;
    private TextView mTextLeftTime, mTextRightTime;
    private String type;
    private int position;
    private ArrayList<SongModel> mSongs = new ArrayList<>();
    private boolean receiverRegistered;
    public Intent iSeekBar;
    private Intent iPlayPause;
    public int seekPos, currentMedia , mediaMax;
    private LinearLayout ll_vp_change_music;
    private LinearLayout mLinearSeeMore;
    private boolean isChange, isPlaying;
    private boolean isRepeat = false;
    private boolean isMore = false;
    private boolean isShuffle = false;
    private boolean isContinue;

    private Toolbar mToolBar;
    private String tag = "BBB";
    @Override
    protected void onStart() {

        super.onStart();
        iSeekBar = new Intent(Constants.ACTION.BROADCAST_SEEK_BAR);
        iPlayPause = new Intent(Constants.ACTION.BROADCAST_PLAY_PAUSE);
        if (!receiverRegistered){
            registerReceiver(brSeekBar, new IntentFilter(Constants.ACTION.BROADCAST_SEEK_BAR));
            registerReceiver(brPlayPauseActivity, new IntentFilter(Constants.ACTION.BROADCAST_PLAY_PAUSE));
            registerReceiver(brIsPlayService, new IntentFilter(Constants.ACTION.IS_PLAY));
            registerReceiver(brPlayNew, new IntentFilter(Constants.ACTION.BROADCAST_PLAY_NEW_AUDIO));
            receiverRegistered = true;
        }
        mSongs = mSongManager.getCurrentSongs();
//        Utils.ChangeSongService(PlayActivity.this, mSongs);

        isContinue = getIntent().getBooleanExtra(Constants.INTENT.SONG_CONTINUE,false);
        if (MediaPlayerService.mMediaPlayer!= null) {
            if (!MediaPlayerService.mMediaPlayer.isPlaying()) {
                mBtnPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_play_light));
                if (mVpMusic != null){
                    mVpMusic.setCurrentItem(SongManager.getInstance().getPositionCurrent());
                }
            } else {
                mBtnPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_pause_light));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            this.unregisterReceiver(brPlayPauseActivity);
            this.unregisterReceiver(brSeekBar);
            this.unregisterReceiver(brIsPlayService);
            this.unregisterReceiver(brPlayNew);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        mSongManager = SongManager.getInstance();
        mSongManager.setContext(this);
        mSharedPrefsUtils = new SharedPrefsUtils(this);

        type = this.getIntent().getStringExtra(Constants.INTENT.TYPE);
        position = this.getIntent().getIntExtra(Constants.INTENT.POSITION, 0);
        isContinue = getIntent().getBooleanExtra(Constants.INTENT.SONG_CONTINUE,false);

        //save current song
        mSongManager.setTypeCurrent(type);
        mSongs = mSongManager.getCurrentSongs(type);


        initView();
//        Utils.ChangeSongService(PlayActivity.this, mSongs);

        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_close_black_24dp));

        assignView();

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
                Intent iBackMusic = new Intent(this, HomeActivity.class);
//                finish();
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
        mVpMusic = findViewById(R.id.vp_change_music);

        mVpMusic.setPageTransformer(true, new ChangeMusicPagerAdapter.ZoomOutPageTransformer());
        mVpMusic.setPageTransformer(false, new ChangeMusicPagerAdapter.DepthPageTransformer());
        mVpMusic.setTranslationX(-1 * mVpMusic.getWidth() * position);
        mSeekBarTime = findViewById(R.id.sb_leftTime);
        mBtnShuffle = findViewById(R.id.icon_shuffle);
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
        mBtnShuffle.setOnClickListener(this);
        mSeekBarTime.setOnSeekBarChangeListener(this);
        mSeekBarTime.setMax(mSongs.get(SongManager.getInstance().getPositionCurrent()).getTime());

        initData(mSongs);

        setupViewPager(position);
    }

    private void setupViewPager(int position){
        mVpMusic.setAdapter(mAdapter);
        mVpMusic.setCurrentItem(position);
        mVpMusic.addOnPageChangeListener(this);
    }

    private void initData(ArrayList<SongModel> mSongs){
        mAdapter = new ChangeMusicPagerAdapter(this,getSupportFragmentManager());
        mAdapter.addData(mSongs);

        mTextRightTime.setText(Utils.formatTime(mSongs.get(mSongManager.getPositionCurrent()).getTime()));

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset == 0.0 && positionOffsetPixels == 0) {

            mSongManager.increase(mSongs.get(position));
            Log.d(tag, "PlayActivity --- onPageScrolled: "+position);
            SongManager.getInstance().setPositionCurrent(this.position);
            mSharedPrefsUtils.setString(Constants.PREFERENCES.SaveAlbumID,
                    mSongs.get(this.position).getAlbumID());
            seekPos = 0;
            mTextLeftTime.setText("00 : 00");
            mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
            Utils.isPlayMediaService(this);
        }
    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
        Log.d(tag, "Play Activity --- onPageSelected: "+position);
        mSeekBarTime.setProgress(0);
        mSeekBarTime.setMax(mSongs.get(position).getTime());
        mTextLeftTime.setText("00 : 00");
        mTextRightTime.setText(Utils.formatTime(mSongs.get(position).getTime()));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    // -- Broadcast Receiver to updateData position of seekbar from service --
    private BroadcastReceiver brSeekBar = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {

            updateUI(serviceIntent);
        }
    };

    private BroadcastReceiver brPlayPauseActivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            // activity gửi broadcast từ service >> activity
            boolean isPlayNotification =
                    serviceIntent.getBooleanExtra(Constants.INTENT.IS_PLAY_MEDIA_NOTIFICATION,
                            false);

            if (isPlayNotification) {
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
                    Log.d(tag, "PlayActivity --- brIsPlayService:" + true);
                    mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
                    isPlaying = true;
                    Utils.PauseMediaService(PlayActivity.this);
                } else {
                    Log.d(tag, "PlayActivity --- brIsPlayService:" + false);
                    mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
                    isPlaying = false;
                    Utils.PlayMediaService(PlayActivity.this);
                }
        }
    };

    private BroadcastReceiver brPlayNew = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {

            if (!isRepeat) {
                String actionNoti = intent.getStringExtra(Constants.INTENT.NOTI_SERVICE_TO_ACTIVITY);
//                mSharedPrefsUtils.setInteger(Constants.PREFERENCES.POSITION_SONG, 0);


                new CountDownTimer(500, 1000) {
                    @Override
                    public void onTick(long l) {
                        mBtnNext.setEnabled(false);
                        mBtnPrev.setEnabled(false);
                        mBtnPlayPause.setEnabled(false);
                    }

                    @Override
                    public void onFinish() {
                        mBtnNext.setEnabled(true);
                        mBtnPrev.setEnabled(true);
                        mBtnPlayPause.setEnabled(true);
                        int pos = intent.getIntExtra(Constants.INTENT.POSITION, -1);
                        mVpMusic.setCurrentItem(pos, true);
                        mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
                    }
                }.start();
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.icon_play:
                // khi bấm play check play trước đã, trong broad cast check play sẽ play video
                SongManager.getInstance().setPositionCurrent(position);
                Utils.isPlayMediaService(PlayActivity.this, mSongs);
                break;
            case R.id.icon_next:
                // khi nhấn next truyền intent xuống service -> do bundle là getString về null
                mBtnNext.setClickable(true);
                mBtnNext.setImageDrawable(getResources().getDrawable(R.drawable.ic_next_white));

                Log.d(tag, "PlayActivity --- icon_next: " + (position));
                Utils.NextMediaService(PlayActivity.this);

                break;
            case R.id.icon_prev:
                mBtnPrev.setClickable(true);
                mBtnPrev.setImageResource(R.drawable.ic_previous_white);

                Log.d(tag, "PlayActivity --- icon_prev: " + (SongManager.getInstance().getPositionCurrent() - 1));
                Utils.PreviousMediaService(PlayActivity.this);
                break;
            case R.id.icon_repeat:
                if (!isRepeat){
                    mBtnRepeat.setImageResource(R.drawable.ic_repeat_blue);
                    SongManager.getInstance().setPositionCurrent(position);
                    isRepeat = true;
                    mBtnShuffle.setEnabled(false);
                    Utils.RepeatMediaService(PlayActivity.this, true);
                    Toast.makeText(this, "Turn On Repeat Music", Toast.LENGTH_SHORT).show();
                    unregisterReceiver(brPlayNew);
                }else {
                    mBtnRepeat.setImageResource(R.drawable.ic_repeat_white);
                    isRepeat = false;
                    mBtnShuffle.setEnabled(true);
                    Utils.RepeatMediaService(PlayActivity.this, false);
                    registerReceiver(brPlayNew,
                            new IntentFilter(Constants.ACTION.BROADCAST_PLAY_NEW_AUDIO));
                    Utils.ToastShort(PlayActivity.this,"Turn Off Repeat Music");
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
                if (!isShuffle){
                    isShuffle = true;
                    mSharedPrefsUtils.setInteger(Constants.PREFERENCES.POSITION_MAIN,
                            position);

                    ArrayList<SongModel> songShuffle = SongManager.getInstance().shuffleSongs();
                    ChangeMusicFragment.newInstance(songShuffle);
                    mBtnShuffle.setImageResource(R.drawable.app_shuffle_blue);
                    Toast.makeText(this, "Turn On Shuffle Music", Toast.LENGTH_SHORT).show();
                    mSongs = songShuffle;
                    mAdapter.addData(mSongs);
                    mAdapter.notifyDataSetChanged();
                    Utils.ShuffleMediaService(PlayActivity.this, songShuffle, true);
                }else {
                    isShuffle = false;
                    mBtnShuffle.setImageResource(R.drawable.app_shuffle_white);
                    ChangeMusicFragment.newInstance(null);

                    mSongs = mSongManager.getCurrentSongs();
                    mAdapter.addData(mSongs);
                    mAdapter.notifyDataSetChanged();
                    Utils.ToastShort(PlayActivity.this,"Turn Off Shuffle Music");
                    Utils.ShuffleMediaService(PlayActivity.this, mSongs, false);
                }
                break;
        }
    }

    private void updateUI(Intent serviceIntent) {
        currentMedia = serviceIntent.getIntExtra("current_pos", 0);
        mediaMax = serviceIntent.getIntExtra("media_max", 0);
        String songTitle = serviceIntent.getStringExtra("song_title");

        mSeekBarTime.setMax(mediaMax);
        mSeekBarTime.setProgress(currentMedia);
        mTextLeftTime.setText(Utils.formatTime(currentMedia));
        mTextRightTime.setText(Utils.formatTime(mSongs.get(SongManager.getInstance().getPositionCurrent()).getTime()));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        seekBar.setMax(mediaMax);

        if (fromUser){
            seekPos = seekBar.getProgress();
            Log.d("MMM", "PlayActivity --- onProgressChanged: "+seekPos);
            mTextLeftTime.setText(Utils.formatTime(seekPos));
//            mSharedPrefsUtils.setInteger(Constants.PREFERENCES.POSITION_SONG,seekPos);
            Utils.ContinueMediaService(PlayActivity.this, seekPos);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d("MMM", "PlayActivity --- onStartTrackingTouch: "+seekPos);
        if (seekPos != 0) {
            seekBar.setProgress(seekPos);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d("MMM", "PlayActivity --- onStopTrackingTouch: "+seekPos);
        if (seekPos != 0) {
            seekBar.setProgress(seekPos);
        }
    }


}
