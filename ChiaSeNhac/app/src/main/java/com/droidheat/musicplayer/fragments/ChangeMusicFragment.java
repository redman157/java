package com.droidheat.musicplayer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.adapters.ChangeMusicPagerAdapter;
import com.droidheat.musicplayer.models.SongModel;

public class ChangeMusicFragment extends Fragment implements ChangeMusicPagerAdapter.SongModelFragment {
    private SongModel songModel;
    private TextView text_playing, text_tittle, text_artists;
    private View view;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.item_change_music, null);
            text_playing = view.findViewById(R.id.item_text_playing);
            text_tittle = view.findViewById(R.id.item_text_title);
            text_artists = view.findViewById(R.id.item_text_album);

        }
        text_artists.setText(songModel.getArtist());
        text_tittle.setText(songModel.getTitle());

        return view;
    }

    @Override
    public Fragment getModel(SongModel songModel) {
        this.songModel = songModel;
        return this;
    }
}
