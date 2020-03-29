package com.droidheat.musicplayer.activities;

import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.droidheat.musicplayer.BaseActivity;
import com.droidheat.musicplayer.ChangeMusic;
import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.OnSongChange;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.services.MediaPlayerService;
import com.droidheat.musicplayer.PlayMusic;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.adapters.ChangeMusicPagerAdapter;
import com.droidheat.musicplayer.fragments.ChangeMusicFragment;
import com.droidheat.musicplayer.manager.SongsManager;
import com.droidheat.musicplayer.models.SongModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

public class PlayActivity extends BaseActivity
        implements ViewPager.OnPageChangeListener, View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, OnSongChange {
    private ViewPager mVpMusic;
    private ChangeMusicPagerAdapter mAdapter;
    private SongsManager mSongManager;
    private SeekBar mSeekBarTime;

    private PlayMusic mPlayMusic;
    public ImageButton mBtnPlayPause;
    private ImageButton mBtnBack, mBtnInfoMusic, mBtnMenu, mBtnPrev, mBtnRepeat, mBtnNext, mBtnFavourite;
    private SharedPrefsManager mSharedPrefsManager;
    private Runnable mRunnable;
    private TextView mTextLeftTime, mTextRightTime;
    private String type;
    private int position;
    private ArrayList<SongModel> MusicType = new ArrayList<>();
    private boolean receiverRegistered;
    public Intent iSeekBar;
    private Intent iPlayPause;
    public   int seekPos;
    private OnSongChange onSongChange;
    private boolean isPlayingMedia;
    private boolean isChange, isPlaying;
    public void setOnChange(OnSongChange onSongChange){
        this.onSongChange = onSongChange;
    }
    @Override
    protected void onStart() {
        super.onStart();
        iSeekBar = new Intent(Constants.ACTION.BROADCAST_SEEK_BAR);
        iPlayPause = new Intent(Constants.ACTION.BROADCAST_PLAY_PAUSE);


        if (!receiverRegistered){
            registerReceiver(brSeekBar, new IntentFilter(Constants.ACTION.BROADCAST_SEEK_BAR));
            registerReceiver(brPlayPauseActivity, new IntentFilter(Constants.ACTION.BROADCAST_PLAY_PAUSE));
            registerReceiver(brCheckPlayService, new IntentFilter(Constants.ACTION.ISPLAY));
            receiverRegistered = true;
        }
        // start service
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            this.unregisterReceiver(brPlayPauseActivity);
            this.unregisterReceiver(brSeekBar);
            this.unregisterReceiver(brCheckPlayService);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mSongManager = SongsManager.getInstance();
        mSongManager.setContext(this);
        mSharedPrefsManager = new SharedPrefsManager();
        mSharedPrefsManager.setContext(this);
        // nhận giá trị postion và type ở home fragment và recently activity
        type = this.getIntent().getStringExtra(Constants.VALUE.TYPE);
        position = this.getIntent().getIntExtra(Constants.VALUE.POSITION, 0);
        MusicType = ChangeMusic.getInstance().switchMusic(type);


        initView();
        assignView();



    }

    @Override
    public void onBackPressed() {
        
    }

    private void initView() {
        mTextLeftTime = findViewById(R.id.text_leftTime);
        mTextRightTime = findViewById(R.id.text_rightTime);
        mBtnPlayPause = findViewById(R.id.icon_play);
        mBtnPrev = findViewById(R.id.icon_prev);
        mBtnRepeat = findViewById(R.id.icon_repeat);
        mBtnNext = findViewById(R.id.icon_next);
        mBtnFavourite = findViewById(R.id.icon_imageFav);
        mBtnBack = findViewById(R.id.imb_BackMusic);
        mBtnInfoMusic = findViewById(R.id.imb_InfoMusic);
        mBtnMenu = findViewById(R.id.imb_SeeMenu);
        mVpMusic = findViewById(R.id.vp_change_music);
        mSeekBarTime = findViewById(R.id.sb_leftTime);
        mSeekBarTime.setMax(MusicType.get(SongsManager.getInstance().getCurrentMusicID()).getTime());
    }

    private void assignView(){
        mBtnBack.setOnClickListener(this);
        mBtnInfoMusic.setOnClickListener(this);
        mBtnMenu.setOnClickListener(this);
        mBtnPlayPause.setOnClickListener(this);
        mBtnPrev.setOnClickListener(this);
        mBtnRepeat.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mBtnFavourite.setOnClickListener(this);

        mSeekBarTime.setOnSeekBarChangeListener(this);
        mAdapter = new ChangeMusicPagerAdapter(getSupportFragmentManager());
        for (int idx = 0 ; idx < MusicType.size(); idx ++){
            mAdapter.addData(new ChangeMusicFragment(), MusicType.get(idx) );
        }
        if (position == 0) {
            mTextLeftTime.setText("00 : 00");
            mTextRightTime.setText(MusicType.get(0).getDuration());
        }else {
            mTextLeftTime.setText("00 : 00");
            mTextRightTime.setText(MusicType.get(position).getDuration());
        }

        setupViewPager(position);



    }

    private void setupViewPager(int position){
        mVpMusic.setAdapter(mAdapter);
        mVpMusic.setCurrentItem(position);
        mVpMusic.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        seekPos = 0;

        MediaPlayerService.mMediaPlayer.stop();
       /* MediaPlayerService.mMediaPlayer.reset();
        if (MediaPlayerService.mMediaPlayer.isPlaying()) {
            mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);

            isPlaying = false;
            Log.d("BBB", "Play acitivy onClick : " + isPlaying + " Media Player : "+MediaPlayerService.mMediaPlayer.isPlaying());
        } else {
            mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
            isPlaying = true;
            Log.d("BBB", "Play acitivy onClick : " + isPlaying + " Media Player : "+MediaPlayerService.mMediaPlayer.isPlaying());
        }*/
        SongsManager.getInstance().setCurrentMusicID(position);


    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
        sendBroadcast(new Intent(Constants.ACTION.BROADCAST_PLAY_NEW_AUDIO));


        mSeekBarTime.setProgress(0);
        mSeekBarTime.setMax(MusicType.get(position).getTime());
        mTextLeftTime.setText("00 : 00");
        mTextRightTime.setText(convertTime(MusicType.get(position).getTime()));

//        Log.d("BBB","Min: "+ 0+ " -- Max: "+ SongsManager.getInstance().allSortSongs().get(position).getTime());

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    private void playNextAudio(){

        if (!MediaPlayerService.isStarted) {
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            MediaPlayerService.isStarted = true;
        } else {
            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            sendBroadcast(new Intent(Constants.ACTION.BROADCAST_PLAY_NEW_AUDIO));

        }
    }
    // -- Broadcast Receiver to update position of seekbar from service --
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
                boolean isPlayingNoti =
                        serviceIntent.getBooleanExtra(Constants.INTENT.IS_PLAYING_NOTI,
                                false);

                if (isPlayingNoti) {

                    mBtnPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_play_light));
                } else {

                    mBtnPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_pause_light));
                }
            }
    };

    private BroadcastReceiver brCheckPlayService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // activity gửi broadcast xuống service
            boolean isPlayingMedia = intent.getBooleanExtra(Constants.INTENT.IS_PLAY_MEDIA, false);

            if (isPlayingMedia) {
                mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);

                isPlaying = false;
                Intent iPlayMedia = new Intent(PlayActivity.this, MediaPlayerService.class);
                iPlayMedia.setAction(Constants.ACTION.PAUSE);
                startService(iPlayMedia);


            } else {
                mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
                isPlaying = true;
                Intent iPlayMedia = new Intent(PlayActivity.this, MediaPlayerService.class);
                iPlayMedia.setAction(Constants.ACTION.PLAY);
                startService(iPlayMedia);

            }

        }
    };
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.icon_play:
                SongsManager.getInstance().setCurrentMusicID(position);
                // khi bấm play check play trước đã, trong broad cast check play sẽ play video
                Intent iPlay = new Intent(this, MediaPlayerService.class);
                iPlay.setAction(Constants.ACTION.ISPLAY);
                startService(iPlay);
                break;
            case R.id.icon_next:
               
                break;
            case R.id.icon_prev:

                break;
            case R.id.icon_repeat:

                break;
            case R.id.icon_imageFav:
                break;
            case R.id.imb_BackMusic:
                // khi back ngược về ta cần phải lưu dc position khi tắt app bật lên ta phải có
                // dc giá trị sẵn để xuất màn hình tất cả có ở Changmusic khi thao tác
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra(Constants.VALUE.POSITION, position);
                startActivity(intent);
                finish();
                break;
            case R.id.imb_InfoMusic:
                break;
            case R.id.imb_SeeMenu:
                break;
        }

    }

    private String convertTime(int currentDuration){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df = new SimpleDateFormat("mm : ss", Locale.getDefault());
        df.setTimeZone(tz);
        String time = String.valueOf(df.format(currentDuration));
        return time;
    }

    private void updateUI(Intent serviceIntent) {

        int currentPos = serviceIntent.getIntExtra("current_pos", 0);
        int mediaMax = serviceIntent.getIntExtra("media_max", 0);
        String songTitle = serviceIntent.getStringExtra("song_title");
//        Log.d("BBB", "current Poss: "+currentPos + " ======= Media Max: "+mediaMax);


        mSeekBarTime.setMax(mediaMax);
        mSeekBarTime.setProgress(currentPos);
        mTextLeftTime.setText(convertTime(currentPos));
        mTextRightTime.setText(convertTime(MusicType.get(SongsManager.getInstance().getCurrentMusicID()).getTime()));

        if (MusicType.get(SongsManager.getInstance().getCurrentMusicID()).getTime() - currentPos < 1000){
            new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    mVpMusic.setCurrentItem(SongsManager.getInstance().getCurrentMusicID()+1, true);
                }
            }.start();
        }

        setEnableButton();
    }
    private void setEnableButton() {
        mBtnPrev.setEnabled(SongsManager.getInstance().getCurrentMusicID() != 0);
        mBtnPrev.setImageResource(SongsManager.getInstance().getCurrentMusicID() != 0 ?
                R.drawable.ic_previous_black : R.drawable.ic_previous_white);
        mBtnNext.setEnabled(SongsManager.getInstance().getCurrentMusicID() < SongsManager.getInstance().newSongs().size() - 1);
        mBtnNext.setImageResource(SongsManager.getInstance().getCurrentMusicID() < SongsManager.getInstance().newSongs().size() - 1 ?
                R.drawable.ic_next_black : R.drawable.ic_next_white);
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!MediaPlayerService.mMediaPlayer.isPlaying()){
            Log.d("KKK", "onProgressChanged: ENTER");
            seekBar.setMax(MusicType.get(SongsManager.getInstance().getCurrentMusicID()).getTime());
        }
        if (fromUser){
            seekPos = seekBar.getProgress();
            Log.d("KKK", "Seek Position: "+seekPos);
//            iSeekBar.putExtra("seekbar_service", seekPos);
            Intent intent = new Intent(this, MediaPlayerService.class);
            intent.setAction(Constants.ACTION.SEEK);
            intent.putExtra(Constants.PREFERENCES.POSITION_SONG, seekPos);
            Log.d("KKK", "Bắt Đầu Intent");
            startService(intent);
//            Log.d("TTT", "PlayActivity Seekbar: " + seekBar.getProgress() + "/" + seekBar.getMax());

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekPos != 0) {

            seekBar.setProgress(seekPos);

        }

    }


    @Override
    public void onChange(boolean isChange) {
        this.isChange = isChange;
    }
}
