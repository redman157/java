package com.android.music_player.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.utils.Constants;
import com.android.music_player.OnMusicChange;
import com.android.music_player.R;
import com.android.music_player.adapters.MusicAdapter;
import com.android.music_player.managers.SongManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ChangeMusicFragment extends Fragment implements View.OnClickListener {
    private SongModel mSongModel;
    private ImageView mImgAlbumArt, mImgShowList, mImgAddPlayList;
    private TextView mTextTittle, mTextArtists, text_leftTime, text_rightTime;
    private SeekBar sb_leftTime;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private View view;
    public TextView mTextPlaying;
    public Dialog mDlOptionMusic;

    private MusicAdapter mMusicAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<SongModel> musicMain;
    private SharedPrefsUtils mSharedPrefsUtils;
    private static ArrayList<SongModel> mSongModels;
    public static void newInstance(ArrayList<SongModel> songModels) {
        mSongModels = songModels;
    }

    public ArrayList<SongModel> getMusicMain() {
        return musicMain;
    }
    private OnMusicChange onMusicChange;
    public ChangeMusicFragment(OnMusicChange onMusicChange) {
        this.onMusicChange = onMusicChange;
    }
    private Dialog mDlAddPlayList, mDlAddMusic;
    public void setMusicMain(ArrayList<SongModel> musicMain) {
        this.musicMain = musicMain;
    }
    private SongManager mSongManager;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPrefsUtils = new SharedPrefsUtils(getContext());


    }

    @Override
    public void onStart() {
        super.onStart();
        mSongManager = SongManager.getInstance();
        mSongManager.setContext(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.item_change_music, null);
        initView();
        assignView();

        mTextPlaying.setText(mSongModel.getAlbum());
        mTextArtists.setText(mSongModel.getArtist());
        mTextTittle.setText(mSongModel.getSongName());
        ImageUtils.getInstance(getContext()).getSmallImageByPicasso(mSongModel.getAlbumID(), mImgAlbumArt);

        return view;
    }

    private void showAddPlayList(){
        mDlAddPlayList = new Dialog(getContext());
        mDlAddPlayList.setContentView(R.layout.dialog_add_playlist);
        mDlAddPlayList.setCanceledOnTouchOutside(false);
        mDlAddPlayList.setCancelable(false);

        TextView  textTitle = mDlAddPlayList.findViewById(R.id.text_title);
        final EditText editTitle = mDlAddPlayList.findViewById(R.id.edit_title);
        Button btnCreate = mDlAddPlayList.findViewById(R.id.btnCreate);
        Button btnCancel = mDlAddPlayList.findViewById(R.id.btnCancel);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDlAddPlayList.cancel();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = editTitle.getText().toString();
                if (!name.isEmpty()){
                    if (!mSongManager.getAllPlaylistDB().searchPlayList(name)) {
                        mSongManager.getAllPlaylistDB().addPlayList(name);
                        Toast.makeText(getContext(), "Create PlayList Name: "+name, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        mDlAddPlayList.show();
    }

    private void showAddMusic(){
        mDlAddMusic = new Dialog(getContext());
        mDlAddMusic.setContentView(R.layout.dialog_add_music);

        ImageView imageView = mDlAddMusic.findViewById(R.id.img_add_music);
        TextView textTitle = mDlAddMusic.findViewById(R.id.text_title_music);
        ImageButton btnAddMusic = mDlAddMusic.findViewById(R.id.imgb_add_music);
        final Button btnAdd = mDlAddMusic.findViewById(R.id.btnAddMusic);

        ImageUtils.getInstance(getContext()).getSmallImageByPicasso(mSongModel.getAlbumID(), imageView);

        textTitle.setText(mSongModel.getSongName());

        btnAddMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPlayList();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Add music cần search playlist rồi mới add music
                Toast.makeText(getContext(), "hehe", Toast.LENGTH_SHORT).show();
            }
        });
        mDlAddMusic.show();
    }

    private void initView(){
        mDlOptionMusic = new Dialog(getContext());
        mTextPlaying = view.findViewById(R.id.item_text_playing);
        mTextTittle = view.findViewById(R.id.item_text_title);
        mTextArtists = view.findViewById(R.id.item_text_album);
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
                mMusicAdapter = new MusicAdapter(getContext(), mDlOptionMusic);
                int pos = mSharedPrefsUtils.getInteger(Constants.PREFERENCES.POSITION, -1);
                for (int i = 0; i < mSongModels.size(); i++) {
                    if (mSongModels.get(i).getSongName().equals(musicMain.get(pos).getSongName())) {
                        mMusicAdapter.setPosition(i);

                        showOptionMusic(mSongModels, i);
                        mDlOptionMusic.show();
                    }
                }


                break;
            case R.id.item_img_addToPlayListImageView:
                showAddMusic();
                break;
        }
    }
    private void showOptionMusic(ArrayList<SongModel> songModels, int pos){
        mDlOptionMusic.setContentView(R.layout.dialog_option_music);
        RecyclerView mRcOptionMusic = mDlOptionMusic.findViewById(R.id.rc_OptionMusic);

        mMusicAdapter.setListMusic(songModels);
        mMusicAdapter.setMusicChange(onMusicChange);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRcOptionMusic.setAdapter(mMusicAdapter);
        mRcOptionMusic.setLayoutManager(layoutManager);
        mRcOptionMusic.getLayoutManager().scrollToPosition(pos);
    }
}
