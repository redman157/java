package com.droidheat.musicplayer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.droidheat.musicplayer.ChangeMusic;
import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.PlayMusic;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.adapters.ChangeMusicPagerAdapter;
import com.droidheat.musicplayer.fragments.ChangeMusicFragment;
import com.droidheat.musicplayer.manager.SongsUtils;
import com.droidheat.musicplayer.models.SongModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

public class PlayActivity extends AppCompatActivity
        implements ViewPager.OnPageChangeListener, View.OnClickListener, PlayMusic.CallBackListener, SeekBar.OnSeekBarChangeListener{
    private ViewPager mVpMusic;
    private ChangeMusicPagerAdapter mAdapter;
    private SongsUtils mSongManager;
    private SeekBar mSbTime;
    private int indexPage = 0;
    private PlayMusic mPlayMusic;
    private ImageButton mBtnBack, mBtnInfoMusic, mBtnMenu, mBtnPlay, mBtnPrev, mBtnRepeat, mBtnNext, mBtnFavourite;
    private Handler mHandler;
    private Runnable mRunnable;
    private TextView mTextLeftTime, mTextRightTime;
    private String type;
    private int position;
    private ArrayList<SongModel> MusicType = new ArrayList<>();
    @Override
    protected void onStart() {
        super.onStart();
        mPlayMusic.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlayMusic.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        mSongManager = SongsUtils.getInstance();
        mSongManager.setContext(this);
        // nhận giá trị postion và type ở home fragment và recently activity
        type = this.getIntent().getStringExtra(Constants.VALUE.TYPE);
        position = this.getIntent().getIntExtra(Constants.VALUE.POSITION, 0);
        MusicType = ChangeMusic.getInstance().switchMusic(type);


        mPlayMusic = PlayMusic.getInstance();
        mPlayMusic.setActivity(this);
        mPlayMusic.initMediaBrowser();
        initView();
        assignView();

    }

    private void initView() {
        mTextLeftTime = findViewById(R.id.text_leftTime);
        mTextRightTime = findViewById(R.id.text_rightTime);
        mBtnPlay = findViewById(R.id.icon_play);
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
        mBtnPlay.setOnClickListener(this);
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
        mTextLeftTime.setText("00 : 00");
        mTextRightTime.setText(convertTime(MusicType.get(position).getTime()));
        mSbTime.setProgress(0);
        mSbTime.setMax(MusicType.get(position).getTime());
        Log.d("BBB","Min: "+ 0+ " -- Max: "+ SongsUtils.getInstance().allSongs().get(position).getTime());

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.icon_play:
                SongsUtils.getInstance().setCurrentMusicID(position);
                MediaControllerCompat
                        .getMediaController(PlayActivity.this)
                        .getTransportControls().play();
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

    @Override
    public void getState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }

        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
//                scheduleSeekBarUpdate();
                mBtnPlay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.app_pause));
                break;
            case PlaybackStateCompat.STATE_PAUSED:
//                stopSeekBarUpdate();
                mBtnPlay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.app_play));
                break;
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
//                stopSeekBarUpdate();
                mBtnPlay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.app_play));
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
    }

    private String convertTime(int currentDuration){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df = new SimpleDateFormat("mm : ss", Locale.getDefault());
        df.setTimeZone(tz);
        String time = String.valueOf(df.format(currentDuration));
        return time;
    }
    @Override
    public void getMetadataCompat(MediaMetadataCompat compat) {
        if (compat == null) {
            return;
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
