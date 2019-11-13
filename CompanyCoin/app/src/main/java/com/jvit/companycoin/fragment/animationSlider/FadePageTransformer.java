package com.jvit.companycoin.fragment.animationSlider;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class FadePageTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(@NonNull View view, float position) {
        if (position < -1 || position > 1) {
            view.setAlpha(0);
        }
        else if (position <= 0 || position <= 1) {
            // Calculate alpha. Position is decimal in [-1,0] or [0,1]
            float alpha = (position <= 0) ? position + 1 : 1 - position;
            view.setAlpha(alpha);
        }
        else if (position == 0) {
            view.setAlpha(1);
        }
    }
}
