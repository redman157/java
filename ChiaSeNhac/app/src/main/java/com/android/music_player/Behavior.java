package com.android.music_player;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

public class Behavior extends CoordinatorLayout.Behavior<LinearLayout> {
    private Float normalizedRange = ((float) (0));
    private Point windowSize;
    private Context context;
    private int profileImageSizeSmall;
    private int profileImageSizeBig;
    private int profileImageMaxMargin, toolBarHeight;
    private View appBar;
    private int appBarHeight, profileNameHeight;
    private int profileTextContainerMaxHeight;
    private View profileImage, profileMisc, profileTextContainer, profileName, profileSubtitle, headerProfile;


    public Behavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        profileImageSizeBig = ((int) (context.getResources().getDimension(R.dimen.profile_big_size)));
        profileImageSizeSmall = ((int) (context.getResources().getDimension(R.dimen.profile_small_size)));
        profileImageMaxMargin = ((int) (context.getResources().getDimension(R.dimen.profile_image_margin_max)));
    }


    private Point displaySize(){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        Display display = wm.getDefaultDisplay();
        display.getSize(size);
        return size;
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull LinearLayout child, @NonNull View dependency) {
        boolean isDependencyAnAppBar = dependency instanceof AppBarLayout;
        if (isDependencyAnAppBar){
            initialize(child, dependency);
        }
        return isDependencyAnAppBar;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull LinearLayout child, @NonNull View dependency) {
        boolean isDependencyAnAppBar = dependency instanceof AppBarLayout;
        if (isDependencyAnAppBar){
            toolBarHeight = appBar.findViewById(R.id.tb_SongActivity).getHeight();
            updateNormalizedRange();
            updateOffset();
        }
        return isDependencyAnAppBar;
    }

    private void initialize(LinearLayout child, View dependency){
        windowSize = displaySize();
        appBar = dependency;
        appBarHeight = appBar.getHeight();
        headerProfile = child;
        profileImage = headerProfile.findViewById(R.id.profileImage);
        profileImage.setPivotX(0f);
        profileImage.setPivotY(0f);

        profileName = profileTextContainer.findViewById(R.id.profileName);
        profileNameHeight = profileName.getHeight();

        profileSubtitle = profileTextContainer.findViewById(R.id.profileSubtitle);
        profileMisc = profileTextContainer.findViewById(R.id.profileMisc);

        int profileSubtitleMaxHeight =
                calculateMaxHeightFromTextView((TextView)profileSubtitle);
        int profileMiscMaxHeight = calculateMaxHeightFromTextView(((TextView)profileMisc));
        profileTextContainerMaxHeight =
                profileNameHeight + profileSubtitleMaxHeight + profileMiscMaxHeight;
    }

    private int calculateMaxHeightFromTextView(TextView textView){

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(windowSize.x,
                View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredHeight();
    }

    public void updateNormalizedRange(){
        AppBarLayout view = (AppBarLayout)appBar;
        normalizedRange = normalize(
                appBar.getPivotY() + view.getTotalScrollRange(),
                0f,
                ((float) (view.getTotalScrollRange())));

        normalizedRange = 1f - normalizedRange;
    }

    private float normalize(float currentValue,float minValue,float maxValue) {
        float dividend = currentValue - minValue;
        float divisor = maxValue - minValue;
        return dividend / divisor;
    }

    private void updateOffset(){
        updateHeaderProfileOffset();
        updateProfileImageSize();
        updateProfileImageMargins();
        updateProfileTextContainerHeight();
        updateProfileTextMargin();
        updateSubtitleAndMiscAlpha();
    }

    private void updateHeaderProfileOffset() {
        headerProfile.setPivotY(appBar.getPivotY());
    }

    public void updateProfileImageSize() {
        float updatedValue = ((int) (getUpdatedInterpolatedValue(((float) (profileImageSizeBig)),
                ((float) (profileImageSizeSmall)))));
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) profileImage.getLayoutParams();
        lp.height = (int) updatedValue;
        lp.width = (int) updatedValue;
        if (updatedValue < 100){
            profileImage.setVisibility(View.GONE);
            profileName.setVisibility(View.GONE);
        }else {
            profileName.setVisibility(View.VISIBLE);
            profileImage.setVisibility(View.VISIBLE);
            profileImage.setLayoutParams(lp);
        }
    }

    private void updateProfileImageMargins(){
        float targetOpenAppbarValue = ((float) (calculateProfileImageSmallMargin()));
        float updatedValue = ((int) (getUpdatedInterpolatedValue(((float) (profileImageMaxMargin)),
                targetOpenAppbarValue)));
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) profileImage.getLayoutParams();
        lp.bottomMargin = (int) updatedValue;
        lp.leftMargin = (int) updatedValue;
        lp.rightMargin = (int) updatedValue;

        profileImage.setLayoutParams(lp);

    }


    private int calculateProfileImageSmallMargin() {
        float halfToolbarHeight = toolBarHeight / 2;
        float halfProfileImageSmall = profileImageSizeSmall / 2;
        return (int) (halfToolbarHeight - halfProfileImageSmall);
    }

    private void updateProfileTextContainerHeight() {
        int updatedValue =
                (int) getUpdatedInterpolatedValue(((float) (profileTextContainerMaxHeight)),
                        ((float) (toolBarHeight)));

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) profileTextContainer.getLayoutParams();
        lp.height = updatedValue;
        profileTextContainer.setLayoutParams(lp);
    }

    private void updateProfileTextMargin() {
        float targetOpenAppbarValue = calculateProfileTextMargin();
        int updatedValue = ((int) (getUpdatedInterpolatedValue(0f, targetOpenAppbarValue)));

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) profileName.getLayoutParams();
        lp.topMargin = updatedValue;
        profileName.setLayoutParams(lp);
    }

    private int calculateProfileTextMargin() {
        float halfToolbarHeight = toolBarHeight / 2;
        float halfProfileTextHeight = profileNameHeight / 2;
        return (int) (halfToolbarHeight - halfProfileTextHeight);
    }

    private void updateSubtitleAndMiscAlpha() {
        int updatedValue = (int) getUpdatedInterpolatedValue(1f, 0f);
        float poweredValue = ((float) (Math.pow(((double) (updatedValue)), 6.0)));

        profileSubtitle.setAlpha(poweredValue);
        profileMisc.setAlpha(poweredValue);
    }

    private float getIntercept(float m, float x, float b){
        return m * x + b;
    }

    private float getUpdatedInterpolatedValue(float openSizeTarget, float closedSizeTarget){
        return getIntercept(closedSizeTarget - openSizeTarget, normalizedRange, openSizeTarget);
    }
}
