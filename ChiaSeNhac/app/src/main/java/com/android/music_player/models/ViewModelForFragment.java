package com.android.music_player.models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModelForFragment extends ViewModel {
    private MutableLiveData<String> selected = new MutableLiveData<>();

    public MutableLiveData<String> getSelected() {
        return selected;
    }

    public void setSelected(MutableLiveData<String> selected) {
        this.selected = selected;
    }
}
