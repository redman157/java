package com.droidheat.musicplayer.activities;

import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
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
import com.droidheat.musicplayer.ChangeMusic;
import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.OnMusicChange;
import com.droidheat.musicplayer.manager.ImageUtils;
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
    mBtnSeeMore, mBtnAbout, mBtnShuffle;
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
        size = mSongs.size();

        Intent iSetMusic = new Intent(this, MediaPlayerService.class);
        iSetMusic.setAction(Constants.ACTION.SET_MUSIC);
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
        mVpMusic = findViewById(R.id.vp_change_music);
        mSeekBarTime = findViewById(R.id.sb_leftTime);
        mBtnShuffle = findViewById(R.id.icon_shuffle);
        mBtnAbout = findViewById(R.id.icon_about);
        mSeekBarTime.setMax(mSongs.get(SongManager.getInstance().getCurrentMusic()).getTime());
    }

    private void assignView(){
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


        mAdapter = new ChangeMusicPagerAdapter(getSupportFragmentManager());
        for (int idx = 0; idx < mSongs.size(); idx ++){
            if (onMusicChange != null){

            }
            ChangeMusicFragment fChangeMusicFragment = new ChangeMusicFragment();
            fChangeMusicFragment.setMusicMain(mSongs);
            fChangeMusicFragment.setMusicChange(fChangeMusicFragment);
            mAdapter.addData(fChangeMusicFragment, mSongs.get(idx));
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
        mSharedPrefsManager.setString(Constants.PREFERENCES.SaveAlbumID, mSongs.get(position).getAlbumID());
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
        SongManager.getInstance().setCurrentMusic(position);

        Intent iPlay = new Intent(this, MediaPlayerService.class);
        iPlay.setAction(Constants.ACTION.IS_PLAY);
        startService(iPlay);
    }


    @Override
    public void onPageSelected(int position) {
        this.position = position;

        Log.d("BBB", "Play Activity --- onPageSelected: "+position);

        mSeekBarTime.setProgress(0);
        mSeekBarTime.setMax(mSongs.get(position).getTime());
        mTextLeftTime.setText("00 : 00");
        mTextRightTime.setText(convertTime(mSongs.get(position).getTime()));


//        Log.d("BBB","Min: "+ 0+ " -- Max: "+ SongManager.getInstance().allSortSongs().get(position).getTime());
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
                isPlaying = true;
                Intent iPlayMedia = new Intent(PlayActivity.this, MediaPlayerService.class);
                iPlayMedia.setAction(Constants.ACTION.PAUSE);
                startService(iPlayMedia);
            } else {

                mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
                isPlaying = false;
                Intent iPlayMedia = new Intent(PlayActivity.this, MediaPlayerService.class);
                iPlayMedia.setAction(Constants.ACTION.PLAY);
                startService(iPlayMedia);
            }
        }
    };

    private BroadcastReceiver brPlayNew = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isRepeat) {
                String actionNoti = intent.getStringExtra(Constants.INTENT.NOTI_SERVICE_TO_ACTIVITY);
           /* String parsedAction = actionNoti.split(":")[0].trim();
            int posMusic = Integer.parseInt(actionNoti.split(":")[1]);*/
                if (actionNoti != null) {
                    if (actionNoti.equals("NextToService")) {
                        Log.d("BBB",
                                "PlayActivity ---  Receiver NextToService:" + (SongManager.getInstance().getCurrentMusic()));
                        mVpMusic.setCurrentItem(SongManager.getInstance().getCurrentMusic(), true);
                    } else if (actionNoti.equals("PreviousToService")) {
                        Log.d("BBB",
                                "PlayActivity ---  Receiver: PreviousToService " + (SongManager.getInstance().getCurrentMusic()));
                        mVpMusic.setCurrentItem(SongManager.getInstance().getCurrentMusic(), true);
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
                            "PlayActivity --- icon_next: " + (SongManager.getInstance().getCurrentMusic() + 1));

                    Intent iNext = new Intent(this, MediaPlayerService.class);
                    iNext.setAction(Constants.ACTION.NEXT);
                    startService(iNext);

                    mVpMusic.setCurrentItem(SongManager.getInstance().getCurrentMusic() + 1);
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
                            "PlayActivity --- icon_prev: " + (SongManager.getInstance().getCurrentMusic() - 1));

                    Intent iPrevious = new Intent(this, MediaPlayerService.class);
                    iPrevious.setAction(Constants.ACTION.PREVIOUS);

                    startService(iPrevious);
                    mVpMusic.setCurrentItem(SongManager.getInstance().getCurrentMusic() - 1);
                }
                break;
            case R.id.icon_repeat:

                if (!isRepeat){

                    mBtnRepeat.setImageResource(R.drawable.ic_repeat_blue);
                    SongManager.getInstance().setCurrentMusic(position);
                    Intent intent = new Intent(PlayActivity.this, MediaPlayerService.class);
                    intent.setAction(Constants.ACTION.REPEAT);
                    intent.putExtra(Constants.INTENT.IS_REPEAT, true);
                    startService(intent);
                    isRepeat = true;
                    Toast.makeText(this, "Turn On Repeat Music", Toast.LENGTH_SHORT).show();
                    unregisterReceiver(brPlayNew);
                }else {
                    mBtnRepeat.setImageResource(R.drawable.ic_repeat_white);
                    isRepeat = false;
                    Intent intent = new Intent(PlayActivity.this, MediaPlayerService.class);
                    intent.setAction(Constants.ACTION.REPEAT);
                    intent.putExtra(Constants.INTENT.IS_REPEAT, false);
                    startService(intent);
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
                Bitmap bitmap =
                        ImageUtils.getInstance(PlayActivity.this).getBitmapIntoPicasso(mSharedPrefsManager.getString(Constants.PREFERENCES.SaveAlbumID,"0"));

                Intent iBackMusic = new Intent(this, HomeActivity.class);
                iBackMusic.putExtra(Constants.INTENT.IS_PLAY, isPlaying);
//                iBackMusic.putExtra("SendAlbumId", bitmap);

                startActivity(iBackMusic);

                break;
            case R.id.imb_InfoMusic:
                for (int i = 0 ; i < mSongs.size(); i++){
                    Log.d("CCC","Album id "+i +" là :"+mSongs.get(i).getAlbumID());
                }
                break;
            case R.id.imb_SeeMenu:
                break;
            case R.id.icon_about:
                showMusicAbout();
                break;
            case R.id.icon_shuffle:
                if (!isShuffle){
                    isShuffle = true;

                    if (onMusicChange != null) {
                        onMusicChange.onChange(SongManager.getInstance().shuffleSongs());
                    }
                    mBtnShuffle.setImageResource(R.drawable.app_shuffle_blue);
                    Intent inShuffle = new Intent(PlayActivity.this, MediaPlayerService.class);
                    inShuffle.setAction(Constants.ACTION.SET_MUSIC);
                    inShuffle.putExtra(Constants.INTENT.SET_MUSIC, SongManager.getInstance().shuffleSongs());
                    startService(inShuffle);
                }else {
                    isShuffle = false;
                    Log.d("TTT", "PlayActivy --- icon_shuffle: "+ (onMusicChange == null ? "null":
                            "khac null"));
                    if (onMusicChange != null) {
                        onMusicChange.onChange(mSongs);
                    }
                    mBtnShuffle.setImageResource(R.drawable.app_shuffle_white);
                    Intent inShuffle = new Intent(PlayActivity.this, MediaPlayerService.class);
                    inShuffle.setAction(Constants.ACTION.SET_MUSIC);
                    inShuffle.putExtra(Constants.INTENT.SET_MUSIC, SongManager.getInstance().newSongs());
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
            seekBar.setMax(mSongs.get(SongManager.getInstance().getCurrentMusic()).getTime());
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
