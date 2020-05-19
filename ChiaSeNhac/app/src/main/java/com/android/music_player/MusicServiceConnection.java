package com.android.music_player;

import android.content.ComponentName;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

public class MusicServiceConnection {
    private ComponentName componentName;
    private Context context;
    private MutableLiveData<Boolean> isConnected;
    private MutableLiveData<Boolean> networkFailure;
    public MusicServiceConnection(Context context, ComponentName serviceComponent){
        MutableLiveData mutableLiveData = new MutableLiveData();
        this.componentName = serviceComponent;
        this.context = context;
        isConnected = mutableLiveData;
    }

    public MutableLiveData getIsConnected() {
        return isConnected;
    }

    public MutableLiveData getNetworkFailure() {
        return networkFailure;
    }
}
