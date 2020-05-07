package com.android.music_player.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.adapters.SongsAdapter;
import com.android.music_player.interfaces.OnClickItemListener;
import com.android.music_player.managers.SongManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.utils.Utils;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.M)
public class SongActivity extends AppCompatActivity implements
        View.OnScrollChangeListener, View.OnClickListener, OnClickItemListener {
    private LinearLayout mLl_Play_Media;
    private ImageView mImgAlbumId;
    private RecyclerView mRcSongs;

    private SongsAdapter mSongsAdapter;
    private SharedPrefsUtils mSharedPrefsUtils;
    private String type;
    private ArrayList<SongModel> mSongs;
    private TextView mTextArtist, mTextTitle;
    private Toolbar mToolBar;
    private Button mBtnTitle;
    private View mViewLayoutPlay;
    private int choosePosition;
    private ImageButton mBtnPlayPause;
    private ImageView mImgMedia;
    private SongManager mSongManager;
    private ActionBar actionBar;
    private boolean receiverRegistered;
    private boolean isPlaying;
    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (!receiverRegistered) {
                registerReceiver(brPlayPauseActivity, new IntentFilter(Constants.ACTION.BROADCAST_PLAY_PAUSE));
                registerReceiver(brIsPlayService, new IntentFilter(Constants.ACTION.IS_PLAY));
                receiverRegistered = true;
            }
        }finally {
            mSongManager = SongManager.getInstance();
            mSongManager.setContext(this);
            mSongs = mSongManager.getCurrentSongs();

            choosePosition = mSongManager.getPositionCurrent();
            Log.d("XXX", "HomeActivity --- onStart: " + choosePosition);
        }
    }


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
            setSongCurrent(mSongs);
        }
    };

    private BroadcastReceiver brIsPlayService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // activity gửi broadcast xuống service
            boolean isPlayingMedia = intent.getBooleanExtra(Constants.INTENT.IS_PLAY_MEDIA_SERVICE, false);

            if (isPlayingMedia) {
                Log.d("XXX", "SongActivity --- brIsPlayService:" +true);
                mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
                isPlaying = true;
                Utils.PauseMediaService(context, mSongManager.getTypeCurrent(), mSongManager.getPositionCurrent() );
            } else {
                Log.d("XXX", "SongActivity --- brIsPlayService:" +false);
                mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
                isPlaying = false;
                Utils.PlayMediaService(context, mSongManager.getTypeCurrent(), mSongManager.getPositionCurrent());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        mSongManager = SongManager.getInstance();
        mSongManager.setContext(this);
        initView();
        mSharedPrefsUtils = new SharedPrefsUtils(this);

        type = getIntent().getStringExtra(Constants.INTENT.TYPE_MUSIC);

        mSongs = mSongManager.getCurrentSongs(type);
        setSupportActionBar(mToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



//        setTypeSong(type);
        setSongCurrent(mSongs);
        assignView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(brPlayPauseActivity);
        this.unregisterReceiver(brIsPlayService);
    }

    public void setSongCurrent(ArrayList<SongModel> song){
        if (mSongManager.getPositionCurrent() == -1){
            mTextArtist.setText(song.get(0).getArtist());
            mTextTitle.setText(song.get(0).getSongName());
            ImageUtils.getInstance(this).getSmallImageByPicasso(song.get(0).getAlbumID(), mImgMedia);
        }else {
            mTextArtist.setText(song.get(SongManager.getInstance().getPositionCurrent()).getArtist());
            mTextTitle.setText(song.get(SongManager.getInstance().getPositionCurrent()).getSongName());
            ImageUtils.getInstance(this).getSmallImageByPicasso(song.get(SongManager.getInstance().getPositionCurrent()).getAlbumID(), mImgMedia);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent iBackMusic = new Intent(this, HomeActivity.class);
                finish();
                startActivity(iBackMusic);
                break;
            case R.id.action_searchBtn:
                finish();
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.sleep_timer:
                finish();
                startActivity(new Intent(this, TimerActivity.class));
                break;
            case R.id.sync:
                finish();
                Intent intent = new Intent(this, SplashActivity.class).putExtra(Constants.VALUE.SYNC,
                        true);
                startActivity(intent);
                break;
            case R.id.settings:
                finish();
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.equalizer:
                finish();
                startActivity(new Intent(this, EqualizerActivity.class));
                break;
            case R.id.changeTheme:
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_choose_accent_color);
                dialog.findViewById(R.id.orange).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(
                                Constants.PREFERENCES.ACCENT_COLOR, Constants.COLOR.ORANGE);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.cyan).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.CYAN);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.green).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.GREEN);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.yellow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.YELLOW);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.pink).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.PINK);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.purple).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.PURPLE);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.grey).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.GREY);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.red).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.RED);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTypeSong(String type) {

        int position = mSharedPrefsUtils.getInteger(Constants.PREFERENCES.POSITION, 0);
        if (mSongManager.getAllPlaylistDB().searchPlayList(type)) {

            mSongs = mSongManager.getAllSongToPlayList(type);
            mTextArtist.setText(mSongs.get(0).getArtist());
            mTextTitle.setText(mSongs.get(0).getSongName());
            ImageUtils.getInstance(this).getSmallImageByPicasso(mSongs.get(0).getAlbumID(), mImgMedia);
        } else {

            if (type.equals(Constants.VALUE.NEW_SONGS) || type.equals(Constants.VALUE.ALL_NEW_SONGS)) {
                mSongs = SongManager.getInstance().newSongs();
            } else if (type.equals(Constants.VALUE.ALL_SONGS)) {
                mSongs = SongManager.getInstance().allSortSongs();
            } else if (type.equals("")) {
                mSongs = SongManager.getInstance().newSongs();
            }
            mTextArtist.setText(mSongs.get(position).getArtist());
            mTextTitle.setText(mSongs.get(position).getSongName());
            ImageUtils.getInstance(this).getSmallImageByPicasso(mSongs.get(position).getAlbumID(), mImgMedia);
        }

    }

    private void initView() {
        mToolBar = findViewById(R.id.tb_SongActivity);
        mViewLayoutPlay = findViewById(R.id.layout_play_media);
        mTextArtist = mViewLayoutPlay.findViewById(R.id.text_artists_media);
        mTextTitle = mViewLayoutPlay.findViewById(R.id.text_title_media);
        mImgMedia = mViewLayoutPlay.findViewById(R.id.img_albumArt_media);
        mBtnPlayPause = mViewLayoutPlay.findViewById(R.id.imbt_Play_media);
        mBtnTitle = mViewLayoutPlay.findViewById(R.id.btn_title_media);

        mLl_Play_Media = findViewById(R.id.ll_play_media);
        mRcSongs = findViewById(R.id.rc_recently_add);
        mImgAlbumId = findViewById(R.id.img_AlbumId);
        ImageUtils.getInstance(this).getSmallImageByPicasso(
                SongManager.getInstance().newSongs().get(0).getAlbumID(),
                mImgAlbumId);
    }

    private void assignView() {
        mBtnTitle.setOnClickListener(this);
        mBtnPlayPause.setOnClickListener(this);
        mSongsAdapter = new SongsAdapter(this, mSongs, type);
        mSongsAdapter.setLimit(false);
        mSongsAdapter.OnClickItem(this);
        mRcSongs.setHasFixedSize(true);
        mSongsAdapter.OnClickItem(this);
        mRcSongs.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mRcSongs.setAdapter(mSongsAdapter);


     /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mScrollView.setOnScrollChangeListener(this);
        }*/
        mLl_Play_Media.setOnClickListener(this);

    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
      /*  int x = scrollY - oldScrollY;
        if (x > 0) {
            //scroll up
            Log.d("RecentlyAllMusicLog", "Scrolls Up");

            mImgAlbumId.setAnimationUp(x);
        } else if (x < 0) {
            //scroll down
            Log.d("RecentlyAllMusicLog", "Scrolls Down");
            mImgAlbumId.setAnimationDown(x);

        } else {

        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_title_media:
                Intent intent = new Intent(SongActivity.this, PlayActivity.class);
                Utils.Builder builder = new Utils.Builder();
                builder.putString(Constants.INTENT.TYPE, type);
                builder.putInteger(Constants.INTENT.CHOOSE_POS, choosePosition);

                if (mSongManager.isPlayCurrentSong(choosePosition)) {
                    Log.d("BBB", "HomeActivity --- btn_title_media: true");
                    builder.putBoolean(Constants.INTENT.SONG_CONTINUE, true);
                }else {
                    Log.d("BBB", "HomeActivity --- btn_title_media: false");
                    Utils.PauseMediaService(this,  Constants.VALUE.NEW_SONGS, choosePosition);
                    builder.putBoolean(Constants.INTENT.SONG_CONTINUE, false);
                }
                intent.putExtras(builder.generate().getBundle());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                builder.generate().clear();
                break;
            case R.id.imbt_Play_media:
                Utils.isPlayMediaService(this, mSongManager.getTypeCurrent(),mSongManager.getPositionCurrent());
                break;
        }
    }

    @Override
    public void onClick(int pos) {

    }

    @Override
    public void onClick(String type, int position) {
        choosePosition = position;
        mSongManager.setPositionCurrent(position);
        mSharedPrefsUtils.setString(Constants.PREFERENCES.SAVE_ALBUM_ID, mSongs.get(position).getAlbumID());
        mSongManager.setTypeCurrent(type);
        if (mLl_Play_Media.getVisibility() == View.GONE){
            mLl_Play_Media.setVisibility(View.VISIBLE);
        }
        ImageUtils.getInstance(SongActivity.this).getSmallImageByPicasso(
                mSongs.get(position).getAlbumID(),
                mImgAlbumId);
        ImageUtils.getInstance(SongActivity.this).getSmallImageByPicasso(
                mSongs.get(position).getAlbumID(),
                mImgMedia);
        mTextTitle.setText(mSongs.get(position).getSongName());
        mTextArtist.setText(mSongs.get(position).getArtist());

    }

}


