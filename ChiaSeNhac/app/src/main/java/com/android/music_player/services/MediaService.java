package com.android.music_player.services;

import android.content.Intent;
import android.media.MediaMetadata;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.android.music_player.MySessionCallback;
import com.android.music_player.PackageValidator;
import com.android.music_player.R;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MediaService extends MediaBrowserServiceCompat {
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";
    private MediaSessionCompat mMediaSessionCompat;
    private PackageValidator packageValidator;
    private ArrayList<SongModel> mSongs;
    private Utils.Builder builder;
    private String MEDIA_SEARCH_SUPPORTED = "android.media.browse.SEARCH_SUPPORTED";

   /**
    * mô tả trạng thái media hiện tại dang là gì
    * vị trí player hiện tại
    **/
    private PlaybackStateCompat.Builder mStateBuilder;


    /**
     * lưu dữ thông tin thuộc tính của bài hát : tên, nghệ sĩ
     * thông tin thời lương bài hát
     * album hiển thị của việc khóa màn hình tối đa của bitmap 320x320dp
     **/
    private MediaMetadata.Builder mMetadataBuilder;

    /**
     * Khởi tạo service và truyền intent xuống
     **/

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaSessionCompat = new MediaSessionCompat(this, "MediaService");
        mMediaSessionCompat.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mMediaSessionCompat.setPlaybackState(mStateBuilder.build());

        // MySessionCallback() has methods that handle callbacks from a media controller
        mMediaSessionCompat.setCallback(new MySessionCallback());

        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mMediaSessionCompat.getSessionToken());
        packageValidator = new PackageValidator(this, R.xml.allowed_media_browser_callers);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*
         * Analyze and Acting on the intent received by Service
         * Every request to Service should be with one of the intents in our switch
         */
        if (intent != null && intent.getAction() != null){
            MediaButtonReceiver.handleIntent(mMediaSessionCompat, intent);
            handleUIActions(intent.getAction());
        }

        return START_STICKY;
    }

    /******* ---------------------------------------------------------------
     Defaults
     ----------------------------------------------------------------*******/
    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.
        if (packageValidator.isKnownCaller(clientPackageName, clientUid)){
            return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
        }else {
            return new BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null);
        }

    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        if (TextUtils.equals(MY_EMPTY_MEDIA_ROOT_ID, parentId)){
            result.sendResult(null);
            return;
        }
    }


    /**
     * ACTION INTENT
     */
    private void handleUIActions(String action){
       switch (action){
           case Constants.ACTION.PLAY:
               break;
           case Constants.ACTION.PAUSE:
               break;
           case Constants.ACTION.NEXT:
               break;
           case Constants.ACTION.PREVIOUS:
               break;
           case Constants.ACTION.SEEK:
               break;
           case Constants.ACTION.STOP:
               break;
           case Constants.ACTION.SHUFFLE:
               break;
           case Constants.ACTION.REPEAT:
               break;
       }
    }
}
