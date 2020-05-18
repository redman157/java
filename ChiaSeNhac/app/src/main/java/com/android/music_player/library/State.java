package com.android.music_player.library;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({State.STATE_CREATED,
        State.STATE_INITIALIZING,
        State.STATE_INITIALIZED,
        State.STATE_ERROR})
@Retention(RetentionPolicy.SOURCE)
@interface State{
    /**
     * State indicating the source was created, but no initialization has performed.
     */
    int STATE_CREATED = 1 ;

    /**
     * State indicating initialization of the source is in progress.
     */
    int STATE_INITIALIZING = 2;

    /**
     * State indicating the source has been initialized and is ready to be used.
     */
    int STATE_INITIALIZED = 3;

    /**
     * State indicating an error has occurred.
     */
    int STATE_ERROR = 4;
}
