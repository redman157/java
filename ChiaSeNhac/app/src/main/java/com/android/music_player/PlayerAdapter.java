package com.android.music_player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import androidx.annotation.NonNull;

public class PlayerAdapter {
    private static final float MEDIA_VOLUME_DEFAULT = 1.0f;
    private static final float MEDIA_VOLUME_DUCK = 0.2f;
    private boolean mAudioNoisyReceiverRegistered = false;
    private final Context mApplicationContext;
    private final AudioManager mAudioManager;
    private final AudioFocusHelper mAudioFocusHelper;

    private boolean mPlayOnAudioFocus = false;

    private final BroadcastReceiver mAudioNoisyReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                        if (isPlaying()) {
                            pause();
                        }
                    }
                }
            };



    private void registerAudioNoisyReceiver() {
        if (!mAudioNoisyReceiverRegistered) {
            mApplicationContext.registerReceiver(mAudioNoisyReceiver,
                    new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
            mAudioNoisyReceiverRegistered = true;
        }
    }

    public PlayerAdapter(@NonNull Context context) {
        mApplicationContext = context.getApplicationContext();
        mAudioManager = (AudioManager) mApplicationContext.getSystemService(Context.AUDIO_SERVICE);
        mAudioFocusHelper = new AudioFocusHelper();
    }
}
