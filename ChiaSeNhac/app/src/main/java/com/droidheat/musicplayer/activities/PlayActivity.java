package com.droidheat.musicplayer.activities;

import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.droidheat.musicplayer.BaseActivity;
import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.OnMusicChange;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongManager;
import com.droidheat.musicplayer.services.MediaPlayerService;
import com.droidheat.musicplayer.PlayMusic;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.adapters.ChangeMusicPagerAdapter;
import com.droidheat.musicplayer.fragments.ChangeMusicFragment;
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
    private SongManager mSongManager;
    private SeekBar mSeekBarTime;

    private PlayMusic mPlayMusic;
    public ImageButton mBtnPlayPause;
    private ImageButton mBtnBack, mBtnInfoMusic, mBtnMenu, mBtnPrev, mBtnRepeat, mBtnNext,
    mBtnSeeMore, mBtnAbout, mBtnShuffle, mBtnEqualizer;
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
    private LinearLayout ll_vp_change_music;
    private int size;
    private LinearLayout mLinearSeeMore;
    private boolean isChange, isPlaying;
    private boolean isRepeat = false;
    private boolean isMore = false;
    private boolean isShuffle = false;
    private Dialog mDlAboutMusic;
    private OnMusicChange onMusicChange;


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
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mSongManager = SongManager.getInstance();
        mSongManager.setContext(this);
        mSharedPrefsManager = new SharedPrefsManager();
        mSharedPrefsManager.setContext(this);

        // nhận giá trị postion và type ở home fragment và recently activity
        type = this.getIntent().getStringExtra(Constants.INTENT.TYPE);
        position = this.getIntent().getIntExtra(Constants.INTENT.POSITION, 0);
        Log.d("BBB", "Play Activity --- onCreate: "+position +" === Type: "+type);
        setTypeSong(type);
        initView();
        assignView();
        size = mSongs.size() - 1;

        Intent iSetMusic = new Intent(this, MediaPlayerService.class);
        iSetMusic.setAction(Constants.ACTION.SET_MUSIC);
        iSetMusic.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, true);
        iSetMusic.putExtra(Constants.INTENT.SET_MUSIC, mSongs);
        startService(iSetMusic);
    }

    private void setTypeSong(String type){
        if (type.equals(Constants.VALUE.NEW_SONGS) || type.equals(Constants.VALUE.ALL_NEW_SONGS)){
            mSongs = SongManager.getInstance().newSongs();
        }else if (type.equals(Constants.VALUE.ALL_SONGS)){
            mSongs = SongManager.getInstance().allSortSongs();
        }
    }

    @Override
    public void onBackPressed() {
        
    }

    private void initView() {
        mBtnEqualizer = findViewById(R.id.icon_equalizer);
        mLinearSeeMore = findViewById(R.id.ll_see_more);
        mTextLeftTime = findViewById(R.id.text_leftTime);
        mTextRightTime = findViewById(R.id.text_rightTime);
        mBtnPlayPause = findViewById(R.id.icon_play);
        mBtnPrev = findViewById(R.id.icon_prev);
        mBtnRepeat = findViewById(R.id.icon_repeat);
        mBtnNext = findViewById(R.id.icon_next);
        mBtnSeeMore = findViewById(R.id.icon_image_More);
        mBtnBack = findViewById(R.id.imb_BackMusic);
        mBtnInfoMusic = findViewById(R.id.imb_InfoMusic);
        mBtnMenu = findViewById(R.id.imb_SeeMenu);
        ll_vp_change_music = findViewById(R.id.ll_vp_change_music);
        mVpMusic = findViewById(R.id.vp_change_music);
        mSeekBarTime = findViewById(R.id.sb_leftTime);
        mBtnShuffle = findViewById(R.id.icon_shuffle);
        mBtnAbout = findViewById(R.id.icon_about);
        mSeekBarTime.setMax(mSongs.get(SongManager.getInstance().getCurrentMusic()).getTime());
    }

    private void assignView(){
        mBtnEqualizer.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnInfoMusic.setOnClickListener(this);
        mBtnMenu.setOnClickListener(this);
        mBtnPlayPause.setOnClickListener(this);
        mBtnPrev.setOnClickListener(this);
        mBtnRepeat.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mBtnSeeMore.setOnClickListener(this);
        mBtnAbout.setOnClickListener(this);
        mBtnShuffle.setOnClickListener(this);
        mSeekBarTime.setOnSeekBarChangeListener(this);


        initData(mSongs);
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

    private void initData(ArrayList<SongModel> mSongs){
        mAdapter = new ChangeMusicPagerAdapter(getSupportFragmentManager());
        for (int idx = 0; idx < mSongs.size(); idx ++){
            ChangeMusicFragment fChangeMusicFragment = new ChangeMusicFragment();
            fChangeMusicFragment.setMusicMain(mSongs);
            mAdapter.addData(fChangeMusicFragment, mSongs.get(idx));
        }
    }

    private void showMusicAbout(){
        mDlAboutMusic = new Dialog(this);

        mDlAboutMusic.setContentView(R.layout.dialog_info_music);

        TextView textName = mDlAboutMusic.findViewById(R.id.dialog_about_music_name);
        TextView textFileName = mDlAboutMusic.findViewById(R.id.dialog_about_music_file_name);
        TextView textSong = mDlAboutMusic.findViewById(R.id.dialog_about_music_title);
        TextView textAlbum = mDlAboutMusic.findViewById(R.id.dialog_about_music_album);
        TextView textArtist = mDlAboutMusic.findViewById(R.id.dialog_about_music_artist);
        TextView textTime = mDlAboutMusic.findViewById(R.id.dialog_about_music_time);
        TextView textLocation = mDlAboutMusic.findViewById(R.id.dialog_about_music_location);
        Button btnDone = mDlAboutMusic.findViewById(R.id.dialog_about_close);

        textName.setText(mSongs.get(position).getTitle());
        textFileName.setText("File Name: "+mSongs.get(position).getFileName());
        textSong.setText("Song Title: "+mSongs.get(position).getTitle());
        textAlbum.setText("Album: "+mSongs.get(position).getAlbum());
        textArtist.setText("Artist: "+mSongs.get(position).getArtist());
        textTime.setText("Time Song: "+convertTime(mSongs.get(position).getTime()));
        textLocation.setText("File Location: "+mSongs.get(position).getPath());

        btnDone.setOnClickListener(this);
        mDlAboutMusic.show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset == 0.0 && positionOffsetPixels == 0) {
            Log.d("BBB", "PlayActivity --- onPageScrolled: "+position);
            SongManager.getInstance().setCurrentMusic(this.position);
            mSharedPrefsManager.setString(Constants.PREFERENCES.SaveAlbumID,
                    mSongs.get(this.position).getAlbumID());


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

            mTextLeftTime.setText("00 : 00");
            mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
            Intent intent = new Intent(this, MediaPlayerService.class);
            intent.setAction(Constants.ACTION.PLAY);
            intent.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, true);
            startService(intent);
        }

    }


    @Override
    public void onPageSelected(int position) {
        this.position = position;

        Log.d("BBB", "Play Activity --- onPageSelected: "+position);

        mSeekBarTime.setProgress(0);
        mSeekBarTime.setMax(mSongs.get(position).getTime());
        mTextLeftTime.setText("00 : 00");
        mTextRightTime.setText(convertTime(mSongs.get(position).getTime()));



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
                isPlaying = true;
                Intent iPlayMedia = new Intent(PlayActivity.this, MediaPlayerService.class);
                iPlayMedia.setAction(Constants.ACTION.PAUSE);
                iPlayMedia.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, true);
                startService(iPlayMedia);
            } else {

                mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
                isPlaying = false;
                Intent iPlayMedia = new Intent(PlayActivity.this, MediaPlayerService.class);
                iPlayMedia.setAction(Constants.ACTION.PLAY);
                iPlayMedia.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, true);
                startService(iPlayMedia);
            }
        }
    };

    private BroadcastReceiver brPlayNew = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isRepeat) {
                String actionNoti = intent.getStringExtra(Constants.INTENT.NOTI_SERVICE_TO_ACTIVITY);
                if (actionNoti != null) {
                    if (actionNoti.equals("NextToService")) {
                        Log.d("KKK", "PlayActiviy --- brPlayNew: NextToService");
                        int pos = intent.getIntExtra(Constants.INTENT.POSITION, -1);
                        Log.d("KKK", "PlayActiviy --- brPlayNew: "+pos);
                        if (SongManager.getInstance().getCurrentMusic() != mSongs.size() - 1) {
                            mVpMusic.setCurrentItem(pos, true);
                        }
                    } else if (actionNoti.equals("PreviousToService")) {
                        int pos = intent.getIntExtra(Constants.INTENT.POSITION, -1);
                        if (SongManager.getInstance().getCurrentMusic() != 0 ) {
                            mVpMusic.setCurrentItem(pos, true);
                        }
                    }
                }else {
                    int pos = intent.getIntExtra(Constants.INTENT.POSITION, -1);
                    if (SongManager.getInstance().getCurrentMusic() != 0 ) {
                        mVpMusic.setCurrentItem(pos, true);
                    }
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.icon_play:
                SongManager.getInstance().setCurrentMusic(position);
                // khi bấm play check play trước đã, trong broad cast check play sẽ play video
                Intent iPlay = new Intent(this, MediaPlayerService.class);
                iPlay.setAction(Constants.ACTION.IS_PLAY);
                iPlay.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, true);

                startService(iPlay);
                break;
            case R.id.icon_next:
                if (position == mSongs.size() - 1){

                    mBtnNext.setClickable(false);
                    mBtnNext.setImageResource(R.drawable.ic_next_black);
                }else {
                    mBtnNext.setClickable(true);
                    mBtnNext.setImageDrawable(getResources().getDrawable(R.drawable.ic_next_white));

                    Log.d("BBB",
                            "PlayActivity --- icon_next: " + (position + 1));

                    Intent iNext = new Intent(this, MediaPlayerService.class);
                    iNext.setAction(Constants.ACTION.NEXT);
                    iNext.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, true);
                    startService(iNext);
//                    mVpMusic.setCurrentItem(position + 1);
                }
                break;
            case R.id.icon_prev:
                if (SongManager.getInstance().getCurrentMusic() ==  0 ){
                    mBtnPrev.setClickable(false);
                    mBtnPrev.setImageResource(R.drawable.ic_previous_black);
                }else {
                    mBtnPrev.setClickable(true);
                    mBtnPrev.setImageResource(R.drawable.ic_previous_white);

                    Log.d("BBB",
                            "PlayActivity --- icon_prev: " + (SongManager.getInstance().getCurrentMusic() - 1));

                    Intent iPrevious = new Intent(this, MediaPlayerService.class);
                    iPrevious.setAction(Constants.ACTION.PREVIOUS);
                    iPrevious.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, true);
                    startService(iPrevious);
//                    mVpMusic.setCurrentItem(position - 1);

                }
                break;
            case R.id.icon_repeat:
                if (!isRepeat){
                    mBtnRepeat.setImageResource(R.drawable.ic_repeat_blue);
                    SongManager.getInstance().setCurrentMusic(position);
                    Intent iRepeat = new Intent(PlayActivity.this, MediaPlayerService.class);
                    iRepeat.setAction(Constants.ACTION.REPEAT);
                    iRepeat.putExtra(Constants.INTENT.IS_REPEAT, true);
                    iRepeat.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, true);
                    startService(iRepeat);
                    isRepeat = true;
                    Toast.makeText(this, "Turn On Repeat Music", Toast.LENGTH_SHORT).show();
                    unregisterReceiver(brPlayNew);
                }else {
                    mBtnRepeat.setImageResource(R.drawable.ic_repeat_white);
                    isRepeat = false;
                    Intent iRepeat = new Intent(PlayActivity.this, MediaPlayerService.class);
                    iRepeat.setAction(Constants.ACTION.REPEAT);
                    iRepeat.putExtra(Constants.INTENT.IS_REPEAT, false);
                    iRepeat.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, true);
                    startService(iRepeat);
                    registerReceiver(brPlayNew,
                            new IntentFilter(Constants.ACTION.BROADCAST_PLAY_NEW_AUDIO));
                    Toast.makeText(this, "Turn Off Repeat Music", Toast.LENGTH_SHORT).show();


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
            case R.id.imb_BackMusic:
                // khi back ngược về ta cần phải lưu dc position khi tắt app bật lên ta phải có
                // dc giá trị sẵn để xuất màn hình tất cả có ở Changmusic khi thao tác

                Intent iBackMusic = new Intent(this, HomeActivity.class);
                startActivity(iBackMusic);
                finish();
                break;
            case R.id.imb_InfoMusic:

                break;
            case R.id.icon_equalizer:
                startActivity(new Intent(this, EqualizerActivity.class));
                break;
            case R.id.imb_SeeMenu:
                break;
            case R.id.icon_about:
                showMusicAbout();
                break;
            case R.id.icon_shuffle:
                if (!isShuffle){
                    isShuffle = true;
                    mSharedPrefsManager.setInteger(Constants.PREFERENCES.POSITION_MAIN,
                            position);

                    ArrayList<SongModel> songShuffle = SongManager.getInstance().shuffleSongs();

                    ChangeMusicFragment.newInstance(songShuffle);
                    mBtnShuffle.setImageResource(R.drawable.app_shuffle_blue);
                    Toast.makeText(this, "Turn On Shuffle Music", Toast.LENGTH_SHORT).show();

                    Intent inShuffle = new Intent(PlayActivity.this, MediaPlayerService.class);
                    inShuffle.setAction(Constants.ACTION.SET_MUSIC);
                    inShuffle.putExtra(Constants.INTENT.SET_MUSIC, songShuffle);
                    inShuffle.putExtra(Constants.INTENT.IS_SHUFFLE, true);
                    inShuffle.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, true);
                    startService(inShuffle);
                }else {
                    isShuffle = false;


                    ChangeMusicFragment.newInstance(null);
                    mBtnShuffle.setImageResource(R.drawable.app_shuffle_white);
                    Toast.makeText(this, "Turn Off Shuffle Music", Toast.LENGTH_SHORT).show();
                    Intent inShuffle = new Intent(PlayActivity.this, MediaPlayerService.class);
                    inShuffle.setAction(Constants.ACTION.SET_MUSIC);
                    inShuffle.putExtra(Constants.INTENT.IS_SHUFFLE, false);
                    inShuffle.putExtra(Constants.INTENT.SET_MUSIC, mSongs);
                    startService(inShuffle);
                }
                break;
            case R.id.dialog_about_close:
                mDlAboutMusic.cancel();
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
        mTextRightTime.setText(convertTime(mSongs.get(SongManager.getInstance().getCurrentMusic()).getTime()));

        if (mSongs.get(SongManager.getInstance().getCurrentMusic()).getTime() - currentPos < 1000){
            new CountDownTimer(2500, 1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    if (!isRepeat) {
                        mVpMusic.setCurrentItem(SongManager.getInstance().getCurrentMusic() + 1, true);
                    }else {
                        Intent iPlayNext = new Intent(PlayActivity.this, MediaPlayerService.class);
                        iPlayNext.setAction(Constants.ACTION.PLAY);
                        iPlayNext.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, true);
                        startService(iPlayNext);
                    }
                }
            }.start();
        }


    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!MediaPlayerService.mMediaPlayer.isPlaying()){
            seekBar.setMax(mSongs.get(SongManager.getInstance().getCurrentMusic()).getTime());
        }
        if (fromUser){
            seekPos = seekBar.getProgress();
            Log.d("MMM", "PlayActivity --- onProgressChanged: "+seekPos);
            mTextLeftTime.setText(convertTime(seekPos));
            mSharedPrefsManager.setInteger(Constants.PREFERENCES.POSITION_SONG, seekPos);

            Intent iSeekChoose = new Intent(this, MediaPlayerService.class);
            iSeekChoose.setAction(Constants.ACTION.SEEK);
            iSeekChoose.putExtra(Constants.INTENT.POSITION_SONG, seekPos);
            iSeekChoose.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, true);
            startService(iSeekChoose);
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
