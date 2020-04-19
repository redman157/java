package com.android.music_player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.TranslateAnimation;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

@SuppressLint("AppCompatCustomView")
public class IconView extends AppCompatImageView {
    public IconView(Context context) {
        super(context);
    }

    public IconView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if ((widthMeasureSpec < getMaxWidth()) && (getMaxWidth() != Integer.MAX_VALUE || getMaxHeight() != Integer.MAX_VALUE)) {
            if (getMaxWidth() > getMaxHeight()) {
                setMeasuredDimension(getMaxHeight(), getMaxHeight());
            } else {
                setMeasuredDimension(getMaxWidth(), getMaxWidth());
            }
        } else {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
        }
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
    }

    public void setAnimationUp(int num){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                num); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        this.startAnimation(animate);
    }
    public void setAnimationDown(int num){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                num,                 // fromYDelta
                0); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        this.startAnimation(animate);
    }

}
