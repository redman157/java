package com.android.music_player.models;

import android.app.Application;
import android.support.v4.media.MediaMetadataCompat;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class StateViewModel extends AndroidViewModel {
    private MutableLiveData<String> mParentId = new MutableLiveData<>();
    private MutableLiveData<String> mNamePlayList = new MutableLiveData<>();
    private MutableLiveData<MediaMetadataCompat> mMediaDataCurrent = new MutableLiveData<>();
    public StateViewModel(@NonNull Application application) {
        super(application);

    }

    public MutableLiveData<MediaMetadataCompat> getMediaDataCurrent() {
        return mMediaDataCurrent;
    }

    public void setMediaDataCurrent(MediaMetadataCompat mMediaDataCurrent) {
        this.mMediaDataCurrent.setValue(mMediaDataCurrent);
    }

    public MutableLiveData<String> getParentId() {
        return mParentId;
    }

    public void setParentId(String parentId) {
        this.mParentId.setValue(parentId);
    }

    public MutableLiveData<String> getNamePlayList() {
        return mNamePlayList;
    }

    public void setNamePlayList(String mNamePlayList) {
        this.mNamePlayList.setValue(mNamePlayList);
    }
}
