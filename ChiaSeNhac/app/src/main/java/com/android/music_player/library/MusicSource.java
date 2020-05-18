package com.android.music_player.library;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;

import java.util.List;



/**
 * Interface used by [MusicService] for looking up [MediaMetadataCompat] objects.
 *
 * Because Kotlin provides methods such as [Iterable.find] and [Iterable.filter],
 * this is a convient interface to have on sources.
 */
public interface MusicSource extends Iterable<MediaMetadataCompat> {
    /**
     * Begins loading the data for this music source.
     */

     void load();

    /**
     * Method which will perform a given action after this [MusicSource] is ready to be used.
     *
     * @param performAction A lambda expression to be called with a boolean parameter when
     * the source is ready. `true` indicates the source was successfully prepared, `false`
     * indicates an error occurred.
     */
    boolean whenReady(boolean performAction);

    List<MediaMetadataCompat> search(String query, Bundle extras);

}

