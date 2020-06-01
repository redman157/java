package com.android.music_player.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.music_player.BaseActivity;
import com.android.music_player.R;
import com.android.music_player.fragments.MainFragment;
import com.android.music_player.interfaces.OnChangePlayListListener;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.managers.MusicManager;
import com.android.music_player.media.MediaBrowserConnection;
import com.android.music_player.media.MediaBrowserHelper;
import com.android.music_player.media.MediaBrowserListener;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.utils.Utils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;


public class HomeActivity extends BaseActivity implements View.OnClickListener,
        MediaBrowserListener.OnPlayPause, SlidingUpPanelLayout.PanelSlideListener{
    private ImageView mImgChangeState;
    private RelativeLayout mLayoutChangeState;
    private LinearLayout mLayoutPlayMedia, mLlChangeState, mLayoutChangeMusic, mLayoutSeeMore, mLayoutControlSong;
    private View mLayoutMedia;
    private String tag = "BBB";
    private ImageUtils mImageUtils;
    private View mViewPlayMedia;
    public ImageView mImgAlbumArt, mImgChangeMusic;
    public TextView mTextTitle, mTextArtist;
    public Button mBtnTitle;
    private SharedPrefsUtils mSharedPrefsUtils;
    private ArrayList<SongModel> mSongs;
    private MusicManager mMusicManager;
    public ImageButton mBtnPlayPause;
    private boolean isPlaying = false;
    public int chooseSong;
    public String type;
    public SlidingUpPanelLayout mSlidingUpPanelLayout;
    private Toolbar mToolBar;
    private OnChangePlayListListener onChangePlayListListener;
    private MediaBrowserHelper mMediaBrowserHelper;
    private MediaBrowserListener mBrowserListener;
    private FrameLayout mLayoutPlaceHolder;

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        onStopService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onStartService() {
        mMediaBrowserHelper.onStart();
    }

    @Override
    public void onStopService() {
        mMediaBrowserHelper.onStop();
    }

    @Override
    public void initService() {
        MediaBrowserConnection browserConnection = MusicManager.getInstance().getMediaBrowserConnection();
        browserConnection.setMediaId(mMusicManager.getMediaId());
        mMediaBrowserHelper = browserConnection;

        mBrowserListener = new MediaBrowserListener();
        mBrowserListener.setOnPlayPause(this);
        mMediaBrowserHelper.registerCallback("PlayActivity", mBrowserListener);
    }

    @Override
    public void initManager() {
        mSharedPrefsUtils = new SharedPrefsUtils(this);
        mMusicManager = MusicManager.getInstance();
        mMusicManager.setContext(this);
        mSongs = mMusicManager.getListSong();
        mImageUtils = ImageUtils.getInstance(this);
        /*if (mSlidingUpPanelLayout != null) {
            mSlidingUpPanelLayout.setAnchorPoint(0.3f); // slide up 50% then stop

        }*/
        if (mMusicManager.getCurrentMusic() == null){
            mSlidingUpPanelLayout.setPanelState(PanelState.HIDDEN);
            mMusicManager.setMediaId("");
        }else {
            mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
            mMusicManager.setMediaId(mMusicManager.getCurrentMusic().getSongName());
        }
    }

    @Override
    public void changeFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_placeholder, fragment);
//        fragmentTransaction1.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initManager();
        initService();
        onStartService();

        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("MainFragment");
        changeFragment(mainFragment);
        setViewMusic(mMusicManager.getCurrentMusic());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null){
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fl_placeholder ,MainFragment.newInstance(),"MainFragment");
            fragmentTransaction.commit();
        }
        setContentView(R.layout.activity_home);
        initView();
        setupToolbar();
        assignView();
        Utils.UpdateButtonPlay(mBtnPlayPause, isPlaying);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    public void setPlayMedia(String songName){
        mImgChangeState
    }



    public void setViewMusic(SongModel songModel){
        if (songModel != null) {
            MediaMetadataCompat metadataCompat = MusicLibrary.getMetadata(this,
                    songModel.getSongName());

            mTextArtist.setText(metadataCompat.getString(Constants.METADATA.Artist));
            mTextTitle.setText(metadataCompat.getString(Constants.METADATA.Title));
            mImgAlbumArt.setImageBitmap(metadataCompat.getBitmap(Constants.METADATA.AlbumID));
        }
    }


    private void initView() {
        mLayoutChangeMusic = findViewById(R.id.ll_vp_change_music);
        mImgChangeMusic = findViewById(R.id.item_img_ChangeMusic);
        mLayoutControlSong = findViewById(R.id.ll_control_song);
        mLayoutSeeMore = findViewById(R.id.ll_see_more);
        mLayoutMedia = findViewById(R.id.layout_main_media);

        mImgChangeState = findViewById(R.id.img_change_state);
        mLayoutChangeState = findViewById(R.id.rl_change_state);
        mLayoutPlayMedia = findViewById(R.id.ll_play_media);
        mSlidingUpPanelLayout = findViewById(R.id.activity_main);
        mToolBar = findViewById(R.id.tb_HomeActivity);
        mViewPlayMedia = findViewById(R.id.layout_play_media);

        mTextTitle = mViewPlayMedia.findViewById(R.id.text_title_media);
        mTextArtist = mViewPlayMedia.findViewById(R.id.text_artists_media);
        mBtnPlayPause = mViewPlayMedia.findViewById(R.id.imbt_Play_media);
        mBtnTitle = mViewPlayMedia.findViewById(R.id.btn_title_media);
        mImgAlbumArt = mViewPlayMedia.findViewById(R.id.img_albumArt_media);
        mLayoutPlaceHolder = findViewById(R.id.fl_placeholder);
    }



    private void assignView(){
        mSlidingUpPanelLayout.addPanelSlideListener(this);

        mBtnPlayPause.setOnClickListener(this);
        mBtnTitle.setOnClickListener(this);
        mViewPlayMedia.setOnClickListener(this);
        mImgChangeState.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.imbt_Play_media:
                if (isPlaying){
                    Utils.UpdateButtonPlay(mBtnPlayPause, true);
                    isPlaying = false;
                }else {
                    Utils.UpdateButtonPlay(mBtnPlayPause, false);
                    isPlaying = true;
                }
                break;
            case R.id.layout_play_media:

                break;
            case R.id.btn_title_media:

            case R.id.img_change_state:
             /*   if (mSlidingUpPanelLayout != null &&
                        (mSlidingUpPanelLayout.getPanelState() == PanelState.EXPANDED || mSlidingUpPanelLayout.getPanelState() == PanelState.ANCHORED)) {
                    mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
                }*/

                break;
            case R.id.menu_search:
                Intent iSearch = new Intent(this, SearchActivity.class);

                startActivity(iSearch);
                break;
            case R.id.vp_Home:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mSlidingUpPanelLayout != null &&
                (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }


    @Override
    public void onCheck(boolean isPlay, PlaybackStateCompat state) {
        Utils.UpdateButtonPlay(mBtnPlayPause, isPlay);
    }

    @Override
    public void onNext(boolean isNext) {

    }

    @Override
    public void onMediaMetadata(MediaMetadataCompat mediaMetadata) {

    }

    // Panel change state
    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        Log.d("UUU", "onPanelSlide : "+slideOffset);
        if (slideOffset == 0f) {
            mLayoutPlayMedia.setAlpha(1f);
        }else if (slideOffset > 0.3){
            mLayoutPlayMedia.setAlpha(0.3f - slideOffset);
            mLayoutPlayMedia.setVisibility(View.GONE);
        }
        else {
            mLayoutPlayMedia.setAlpha(slideOffset);
        }
        mLayoutChangeState.setAlpha(slideOffset);

    }

    @Override
    public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
        /**
         *  EXPANDED vuốt thành công,
         *  COLLAPSED trạng thái ban đầu chưa vuốt,
         *  ANCHORED vuốt sẽ dừng lại ở mức set cứng,
         *  HIDDEN ẩn thanh sliding,
         *  DRAGGING đang vuốt
         * **/
        Log.d("III", "onPanelStateChanged previousState: "+previousState.toString() + " ---newState: "+newState.toString());

        switch (newState){
            case EXPANDED:
                mLayoutPlayMedia.setVisibility(View.GONE);
                mSlidingUpPanelLayout.setTouchEnabled(false);
                break;
            case COLLAPSED:
                mLayoutPlayMedia.setAlpha(1);
                mLayoutPlayMedia.setVisibility(View.VISIBLE);
                mSlidingUpPanelLayout.setTouchEnabled(true);
                break;
        }
    }


}
