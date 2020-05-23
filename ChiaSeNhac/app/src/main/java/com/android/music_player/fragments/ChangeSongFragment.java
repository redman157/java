package com.android.music_player.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.adapters.MusicAdapter;
import com.android.music_player.adapters.PlayListAdapter;
import com.android.music_player.interfaces.OnClickItem;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.managers.MusicManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.DialogUtils;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ChangeSongFragment extends Fragment implements View.OnClickListener, PlayListAdapter.OnClickItem {
    private SongModel mSongModel;
    private ImageView mImgAlbumArt, mImgShowList, mImgAddPlayList;
    private TextView text_leftTime, text_rightTime;
    private SeekBar sb_leftTime;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private View view;
    public TextView mTextArtist, mTextAlbum,mTextTittle;
    public Dialog mDlOptionMusic;

    private MusicAdapter mMusicAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<SongModel> musicMain;
    private MediaBrowserCompat.MediaItem mediaItem;
    private String songName;
    private SharedPrefsUtils mSharedPrefsUtils;
    private static ArrayList<SongModel> mSongModels;
    public static void newInstance(ArrayList<SongModel> songModels) {
        mSongModels = songModels;
    }

    public ArrayList<SongModel> getMusicMain() {
        return musicMain;
    }
    private OnClickItem onClickItem;

    private MediaMetadataCompat mMediaMetadataCompat;
    public ChangeSongFragment(OnClickItem onClickItem) {
        this.onClickItem = onClickItem;

    }
    private Dialog mDlAddPlayList, mDlAddMusic, mDlAllPlayList;
    public void setMusicMain(ArrayList<SongModel> musicMain) {
        this.musicMain = musicMain;
    }

    public void setMusicMain(MediaMetadataCompat mediaMetadataCompat) {
        mMediaMetadataCompat = mediaMetadataCompat;
    }

    public void setMedia(String songName, Context context){
        this.songName = songName;
//        this.mediaItem = mediaItem;
        mMediaMetadataCompat = MusicLibrary.getMetadata(context, songName);
    }
    private MusicManager mMusicManager;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPrefsUtils = new SharedPrefsUtils(getContext());

    }

    @Override
    public void onStart() {
        super.onStart();
        mMusicManager = MusicManager.getInstance();
        mMusicManager.setContext(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.item_change_music, null);
        initView();
        assignView();
        Log.d("XXX", mMediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        mTextArtist.setText(mMediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        mTextAlbum.setText(mMediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ALBUM));
        mTextTittle.setText(mMediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        ImageUtils.getInstance(getContext()).getSmallImageByPicasso(
                String.valueOf(MusicLibrary.getAlbumRes(mMediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))),
                mImgAlbumArt);

        return view;
    }

    private void initView(){
        mDlOptionMusic = new Dialog(getContext());
        mTextArtist = view.findViewById(R.id.item_text_artist);
        mTextTittle = view.findViewById(R.id.item_text_title);
        mTextAlbum = view.findViewById(R.id.item_text_album);
        mImgAlbumArt = view.findViewById(R.id.item_img_ChangeMusic);
        mImgShowList = view.findViewById(R.id.item_img_viewQueue);
        mImgAddPlayList = view.findViewById(R.id.item_img_addToPlayListImageView);
    }

    private void assignView(){
        mImgShowList.setOnClickListener(this);
        mImgAddPlayList.setOnClickListener(this);
    }

    public void setSongModel(SongModel songModel) {
        mSongModel = songModel;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.item_img_viewQueue:
                if (mSongModels == null){
                    mSongModels = musicMain;
                }
                mMusicAdapter = new MusicAdapter(getContext(), mDlOptionMusic, mSongModels);
                mMusicAdapter.notifyDataSetChanged();
                int pos = mSharedPrefsUtils.getInteger(Constants.PREFERENCES.POSITION, -1);
                for (int i = 0; i < mSongModels.size(); i++) {
                    if (mSongModels.get(i).getSongName().equals(musicMain.get(pos).getSongName())) {
                        mMusicAdapter.setPosition(i);
                        mMusicAdapter.setOnClick(onClickItem);
                        DialogUtils.showSelectSong(getContext(),mMusicAdapter, i);
                    }
                }

                break;
            case R.id.item_img_addToPlayListImageView:
                DialogUtils.showAllPlayList(getContext(), this);

                break;
        }
    }

    @Override
    public void onClick(String title) {
        DialogUtils.cancelDialog();
        DialogUtils.showAddSongs(getContext(),mSongModel ,title);

    }
}
