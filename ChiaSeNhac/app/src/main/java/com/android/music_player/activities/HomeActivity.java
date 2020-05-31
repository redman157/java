package com.android.music_player.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.android.music_player.R;
import com.android.music_player.adapters.ViewPagerAdapter;
import com.android.music_player.fragments.HomeFragment;
import com.android.music_player.fragments.LibraryFragment;
import com.android.music_player.interfaces.OnChangePlayListListener;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.managers.MusicManager;
import com.android.music_player.media.MediaBrowserHelper;
import com.android.music_player.media.MediaBrowserListener;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.utils.Utils;
import com.google.android.material.tabs.TabLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener, TabLayout.OnTabSelectedListener,
        ViewTreeObserver.OnGlobalLayoutListener, MediaBrowserListener.OnPlayPause,
        SlidingUpPanelLayout.PanelSlideListener {
    private ViewPager mViewPager_Home;
    private ImageView mImgChangeState;
    private RelativeLayout mRlChangeState;
    private LinearLayout mLlPlayMedia, mLlChangeState, mLlChangeMusic, mLlSeeMore, mLlControlSong;
    private View mLayoutMedia;
    private String tag = "BBB";
    private ViewPagerAdapter mViewPagerAdapter;
    private TabLayout mTabLayoutHome;
    private ImageUtils imageUtils;
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
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    public boolean isContinue;
    private OnChangePlayListListener onChangePlayListListener;
    private MediaBrowserHelper mMediaBrowserHelper;
    private MediaBrowserListener mBrowserListener;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mMediaBrowserHelper.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    protected void onStart() {
        super.onStart();

//        mMediaBrowserHelper.onStart();
        chooseSong = mMusicManager.getPosition();
//        MusicManager.getInstance().setContext(this);
//        MediaBrowserConnection browserConnection =
//                MusicManager.getInstance().getMediaBrowserConnection();
//        browserConnection.setMediaId(mMusicManager.getMediaId());
//        mMediaBrowserHelper = browserConnection;
//
//        mBrowserListener = new MediaBrowserListener();
//        mBrowserListener.setOnPlayPause(this);
//        mMediaBrowserHelper.registerCallback("PlayActivity", mBrowserListener);
//
//        Log.d("JJJ", "PlayActivity onStart: "+ mMusicManager.getMediaId());
//
//        mMediaBrowserHelper.onStart();
        setViewMusic();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // khởi tạo màn hình chính là home ta cần check position để gán sẵn vị trí luôn
        initView();

        mSharedPrefsUtils = new SharedPrefsUtils(this);
        mMusicManager = MusicManager.getInstance();
        mMusicManager.setContext(this);
        mSongs = mMusicManager.getListSong();
        imageUtils = ImageUtils.getInstance(this);

        setupToolbar();
        assignView();
        mMusicManager.setMediaId(mMusicManager.getCurrentMusic()== null? "":mMusicManager.getCurrentMusic().getSongName() );
        Utils.UpdateButtonPlay(mBtnPlayPause, isPlaying);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    public void setViewMusic(){
        if (mMusicManager.getCurrentMusic() != null) {
            MediaMetadataCompat metadataCompat = MusicLibrary.getMetadata(this,
                    mMusicManager.getCurrentMusic().getSongName());

            mTextArtist.setText(metadataCompat.getString(Constants.METADATA.Artist));
            mTextTitle.setText(metadataCompat.getString(Constants.METADATA.Title));
            mImgAlbumArt.setImageBitmap(metadataCompat.getBitmap(Constants.METADATA.AlbumID));
        }
    }

    private void setupViewPager(ViewPager viewPager){
        mViewPagerAdapter = new ViewPagerAdapter(this,getSupportFragmentManager());
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setOnChangePlayListListener(onChangePlayListListener);

        mViewPagerAdapter.addFragment(homeFragment);
        mViewPagerAdapter.addFragment(new LibraryFragment());

        viewPager.setAdapter(mViewPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOnClickListener(this);

        mTabLayoutHome.setupWithViewPager(viewPager);

        for (int i = 0; i < mTabLayoutHome.getTabCount(); i++) {
            mTabLayoutHome.getTabAt(i).setCustomView(mViewPagerAdapter.getTabHome(i));
        }
        mTabLayoutHome.addOnTabSelectedListener(this);
    }

    private void initView() {
        mLlChangeMusic = findViewById(R.id.ll_vp_change_music);
        mImgChangeMusic = findViewById(R.id.item_img_ChangeMusic);
        mLlControlSong = findViewById(R.id.ll_control_song);
        mLlSeeMore = findViewById(R.id.ll_see_more);
        mLayoutMedia = findViewById(R.id.layout_main_media);

        mImgChangeState = findViewById(R.id.img_change_state);
        mRlChangeState = findViewById(R.id.rl_change_state);
        mLlPlayMedia = findViewById(R.id.ll_play_media);
        mSlidingUpPanelLayout = findViewById(R.id.activity_main);
        mTabLayoutHome = findViewById(R.id.tab_HomeActivity);
        mToolBar = findViewById(R.id.tb_HomeActivity);
        mViewPlayMedia = findViewById(R.id.layout_play_media);

        mTextTitle = mViewPlayMedia.findViewById(R.id.text_title_media);
        mTextArtist = mViewPlayMedia.findViewById(R.id.text_artists_media);
        mBtnPlayPause = mViewPlayMedia.findViewById(R.id.imbt_Play_media);
        mBtnTitle = mViewPlayMedia.findViewById(R.id.btn_title_media);
        mImgAlbumArt = mViewPlayMedia.findViewById(R.id.img_albumArt_media);

        mViewPager_Home = findViewById(R.id.vp_Home);
        setupViewPager(mViewPager_Home);
        mImgAlbumArt.getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    private void assignView(){
//        mSlidingUpPanelLayout.removePanelSlideListener(this);
//        mSlidingUpPanelLayout.setFadeOnClickListener(this);
        mSlidingUpPanelLayout.addPanelSlideListener(this);
        mBtnPlayPause.setOnClickListener(this);
        mBtnTitle.setOnClickListener(this);
        mViewPlayMedia.setOnClickListener(this);
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
                if (mSlidingUpPanelLayout != null &&
                        (mSlidingUpPanelLayout.getPanelState() == PanelState.EXPANDED || mSlidingUpPanelLayout.getPanelState() == PanelState.ANCHORED)) {
                    mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
                }

                break;
            case R.id.img_change_state:
                if (mSlidingUpPanelLayout != null &&
                        (mSlidingUpPanelLayout.getPanelState() == PanelState.EXPANDED || mSlidingUpPanelLayout.getPanelState() == PanelState.ANCHORED)) {
                    mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
                }
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
        } else {
//            super.onBackPressed();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onPageSelected(int position) {
     /*   mNavigationView.getMenu().getItem(currentViewPagerPosition).setChecked(false);
        mNavigationView.getMenu().getItem(position).setChecked(true);
        currentViewPagerPosition = position;*/

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        for (int i = 0; i < mTabLayoutHome.getTabCount(); i ++){
            View view = mTabLayoutHome.getTabAt(i).getCustomView();
            TextView title = view.findViewById(R.id.item_tl_text_home);
            int color = (i == tab.getPosition()) ? getResources().getColor(R.color.red) :
                    getResources().getColor(R.color.white);
            title.setTextColor(color);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onGlobalLayout() {
        mImgAlbumArt.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        final int[] locat_0 = new int[2];
        mImgAlbumArt.getLocationOnScreen(locat_0);
        int x_0 = locat_0[0];
        int y_0 = locat_0[1];

        mTabLayoutHome.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        final int[] locat_1 = new int[2];
        mTabLayoutHome.getLocationOnScreen(locat_1);
        int x_1 = locat_1[0];
        int y_1 = locat_1[1];
        Log.d("HHH", "initView --- ImgMedia: x --- "+x_0 +" y --- "+y_0);
        Log.d("HHH", "initView --- TabLayoutHome: x --- "+x_1 +" y --- "+y_1);
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
            mLlPlayMedia.setAlpha(1f);
        }else if (slideOffset > 0.3){
            mLlPlayMedia.setAlpha(0.3f - slideOffset);
            mLlPlayMedia.setVisibility(View.GONE);
        }
        else {
            mLlPlayMedia.setAlpha(slideOffset);
        }
        mRlChangeState.setAlpha(slideOffset);

    }

    @Override
    public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
        /**
         *  EXPANDED,
         *  COLLAPSED,
         *  ANCHORED,
         *  HIDDEN,
         *  DRAGGING
         * **/
        Log.d("III", "onPanelStateChanged previousState: "+previousState.toString() + " ---newState: "+newState.toString());
        switch (newState){
            case EXPANDED:

                mLlPlayMedia.setVisibility(View.GONE);
                break;
            case COLLAPSED:
                mLlPlayMedia.setAlpha(1);
                mLlPlayMedia.setVisibility(View.VISIBLE);
                break;

        }
    }
}
