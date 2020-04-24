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

    public static void ChangeSongService(Context context, boolean isPlayActivity,
                                         ArrayList<SongModel> songs){
        Intent iSetMusic = new Intent(context, MediaPlayerService.class);
        iSetMusic.setAction(Constants.ACTION.CHANGE_SONG);
        iSetMusic.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, isPlayActivity);
        iSetMusic.putExtra(Constants.INTENT.CHANGE_MUSIC, songs);
        context.startService(iSetMusic);
    }

    public static void PlayMediaService(Context context ,boolean isPlayActivity){
        Intent intent = new Intent(context, MediaPlayerService.class);
        intent.setAction(Constants.ACTION.PLAY);
        intent.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, isPlayActivity);
        context.startService(intent);
    }

    public static void PauseMediaService(Context context ,boolean isPlayActivity){
        Intent iPlayMedia = new Intent(context ,MediaPlayerService.class);
        iPlayMedia.setAction(Constants.ACTION.PAUSE);
        iPlayMedia.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, isPlayActivity);
        context.startService(iPlayMedia);
    }

    public static void isPlayMediaService(Context context ,boolean isPlayActivity,
                                          ArrayList<SongModel> songs){
        Intent iPlay = new Intent(context, MediaPlayerService.class);
        iPlay.setAction(Constants.ACTION.IS_PLAY);
        iPlay.putExtra(Constants.INTENT.CHANGE_MUSIC, songs);
        iPlay.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, isPlayActivity);
        context.startService(iPlay);
    }


    public static void NextMediaService(Context context ,boolean isPlayActivity){
        Intent iNext = new Intent(context, MediaPlayerService.class);
        iNext.setAction(Constants.ACTION.NEXT);
        iNext.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, isPlayActivity);
        context.startService(iNext);
    }


    public static void PreviousMediaService(Context context ,boolean isPlayActivity){
        Intent iPrevious = new Intent(context, MediaPlayerService.class);
        iPrevious.setAction(Constants.ACTION.PREVIOUS);
        iPrevious.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, isPlayActivity);
        context.startService(iPrevious);
    }

    public static void StopMediaService(Context context ,boolean isPlayActivity){
        Intent iPrevious = new Intent(context, MediaPlayerService.class);
        iPrevious.setAction(Constants.ACTION.STOP);
        iPrevious.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, isPlayActivity);
        context.startService(iPrevious);
    }

    public static void RepeatMediaService(Context context ,boolean isPlayActivity, boolean isRepeat){
        Intent iRepeat = new Intent(context, MediaPlayerService.class);
        iRepeat.setAction(Constants.ACTION.REPEAT);
        iRepeat.putExtra(Constants.INTENT.IS_REPEAT, isRepeat);
        iRepeat.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, isPlayActivity);
        context.startService(iRepeat);
    }

    public static void ShuffleMediaService(Context context ,ArrayList<SongModel> songShuffle,
                                           boolean isPlayActivity, boolean isShuffle){
        Intent inShuffle = new Intent(context, MediaPlayerService.class);
        inShuffle.setAction(Constants.ACTION.CHANGE_SONG);
        inShuffle.putExtra(Constants.INTENT.CHANGE_MUSIC, songShuffle);
        inShuffle.putExtra(Constants.INTENT.IS_SHUFFLE, isShuffle);
        inShuffle.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, isPlayActivity);
        context.startService(inShuffle);
    }

    public static void ContinueMediaService(Context context,
                                            boolean isPlayActivity, int seekPos){
        Intent iSeekChoose = new Intent(context, MediaPlayerService.class);
        iSeekChoose.setAction(Constants.ACTION.SEEK);
        iSeekChoose.putExtra(Constants.INTENT.POSITION_SONG, seekPos);
        iSeekChoose.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY, isPlayActivity);
        context.startService(iSeekChoose);
    }

}
