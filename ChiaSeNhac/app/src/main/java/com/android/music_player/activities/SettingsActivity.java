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

import com.android.music_player.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


public class SettingsActivity extends AppCompatActivity {
    private LinearLayout ll_play_media;
    private boolean sPlayerInfoLongPressed;
    private SlidingUpPanelLayout mSlidingUpPanel;
    private CoordinatorLayout coordinatorLayout;
    private static final String TAG = "SettingsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ll_play_media = findViewById(R.id.ll_play_media);

        mSlidingUpPanel = findViewById(R.id.sliding_panel);
        mSlidingUpPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
            }
        });
        mSlidingUpPanel.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mSlidingUpPanel != null &&
                (mSlidingUpPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mSlidingUpPanel.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mSlidingUpPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }

    }
    private void setupSlidingUpPanel() {

        final ViewTreeObserver observer = ll_play_media.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                mSlidingUpPanel.setupSlidingUpPanel(mSongsRecyclerView, Gravity.BOTTOM, ll_play_media.getHeight());
                ll_play_media.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }


    //https://stackoverflow.com/questions/6183874/android-detect-end-of-long-press
    @SuppressLint("ClickableViewAccessibility")
    private void setupPlayerInfoView() {
        ll_play_media.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!sPlayerInfoLongPressed) {

                    sPlayerInfoLongPressed = true;
                }
                return true;
            }
        });
        ll_play_media.setOnTouchListener(new View.OnTouchListener() {
            @Override
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (sPlayerInfoLongPressed) {
                        sPlayerInfoLongPressed = false;
                    }
                }
                return false;
            }
        });
    }

}
