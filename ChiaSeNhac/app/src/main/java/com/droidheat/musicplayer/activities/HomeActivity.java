package com.droidheat.musicplayer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.adapters.OptionMenuAdapter;
import com.droidheat.musicplayer.adapters.ViewPagerAdapter;
import com.droidheat.musicplayer.fragments.AlbumGridFragment;
import com.droidheat.musicplayer.fragments.AllSongsFragment;
import com.droidheat.musicplayer.fragments.ArtistGridFragment;
import com.droidheat.musicplayer.fragments.HomeFragment;
import com.droidheat.musicplayer.fragments.PlaylistFragment;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener,
        OptionMenuAdapter.SetOnClickItemMenu, ViewPager.OnPageChangeListener {
    private View view_LayoutMenu;

    private TextView txt_Home, txt_Albums, txt_Artists, txt_Songs, txt_PlayLists;
    private ImageView menu_Item, menu_Search, img_Artists, img_Songs, img_PlayLists;
    private LinearLayout ll_Home, ll_Albums, ll_Artists, ll_Songs, ll_PlayLists;
    private ArrayList<LinearLayout> linearLayouts;
    private TabLayout mTabLayout_Home;
    private Toolbar mToolbar_Home;
    private ViewPager mViewPager_Home;
    private OptionMenuAdapter mOptionMenuAdapter;
    private String tag = "BBB";
    private RecyclerView rc_OptionMenu;
    private ;
    private ArrayList<Fragment> mFragments;
    private ArrayList<String> mMenus;
    private ViewPagerAdapter mViewPagerAdapter;
    private boolean isHide = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initMenu();
        initView();
        assignView();
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
        final BottomNavigationView mNavigationView = findViewById(R.id.nav_view);

        mNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mFragments = new ArrayList<>();
        mFragments.add(new HomeFragment());
        mFragments.add(new AllSongsFragment());
        mFragments.add(new AlbumGridFragment());
        mFragments.add(new ArtistGridFragment());
        mFragments.add(new PlaylistFragment());
        mViewPagerAdapter.setFragments(mFragments);

        viewPager.setAdapter(mViewPagerAdapter);
    }
    private void initView() {
//        view_LayoutMenu = findViewById(R.id.layout_menu);
        menu_Item = findViewById(R.id.menu_item);
        menu_Search = findViewById(R.id.menu_search);
        rc_OptionMenu = findViewById(R.id.rc_OptionMenu);
        rc_OptionMenu.setVisibility(View.GONE);



        mViewPager_Home = findViewById(R.id.vp_Home);
        setupViewPager(mViewPager_Home);
        mViewPager_Home.setCurrentItem(0);
        mViewPager_Home.addOnPageChangeListener(this);


    }

    private void assignView(){
        mOptionMenuAdapter = new OptionMenuAdapter(mMenus, this);
        mOptionMenuAdapter.setSetOnClick(this);
        rc_OptionMenu.setAdapter(mOptionMenuAdapter);

        rc_OptionMenu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        menu_Item.setOnClickListener(this);
        menu_Search.setOnClickListener(this);
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


    @Override
    public void onClickItem(String item) {
        switch (item){
            case Constants.MENU.Set_Sleep_Timer:
                startActivity(new Intent(this, TimerActivity.class));
                break;
            case Constants.MENU.Sync_Music:
                startActivity(new Intent(this, SplashActivity.class).putExtra("sync", true));
                break;
            case Constants.MENU.Settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case Constants.MENU.Equalizer:
                startActivity(new Intent(this, EqualizerActivity.class));
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
            case R.id.menu_item:
                Log.d("BBB", "menu_item");
                if (!isHide){
                    rc_OptionMenu.setVisibility(View.VISIBLE);
                    isHide = true;
                }else if (isHide){
                    rc_OptionMenu.setVisibility(View.GONE);
                    isHide = false;
                }
                break;
            case R.id.menu_search:
                Log.d("BBB", "menu_search");
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;

        }
    }

    private int currentViewPagerPosition = 0;
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mNavigationView.getMenu().getItem(currentViewPagerPosition).setCheckable(false);
        mNavigationView.getMenu().getItem(position).setCheckable(true);
        currentViewPagerPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
