package com.android.music_player.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.music_player.R;
import com.android.music_player.services.MediaPlayerService;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {
    private static BundleUtils.Builder builder;
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

    public static void PlayMediaService(Context context,String type, int pos){
        Intent iPlay = new Intent(context, MediaPlayerService.class);
        iPlay.setAction(Constants.ACTION.PLAY);
        builder = new BundleUtils.Builder();
        builder.putString(Constants.INTENT.TYPE, type);
        builder.putInteger(Constants.INTENT.CURR_POS,pos);
        context.startService(iPlay);
    }

    public static void PauseMediaService(Context context, String type, int pos){
        Intent iPause = new Intent(context ,MediaPlayerService.class);
        iPause.setAction(Constants.ACTION.PAUSE);
        builder = new BundleUtils.Builder();
        builder.putString(Constants.INTENT.TYPE, type);
        builder.putInteger(Constants.INTENT.CURR_POS,pos);
        context.startService(iPause);
    }

    public static void isPlayMediaService(Context context ,
                                          String type, int pos){
        Intent iPlay = new Intent(context, MediaPlayerService.class);
        iPlay.setAction(Constants.ACTION.IS_PLAY);
        builder = new BundleUtils.Builder();
        builder.putString(Constants.INTENT.TYPE, type);
        builder.putInteger(Constants.INTENT.CURR_POS,pos);
        context.startService(iPlay);
    }

    public static void NextMediaService(Context context, String type, int pos){
        Intent iNext = new Intent(context, MediaPlayerService.class);
        iNext.setAction(Constants.ACTION.NEXT);
        builder = new BundleUtils.Builder();
        builder.putString(Constants.INTENT.TYPE, type);
        builder.putInteger(Constants.INTENT.CURR_POS,pos);
        context.startService(iNext);
    }
    
    public static void PreviousMediaService(Context context, String type, int pos){
        Intent iPrevious = new Intent(context, MediaPlayerService.class);
        iPrevious.setAction(Constants.ACTION.PREVIOUS);
        builder = new BundleUtils.Builder();
        builder.putString(Constants.INTENT.TYPE, type);
        builder.putInteger(Constants.INTENT.CURR_POS,pos);
        context.startService(iPrevious);
    }

    public static void StopMediaService(Context context){
        Intent iPrevious = new Intent(context, MediaPlayerService.class);
        iPrevious.setAction(Constants.ACTION.STOP);
        context.startService(iPrevious);
    }

    public static void RepeatMediaService(Context context, boolean isRepeat, String type, int pos){
        Intent iRepeat = new Intent(context, MediaPlayerService.class);
        iRepeat.setAction(Constants.ACTION.REPEAT);
        builder.putBoolean(Constants.INTENT.IS_REPEAT, isRepeat);
        builder = new BundleUtils.Builder();
        builder.putString(Constants.INTENT.TYPE, type);
        builder.putInteger(Constants.INTENT.CURR_POS,pos);
        context.startService(iRepeat);
    }

    public static void ShuffleMediaService(Context context ,boolean isShuffle, String type,
                                           int pos){
        Intent inShuffle = new Intent(context, MediaPlayerService.class);
        inShuffle.setAction(Constants.ACTION.SHUFFLE);
        builder = new BundleUtils.Builder();
        builder.putBoolean(Constants.INTENT.IS_SHUFFLE, isShuffle);
        builder.putString(Constants.INTENT.TYPE, type);
        builder.putInteger(Constants.INTENT.CURR_POS,pos);

        context.startService(inShuffle);
    }

    public static void ContinueMediaService(Context context,String type, int pos, int seekPos){
        Intent iSeekChoose = new Intent(context, MediaPlayerService.class);
        iSeekChoose.setAction(Constants.ACTION.SEEK);
        builder = new BundleUtils.Builder();
        builder.putInteger(Constants.INTENT.POSITION_SONG, seekPos);
        builder.putString(Constants.INTENT.TYPE, type);
        builder.putInteger(Constants.INTENT.CURR_POS,pos);
        context.startService(iSeekChoose);
    }

}
