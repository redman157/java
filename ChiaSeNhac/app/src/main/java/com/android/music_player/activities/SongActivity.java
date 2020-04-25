package com.android.music_player.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
    private LinearLayout mLinearStatusPlayMusic;
    private IconView mImgAlbumId;
    private RecyclerView mRcSongs;
    private ScrollView mScrollView;
    private ImageButton mBtnBack;
    private SongsAdapter mSongsAdapter;
    private SharedPrefsUtils mSharedPrefsUtils;
    private String type;
    private ArrayList<SongModel> mSongs;
    private TextView mTextArtist, mTextTitle;

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
        setContentView(R.layout.activity_recently_all_music);
        mSongManager = SongManager.getInstance();
        mSongManager.setContext(this);

        mSharedPrefsUtils = new SharedPrefsUtils(this);
        mSongs = SongManager.getInstance().newSongs();
        type = getIntent().getStringExtra(Constants.INTENT.TYPE_MUSIC);

        initView();
        setTypeSong(type);
        assignView();
        
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
        mViewLayoutPlay = findViewById(R.id.layout_play_media);
        mTextArtist = mViewLayoutPlay.findViewById(R.id.text_artists_media);
        mTextTitle = mViewLayoutPlay.findViewById(R.id.text_title_media);
        mImgMedia = mViewLayoutPlay.findViewById(R.id.img_albumArt_media);
        mBtnPlay = mViewLayoutPlay.findViewById(R.id.imbt_Play_media);
        mBtnTitle = mViewLayoutPlay.findViewById(R.id.btn_title_media);

        mBtnBack = findViewById(R.id.imb_BackMusic);
        mLinearStatusPlayMusic = findViewById(R.id.ll_StatusPlayMusic);
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
        mLinearStatusPlayMusic.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
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
            case R.id.imb_BackMusic:

                Intent iBackMusic = new Intent(this, HomeActivity.class);
                this.finish();
                startActivity(iBackMusic);
                break;
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


