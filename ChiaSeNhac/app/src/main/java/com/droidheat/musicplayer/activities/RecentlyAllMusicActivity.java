package com.droidheat.musicplayer.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
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

import com.droidheat.musicplayer.ChangeMusic;
import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.IconView;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.adapters.RecentlyAdderAdapter;
import com.droidheat.musicplayer.fragments.MusicDockFragment;
import com.droidheat.musicplayer.manager.ImageUtils;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongManager;
import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.M)
public class RecentlyAllMusicActivity extends AppCompatActivity implements
        View.OnScrollChangeListener, View.OnClickListener, RecentlyAdderAdapter.OnClickItem {
    private LinearLayout mLinearStatusPlayMusic;
    private IconView mImgAlbumId;
    private RecyclerView mRcRecentlyAdd;
    private ScrollView mScrollView;
    private ImageButton mBtnBack;
    private RecentlyAdderAdapter recentlyAdderAdapter;
    private SharedPrefsManager mSharedPrefsManager;
    private String type;
    private ArrayList<SongModel> mSongs;
    private TextView mTextArtist, mTextTitle;
    private ImageUtils imageUtils;
    private Button mBtnTitle;
    private View mViewLayoutPlay;
    private ImageButton mBtnPlay;
    private ImageView mImgMedia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently_all_music);
        mSongs =SongManager.getInstance().newSongs();
        initView();
        assignView();
        type = getIntent().getStringExtra(Constants.INTENT.TYPE_MUSIC);
        setTypeSong(type);



    }

    private void setTypeSong(String type){
        int position = mSharedPrefsManager.getInteger(Constants.PREFERENCES.POSITION, 0);
        if (type.equals(Constants.VALUE.NEW_SONGS) || type.equals(Constants.VALUE.ALL_NEW_SONGS)){
            mSongs = SongManager.getInstance().newSongs();
        }else if (type.equals(Constants.VALUE.ALL_SONGS)){
            mSongs = SongManager.getInstance().allSortSongs();
        }else if (type.equals("")){
            mSongs = SongManager.getInstance().newSongs();

        }
        mTextArtist.setText(mSongs.get(position).getArtist());
        mTextTitle.setText(mSongs.get(position).getTitle());
        imageUtils.getSmallImageByPicasso(mSongs.get(position).getAlbumID(), mImgMedia);
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
        mRcRecentlyAdd = findViewById(R.id.rc_recently_add);
        mImgAlbumId = findViewById(R.id.img_AlbumId);
        mScrollView = findViewById(R.id.scrollView);
        ImageUtils.getInstance(this).getSmallImageByPicasso(
                SongManager.getInstance().newSongs().get(0).getAlbumID(),
                mImgAlbumId);
    }

    private void assignView() {
        mBtnTitle.setOnClickListener(this);
        mBtnPlay.setOnClickListener(this);
        imageUtils = ImageUtils.getInstance(this);
        mSharedPrefsManager = new SharedPrefsManager();
        mSharedPrefsManager.setContext(this);
        recentlyAdderAdapter = new RecentlyAdderAdapter(
                this,
                mSongs,
                Constants.VALUE.ALL_NEW_SONGS);
//        mRcRecentlyAdd.setNestedScrollingEnabled(false);
        mRcRecentlyAdd.setHasFixedSize(true);
        recentlyAdderAdapter.OnClickItem(this);
        mRcRecentlyAdd.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mRcRecentlyAdd.setAdapter(recentlyAdderAdapter);


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
        switch (v.getId()){
            case R.id.imb_BackMusic:

                Intent iBackMusic = new Intent(this, HomeActivity.class);
                startActivity(iBackMusic);
                break;
            case R.id.btn_title_media:
                Intent playMedia = new Intent(this, PlayActivity.class);
                playMedia.putExtra(Constants.INTENT.POSITION,
                        mSharedPrefsManager.getInteger(Constants.PREFERENCES.POSITION,0));
                playMedia.putExtra(Constants.INTENT.TYPE,
                        mSharedPrefsManager.getString(Constants.PREFERENCES.TYPE,""));
                startActivity(playMedia);
                break;
            case R.id.imbt_Play_media:
                break;
        }


    }

    @Override
    public void onClick(String type, int position) {
        mSharedPrefsManager.setInteger(Constants.PREFERENCES.POSITION, position);
        mSharedPrefsManager.setString(Constants.PREFERENCES.SaveAlbumID, mSongs.get(position).getAlbumID());
        mSharedPrefsManager.setString(Constants.PREFERENCES.TYPE, type);
        if (type.equals(Constants.VALUE.ALL_NEW_SONGS)){
            ImageUtils.getInstance(RecentlyAllMusicActivity.this).getSmallImageByPicasso(
                    SongManager.getInstance().newSongs().get(position).getAlbumID(),
                    mImgAlbumId);
            ImageUtils.getInstance(RecentlyAllMusicActivity.this).getSmallImageByPicasso(
                    SongManager.getInstance().newSongs().get(position).getAlbumID(),
                    mImgMedia);
            mTextTitle.setText(mSongs.get(position).getTitle());
            mTextArtist.setText(mSongs.get(position).getArtist());
            }
        }
    }

