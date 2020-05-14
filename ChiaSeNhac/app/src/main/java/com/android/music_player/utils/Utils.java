package com.android.music_player.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.music_player.R;
import com.android.music_player.activities.PlayActivity;
import com.android.music_player.managers.SongManager;
import com.android.music_player.services.MediaPlayerService;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {
    private static Utils.Builder builder;
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

    public static void UpdateButtonPlay(Activity activity, ImageButton button){
        if (MediaPlayerService.mMediaPlayer != null && MediaPlayerService.mMediaPlayer.isPlaying()){
            button.setImageResource(R.drawable.ic_media_pause_light);
        }else {
            button.setImageResource(R.drawable.ic_media_play_light);
        }
    }

    public static void PlayMediaService(Context context,String type, int pos){
        Intent iPlay = new Intent(context, MediaPlayerService.class);
        iPlay.setAction(Constants.ACTION.PLAY);
        builder = new Utils.Builder();
        builder.putString(Constants.INTENT.TYPE, type);
        builder.putInteger(Constants.INTENT.CURR_POS,pos);
        iPlay.putExtras(builder.generate().getBundle());
        context.startService(iPlay);
    }

    public static void PauseMediaService(Context context, String type, int pos){
        Intent iPause = new Intent(context ,MediaPlayerService.class);
        iPause.setAction(Constants.ACTION.PAUSE);
        builder = new Utils.Builder();
        builder.putString(Constants.INTENT.TYPE, type);
        builder.putInteger(Constants.INTENT.CURR_POS,pos);
        iPause.putExtras(builder.generate().getBundle());
        context.startService(iPause);
    }

    public static void IntentToPlayActivity(Activity activity, int position, String type){
        SongManager manager = SongManager.getInstance();
        manager.setContext(activity);
        Intent intent = new Intent(activity, PlayActivity.class);
        Utils.Builder builder = new Utils.Builder();
        builder.putString(Constants.INTENT.TYPE,type);
        builder.putInteger(Constants.INTENT.CHOOSE_POS, position);

        if (manager.isPlayCurrentSong(manager.getListSong(type).get(position).getPath())) {
            Log.d("BBB", "Utils --- IntentToPlayActivity: true");
            builder.putBoolean(Constants.INTENT.SONG_CONTINUE, true);
        }else {
            Log.d("BBB", "Utils --- IntentToPlayActivity: false");
            Utils.PauseMediaService(activity, type, position);
            builder.putBoolean(Constants.INTENT.SONG_CONTINUE, false);
        }
        intent.putExtras(builder.generate().getBundle());

        activity.finish();
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        builder.generate().clear();
    }

    public static void isPlayMediaService(Context context ,
                                          String type, int pos){
        Intent iPlay = new Intent(context, MediaPlayerService.class);
        iPlay.setAction(Constants.ACTION.IS_PLAY);
        builder = new Utils.Builder();
        builder.putString(Constants.INTENT.TYPE, type);
        builder.putInteger(Constants.INTENT.CURR_POS,pos);
        iPlay.putExtras(builder.generate().getBundle());
        context.startService(iPlay);
    }

    public static void NextMediaService(Context context, String type, int pos){
        Intent iNext = new Intent(context, MediaPlayerService.class);
        iNext.setAction(Constants.ACTION.NEXT);
        builder = new Utils.Builder();
        builder.putString(Constants.INTENT.TYPE, type);
        builder.putInteger(Constants.INTENT.CURR_POS,pos);
        iNext.putExtras(builder.generate().getBundle());
        context.startService(iNext);
    }
    
    public static void PreviousMediaService(Context context, String type, int pos){
        Intent iPrevious = new Intent(context, MediaPlayerService.class);
        iPrevious.setAction(Constants.ACTION.PREVIOUS);
        builder = new Utils.Builder();
        builder.putString(Constants.INTENT.TYPE, type);
        builder.putInteger(Constants.INTENT.CURR_POS,pos);
        iPrevious.putExtras(builder.generate().getBundle());
        context.startService(iPrevious);
    }

    public static void StopMediaService(Context context, String type, int pos){
        Intent iStop = new Intent(context, MediaPlayerService.class);
        iStop.setAction(Constants.ACTION.STOP);
        builder = new Utils.Builder();
        builder.putString(Constants.INTENT.TYPE, type);
        builder.putInteger(Constants.INTENT.CURR_POS,pos);
        iStop.putExtras(builder.generate().getBundle());
        context.startService(iStop);
    }

    public static void RepeatMediaService(Context context, boolean isRepeat, String type, int pos){
        Intent iRepeat = new Intent(context, MediaPlayerService.class);
        iRepeat.setAction(Constants.ACTION.REPEAT);

        builder = new Utils.Builder();
        builder.putBoolean(Constants.INTENT.IS_REPEAT, isRepeat);
        builder.putString(Constants.INTENT.TYPE, type);
        builder.putInteger(Constants.INTENT.CURR_POS,pos);
        iRepeat.putExtras(builder.generate().getBundle());
        context.startService(iRepeat);
    }

    public static void ShuffleMediaService(Context context ,boolean isShuffle,String type, int pos){
        Intent inShuffle = new Intent(context, MediaPlayerService.class);
        inShuffle.setAction(Constants.ACTION.SHUFFLE);
        builder = new Utils.Builder();
        builder.putBoolean(Constants.INTENT.IS_SHUFFLE, isShuffle);
        builder.putString(Constants.INTENT.TYPE, type);
        builder.putInteger(Constants.INTENT.CURR_POS,pos);
        inShuffle.putExtras(builder.generate().getBundle());
        context.startService(inShuffle);
    }

    public static void ContinueMediaService(Context context,String type, int pos, int seekPos){
        Intent iSeekChoose = new Intent(context, MediaPlayerService.class);
        iSeekChoose.setAction(Constants.ACTION.SEEK);
        builder = new Utils.Builder();
        builder.putInteger(Constants.INTENT.POSITION_SONG, seekPos);
        builder.putString(Constants.INTENT.TYPE, type);
        builder.putInteger(Constants.INTENT.CURR_POS,pos);
        iSeekChoose.putExtras(builder.generate().getBundle());
        context.startService(iSeekChoose);
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
}
