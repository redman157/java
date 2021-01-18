/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.music_player.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.android.music_player.R;
import com.android.music_player.fragments.HomeFragment;
import com.android.music_player.fragments.SettingsFragment;
import com.android.music_player.managers.QueueManager;
import com.android.music_player.utils.ChangeTheme;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.SharedPrefsUtils;
import com.google.android.material.navigation.NavigationView;

/**
 * Abstract activity with toolbar, navigation drawer and cast support. Needs to be extended by
 * any activity that wants to be shown as a top level activity.
 *
 * The requirements for a subclass is to call {@link #initializeToolbar()} on onCreate, after
 * setContentView() is called and have three mandatory layout elements:
 * a {@link Toolbar} with id 'toolbar',
 * a {@link DrawerLayout} with id 'drawerLayout' and
 * a {@link android.widget.ListView} with id 'drawerList'.
 */
public abstract class ActionBarCastActivity extends AppCompatActivity {

    private static final String TAG = "ActionBarCastActivity";

    private static final int DELAY_MILLIS = 1000;
    private MenuItem mMediaRouteMenuItem;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    public static int REQUEST_CODE_RESTORE = 100;
    private boolean mToolbarInitialized;
    private int mAccent;
    private boolean sThemeInverted;
    private int mItemToOpenWhenDrawerCloses = -1;
    private SharedPrefsUtils mSharedPrefsUtils ;

    private QueueManager mQueueManager;
    private ImageView imgBack;

    private final FragmentManager.OnBackStackChangedListener mBackStackChangedListener =
        new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                updateDrawerToggle();
            }
        };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Activity onCreate");
        mQueueManager = QueueManager.getInstance(this);
        mSharedPrefsUtils = new SharedPrefsUtils(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mToolbarInitialized) {
            throw new IllegalStateException("You must run super.initializeToolbar at " +
                "the end of your onCreate method");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Whenever the fragment back stack changes, we may need to update the
        // action bar toggle: only top level screens show the hamburger-like icon, inner
        // screens - either Activities or fragments - show the "Up" icon instead.
        getSupportFragmentManager().addOnBackStackChangedListener(mBackStackChangedListener);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getSupportFragmentManager().removeOnBackStackChangedListener(mBackStackChangedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // If not handled by drawerToggle, home needs to be handled by returning to previous
        if (item != null && item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        switch (item.getItemId()) {
            case R.id.changeTheme:
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_choose_accent_color);
                dialog.findViewById(R.id.black).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setInteger(
                                Constants.PREFERENCES.ACCENT_COLOR, R.style.OverlayThemeBlack);
                        ChangeTheme.setAccent(ActionBarCastActivity.this, R.color.black);
                        dialog.cancel();
                        Intent intent = new Intent(ActionBarCastActivity.this,
                                SplashActivity.class); // from getIntent()
                        intent.putExtra(Constants.VALUE.SYNC,
                                false);
                        startActivity(intent);
                    }
                });
                dialog.findViewById(R.id.white).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.ACCENT_COLOR,
                                R.style.OverlayThemeWhite);
                        ChangeTheme.setAccent(ActionBarCastActivity.this, R.color.white);
                        dialog.cancel();
                        Intent intent = new Intent(ActionBarCastActivity.this,
                                SplashActivity.class); // from getIntent()
                        intent.putExtra(Constants.VALUE.SYNC, false);
                        startActivity(intent);

                    }
                });
                dialog.findViewById(R.id.purple).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.ACCENT_COLOR,
                                R.style.OverlayThemePurpe);
                        ChangeTheme.setAccent(ActionBarCastActivity.this, R.color.purple);
                        dialog.cancel();
                        Intent intent = new Intent(ActionBarCastActivity.this,
                                SplashActivity.class); // from getIntent()
                        intent.putExtra(Constants.VALUE.SYNC, false);
                        startActivity(intent);
                    }
                });
                dialog.findViewById(R.id.brown).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.ACCENT_COLOR,
                                R.style.OverlayThemeBrown);
                        ChangeTheme.setAccent(ActionBarCastActivity.this, R.color.brown_400);
                        dialog.cancel();
                        Intent intent = new Intent(ActionBarCastActivity.this,
                                SplashActivity.class); // from getIntent()
                        intent.putExtra(Constants.VALUE.SYNC, false);
                        startActivity(intent);
                    }
                });
                dialog.findViewById(R.id.cyan).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.ACCENT_COLOR,
                                R.style.OverlayThemeCyan);
                        ChangeTheme.setAccent(ActionBarCastActivity.this, R.color.cyan);
                        dialog.cancel();
                        Intent intent = new Intent(ActionBarCastActivity.this,
                                SplashActivity.class); // from getIntent()
                        intent.putExtra(Constants.VALUE.SYNC, false);
                        startActivity(intent);
                    }
                });

                dialog.findViewById(R.id.blue).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.ACCENT_COLOR,
                                R.style.OverlayThemeBlue);
                        ChangeTheme.setAccent(ActionBarCastActivity.this, R.color.blue_A400);
                        dialog.cancel();
                        Intent intent = new Intent(ActionBarCastActivity.this,
                                SplashActivity.class); // from getIntent()
                        intent.putExtra(Constants.VALUE.SYNC, false);
                        startActivity(intent);
                    }
                });
                dialog.findViewById(R.id.deep_purple).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.ACCENT_COLOR,
                                R.style.OverlayThemeDeepPurple);
                        ChangeTheme.setAccent(ActionBarCastActivity.this, R.color.deep_purple_A400);
                        dialog.cancel();
                        Intent intent = new Intent(ActionBarCastActivity.this,
                                SplashActivity.class); // from getIntent()
                        intent.putExtra(Constants.VALUE.SYNC, false);
                        startActivity(intent);
                    }
                });
                dialog.findViewById(R.id.yellow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.ACCENT_COLOR,
                                R.style.OverlayThemeYellow);
                        ChangeTheme.setAccent(ActionBarCastActivity.this, R.color.yellow);
                        dialog.cancel();
                        Intent intent = new Intent(ActionBarCastActivity.this,
                                SplashActivity.class); // from getIntent()
                        intent.putExtra(Constants.VALUE.SYNC, false);
                        startActivity(intent);
                    }
                });

                dialog.findViewById(R.id.green).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.ACCENT_COLOR,
                                R.style.OverlayThemeLime);
                        ChangeTheme.setAccent(ActionBarCastActivity.this, R.color.green);
                        dialog.cancel();
                        Intent intent = new Intent(ActionBarCastActivity.this,
                                SplashActivity.class); // from getIntent()
                        intent.putExtra(Constants.VALUE.SYNC, false);
                        startActivity(intent);
                    }
                });
                dialog.findViewById(R.id.orange).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.ACCENT_COLOR,
                                R.style.OverlayThemeOrange);
                        ChangeTheme.setAccent(ActionBarCastActivity.this, R.color.orange);
                        dialog.cancel();
                        Intent intent = new Intent(ActionBarCastActivity.this,
                                SplashActivity.class); // from getIntent()
                        intent.putExtra(Constants.VALUE.SYNC, false);
                        startActivity(intent);
                    }
                });
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        baseBackPressed();
    }

    public void baseBackPressed(){
        // If the drawer is open, back will close it
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        // Otherwise, it may return to the previous fragment stack
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        } else {
            // Lastly, it will rely on the system behavior for back
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bạn có muốn thoát App không ?");
            builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(1);
                }
            });
            builder.show();
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mToolbar.setTitle(title);
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        mToolbar.setTitle(titleId);
    }

    protected void initializeToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setPopupTheme(R.style.AppThemeToolbar);
        if (mToolbar == null) {
            throw new IllegalStateException("Layout is required to include a Toolbar with id " +
                "'toolbar'");
        }
        mToolbar.inflateMenu(R.menu.main);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        setSupportActionBar(mToolbar);

        mToolbarInitialized = true;
    }

    private void populateDrawerItems(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    menuItem.setChecked(true);
                    mItemToOpenWhenDrawerCloses = menuItem.getItemId();
                    mDrawerLayout.closeDrawers();
                    return true;
                }
            });
    }

    protected void updateDrawerToggle() {
        if (mDrawerToggle == null) {
            return;
        }
        boolean isRoot = getFragmentManager().getBackStackEntryCount() == 0;
        mDrawerToggle.setDrawerIndicatorEnabled(isRoot);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(!isRoot);
            getSupportActionBar().setDisplayHomeAsUpEnabled(!isRoot);
            getSupportActionBar().setHomeButtonEnabled(!isRoot);
        }
        if (isRoot) {
            mDrawerToggle.syncState();
        }
    }

    /**
     * @see MediaControllerCompat#getMediaController(Activity)
     */
    public MediaControllerCompat getSupportMediaController() {
        return MediaControllerCompat.getMediaController(this);
    }

    /**
     * @see MediaControllerCompat#setMediaController(Activity, MediaControllerCompat)
     */
    public void setSupportMediaController(MediaControllerCompat mediaController) {
        MediaControllerCompat.setMediaController(this, mediaController);
    }



}
