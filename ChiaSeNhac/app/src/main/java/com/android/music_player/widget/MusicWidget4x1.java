package com.android.music_player.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.RemoteViews;

import com.android.music_player.R;
import com.android.music_player.activities.HomeActivity;
import com.android.music_player.utils.Constants;

public class MusicWidget4x1 extends AppWidgetProvider {
    private int playbackState = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        playbackState = intent.getIntExtra(Constants.PREFERENCES.STATE, 0);
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

/*

                Intent previousIntent = new Intent(context, MediaPlayerService.class);
                Intent playIntent = new Intent(context, MediaPlayerService.class);
                Intent nextIntent = new Intent(context, MediaPlayerService.class);
                Intent repeatIntent = new Intent(context, MediaPlayerService.class);

*/

               /* previousIntent.setAction(Constants.ACTION.PREVIOUS);
                playIntent.setAction(Constants.ACTION.PLAY);
                nextIntent.setAction(Constants.ACTION.NEXT);
                repeatIntent.setAction(Constants.ACTION.REPEAT);

                PendingIntent pPreviousIntent = PendingIntent.getService(context, 0,
                        previousIntent,0);
                PendingIntent pPlayIntent = PendingIntent.getService(context, 0,
                        playIntent,0);
                PendingIntent pNextIntent = PendingIntent.getService(context, 0,
                        nextIntent,0);
                PendingIntent pRepeatIntent = PendingIntent.getService(context, 0,
                        repeatIntent,0);*/

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget4x1);

             /*   views.setOnClickPendingIntent(R.id.playImageView, pPlayIntent);
                views.setOnClickPendingIntent(R.id.prevImageView, pPreviousIntent);
                views.setOnClickPendingIntent(R.id.nextImageView, pNextIntent);*/
                views.setOnClickPendingIntent(R.id.albumArtImageView, pendingIntent);
                views.setOnClickPendingIntent(R.id.titleTextView, pendingIntent);
                views.setOnClickPendingIntent(R.id.albumTextView, pendingIntent);
                views.setOnClickPendingIntent(R.id.artistTextView, pendingIntent);

                if (playbackState == PlaybackStateCompat.STATE_PLAYING){
                    views.setImageViewResource(R.id.playImageView, R.drawable.app_pause);
                }else {
                    views.setImageViewResource(R.id.playImageView, R.drawable.app_play);
                }


                // chú ý tìm và search khúc này updateData UI widget
              /*  if (MusicPlayback.mMediaSessionCompat
                        .getController().getMetadata().getBitmap(MediaMetadata.METADATA_KEY_ALBUM) != null){
                    views.setImageViewBitmap(R.id.albumArtImageView,
                            MusicPlayback.mMediaSessionCompat
                                    .getController().getMetadata().getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART));
                }
                views.setTextViewText(R.id.titleTextView, MusicPlayback.mMediaSessionCompat.getController().getMetadata().getString(MediaMetadataCompat.METADATA_KEY_TITLE));
                views.setTextViewText(R.id.albumTextView, MusicPlayback.mMediaSessionCompat.getController().getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ALBUM));*/

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
