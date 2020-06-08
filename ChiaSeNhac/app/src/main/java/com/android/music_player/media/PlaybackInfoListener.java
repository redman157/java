package com.android.music_player.media;

import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.android.music_player.services.MediaService;
/**
 * Listener to provide state updates from {@link MediaPlayerAdapter} (the media player)
 * to {@link MediaService} (the service that holds our {@link MediaSessionCompat}.
 */
public abstract class PlaybackInfoListener {

    public abstract void onPlaybackStateChange(PlaybackStateCompat state);

    public abstract void onPlaybackCompleted(boolean isNext);



}