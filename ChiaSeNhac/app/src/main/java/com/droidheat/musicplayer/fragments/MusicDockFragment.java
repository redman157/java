package com.droidheat.musicplayer.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.droidheat.musicplayer.PlayMusic;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.activities.PlayActivity;
import com.droidheat.musicplayer.manager.SongsManager;
import com.droidheat.musicplayer.services.MusicPlayback;

public class MusicDockFragment extends Fragment implements View.OnClickListener, PlayMusic.CallBackListener {
    private View view;
    private Button mBtnTitle;
    private ImageView mImgArt;
    private ImageButton mImbPlay;
    private TextView mTextTitle, mTextArtists;
    private PlayMusic mPlayMusic;
    private SongsManager mSongsManager;
    private MediaBrowserCompat mMediaBrowser;
    public static MusicDockFragment newInstance() {

        Bundle args = new Bundle();

        MusicDockFragment fragment = new MusicDockFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayMusic = PlayMusic.getInstance();
        mSongsManager = SongsManager.getInstance();
        mSongsManager.setContext(getActivity());
        mPlayMusic.setActivity(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        mPlayMusic.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPlayMusic.disconnect();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_music_dock, null);
            initView();
            assignView();
        }

        mPlayMusic.setCallBack(this);
        mPlayMusic.initMediaBrowser();

        return view;

    }

    private void initView() {
        mTextTitle = view.findViewById(R.id.fm_text_title);
        mImbPlay = view.findViewById(R.id.fm_btn_Play);
        mBtnTitle = view.findViewById(R.id.fm_btn_title);
        mTextArtists = view.findViewById(R.id.fm_text_artists);
        mImgArt = view.findViewById(R.id.fm_img_albumArt);
    }

    private void assignView(){
        mBtnTitle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fm_btn_title:
                startActivity(new Intent(getActivity(), PlayActivity.class));
                break;
        }
    }

    @Override
    public void getState(PlaybackStateCompat stateCompat) {
        if (stateCompat == null) {
            return;
        }

        switch (stateCompat.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                mImbPlay.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.app_pause));
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                mImbPlay.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.app_play));
                break;
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                mImbPlay.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.app_play));
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                break;
            default:
//                Log.d(TAG, "Unhandled state " + state.getState());
            case PlaybackStateCompat.STATE_CONNECTING:
                break;
            case PlaybackStateCompat.STATE_ERROR:
                break;
            case PlaybackStateCompat.STATE_FAST_FORWARDING:
                break;
            case PlaybackStateCompat.STATE_REWINDING:
                break;
            case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT:
                break;
            case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS:
                break;
            case PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM:
                break;
        }
    }

    @Override
    public void getMetadataCompat(MediaMetadataCompat compat) {
        mTextTitle.setText(compat.getText(MediaMetadataCompat.METADATA_KEY_TITLE));
        mTextArtists.setText(compat.getText(MediaMetadataCompat.METADATA_KEY_ARTIST));
        mImgArt.setImageBitmap(compat.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART));
    }
}
