package com.droidheat.musicplayer.activities;

import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.droidheat.musicplayer.BaseActivity;
import com.droidheat.musicplayer.ChangeMusic;
import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.MediaPlayerService;
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
        implements ViewPager.OnPageChangeListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener{
    private ViewPager mVpMusic;
    private ChangeMusicPagerAdapter mAdapter;
    private SongsManager mSongManager;
    private SeekBar mSbTime;
    private int indexPage = 0;
    private PlayMusic mPlayMusic;
    private ImageButton mBtnBack, mBtnInfoMusic, mBtnMenu, mBtnPlayPause, mBtnPrev, mBtnRepeat, mBtnNext, mBtnFavourite;
    private Handler mHandler;
    private Runnable mRunnable;
    private TextView mTextLeftTime, mTextRightTime;
    private String type;
    private int position;
    private ArrayList<SongModel> MusicType = new ArrayList<>();
    private boolean receiverRegistered;
    private Intent iSeekBar;
    private Intent iPlayPause;

    @Override
    protected void onStart() {
        super.onStart();
        iSeekBar = new Intent(Constants.ACTION.BROADCAST_SEEK_BAR);
        iPlayPause = new Intent(Constants.ACTION.BROADCAST_PLAY_PAUSE);

        registerReceiver(brSeekBar, new IntentFilter(Constants.ACTION.BROADCAST_SEEK_BAR));
        registerReceiver(brPlayPause, new IntentFilter(Constants.ACTION.BROADCAST_PLAY_PAUSE));
        // start service
        Intent playerIntent = new Intent(this, MediaPlayerService.class);
        startService(playerIntent);


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            this.unregisterReceiver(brPlayPause);
            this.unregisterReceiver(brSeekBar);
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
        // nhận giá trị postion và type ở home fragment và recently activity
        type = this.getIntent().getStringExtra(Constants.VALUE.TYPE);
        position = this.getIntent().getIntExtra(Constants.VALUE.POSITION, 0);
        MusicType = ChangeMusic.getInstance().switchMusic(type);


//        mPlayMusic = PlayMusic.getInstance();
//        mPlayMusic.setActivity(this);
//        mPlayMusic.initMediaBrowser();
        initView();
        assignView();
        playAudio();
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
        mSbTime = findViewById(R.id.sb_leftTime);
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



        mSbTime.setOnSeekBarChangeListener(this);
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

    }

    @Override
    public void onPageSelected(int position) {


        this.position = position;
        Log.d("OOO","onPageSelected: "+position);
        SongsManager.getInstance().setCurrentMusicID(position);
        MediaPlayerService.mMediaPlayer.stop();
        if (mBtnPlayPause.getDrawable() == getResources().getDrawable(R.drawable.ic_media_pause_light)){
            mBtnPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_play_light));

        }
        mTextLeftTime.setText("00 : 00");
        mTextRightTime.setText(convertTime(MusicType.get(position).getTime()));

        mSbTime.setProgress(0);
        mSbTime.setMax(MusicType.get(position).getTime());





//        Log.d("BBB","Min: "+ 0+ " -- Max: "+ SongsManager.getInstance().allSongs().get(position).getTime());

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    private void playAudio(){

        if (!MediaPlayerService.isStarted) {
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
    private BroadcastReceiver brPlayPause = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            // CHANGE NOTIFICATION == IS_PLAYING_STATUS ->>> IS_PLAYING
            boolean isPlaying = serviceIntent.getBooleanExtra(Constants.NOTIFICATION.IS_PLAYING,
                    true);
            if(isPlaying){
                Log.d("BBB", "brPlayPause notification: "+isPlaying);
                mBtnPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_pause_light));
            }else {
                Log.d("BBB", "brPlayPause: "+isPlaying);
                mBtnPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_play_light));
            }

        }
    };
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.icon_play:
                SongsManager.getInstance().setCurrentMusicID(position);

                boolean isPlaying;
                if (MediaPlayerService.mMediaPlayer != null) {

                    if (MediaPlayerService.mMediaPlayer.isPlaying()) {
                        mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);

                        isPlaying = false;
                        Log.d("BBB", "Play acitivy onClick : " + isPlaying + " Media Player : "+MediaPlayerService.mMediaPlayer.isPlaying());
                    } else {
                        mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
                        isPlaying = true;
                        Log.d("BBB", "Play acitivy onClick : " + isPlaying + " Media Player : "+MediaPlayerService.mMediaPlayer.isPlaying());
                    }
                    Log.d("BBB", " sendBroadcast(iPlayPause): "+isPlaying);
                    iPlayPause.putExtra(Constants.NOTIFICATION.IS_PLAYING, isPlaying);
                    sendBroadcast(iPlayPause);
                }


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

/*    @Override
    public void getState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }

        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
//                scheduleSeekBarUpdate();
                mBtnPlayPause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.app_pause));
                break;
            case PlaybackStateCompat.STATE_PAUSED:
//                stopSeekBarUpdate();
                mBtnPlayPause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.app_play));
                break;
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
//                stopSeekBarUpdate();
                mBtnPlayPause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.app_play));
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
//                stopSeekBarUpdate();
                break;
            default:
                Log.d("BBB", "Unhandled state " + state.getState());
            case PlaybackStateCompat.STATE_CONNECTING:
                break;
            case PlaybackStateCompat.STATE_ERROR:
                break;
            case PlaybackStateCompat.STATE_FAST_FORWARDING:
                break;
            case PlaybackStateCompat.STATE_REWINDING:
                break;
            case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT:
                break;
            case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS:
                break;
            case PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM:
                break;
        }
    }*/

    private String convertTime(int currentDuration){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df = new SimpleDateFormat("mm : ss", Locale.getDefault());
        df.setTimeZone(tz);
        String time = String.valueOf(df.format(currentDuration));
        return time;
    }
    /*@Override
    public void getMetadataCompat(MediaMetadataCompat compat) {
        if (compat == null) {
            return;
        }

    }*/
    private void updateUI(Intent serviceIntent) {
        int currentPos = serviceIntent.getIntExtra("current_pos", 0);
        int mediaMax = serviceIntent.getIntExtra("media_max", 0);
        String songTitle = serviceIntent.getStringExtra("song_title");
        Log.d("BBB", "current Poss: "+currentPos + " ======= Media Max: "+mediaMax);
        mSbTime.setMax(mediaMax);
        mSbTime.setProgress(currentPos);
        mTextLeftTime.setText(convertTime(currentPos));
        mTextRightTime.setText(convertTime(mediaMax));


        if (MediaPlayerService.mMediaPlayer.isPlaying() && mediaMax - currentPos > 1000) {
            mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
        }
        else {
            mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
        }
        setEnableButton();
    }
    private void setEnableButton() {
        mBtnPrev.setEnabled(SongsManager.getInstance().getCurrentMusicID() != 0);
        mBtnPrev.setImageResource(SongsManager.getInstance().getCurrentMusicID() != 0 ?
                R.drawable.ic_previous_black : R.drawable.ic_previous_gray);
        mBtnNext.setEnabled(SongsManager.getInstance().getCurrentMusicID() < SongsManager.getInstance().newSongs().size() - 1);
        mBtnNext.setImageResource(SongsManager.getInstance().getCurrentMusicID() < SongsManager.getInstance().newSongs().size() - 1 ?
                R.drawable.ic_next_black : R.drawable.ic_next_gray);
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser){

            int seekPos = seekBar.getProgress();

            iSeekBar.putExtra(Constants.NOTIFICATION.SEEK_POS, seekPos);
            sendBroadcast(iSeekBar);
            Log.d("BBB", "PlayActivity Seekbar: " + seekPos + "/" + seekBar.getMax());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
