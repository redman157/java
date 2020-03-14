package com.droidheat.musicplayer.fragments;

import android.media.Image;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.droidheat.musicplayer.PlayMusic;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.adapters.ChangeMusicPagerAdapter;
import com.droidheat.musicplayer.manager.ImageUtils;
import com.droidheat.musicplayer.models.SongModel;

public class ChangeMusicFragment extends Fragment  {
    private SongModel mSongModel;
    private ImageView img_AblumArt;
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
            img_AblumArt = view.findViewById(R.id.item_img_ChangeMusic);

        }
        text_artists.setText(mSongModel.getArtist());
        text_tittle.setText(mSongModel.getTitle());
        (new ImageUtils(getContext())).getSmallImageByPicasso(mSongModel.getAlbumID(), img_AblumArt);

        return view;
    }

    public void setSongModel(SongModel songModel) {
        mSongModel = songModel;
    }

}
