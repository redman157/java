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
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;

import me.zhanghai.android.materialplaypausedrawable.MaterialPlayPauseButton;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener, TabLayout.OnTabSelectedListener,
        ViewTreeObserver.OnGlobalLayoutListener, MediaBrowserListener.OnPlayPause {
    private ViewPager mViewPager_Home;

    private String tag = "BBB";
    private ViewPagerAdapter mViewPagerAdapter;
    private TabLayout mTabLayoutHome;
    private ImageUtils imageUtils;
    private View mViewPlayMedia;
    public ImageView mImgMedia;
    public TextView mTextTitle, mTextArtist;
    public Button mBtnTitle;
    private SharedPrefsUtils mSharedPrefsUtils;
    private ArrayList<SongModel> mSongs;
    private MusicManager mMusicManager;
    public MaterialPlayPauseButton mBtnPlayPause;
    private boolean isPlaying;
    public LinearLayout mLlPlayMedia;
    public int chooseSong;
    public String type;
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


        mMusicManager.setMediaId(mMusicManager.getCurrentMusic().getSongName());
      /*  mMediaBrowserHelper = new MediaBrowserConnection(this);
        mBrowserListener = new MediaBrowserListener();
        mMediaBrowserHelper.registerCallback(mBrowserListener);*/
    }


    private void setupToolbar() {
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    public void setViewMusic(){
        MediaMetadataCompat metadataCompat = MusicLibrary.getMetadata(this,
                mMusicManager.getCurrentMusic().getSongName());

        mTextArtist.setText(metadataCompat.getString(Constants.METADATA.Artist));
        mTextTitle.setText(metadataCompat.getString(Constants.METADATA.Title));
        mImgMedia.setImageBitmap(metadataCompat.getBitmap(Constants.METADATA.AlbumID));
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
        mTabLayoutHome = findViewById(R.id.tab_HomeActivity);
        mLlPlayMedia = findViewById(R.id.ll_play_media);
        mToolBar = findViewById(R.id.tb_HomeActivity);
        mViewPlayMedia = findViewById(R.id.layout_play_media);

        mTextTitle = mViewPlayMedia.findViewById(R.id.text_title_media);
        mTextArtist = mViewPlayMedia.findViewById(R.id.text_artists_media);
        mBtnPlayPause = mViewPlayMedia.findViewById(R.id.imbt_Play_media);
        mBtnTitle = mViewPlayMedia.findViewById(R.id.btn_title_media);
        mImgMedia = mViewPlayMedia.findViewById(R.id.img_albumArt_media);

        mViewPager_Home = findViewById(R.id.vp_Home);
        setupViewPager(mViewPager_Home);
        mImgMedia.getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    private void assignView(){
        mBtnPlayPause.setOnClickListener(this);
        mBtnPlayPause.setAnimationDuration(1500);
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
//                Utils.isPlayMediaService(this, mMusicManager.getType(), mMusicManager.getPosition());
                break;
            case R.id.layout_play_media:

                break;
            case R.id.btn_title_media:
//                Utils.IntentToPlayActivity(this, chooseSong, mMusicManager.getType() );
                /*Intent intent = new Intent(this, PlayActivity.class);
                Utils.Builder builder = new Utils.Builder();
                builder.putString(Constants.INTENT.TYPE,type);
                builder.putInteger(Constants.INTENT.CHOOSE_POS, chooseSong);
                intent.putExtras(builder.generate().getBundle());
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);*/

                Log.d("FFF","tên : "+mTextTitle.getText());
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
//        super.onBackPressed();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      /*  if (mLlPlayMedia.getVisibility() == View.VISIBLE){
            mLlPlayMedia.setVisibility(View.GONE);
        }*/
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
        mImgMedia.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        final int[] locat_0 = new int[2];
        mImgMedia.getLocationOnScreen(locat_0);
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
    public void onMediaMetadata(MediaMetadataCompat mediaMetadata) {

    }
}
