package com.android.music_player.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.music_player.R;
import com.android.music_player.models.SongModel;
import com.android.music_player.services.MediaPlayerService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public static void ChangeSongService(Context context,
                                         ArrayList<SongModel> songs){
        Intent iSetMusic = new Intent(context, MediaPlayerService.class);
        iSetMusic.setAction(Constants.ACTION.CHANGE_SONG);
        iSetMusic.putExtra(Constants.INTENT.CHANGE_MUSIC, songs);
        context.startService(iSetMusic);
    }

    public static void PlayMediaService(Context context){
        Intent intent = new Intent(context, MediaPlayerService.class);
        intent.setAction(Constants.ACTION.PLAY);
        context.startService(intent);
    }

    public static void PauseMediaService(Context context){
        Intent iPlayMedia = new Intent(context ,MediaPlayerService.class);
        iPlayMedia.setAction(Constants.ACTION.PAUSE);
        context.startService(iPlayMedia);
    }

    public static void isPlayMediaService(Context context ,
                                          ArrayList<SongModel> songs){
        Intent iPlay = new Intent(context, MediaPlayerService.class);
        iPlay.setAction(Constants.ACTION.IS_PLAY);
        iPlay.putExtra(Constants.INTENT.CHANGE_MUSIC, songs);

        context.startService(iPlay);
    }

    public static void isPlayMediaService(Context context){
        Intent iPlay = new Intent(context, MediaPlayerService.class);
        iPlay.setAction(Constants.ACTION.IS_PLAY);
        context.startService(iPlay);
    }


    public static void NextMediaService(Context context){
        Intent iNext = new Intent(context, MediaPlayerService.class);
        iNext.setAction(Constants.ACTION.NEXT);
        context.startService(iNext);
    }


    public static void PreviousMediaService(Context context){
        Intent iPrevious = new Intent(context, MediaPlayerService.class);
        iPrevious.setAction(Constants.ACTION.PREVIOUS);

        context.startService(iPrevious);
    }

    public static void StopMediaService(Context context){
        Intent iPrevious = new Intent(context, MediaPlayerService.class);
        iPrevious.setAction(Constants.ACTION.STOP);
        context.startService(iPrevious);
    }

    public static void RepeatMediaService(Context context, boolean isRepeat){
        Intent iRepeat = new Intent(context, MediaPlayerService.class);
        iRepeat.setAction(Constants.ACTION.REPEAT);
        iRepeat.putExtra(Constants.INTENT.IS_REPEAT, isRepeat);
        context.startService(iRepeat);
    }

    public static void ShuffleMediaService(Context context ,ArrayList<SongModel> songShuffle,
                                           boolean isShuffle){
        Intent inShuffle = new Intent(context, MediaPlayerService.class);
        inShuffle.setAction(Constants.ACTION.SHUFFLE);
        inShuffle.putExtra(Constants.INTENT.CHANGE_MUSIC, songShuffle);
        inShuffle.putExtra(Constants.INTENT.IS_SHUFFLE, isShuffle);
        context.startService(inShuffle);
    }

    public static void ContinueMediaService(Context context, int seekPos){
        Intent iSeekChoose = new Intent(context, MediaPlayerService.class);
        iSeekChoose.setAction(Constants.ACTION.SEEK);
        iSeekChoose.putExtra(Constants.INTENT.POSITION_SONG, seekPos);
        context.startService(iSeekChoose);
    }

}
