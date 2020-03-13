package com.droidheat.musicplayer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.RemoteViews;

import com.droidheat.musicplayer.activities.HomeActivity;
import com.droidheat.musicplayer.services.MusicPlayback;

public class MusicWidget4x1 extends AppWidgetProvider {
    private int playbackState = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        playbackState = intent.getIntExtra(Constants.PREFERENCES.state, 0);
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds){
            try {
                PendingIntent pendingIntent = PendingIntent.getActivities(
                        context,0 , new Intent[]{new Intent(context, HomeActivity.class)},0
                );


                Intent previousIntent = new Intent(context, MusicPlayback.class);
                Intent playIntent = new Intent(context, MusicPlayback.class);
                Intent nextIntent = new Intent(context, MusicPlayback.class);
                Intent repeatIntent = new Intent(context, MusicPlayback.class);


                previousIntent.setAction(Constants.ACTION.ACTION_TRACK_PREV);
                playIntent.setAction(Constants.ACTION.ACTION_PLAY);
                nextIntent.setAction(Constants.ACTION.ACTION_TRACK_NEXT);
                repeatIntent.setAction(Constants.ACTION.ACTION_REPEAT);

                PendingIntent pPreviousIntent = PendingIntent.getService(context, 0,
                        previousIntent,0);
                PendingIntent pPlayIntent = PendingIntent.getService(context, 0,
                        playIntent,0);
                PendingIntent pNextIntent = PendingIntent.getService(context, 0,
                        nextIntent,0);
                PendingIntent pRepeatIntent = PendingIntent.getService(context, 0,
                        repeatIntent,0);

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget4x1);

                views.setOnClickPendingIntent(R.id.playImageView, pPlayIntent);
                views.setOnClickPendingIntent(R.id.prevImageView, pPreviousIntent);
                views.setOnClickPendingIntent(R.id.nextImageView, pNextIntent);
                views.setOnClickPendingIntent(R.id.albumArtImageView, pendingIntent);
                views.setOnClickPendingIntent(R.id.titleTextView, pendingIntent);
                views.setOnClickPendingIntent(R.id.albumTextView, pendingIntent);
                views.setOnClickPendingIntent(R.id.artistTextView, pendingIntent);

                if (playbackState == PlaybackStateCompat.STATE_PLAYING){
                    views.setImageViewResource(R.id.playImageView, R.drawable.app_pause);
                }else {
                    views.setImageViewResource(R.id.playImageView, R.drawable.app_play);
                }

                if (MusicPlayback.mMediaSessionCompat
                        .getController().getMetadata().getBitmap(MediaMetadata.METADATA_KEY_ALBUM) != null){
                    views.setImageViewBitmap(R.id.albumArtImageView,
                            MusicPlayback.mMediaSessionCompat
                                    .getController().getMetadata().getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART));
                }
                views.setTextViewText(R.id.titleTextView, MusicPlayback.mMediaSessionCompat.getController().getMetadata().getString(MediaMetadataCompat.METADATA_KEY_TITLE));
                views.setTextViewText(R.id.albumTextView, MusicPlayback.mMediaSessionCompat.getController().getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ALBUM));

                appWidgetManager.updateAppWidget(appWidgetId, views);

            }catch (Exception e ){
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgetblank);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, HomeActivity.class), 0);
                views.setOnClickPendingIntent(R.id.titleTextView, pendingIntent);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }
}
