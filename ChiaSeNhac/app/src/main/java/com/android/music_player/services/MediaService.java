package com.android.music_player.services;

import android.content.Intent;
import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.service.media.MediaBrowserService;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.session.MediaButtonReceiver;

import com.android.music_player.R;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class MediaService extends MediaBrowserService {
    private MediaSessionCompat mMediaSessionCompat;

    private ArrayList<SongModel> mSongs;
   /**
    * mô tả trạng thái media hiện tại dang là gì
    * vị trí player hiện tại
    **/
    private PlaybackState.Builder mStateBuilder;


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
        mMediaSessionCompat = new MediaSessionCompat(this);
        mMediaSessionCompat.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

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
        if(TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(getString(R.string.app_name), null);
        }
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowser.MediaItem>> result) {

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
