package com.android.music_player.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.android.music_player.R;
import com.android.music_player.managers.MusicLibrary;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;

public class Utils {


    public static String formatTime(int currentDuration){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df = new SimpleDateFormat("mm : ss", Locale.getDefault());
        df.setTimeZone(tz);
        String time = String.valueOf(df.format(currentDuration));
        return time;
    }

    public static void ToastShort(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void ToastLong(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

    /**
     * Get a color value from a theme attribute.
     * @param context used for getting the color.
     * @param attribute theme attribute.
     * @param defaultColor default to use.
     * @return color value
     */
    public static int getThemeColor(Context context, int attribute, int defaultColor) {
        int themeColor = 0;
        String packageName = context.getPackageName();
        try {
            Context packageContext = context.createPackageContext(packageName, 0);
            ApplicationInfo applicationInfo =
                    context.getPackageManager().getApplicationInfo(packageName, 0);
            packageContext.setTheme(applicationInfo.theme);
            Resources.Theme theme = packageContext.getTheme();
            TypedArray ta = theme.obtainStyledAttributes(new int[] {attribute});
            themeColor = ta.getColor(0, defaultColor);
            ta.recycle();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return themeColor;
    }

    public static int accentColor(SharedPrefsUtils mSharedPrefsUtils) {
        switch (mSharedPrefsUtils.getString("ACCENT_COLOR", "PINK")) {
            case "GREEN":
                return R.color.green;
            case "ORANGE":
                return R.color.orange;
            case "PINK":
                return R.color.pink;
            case "CYAN":
                return R.color.cyan;
            case "YELLOW":
                return R.color.yellow;
            case "PURPLE":
                return R.color.purple;
            case "RED":
                return R.color.red;
            case "GREY":
                return R.color.grey;
            default:
                return R.color.pink;
        }
    }

    public static int randomInt(){
        Random rd = new Random();
        return rd.nextInt(MusicLibrary.music.size());
    }


    private static AnimatedVectorDrawableCompat vectorDrawableCompat;
    private static AnimatedVectorDrawable vectorDrawable;
    public static void UpdateButtonPlay(ImageButton button, boolean isPlay){
        if (isPlay){
            button.setImageResource(R.drawable.avd_play_to_pause);
            Drawable drawable = button.getDrawable();
            if (drawable instanceof AnimatedVectorDrawableCompat) {
                vectorDrawableCompat = (AnimatedVectorDrawableCompat) drawable;
                vectorDrawableCompat.start();
            }else if (drawable instanceof AnimatedVectorDrawable){
                vectorDrawable = (AnimatedVectorDrawable) drawable;
                vectorDrawable.start();
            }
        }else {
            button.setImageResource(R.drawable.avd_pause_to_play);
            Drawable drawable = button.getDrawable();
            if (drawable instanceof AnimatedVectorDrawableCompat) {
                vectorDrawableCompat = (AnimatedVectorDrawableCompat) drawable;
                vectorDrawableCompat.start();
            }else if (drawable instanceof AnimatedVectorDrawable){
                vectorDrawable = (AnimatedVectorDrawable) drawable;
                vectorDrawable.start();
            }
        }
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }


}
