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
import android.widget.Toast;

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
        SeekBar.OnSeekBarChangeListener {
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
    private ArrayList<SongModel> mSongs = new ArrayList<>();
    private boolean receiverRegistered;
    public Intent iSeekBar;
    private Intent iPlayPause;
    public   int seekPos;

    private boolean isPlayingMedia;
    private int size;
    private boolean isChange, isPlaying;
    private boolean isRepeat = false;
    private OnSongChange onSongChange;
    private void setStatus(OnSongChange onSongChange){
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
            registerReceiver(brCheckPlayService, new IntentFilter(Constants.ACTION.IS_PLAY));
            registerReceiver(brPlayNew, new IntentFilter(Constants.ACTION.BROADCAST_PLAY_NEW_AUDIO));
            receiverRegistered = true;
        }
        // start service
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            this.unregisterReceiver(brPlayPauseActivity);
            this.unregisterReceiver(brSeekBar);
            this.unregisterReceiver(brCheckPlayService);
            this.unregisterReceiver(brPlayNew);
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
        Log.d("BBB", "Play Activity --- onCreate: "+position);
        mSongs = ChangeMusic.getInstance().switchMusic(type);
        initView();
        assignView();
        size = mSongs.size();

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
        mSeekBarTime.setMax(mSongs.get(SongsManager.getInstance().getCurrentMusic()).getTime());
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
        for (int idx = 0; idx < mSongs.size(); idx ++){
            mAdapter.addData(new ChangeMusicFragment(), mSongs.get(idx) );
        }
        if (position == 0) {
            mBtnPrev.setImageResource(R.drawable.ic_previous_black);
            mTextLeftTime.setText("00 : 00");
            mTextRightTime.setText(mSongs.get(0).getDuration());
        }else {
            mTextLeftTime.setText("00 : 00");
            mTextRightTime.setText(mSongs.get(position).getDuration());
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

        if (position==0) {
            mBtnPrev.setImageResource(R.drawable.ic_previous_black);
            mBtnNext.setImageResource(R.drawable.ic_next_white);
        }else if (position == mSongs.size()) {
            mBtnPrev.setImageResource(R.drawable.ic_previous_white);
            mBtnNext.setImageResource(R.drawable.ic_next_black);
        }else {
            mBtnPrev.setImageResource(R.drawable.ic_previous_white);
            mBtnNext.setImageResource(R.drawable.ic_next_white);
        }

        seekPos = 0;

        MediaPlayerService.mMediaPlayer.stop();
        MediaPlayerService.mMediaPlayer.reset();
        mTextLeftTime.setText("00 : 00");
        mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
        SongsManager.getInstance().setCurrentMusic(position);

        Intent iPlay = new Intent(this, MediaPlayerService.class);
        iPlay.setAction(Constants.ACTION.IS_PLAY);
        startService(iPlay);
    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
//        sendBroadcast(new Intent(Constants.ACTION.BROADCAST_PLAY_NEW_AUDIO));
        Log.d("BBB", "Play Activity --- onPageSelected: "+position);

        mSeekBarTime.setProgress(0);
        mSeekBarTime.setMax(mSongs.get(position).getTime());
        mTextLeftTime.setText("00 : 00");
        mTextRightTime.setText(convertTime(mSongs.get(position).getTime()));


//        Log.d("BBB","Min: "+ 0+ " -- Max: "+ SongsManager.getInstance().allSortSongs().get(position).getTime());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
                        serviceIntent.getBooleanExtra(Constants.INTENT.IS_PLAY_MEDIA_NOTIFICATION,
                                false);

                if (isPlayingNoti) {

                    mTextLeftTime.setText("00 : 00");
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
            boolean isPlayingMedia = intent.getBooleanExtra(Constants.INTENT.IS_PLAY_MEDIA_SERVICE, false);

            if (isPlayingMedia) {
                mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);

                if (onSongChange != null) {
                    onSongChange.onChange("Pause");
                }

                isPlaying = false;
                Intent iPlayMedia = new Intent(PlayActivity.this, MediaPlayerService.class);
                iPlayMedia.setAction(Constants.ACTION.PAUSE);
                startService(iPlayMedia);
            } else {
                mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
                if (onSongChange != null){
                    onSongChange.onChange("Playing");
                }
                isPlaying = true;
                Intent iPlayMedia = new Intent(PlayActivity.this, MediaPlayerService.class);
                iPlayMedia.setAction(Constants.ACTION.PLAY);

                startService(iPlayMedia);
            }
        }
    };

    private BroadcastReceiver brPlayNew = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isRepeat == false) {
                String actionNoti = intent.getStringExtra(Constants.INTENT.NOTI_SERVICE_TO_ACTIVITY);
           /* String parsedAction = actionNoti.split(":")[0].trim();
            int posMusic = Integer.parseInt(actionNoti.split(":")[1]);*/
                if (actionNoti != null) {
                    if (actionNoti.equals("NextToService")) {
                        Log.d("BBB",
                                "PlayActivity ---  Receiver NextToService:" + (SongsManager.getInstance().getCurrentMusic()));
                        mVpMusic.setCurrentItem(SongsManager.getInstance().getCurrentMusic(), true);
                    } else if (actionNoti.equals("PreviousToService")) {
                        Log.d("BBB",
                                "PlayActivity ---  Receiver: PreviousToService " + (SongsManager.getInstance().getCurrentMusic()));
                        mVpMusic.setCurrentItem(SongsManager.getInstance().getCurrentMusic(), true);
                    }
                }
            }

        }
    };
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.icon_play:
                SongsManager.getInstance().setCurrentMusic(position);
                // khi bấm play check play trước đã, trong broad cast check play sẽ play video
                Intent iPlay = new Intent(this, MediaPlayerService.class);
                iPlay.setAction(Constants.ACTION.IS_PLAY);
                startService(iPlay);
                break;
            case R.id.icon_next:
                if (position == mSongs.size() - 1){

                    mBtnNext.setClickable(false);
                    mBtnNext.setImageResource(R.drawable.ic_next_black);
                }else {
                    mBtnNext.setClickable(true);
                    mBtnNext.setImageDrawable(getResources().getDrawable(R.drawable.ic_next_white));
                    mVpMusic.setCurrentItem(SongsManager.getInstance().getCurrentMusic() + 1);
                    Log.d("BBB",
                            "PlayActivity --- icon_next: " + (SongsManager.getInstance().getCurrentMusic() + 1));
                    Intent iNext = new Intent(this, MediaPlayerService.class);
                    iNext.setAction(Constants.ACTION.NEXT);
                    startService(iNext);
                }
                break;
            case R.id.icon_prev:


                if (position ==  0 ){

                    mBtnPrev.setClickable(false);
                    mBtnPrev.setImageResource(R.drawable.ic_previous_black);
                }else {
                    mBtnPrev.setClickable(true);
                    mBtnPrev.setImageResource(R.drawable.ic_previous_white);

                    Log.d("BBB",
                            "PlayActivity --- icon_prev: " + (SongsManager.getInstance().getCurrentMusic() - 1));
                    Intent iPrevious = new Intent(this, MediaPlayerService.class);
                    iPrevious.setAction(Constants.ACTION.PREVIOUS);

                    startService(iPrevious);
                    mVpMusic.setCurrentItem(SongsManager.getInstance().getCurrentMusic() - 1);
                }

                break;
            case R.id.icon_repeat:

                if (isRepeat == false){

                    mBtnRepeat.setImageResource(R.drawable.ic_repeat_blue);
                    SongsManager.getInstance().setCurrentMusic(position);

                    isRepeat = true;
                    Toast.makeText(this, "Turn On Repeat Music", Toast.LENGTH_SHORT).show();
                    unregisterReceiver(brPlayNew);
                }else {
                    mBtnRepeat.setImageResource(R.drawable.ic_repeat_white);
                    isRepeat = false;
                    registerReceiver(brPlayNew,
                            new IntentFilter(Constants.ACTION.BROADCAST_PLAY_NEW_AUDIO));
                    Toast.makeText(this, "Turn Off Repeat Music", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.icon_imageFav:
                break;
            case R.id.imb_BackMusic:
                // khi back ngược về ta cần phải lưu dc position khi tắt app bật lên ta phải có
                // dc giá trị sẵn để xuất màn hình tất cả có ở Changmusic khi thao tác
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra(Constants.INTENT.POS_HOME, position);
                finish();
                startActivity(intent);

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
        mTextRightTime.setText(convertTime(mSongs.get(SongsManager.getInstance().getCurrentMusic()).getTime()));

        if (mSongs.get(SongsManager.getInstance().getCurrentMusic()).getTime() - currentPos < 1000){
            new CountDownTimer(2500, 1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    if (!isRepeat) {
                        mVpMusic.setCurrentItem(SongsManager.getInstance().getCurrentMusic() + 1, true);
                    }else {
                        Intent intent = new Intent(PlayActivity.this, MediaPlayerService.class);
                        intent.setAction(Constants.ACTION.PLAY);
                        startService(intent);
                    }
                }
            }.start();
        }


    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!MediaPlayerService.mMediaPlayer.isPlaying()){
            seekBar.setMax(mSongs.get(SongsManager.getInstance().getCurrentMusic()).getTime());
        }
        if (fromUser){
            seekPos = seekBar.getProgress();

            Intent intent = new Intent(this, MediaPlayerService.class);
            intent.setAction(Constants.ACTION.SEEK);
            intent.putExtra(Constants.PREFERENCES.POSITION_SONG, seekPos);

            startService(intent);


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


}
