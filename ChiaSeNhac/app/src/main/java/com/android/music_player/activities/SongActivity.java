package com.android.music_player.activities;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.utils.Constants;
import com.android.music_player.IconView;
import com.android.music_player.R;
import com.android.music_player.adapters.SongsAdapter;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.managers.SongManager;
import com.android.music_player.models.SongModel;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.M)
public class SongActivity extends AppCompatActivity implements
        View.OnScrollChangeListener, View.OnClickListener, SongsAdapter.OnClickItem {
    private LinearLayout mLl_Play_Media;
    private IconView mImgAlbumId;
    private RecyclerView mRcSongs;
    private ScrollView mScrollView;
    private SongsAdapter mSongsAdapter;
    private SharedPrefsUtils mSharedPrefsUtils;
    private String type;
    private ArrayList<SongModel> mSongs;
    private TextView mTextArtist, mTextTitle;
    private Toolbar mToolBar;
    private Button mBtnTitle;
    private View mViewLayoutPlay;
    private ImageButton mBtnPlay;
    private ImageView mImgMedia;
    private SongManager mSongManager;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        mSongManager = SongManager.getInstance();
        mSongManager.setContext(this);
        initView();
        mSharedPrefsUtils = new SharedPrefsUtils(this);
        mSongs = SongManager.getInstance().newSongs();
        type = getIntent().getStringExtra(Constants.INTENT.TYPE_MUSIC);

        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setTypeSong(type);
        assignView();
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
        mBtnPlay = mViewLayoutPlay.findViewById(R.id.imbt_Play_media);
        mBtnTitle = mViewLayoutPlay.findViewById(R.id.btn_title_media);

        mLl_Play_Media = findViewById(R.id.ll_play_media);
        mRcSongs = findViewById(R.id.rc_recently_add);
        mImgAlbumId = findViewById(R.id.img_AlbumId);
        mScrollView = findViewById(R.id.scrollView);
        ImageUtils.getInstance(this).getSmallImageByPicasso(
                SongManager.getInstance().newSongs().get(0).getAlbumID(),
                mImgAlbumId);
    }

    private void assignView() {
        mBtnTitle.setOnClickListener(this);
        mBtnPlay.setOnClickListener(this);
        mSongsAdapter = new SongsAdapter(this, mSongs, type);
        mSongsAdapter.setLimit(false);
        mSongsAdapter.OnClickItem(this);
        mRcSongs.setHasFixedSize(true);
        mSongsAdapter.OnClickItem(this);
        mRcSongs.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mRcSongs.setAdapter(mSongsAdapter);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mScrollView.setOnScrollChangeListener(this);
        }
        mLl_Play_Media.setOnClickListener(this);

    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        int x = scrollY - oldScrollY;
        if (x > 0) {
            //scroll up
            Log.d("RecentlyAllMusicLog", "Scrolls Up");

            mImgAlbumId.setAnimationUp(x);
        } else if (x < 0) {
            //scroll down
            Log.d("RecentlyAllMusicLog", "Scrolls Down");
            mImgAlbumId.setAnimationDown(x);

        } else {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_title_media:
                Intent playMedia = new Intent(this, PlayActivity.class);
                playMedia.putExtra(Constants.INTENT.POSITION,
                        mSharedPrefsUtils.getInteger(Constants.PREFERENCES.POSITION, 0));
                playMedia.putExtra(Constants.INTENT.TYPE,
                        mSharedPrefsUtils.getString(Constants.PREFERENCES.TYPE, ""));
                startActivity(playMedia);
                break;
            case R.id.imbt_Play_media:
                break;
        }


    }

    @Override
    public void onClick(String type, int position) {
        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.POSITION, position);
        mSharedPrefsUtils.setString(Constants.PREFERENCES.SaveAlbumID, mSongs.get(position).getAlbumID());
        mSharedPrefsUtils.setString(Constants.PREFERENCES.TYPE, type);
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


