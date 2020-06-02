package com.android.music_player.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
    private static Utils.Builder builder;
    private static AnimatedVectorDrawableCompat vectorDrawableCompat;
    private static AnimatedVectorDrawable vectorDrawable;
    public Utils(Bundle bundle){
        this.bundle = bundle;
    }

    public Utils(Intent intent){
        bundle = intent.getExtras();
    }

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


    // custom bundle
    private Bundle bundle;
    public static class Builder{
        private Bundle bundle;
        public Builder (){
            bundle = new Bundle();
        }
        public Utils.Builder putString(String key, String value){
            bundle.putString(key, value);
            return this;
        }

        public Utils.Builder putBoolean(String key, boolean value){
            bundle.putBoolean(key, value);
            return this;
        }

        public Utils.Builder putInteger(String key, int value){
            bundle.putInt(key, value);
            return this;
        }

        public Utils generate(){
            return new Utils(bundle);
        }
    }

    public Bundle getBundle() {
        return bundle;
    }

    public String getString(String key, String defaultValue){
        if (bundle!= null) {
            return bundle.getString(key);
        }else {
            return defaultValue;
        }
    }

    public void clear(){
        if(bundle!=null) {
            bundle.clear();

        }
    }

    public int getInteger(String key, int defaultValue){
        if (bundle!= null) {
            return bundle.getInt(key);
        }else {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue){
        if (bundle!= null) {
            return bundle.getBoolean(key);
        }else {
            return defaultValue;
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
