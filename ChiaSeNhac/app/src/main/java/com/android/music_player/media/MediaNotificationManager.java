package com.android.music_player.media;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import com.android.music_player.R;
import com.android.music_player.activities.HomeActivity;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.services.MediaService;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;

public class MediaNotificationManager {
    public static final int NOTIFICATION_ID = 412;
    private static final String TAG = MediaNotificationManager.class.getSimpleName();
    private static final String CHANNEL_ID = "com.android.music_player.channel";
    private static final int REQUEST_CODE = 501;
    private final MediaService mService;

    private final NotificationCompat.Action mPlayAction;
    private final NotificationCompat.Action mPauseAction;
    private final NotificationCompat.Action mNextAction;
    private final NotificationCompat.Action mPrevAction;
    private final NotificationCompat.Action mStopAction;
    private final NotificationManager mNotificationManager;

    public MediaNotificationManager(MediaService service) {
        mService = service;
        mNotificationManager =
                (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);

        mPlayAction = new NotificationCompat.Action(
                R.drawable.app_play,
                Constants.ACTION.PLAY,
                MediaButtonReceiver.buildMediaButtonPendingIntent(mService,
                        PlaybackStateCompat.ACTION_PLAY));

        mPauseAction = new NotificationCompat.Action(
                R.drawable.app_pause,
                Constants.ACTION.PAUSE,
                MediaButtonReceiver.buildMediaButtonPendingIntent(mService,
                        PlaybackStateCompat.ACTION_PAUSE));

        mNextAction = new NotificationCompat.Action(
                R.drawable.app_next,
                Constants.ACTION.NEXT,
                MediaButtonReceiver.buildMediaButtonPendingIntent(mService,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT));

        mStopAction = new NotificationCompat.Action(
                R.drawable.ic_close_black_24dp,
                "Stop",
                MediaButtonReceiver.buildMediaButtonPendingIntent(mService,
                        PlaybackStateCompat.ACTION_STOP));

        mPrevAction = new NotificationCompat.Action(
                R.drawable.app_previous,
                Constants.ACTION.PREVIOUS,
                MediaButtonReceiver.buildMediaButtonPendingIntent(mService,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        mNotificationManager.cancelAll();
    }
    private PendingIntent closeNotification() {
        Intent dismissIntent = new Intent(Constants.ACTION.CLOSE_NOTIFICATION);
        return PendingIntent.getBroadcast(mService, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    public NotificationManager getNotificationManager() {
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
        androidx.core.app.NotificationCompat.Builder builder;
        // Create the (mandatory) notification channel when running on Android Oreo.
        if (isAndroidOOrHigher()) {
            createChannel();
        }

        if (isAndroidOOrHigher()){
            builder = new androidx.core.app.NotificationCompat.Builder(mService,
                    CHANNEL_ID)
                    .setChannelId(CHANNEL_ID)
                    .setAutoCancel(false)

                    // For backwards compatibility with Android L and earlier.
                    .setShowWhen(false)
                    .setColorized(true).setColor(mService.getResources().getColor(R.color.white))

                    .setSmallIcon(R.drawable.ic_music_note_white_24dp)
                    .setLargeIcon(ImageUtils.getAlbumArt(mService,
                            Long.valueOf(MusicLibrary.getAlbumRes(description.getMediaId()))))
                    // Pending intent that is fired when user clicks on notification.
                    .setContentIntent(createContentIntent())
                    // Title - Usually Song name.
                    .setContentTitle(description.getTitle())
                    // Subtitle - Usually Artist name.
                    .setContentText(description.getSubtitle())

                    // When notification is deleted (when playback is paused and notification can be
                    // deleted) fire MediaButtonPendingIntent with ACTION_STOP.
                    .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                            mService, PlaybackStateCompat.ACTION_STOP))
                    // Show controls on lock screen even when user hides sensitive content.
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                    .setStyle( new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setShowCancelButton(true)
                            .setCancelButtonIntent(
                                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                                            mService,
                                            PlaybackStateCompat.ACTION_STOP))
                            .setMediaSession(token));
        }else {
            builder = new NotificationCompat.Builder(mService, CHANNEL_ID);
            builder.setStyle( new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(token)
                        .setShowActionsInCompactView(0, 1, 2)
                        // For backwards compatibility with Android L and earlier.
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                    mService,
                                    PlaybackStateCompat.ACTION_STOP)))
                    .setColorized(true).setColor(mService.getResources().getColor(R.color.white))
                    .setSmallIcon(R.drawable.ic_music_note_white_24dp)
                    // Pending intent that is fired when user clicks on notification.
                    .setContentIntent(createContentIntent())
                    // Title - Usually Song name.
                    .setContentTitle(description.getTitle())
                    // Subtitle - Usually Artist name.
                    .setContentText(description.getSubtitle())
                    .setLargeIcon(ImageUtils.getAlbumArt(mService,
                            Long.valueOf(MusicLibrary.getAlbumRes(description.getMediaId()))))  .setSubText(MusicLibrary.getMusicFilename(description.getMediaId()))
                    // When notification is deleted (when playback is paused and notification can be
                    // deleted) fire MediaButtonPendingIntent with ACTION_STOP.
                    .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                            mService, PlaybackStateCompat.ACTION_STOP))
                    // Show controls on lock screen even when user hides sensitive content.
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }

        // If skip to next action is enabled.
        if ((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
            builder.addAction(mPrevAction);
        }

        builder.addAction(isPlaying ? mPauseAction : mPlayAction);

        // If skip to prev action is enabled.
        if ((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
            builder.addAction(mNextAction);
        }


        builder.addAction(mStopAction);


        return builder;
    }
    // Does nothing on versions of Android earlier than O.
    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            // The user-visible name of the channel.
            CharSequence name = "MediaSession";
            // The user-visible description of the channel.
            String description = "MediaSession and MediaPlayer";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(
                    new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
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
        Intent openUI = new Intent(mService, HomeActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(
                mService, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
    }
}
