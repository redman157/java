package com.android.music_player.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.android.music_player.R;

import static com.android.music_player.utils.Utils.isMarshmallow;

public class ChangeTheme {
    private boolean sThemeInverted;

    static void invertTheme(@NonNull final Activity activity) {
        boolean isDark = isThemeInverted(activity);
        boolean value = !isDark;

        SharedPreferences preferences = activity.getSharedPreferences(Constants.PREFERENCES.THEME_PREF, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(Constants.PREFERENCES.THEME_VALUES, value).apply();
        activity.recreate();
    }
    public static int getAccent(@NonNull final Context context) {
        int accent;
        try {
            accent = context.getSharedPreferences(Constants.PREFERENCES.ACCENT_PREF,
                    Context.MODE_PRIVATE).getInt(Constants.PREFERENCES.ACCENT_VALUE, R.color.white);
        } catch (Exception e) {
            e.printStackTrace();
            accent = R.color.white;
        }
        return accent;
    }

    private static int resolveTheme(boolean isThemeDark, int accent) {

        int selectedTheme;

        switch (accent) {

            case R.color.red_A400:
                selectedTheme = isThemeDark ? R.style.AppThemeRedInverted : R.style.AppThemeRed;
                break;

            case R.color.pink_A400:
                selectedTheme = isThemeDark ? R.style.AppThemePinkInverted : R.style.AppThemePink;
                break;

            case R.color.purple_A400:
                selectedTheme = isThemeDark ? R.style.AppThemePurpleInverted : R.style.AppThemePurple;
                break;

            case R.color.deep_purple_A400:
                selectedTheme = isThemeDark ? R.style.AppThemeDeepPurpleInverted : R.style.AppThemeDeepPurple;
                break;

            case R.color.indigo_A400:
                selectedTheme = isThemeDark ? R.style.AppThemeIndigoInverted : R.style.AppThemeIndigo;
                break;

            case R.color.blue_A400:
                selectedTheme = isThemeDark ? R.style.AppThemeBlueInverted : R.style.AppThemeBlue;
                break;

            default:
            case R.color.light_blue_A400:
                selectedTheme = isThemeDark ? R.style.AppThemeLightBlueInverted : R.style.AppThemeLightBlue;
                break;

            case R.color.cyan_A400:
                selectedTheme = isThemeDark ? R.style.AppThemeCyanInverted : R.style.AppThemeCyan;
                break;

            case R.color.teal_A400:
                selectedTheme = isThemeDark ? R.style.AppThemeTealInverted : R.style.AppThemeTeal;
                break;

            case R.color.green_A400:
                selectedTheme = isThemeDark ? R.style.AppThemeGreenInverted : R.style.AppThemeGreen;
                break;

            case R.color.amber_A400:
                selectedTheme = isThemeDark ? R.style.AppThemeAmberInverted : R.style.AppThemeAmber;
                break;

            case R.color.orange_A400:
                selectedTheme = isThemeDark ? R.style.AppThemeOrangeInverted : R.style.AppThemeOrange;
                break;

            case R.color.deep_orange_A400:
                selectedTheme = isThemeDark ? R.style.AppThemeDeepOrangeInverted : R.style.AppThemeDeepOrange;
                break;

            case R.color.brown_400:
                selectedTheme = isThemeDark ? R.style.AppThemeBrownInverted : R.style.AppThemeBrown;
                break;

            case R.color.gray_400:
                selectedTheme = isThemeDark ? R.style.AppThemeGrayLightInverted : R.style.AppThemeGrayLight;
                break;

            case R.color.gray_800:
                selectedTheme = isThemeDark ? R.style.AppThemeGrayDarkInverted : R.style.AppThemeGrayDark;
                break;

            case R.color.blue_gray_400:
                selectedTheme = isThemeDark ? R.style.AppThemeBlueGrayInverted : R.style.AppThemeBlueGray;
                break;
        }
        return selectedTheme;
    }

    static void setThemeAccent(@NonNull final Activity activity, int accent) {
        SharedPreferences preferences = activity.getSharedPreferences(Constants.PREFERENCES.ACCENT_PREF, Context.MODE_PRIVATE);
        preferences.edit().putInt(Constants.PREFERENCES.ACCENT_VALUE, accent).apply();
        activity.recreate();
    }

    public static boolean isThemeInverted(@NonNull final Context context) {
        boolean isThemeInverted;
        try {
            isThemeInverted =
                    context.getSharedPreferences(Constants.PREFERENCES.THEME_PREF,
                            Context.MODE_PRIVATE).getBoolean(Constants.PREFERENCES.THEME_VALUES
                            , false);
        } catch (Exception e) {
            e.printStackTrace();
            isThemeInverted = false;
        }
        return isThemeInverted;
    }
    @TargetApi(23)
    private static void enableLightStatusBar(Activity activity, int accent) {

        View decorView = activity.getWindow().getDecorView();
        int oldSystemUiFlags = decorView.getSystemUiVisibility();
        int newSystemUiFlags = oldSystemUiFlags;

        boolean isColorDark = ColorUtils.calculateLuminance(accent) < 0.35;
        if (isColorDark) {
            newSystemUiFlags &= ~(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            newSystemUiFlags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }

        //just to avoid to set light status bar if it already enabled and viceversa
        if (newSystemUiFlags != oldSystemUiFlags) {
            decorView.setSystemUiVisibility(newSystemUiFlags);
        }
    }

    public static void setTheme(@NonNull final Activity activity, boolean isThemeInverted, int accent) {
        int theme = resolveTheme(isThemeInverted, accent);
        activity.setTheme(theme);
        if (isMarshmallow()) {
            enableLightStatusBar(activity, ContextCompat.getColor(activity, accent));
        }
    }
    private void updateResetStatus(boolean onPlaybackCompletion) {

        int themeColor = sThemeInverted ? Color.WHITE : Color.BLACK;
//        int color = onPlaybackCompletion ? themeColor : mPlayerAdapter.isReset() ? ContextCompat.getColor(this, mAccent) : themeColor;
//        mResetButton.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
}
