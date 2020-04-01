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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

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
    private LinearLayout ll_StatusPlayMusic;
    private IconView img_AlbumId;
    private RecyclerView rc_recently_add;
    private ScrollView scrollView;
    private ImageButton btnBack;
    private RecentlyAdderAdapter recentlyAdderAdapter;
    private SharedPrefsManager mSharedPrefsManager;
    private String type;
    private ArrayList<SongModel> mSongs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently_all_music);
        type = getIntent().getStringExtra(Constants.INTENT.TYPE_MUSIC);
        mSharedPrefsManager = new SharedPrefsManager();
        mSharedPrefsManager.setContext(this);
        mSongs =SongManager.getInstance().newSongs();
        initView();
        assignView();
    }

    private void assignView() {
        recentlyAdderAdapter = new RecentlyAdderAdapter(
                this,
                mSongs,
                Constants.VALUE.ALL_NEW_SONGS);
//        rc_recently_add.setNestedScrollingEnabled(false);
        rc_recently_add.setHasFixedSize(true);
        recentlyAdderAdapter.OnClickItem(this);
        rc_recently_add.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        rc_recently_add.setAdapter(recentlyAdderAdapter);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(this);
        }
        ll_StatusPlayMusic.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    private void initView() {
        btnBack = findViewById(R.id.imb_BackMusic);
        ll_StatusPlayMusic = findViewById(R.id.ll_StatusPlayMusic);
        rc_recently_add = findViewById(R.id.rc_recently_add);
        img_AlbumId = findViewById(R.id.img_AlbumId);
        scrollView = findViewById(R.id.scrollView);
        ImageUtils.getInstance(this).getSmallImageByPicasso(
                SongManager.getInstance().newSongs().get(0).getAlbumID(),
                img_AlbumId);
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        int x = scrollY - oldScrollY;
        if (x > 0) {
            //scroll up
            Log.d("RecentlyAllMusicLog", "Scrolls Up");

            img_AlbumId.setAnimationUp(x);
        } else if (x < 0) {
            //scroll down
            Log.d("RecentlyAllMusicLog", "Scrolls Down");
            img_AlbumId.setAnimationDown(x);

        } else {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imb_BackMusic:
                Bitmap bitmap =
                        ImageUtils.getInstance(RecentlyAllMusicActivity.this).getBitmapIntoPicasso(mSharedPrefsManager.getString(Constants.PREFERENCES.SaveAlbumID,"0"));

                Intent intent = new Intent(this, HomeActivity.class);
//                intent.putExtra("SendAlbumId", bitmap);
                startActivity(intent);
                break;
        }


    }

    @Override
    public void onClick(String type, int position) {
        if (type.equals(Constants.VALUE.ALL_NEW_SONGS)){
            mSharedPrefsManager.setInteger(Constants.PREFERENCES.POSITION, position);
            mSharedPrefsManager.setString(Constants.PREFERENCES.SaveAlbumID, mSongs.get(position).getAlbumID());
            ImageUtils.getInstance(RecentlyAllMusicActivity.this).getSmallImageByPicasso(
                    SongManager.getInstance().newSongs().get(position).getAlbumID(),
                    img_AlbumId);

            Fragment fragment = this.getSupportFragmentManager().findFragmentById(R.id.fm_music_dock);
            if (fragment instanceof MusicDockFragment){
                // set switch vị trí và type music cho play activity chạy
                ChangeMusic.getInstance().setContext(this);
                ChangeMusic.getInstance().setFragment((MusicDockFragment) fragment);
                ChangeMusic.getInstance().setPosition(Constants.VALUE.NEW_SONGS, position);
                ChangeMusic.getInstance().switchMusic();
            }
        }
    }
}
