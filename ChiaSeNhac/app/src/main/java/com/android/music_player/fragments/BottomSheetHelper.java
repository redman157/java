package com.android.music_player.fragments;

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
import com.android.music_player.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetHelper extends BottomSheetDialogFragment implements BottomSheetDialog.OnShowListener{
    private BottomSheetDialog bottomSheet;
    private BottomSheetBehavior behavior;
    private View view;
    private DIALOG type;
    private PlayListAdapter.OnClickItem onClickItem;
    private ChooseMusicAdapter chooseMusicAdapter;
    public enum  DIALOG{
        CHANGE_MUSIC,
        CREATE_PLAY_LIST,
        CHOOSE_MUSIC;
    }

    public BottomSheetHelper(DIALOG type){
        this.type = type;
    }

    public BottomSheetHelper(DIALOG type, PlayListAdapter.OnClickItem onClickItem ){
        this.type = type;
        this.onClickItem = onClickItem;
    }

    public BottomSheetHelper(DIALOG type, MusicModel musicModel, String title ){
        this.type = type;
        this.onClickItem = onClickItem;
    }

    public BottomSheetHelper(DIALOG type, ChooseMusicAdapter chooseMusicAdapter){
        this.type = type;
        this.chooseMusicAdapter = chooseMusicAdapter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        }else if (type == DIALOG.CREATE_PLAY_LIST){
            view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_create_playlist,
                    container,false);
            initCreatePlayList(view);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                    }
                    else {
                        Utils.ToastShort(getContext(), "Bài hát đã add vào playlist: "+namePlayList);
                    }
                }else {
                    Utils.ToastShort(getContext(), "Name play list not empty !!!");
                }
                dismiss();
            }
        });
    }
   /* public static void showChangeMusic(final Context context, String title){
        SelectMusicAdapter selectMusicAdapter = new SelectMusicAdapter(context);
        initDialog(context, R.layout.dialog_change_music);

        RecyclerView options = dialog.findViewById(R.id.rc_selection_music);
        TextView textTitle = dialog.findViewById(R.id.text_title);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        textTitle.setText(title);
        options.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        options.hasFixedSize();
        options.setAdapter(selectMusicAdapter);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }*/

}
