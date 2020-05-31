package com.android.music_player.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.android.music_player.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.jetbrains.annotations.NotNull;


public class SettingsActivity extends AppCompatActivity {
    private LinearLayout ll_play_media;
    private boolean sPlayerInfoLongPressed;
    private SlidingUpPanelLayout mLayout;
    private CoordinatorLayout coordinatorLayout;
    private static final String TAG = "SettingsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mLayout = findViewById(R.id.activity_main);
        ll_play_media = findViewById(R.id.ll_play_media);

        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                        mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            ll_play_media.setVisibility(View.GONE);
        } else {
            ll_play_media.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }


}
