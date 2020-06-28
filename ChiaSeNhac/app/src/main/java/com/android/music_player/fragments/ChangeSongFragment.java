package com.android.music_player.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
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
import com.android.music_player.adapters.MusicDialogAdapter;
import com.android.music_player.adapters.PlayListAdapter;
import com.android.music_player.interfaces.OnMediaItem;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.models.MusicModel;
import com.android.music_player.utils.DialogHelper;
import com.android.music_player.utils.ImageHelper;
import com.android.music_player.utils.SharedPrefsUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ChangeSongFragment extends Fragment implements View.OnClickListener, PlayListAdapter.OnClickItem {
    private MusicModel mMusicModel;
    private ImageView mImgAlbumArt, mImgShowList, mImgAddPlayList;
    private TextView text_leftTime, text_rightTime;
    private SeekBar sb_leftTime;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private View view;
    public TextView mTextArtist, mTextAlbum,mTextTittle;
    public Dialog mDlOptionMusic;

    private MusicDialogAdapter mMusicDialogAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MusicModel> musicMain;
    private MediaBrowserCompat.MediaItem mediaItem;
    private SharedPrefsUtils mSharedPrefsUtils;
    private static ArrayList<MusicModel> mMusicModels;
    public static void newInstance(ArrayList<MusicModel> musicModels) {
        mMusicModels = musicModels;
    }

    public ArrayList<MusicModel> getMusicMain() {
        return musicMain;
    }
    private OnMediaItem onMediaItem;

    private MediaMetadataCompat mMediaMetadataCompat;
    public ChangeSongFragment(OnMediaItem onMediaItem) {
        this.onMediaItem = onMediaItem;

    }
    private Dialog mDlAddPlayList, mDlAddMusic, mDlAllPlayList;
    public void setMusicMain(ArrayList<MusicModel> musicMain) {
        this.musicMain = musicMain;
    }

    public void setMusicMain(MediaMetadataCompat mediaMetadataCompat) {
        mMediaMetadataCompat = mediaMetadataCompat;
    }

    public void setMedia(MediaMetadataCompat mediaItem){
        this.mMediaMetadataCompat = mediaItem;
    }
    private MediaManager mMediaManager;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPrefsUtils = new SharedPrefsUtils(getContext());

    }

    @Override
    public void onStart() {
        super.onStart();
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.item_change_music, null);
        initView();
        assignView();
        mTextArtist.setText(mMediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        mTextAlbum.setText(mMediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ALBUM));
        mTextTittle.setText(mMediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        ImageHelper.getInstance(getContext()).getSmallImageByPicasso(
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

    public void setSongModel(MusicModel musicModel) {
        mMusicModel = musicModel;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.item_img_viewQueue:
                if (mMusicModels == null){
                    mMusicModels = musicMain;
                }
//                mMusicDialogAdapter = new MusicDialogAdapter(getContext(), mDlOptionMusic, mMusicModels);
//                mMusicDialogAdapter.notifyDataSetChanged();
//                int pos = mSharedPrefsUtils.getInteger(Constants.PREFERENCES.POSITION, -1);
//                for (int i = 0; i < mMusicModels.size(); i++) {
//                    if (mMusicModels.get(i).getSongName().equals(musicMain.get(pos).getSongName())) {
//                        mMusicDialogAdapter.setPosition(i);
//                        mMusicDialogAdapter.setOnClickItemListener(onMediaItem);
//                        DialogHelper.showSelectSong(getContext(), mMusicDialogAdapter, i);
//                    }
//                }

                break;
            case R.id.item_img_addToPlayListImageView:
                DialogHelper.showAllPlayList(getContext(), this);

                break;
        }
    }

    @Override
    public void onClick(String title) {
        DialogHelper.cancelDialog();
        DialogHelper.showAddSongs(getContext(), mMusicModel,title);

    }
}
