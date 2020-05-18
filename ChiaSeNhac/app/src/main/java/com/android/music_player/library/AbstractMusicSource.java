package com.android.music_player.library;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractMusicSource implements MusicSource {
    final List<Boolean> onReadyListeners = new ArrayList<Boolean>();


    @State
    int state  = State.STATE_CREATED;

    public void setState(final int state) {
        this.state = state;
        if (state == State.STATE_INITIALIZED || state == State.STATE_ERROR){
            synchronized (onReadyListeners){
                this.state = state;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    onReadyListeners.forEach(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean listener) {
                            listener(state == State.STATE_INITIALIZED);
                        }
                    });
                }
            }
        }
    }

    protected abstract void listener(boolean b);


    @Override
    public void load() {

    }

    @Override
    public boolean whenReady(boolean performAction) {
        return false;
    }

    @Override
    public List<MediaMetadataCompat> search(String query, Bundle extras) {
        return null;
    }
}
