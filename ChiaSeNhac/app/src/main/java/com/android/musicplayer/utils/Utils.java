package com.android.musicplayer.utils;

import android.content.Context;
import android.widget.Toast;

import com.android.musicplayer.R;

import java.text.SimpleDateFormat;
import java.util.Locale;
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

    public static int accentColor(SharedPrefsUtils mSharedPrefsUtils) {
        switch (mSharedPrefsUtils.getString("accentColor", "pink")) {
            case "green":
                return R.color.green;
            case "orange":
                return R.color.orange;
            case "pink":
                return R.color.pink;
            case "cyan":
                return R.color.cyan;
            case "yellow":
                return R.color.yellow;
            case "purple":
                return R.color.purple;
            case "red":
                return R.color.red;
            case "grey":
                return R.color.grey;
            default:
                return R.color.pink;

        }
    }

}
