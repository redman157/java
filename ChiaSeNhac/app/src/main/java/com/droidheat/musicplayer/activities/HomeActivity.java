package com.droidheat.musicplayer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.droidheat.musicplayer.BaseActivity;
import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.adapters.OptionMenuAdapter;
import com.droidheat.musicplayer.adapters.ViewPagerAdapter;
import com.droidheat.musicplayer.fragments.AlbumGridFragment;
import com.droidheat.musicplayer.fragments.AllSongsFragment;
import com.droidheat.musicplayer.fragments.ArtistGridFragment;
import com.droidheat.musicplayer.fragments.HomeFragment;
import com.droidheat.musicplayer.fragments.PlaylistFragment;
import com.droidheat.musicplayer.manager.ImageUtils;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongManager;
import com.droidheat.musicplayer.models.SongModel;
import com.droidheat.musicplayer.services.MediaPlayerService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;


import java.util.ArrayList;

import me.zhanghai.android.materialplaypausedrawable.MaterialPlayPauseButton;
import me.zhanghai.android.materialplaypausedrawable.MaterialPlayPauseDrawable;

public class HomeActivity extends BaseActivity implements View.OnClickListener,
        OptionMenuAdapter.OnClickItem, ViewPager.OnPageChangeListener {
    private View view_LayoutMenu;

    private TextView txt_Home, txt_Albums, txt_Artists, txt_Songs, txt_PlayLists;
    private ImageView mImgMenu, mImgSearch, img_Artists, img_Songs, img_PlayLists;
    private LinearLayout ll_Home, ll_Albums, ll_Artists, ll_Songs, ll_PlayLists;
    private ArrayList<LinearLayout> linearLayouts;
    private TabLayout mTabLayout_Home;
    private Toolbar mToolbar_Home;
    private ViewPager mViewPager_Home;
    private OptionMenuAdapter mOptionMenuAdapter;
    private String tag = "BBB";
    private RecyclerView mRcOptionMenu;
    private BottomNavigationView mNavigationView;
    private ArrayList<Fragment> mFragments;
    private ArrayList<String> mMenus;
    private ViewPagerAdapter mViewPagerAdapter;
    private boolean isHide = false;
    private int position;
    private ImageUtils imageUtils;
    private View mViewPlayMedia;
    public ImageView mImgMedia;
    public TextView mTextTitle, mTextArtist;

    public Button mBtnTitle;
    private SharedPrefsManager mSharedPrefsManager;
    private ArrayList<SongModel> mSongs;
    public MaterialPlayPauseButton mBtnPlay;
    private boolean isPlay;

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {

        super.onStop();

    }

    @Override
    protected void onStart() {
        super.onStart();
        setTypeSong(mSharedPrefsManager.getString(Constants.PREFERENCES.TYPE, ""));
        Intent iService = new Intent(this, MediaPlayerService.class);
        iService.setAction(Constants.ACTION.SET_MUSIC);
        iService.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, false);
        iService.putExtra(Constants.INTENT.SET_MUSIC, mSongs);
        startService(iService);

        Log.d("KKK", "Home Activity --- onResume: "+isPlay);
        if (MediaPlayerService.mMediaPlayer != null) {
            if (MediaPlayerService.mMediaPlayer.isPlaying()) {

                mBtnPlay.setImageResource(R.drawable.ic_media_pause_light);
            } else {
                mBtnPlay.setImageResource(R.drawable.ic_media_play_light);
            }
        }else {
            mBtnPlay.setImageResource(R.drawable.ic_media_play_light);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("PPP", "onResume : Enter");


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // khởi tạo màn hình chính là home ta cần check position để gán sẵn vị trí luôn
        mSharedPrefsManager = new SharedPrefsManager();
        mSharedPrefsManager.setContext(this);
        initMenu();
        initView();

        imageUtils = ImageUtils.getInstance(this);
        setTypeSong(mSharedPrefsManager.getString(Constants.PREFERENCES.TYPE, ""));

        assignView();
        Intent iService = new Intent(this, MediaPlayerService.class);
        iService.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, false);
        startService(iService);
    }

    private void initMenu(){
        mMenus = new ArrayList<>();
        mMenus.add(Constants.MENU.Set_Sleep_Timer);
        mMenus.add(Constants.MENU.Sync_Music);
        mMenus.add(Constants.MENU.Change_Theme);
        mMenus.add(Constants.MENU.Equalizer);
        mMenus.add(Constants.MENU.Settings);
    }
    private void setupViewPager(ViewPager viewPager){

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        mViewPagerAdapter.addFragment(new HomeFragment(this));
        mViewPagerAdapter.addFragment(new AllSongsFragment());
        mViewPagerAdapter.addFragment(new AlbumGridFragment());
        mViewPagerAdapter.addFragment(new ArtistGridFragment());
        mViewPagerAdapter.addFragment(new PlaylistFragment());

        viewPager.setAdapter(mViewPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOnClickListener(this);
    }

    private void initView() {
        mViewPlayMedia = findViewById(R.id.layout_play_media);
        mTextTitle = mViewPlayMedia.findViewById(R.id.text_title_media);
        mTextArtist = mViewPlayMedia.findViewById(R.id.text_artists_media);
        mBtnPlay = mViewPlayMedia.findViewById(R.id.imbt_Play_media);
        mBtnTitle = mViewPlayMedia.findViewById(R.id.btn_title_media);
        mImgMedia = mViewPlayMedia.findViewById(R.id.img_albumArt_media);

        if (MediaPlayerService.mMediaPlayer != null) {
            if (MediaPlayerService.mMediaPlayer.isPlaying()) {
                mBtnPlay.setImageResource(R.drawable.ic_media_pause_light);
            } else {
                mBtnPlay.setImageResource(R.drawable.ic_media_play_light);
            }
        }else {
            mBtnPlay.setImageResource(R.drawable.ic_media_play_light);
        }


        mImgMenu = findViewById(R.id.menu_item);
        mImgSearch = findViewById(R.id.menu_search);
        mRcOptionMenu = findViewById(R.id.rc_OptionMenu);
        mRcOptionMenu.setVisibility(View.GONE);

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mViewPager_Home = findViewById(R.id.vp_Home);
        setupViewPager(mViewPager_Home);

    }

    private void assignView(){
        mBtnPlay.setOnClickListener(this);
        mBtnPlay.setAnimationDuration(1500);

        mBtnTitle.setOnClickListener(this);
        mOptionMenuAdapter = new OptionMenuAdapter(mMenus, this);
        mOptionMenuAdapter.OnClickItemMenu(this);
        mViewPlayMedia.setOnClickListener(this);
        mRcOptionMenu.setAdapter(mOptionMenuAdapter);
        mRcOptionMenu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mImgMenu.setOnClickListener(this);
        mImgSearch.setOnClickListener(this);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.navigation_home:
                    mViewPager_Home.setCurrentItem(0);
                    return true;
                case R.id.navigation_songs:
                    mViewPager_Home.setCurrentItem(1);
                    return true;
                case R.id.navigation_albums:
                    mViewPager_Home.setCurrentItem(2);
                    return true;
                case R.id.navigation_artists:
                    mViewPager_Home.setCurrentItem(3);
                    return true;
                case R.id.navigation_playlists:
                    mViewPager_Home.setCurrentItem(4);
                    return true;
            }
            return false;
        }
    };

    private void setTypeSong(String type){
        Log.d("PPP", "setTypeSong: "+SongManager.getInstance().getCurrentMusic());
        if (type.equals(Constants.VALUE.NEW_SONGS) || type.equals(Constants.VALUE.ALL_NEW_SONGS)){
            mSongs = SongManager.getInstance().newSongs();
        }else if (type.equals(Constants.VALUE.ALL_SONGS)){
            mSongs = SongManager.getInstance().allSortSongs();
        }else if (type.equals("")){
            mSongs = SongManager.getInstance().newSongs();
        }
        processEndOfList(SongManager.getInstance().getCurrentMusic());
        Log.d("PPP", mSongs.get(SongManager.getInstance().getCurrentMusic()).getTitle());
        mTextArtist.setText(mSongs.get(SongManager.getInstance().getCurrentMusic()).getArtist());
        mTextTitle.setText(mSongs.get(SongManager.getInstance().getCurrentMusic()).getTitle());
        imageUtils.getSmallImageByPicasso(mSongs.get(SongManager.getInstance().getCurrentMusic()).getAlbumID(), mImgMedia);

    }

    private void processEndOfList(int position){
        Log.d("CCC", "processEndOfList: "+position);
        int size = mSongs.size() - 2;
        if (position == size || position > size){
            SongManager.getInstance().setCurrentMusic(0);

        }else if (position < 0){
            SongManager.getInstance().setCurrentMusic(69);
        }else {
            SongManager.getInstance().setCurrentMusic(position);
        }
    }
    @Override
    public void onClickItemMenu(String item) {
        switch (item){
            case Constants.MENU.Set_Sleep_Timer:
                startActivity(new Intent(this, TimerActivity.class));
                mRcOptionMenu.setVisibility(View.GONE);
                break;
            case Constants.MENU.Sync_Music:
                finish();
                Intent intent = new Intent(this, SplashActivity.class).putExtra(Constants.VALUE.SYNC,
                        true);
                startActivity(intent);
                mRcOptionMenu.setVisibility(View.GONE);
                break;
            case Constants.MENU.Settings:
                startActivity(new Intent(this, SettingsActivity.class));
                mRcOptionMenu.setVisibility(View.GONE);
                break;
            case Constants.MENU.Equalizer:
                startActivity(new Intent(this, EqualizerActivity.class));
                mRcOptionMenu.setVisibility(View.GONE);
                break;
            case Constants.MENU.Change_Theme:
                final SharedPrefsManager sharedPrefsUtils = new SharedPrefsManager();
                sharedPrefsUtils.setContext(this);
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_choose_accent_color);
                dialog.findViewById(R.id.orange).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharedPrefsUtils.setString(
                                Constants.PREFERENCES.accentColor, Constants.COLOR.orange);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.cyan).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharedPrefsUtils.setString(Constants.PREFERENCES.accentColor,
                                Constants.COLOR.cyan);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.green).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharedPrefsUtils.setString(Constants.PREFERENCES.accentColor,
                                Constants.COLOR.green);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.yellow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharedPrefsUtils.setString(Constants.PREFERENCES.accentColor,
                                Constants.COLOR.yellow);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.pink).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharedPrefsUtils.setString(Constants.PREFERENCES.accentColor,
                                Constants.COLOR.pink);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.purple).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharedPrefsUtils.setString(Constants.PREFERENCES.accentColor,
                                Constants.COLOR.purple);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.grey).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharedPrefsUtils.setString(Constants.PREFERENCES.accentColor,
                                Constants.COLOR.grey);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.findViewById(R.id.red).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharedPrefsUtils.setString(Constants.PREFERENCES.accentColor,
                                Constants.COLOR.red);
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });
                dialog.show();

                break;

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imbt_Play_media:

                if(MediaPlayerService.mMediaPlayer != null){
                    if (MediaPlayerService.mMediaPlayer.isPlaying()){
                        mBtnPlay.setImageResource(R.drawable.ic_media_play_light);
                        mBtnPlay.setState(MaterialPlayPauseDrawable.State.Pause);
                        Intent iPause = new Intent(this, MediaPlayerService.class);
                        iPause.setAction(Constants.ACTION.PAUSE);
                        iPause.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, false);
                        startService(iPause);
                    }else {
                        mBtnPlay.setImageResource(R.drawable.ic_media_pause_light);
                        mBtnPlay.setState(MaterialPlayPauseDrawable.State.Play);
                        Intent iPlay = new Intent(this, MediaPlayerService.class);
                        iPlay.setAction(Constants.ACTION.PLAY);
                        iPlay.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, false);
                        startService(iPlay);
                    }
                }
                Toast.makeText(this, "test thôi chưa xài", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layout_play_media:

                break;
            case R.id.btn_title_media:
                if (!SongManager.getInstance().queue().isEmpty()) {

                    Intent intent = new Intent(HomeActivity.this, PlayActivity.class);
                    intent.putExtra(Constants.INTENT.TYPE, Constants.VALUE.NEW_SONGS);
                    intent.putExtra(Constants.INTENT.POSITION,
                            mSharedPrefsManager.getInteger(Constants.PREFERENCES.POSITION, 0));
                    startActivity(intent);
                }
                break;
            case R.id.menu_item:

                if (!isHide){
                    mRcOptionMenu.setAlpha(1);
                    Animation fadeIn = AnimationUtils.loadAnimation(HomeActivity.this,R.anim.slide_in_right);
                    mRcOptionMenu.setAnimation(fadeIn);

                    mRcOptionMenu.setVisibility(View.VISIBLE);
                    isHide = true;
                }else if (isHide){
                    mRcOptionMenu.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mRcOptionMenu.setVisibility(View.GONE);
                        }
                    });

                    isHide = false;
                }
                break;
            case R.id.menu_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.vp_Home:
                if (mRcOptionMenu.getVisibility() == View.GONE){
                    Animation fadeIn = AnimationUtils.loadAnimation(HomeActivity.this,R.anim.fadein);
                    mRcOptionMenu.setAnimation(fadeIn);
                    mRcOptionMenu.setVisibility(View.VISIBLE);
                    isHide = true;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private int currentViewPagerPosition = 0;
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mNavigationView.getMenu().getItem(currentViewPagerPosition).setChecked(false);
        mNavigationView.getMenu().getItem(position).setChecked(true);
        currentViewPagerPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
