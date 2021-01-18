package com.android.music_player.interfaces;

import com.android.music_player.models.MusicModel;

import java.util.ArrayList;

public interface OnClickItemListener {
    void onAddMusicToPlayList(String namePlayList);
    void onChooseItemLibrary(ArrayList<String> models);
}