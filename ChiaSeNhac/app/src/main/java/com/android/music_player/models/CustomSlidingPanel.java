package com.android.music_player.models;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class CustomSlidingPanel extends SlidingUpPanelLayout implements SlidingUpPanelLayout.PanelSlideListener{


    public CustomSlidingPanel(Context context) {
        super(context);
    }

    public CustomSlidingPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSlidingPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {

    }
}
