package com.android.music_player.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.adapters.ChooseMusicAdapter;
import com.android.music_player.adapters.PlayListAdapter;
import com.android.music_player.adapters.SelectMusicAdapter;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.models.MusicModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class BottomSheetHelper extends BottomSheetDialogFragment implements BottomSheetDialog.OnShowListener{
    private BottomSheetDialog bottomSheet;
    private BottomSheetBehavior behavior;
    private View view;
    private MediaManager mMediaManager;
    private DIALOG type;
    private PlayListAdapter.OnClickItemListener onClickItemListener;
    private ChooseMusicAdapter chooseMusicAdapter;
    public enum  DIALOG{
        CHANGE_MUSIC,
        CREATE_PLAY_LIST,
        ADD_PLAY_LIST,
        CHOOSE_MUSIC;
    }

    public BottomSheetHelper(DIALOG type){
        this.type = type;
    }

    public BottomSheetHelper(DIALOG type, PlayListAdapter.OnClickItemListener onClickItemListener){
        this.type = type;
        this.onClickItemListener = onClickItemListener;
    }

    public BottomSheetHelper(DIALOG type, MusicModel musicModel, String title ){
        this.type = type;

    }

    public BottomSheetHelper(DIALOG type, ChooseMusicAdapter chooseMusicAdapter){
        this.type = type;
        this.chooseMusicAdapter = chooseMusicAdapter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(getContext());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        bottomSheet = new BottomSheetDialog(getContext(), getTheme());
        bottomSheet.setOnShowListener(this);
        return bottomSheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (type == DIALOG.CHANGE_MUSIC){
            view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_music,
                    container, false);
            initChangeMusic(view);
        }else if (type == DIALOG.CREATE_PLAY_LIST) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_create_playlist,
                    container, false);
            initCreatePlayList(view);
        } else if (type == DIALOG.ADD_PLAY_LIST) {

            view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_show_all_play_list,
                    container, false);
            initAddMusicToPlayList(view, onClickItemListener);
        }else if (type == DIALOG.CHOOSE_MUSIC){

            view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_choose_music,
                    container,false);
            initChooseMusic(view, chooseMusicAdapter);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    @Override
    public int getTheme() {
        return R.style.MyCustomBottomSheetDialog;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        bottomSheet = (BottomSheetDialog) dialog;
        View bottomSheetInternal =
                bottomSheet.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheetInternal);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void initChooseMusic(View view,  ChooseMusicAdapter chooseMusicAdapter){
        MediaManager.getInstance().setContext(getContext());
        RecyclerView mRecyclerChooseMusic = view.findViewById(R.id.rc_choose_music);
        mRecyclerChooseMusic.setAdapter(chooseMusicAdapter);
        mRecyclerChooseMusic.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

        mRecyclerChooseMusic.getLayoutManager().scrollToPosition(
                MusicLibrary.getPosition(chooseMusicAdapter.getQueueItems(),
                        MediaManager.getInstance().getCurrentMusic()));
    }

    private void initChangeMusic(View view){
        SelectMusicAdapter selectMusicAdapter = new SelectMusicAdapter(getContext());

        RecyclerView options = view.findViewById(R.id.rc_selection_music);
        TextView textTitle = view.findViewById(R.id.text_title);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        MediaManager.getInstance().setContext(getContext());
        textTitle.setText(MediaManager.getInstance().getCurrentMusic());
        options.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        options.hasFixedSize();
        options.setAdapter(selectMusicAdapter);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initAddMusicToPlayList(View view, PlayListAdapter.OnClickItemListener onClickItemListener){
        ArrayList<String> allPlayList = MediaManager.getInstance().getAllPlayList();
        if (allPlayList != null && allPlayList.size() > 0) {
            Button btnCreate = view.findViewById(R.id.btn_create_playlist);
            RecyclerView mRcAddPlayList = view.findViewById(R.id.rc_all_playlist);

            PlayListAdapter mPlayListAdapter = new PlayListAdapter(getContext(), allPlayList);
            mPlayListAdapter.OnClickItem(onClickItemListener);
            mRcAddPlayList.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.VERTICAL, false));
            mRcAddPlayList.setAdapter(mPlayListAdapter);

            btnCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogHelper.showCreatePlayList(getContext());
                }
            });
        }
    }

    private void initCreatePlayList(View view){
        MediaManager.getInstance().setContext(getContext());

        TextView textTitle = view.findViewById(R.id.text_title);
        final EditText editTitle = view.findViewById(R.id.edit_title);
        Button btnCreate = view.findViewById(R.id.btnCreate);
        Button btnCancel = view.findViewById(R.id.btnCancel);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String namePlayList = editTitle.getText().toString();
                if (!namePlayList.isEmpty()){
                    if (MediaManager.getInstance().addPlayList(namePlayList)) {
                        Utils.ToastShort(getContext(), "Create NAME Name: "+namePlayList);
                    } else {
                        Utils.ToastShort(getContext(), "Bài hát đã add vào playlist: "+namePlayList);
                    }
                }else {
                    Utils.ToastShort(getContext(), "Name play list not empty !!!");
                }
                dismiss();
            }
        });
    }

}
