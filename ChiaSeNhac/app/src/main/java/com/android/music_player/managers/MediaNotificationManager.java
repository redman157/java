package com.android.music_player.managers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.app.NotificationCompat.MediaStyle;
import androidx.media.session.MediaButtonReceiver;

import com.android.music_player.R;
import com.android.music_player.activities.HomeActivity;
import com.android.music_player.services.MediaService;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageHelper;

public class MediaNotificationManager {
    public static final int NOTIFICATION_ID = 412;
    private static final String TAG = MediaNotificationManager.class.getSimpleName();
    private static final String CHANNEL_ID = "com.android.music_player.channel";
    private static final int REQUEST_CODE = 501;
    private final MediaService mMusicService;
    private int mAccent;

    private NotificationCompat.Action mPlayAction;
    private NotificationCompat.Action mPauseAction;
    private NotificationCompat.Action mNextAction;
    private NotificationCompat.Action mPrevAction;
    private NotificationCompat.Action mStopAction;

    private final NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
    @SuppressLint("WrongConstant")
    public MediaNotificationManager(MediaService service) {
        mMusicService = service;
        mNotificationManager =
                (NotificationManager) mMusicService.getSystemService(Context.NOTIFICATION_SERVICE);

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
//        mNotificationManager.cancelAll();
    }

    public void setAccentColor(int color) {
        mAccent = ContextCompat.getColor(mMusicService, color);
    }

    private void notificationAction(){
        mPlayAction = new NotificationCompat.Action(
                R.drawable.avd_play_to_pause,
                mMusicService.getString(R.string.label_play),
                MediaButtonReceiver.buildMediaButtonPendingIntent(mMusicService,
                        PlaybackStateCompat.ACTION_PLAY));

        mPauseAction = new NotificationCompat.Action(
                R.drawable.avd_pause_to_play,
                mMusicService.getString(R.string.label_pause),
                MediaButtonReceiver.buildMediaButtonPendingIntent(mMusicService,
                        PlaybackStateCompat.ACTION_PAUSE));

        mNextAction = new NotificationCompat.Action(
                R.drawable.app_next,
                mMusicService.getString(R.string.label_next),
                MediaButtonReceiver.buildMediaButtonPendingIntent(mMusicService,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT));

        mStopAction = new NotificationCompat.Action(
                R.drawable.ic_close_black_24dp,
                mMusicService.getString(R.string.label_stop),
                MediaButtonReceiver.buildMediaButtonPendingIntent(mMusicService,
                        PlaybackStateCompat.ACTION_STOP));

        mPrevAction = new NotificationCompat.Action(
                R.drawable.app_previous,
                mMusicService.getString(R.string.label_previous),
                MediaButtonReceiver.buildMediaButtonPendingIntent(mMusicService,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));
    }

    public android.app.NotificationManager getNotificationManager() {
        return mNotificationManager;
    }

    public Notification getNotification(MediaMetadataCompat metadata,
                                        @NonNull PlaybackStateCompat state,
                                        MediaSessionCompat.Token token) {
        boolean isPlaying = state.getState() == PlaybackStateCompat.STATE_PLAYING;
        MediaDescriptionCompat description = metadata.getDescription();
        NotificationCompat.Builder builder = buildNotification(state, token, isPlaying, description);
        return builder.build();
    }

    private NotificationCompat.Builder buildNotification(@NonNull PlaybackStateCompat state,
                                                         MediaSessionCompat.Token token,
                                                         boolean isPlaying,
                                                         MediaDescriptionCompat description){

        // Create the (mandatory) notification channel when running on Android Oreo.
        if (isAndroidOOrHigher()) {
            createChannel();
        }
        notificationAction();
        if (isAndroidOOrHigher()){
            mNotificationBuilder = new androidx.core.app.NotificationCompat.Builder(mMusicService,
                    CHANNEL_ID)
                    .setChannelId(CHANNEL_ID)
                    .setAutoCancel(false)
                    .setShowWhen(false)

                    .setSmallIcon(R.drawable.app_icon_music)
                    .setLargeIcon(ImageHelper.getAlbumArtNotification(mMusicService,
                            Long.valueOf(MusicLibrary.getAlbumRes(description.getMediaId()))))
                    .setColor(mAccent)
                    .setContentIntent(createContentIntent())
                    .setContentTitle(description.getTitle())
                    .setContentText(description.getSubtitle())
                    .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                            mMusicService, PlaybackStateCompat.ACTION_STOP))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setStyle( new MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2))
                    .addAction(mPrevAction)
                    .addAction(isPlaying ? mPauseAction : mPlayAction)
                    .addAction(mNextAction)
                    .addAction(mStopAction);

        }else {
            mNotificationBuilder = new NotificationCompat.Builder(mMusicService, CHANNEL_ID);
            mNotificationBuilder.setStyle(new MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2))
                    .setColor(mAccent)
                    .setSmallIcon(R.drawable.app_icon_music)
                    .setContentIntent(createContentIntent())
                    .setContentText(description.getSubtitle())
                    .setLargeIcon(ImageHelper.getAlbumArtNotification(mMusicService,
                            Long.valueOf(MusicLibrary.getAlbumRes(description.getMediaId()))))  .setSubText(MusicLibrary.getMusicFilename(description.getMediaId()))
                    .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                            mMusicService, PlaybackStateCompat.ACTION_STOP))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .addAction(mPrevAction)
                    .addAction(isPlaying ? mPauseAction:mPlayAction)
                    .addAction(mNextAction)
                    .addAction(mStopAction);
        }
        return mNotificationBuilder;
    }
    // Does nothing on versions of Android earlier than O.
    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            // The user-visible name of the channel.
            CharSequence name = "MediaSession";
            // The user-visible description of the channel.
            String description = "MediaSession and MediaPlayer";
            int importance = android.app.NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(false);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.enableVibration(false);
            mChannel.setShowBadge(false);
            mNotificationManager.createNotificationChannel(mChannel);
            Log.d(TAG, "createChannel: New channel created");
        } else {
            Log.d(TAG, "createChannel: Existing channel reused");
        }
    }

    private boolean isAndroidOOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    private PendingIntent createContentIntent() {
        Intent openUI = new Intent(mMusicService, HomeActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        openUI.putExtra(Constants.INTENT.OPEN_SLIDING_PANEL, true);
        return PendingIntent.getActivity(
                mMusicService, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
    }

}
