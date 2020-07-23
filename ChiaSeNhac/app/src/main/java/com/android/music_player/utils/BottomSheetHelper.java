package com.android.music_player.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.adapters.ChooseMusicAdapter;
import com.android.music_player.adapters.PlayListAdapter;
import com.android.music_player.adapters.SelectOptionsAdapter;
import com.android.music_player.interfaces.DialogType;
import com.android.music_player.interfaces.OnClickItemListener;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.managers.QueueManager;
import com.android.music_player.models.MusicModel;
import com.android.music_player.view.SpeedyLinearLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class BottomSheetHelper extends BottomSheetDialogFragment implements BottomSheetDialog.OnShowListener{
    private BottomSheetDialog mBottomSheet;
    private BottomSheetBehavior mBehavior;
    private View view;
    private MediaManager mMediaManager;
    private DialogType mType;
    private OnClickItemListener onClickItemListener;
    private ChooseMusicAdapter mChooseMusicAdapter;
    private String title = "";
    private QueueManager mQueueManager;
    private String titleType;
    private Map<String, ArrayList<String>> items;
    public void cancelDialgo(){
        dismiss();
    }

    public BottomSheetHelper(DialogType type){
        this.mType = type;
    }

    public BottomSheetHelper(DialogType type, OnClickItemListener onClickItemListener){
        mType = type;
        this.onClickItemListener = onClickItemListener;
    }

    public BottomSheetHelper(DialogType type, String title, Map<String, ArrayList<String>> items,
                             OnClickItemListener onClickItemListener){
        mType = type;
        this.titleType = title;
        this.items = items;
        this.onClickItemListener = onClickItemListener;
    }

    public BottomSheetHelper(DialogType type, MusicModel musicModel, String title ){
        mType = type;
    }

    public BottomSheetHelper(DialogType type, ChooseMusicAdapter chooseMusicAdapter){
        mType = type;
        mChooseMusicAdapter = chooseMusicAdapter;
    }

    public void setTitle(String title){
        this.title = title;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(getContext());
        mQueueManager = QueueManager.getInstance(getContext());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mBottomSheet = new BottomSheetDialog(getContext(), getTheme());
        mBottomSheet.setOnShowListener(this);

        return mBottomSheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mType == DialogType.CHANGE_MUSIC){
            view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_music,
                    container, false);
            initSelectOptions(view);
        }else if (mType == DialogType.CREATE_PLAY_LIST) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_create_playlist,
                    container, false);
            initCreatePlayList(view);
        } else if (mType == DialogType.ADD_MUSIC_TO_PLAYLIST) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_show_all_play_list,
                    container, false);
            initAddMusicToPlayList(view, onClickItemListener);
        }else if (mType == DialogType.CHOOSE_MUSIC){
            view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_choose_music,
                    null);
            initChooseMusic(view,title, mChooseMusicAdapter);
        }else if (mType == DialogType.CHOOSE_ITEM_LIBRARY){
            view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_choose_item_library,
                    container, false);
            initAllItemLibrary(view, titleType, items, onClickItemListener);
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
        mBottomSheet = (BottomSheetDialog) dialog;
        View bottomSheetInternal =
                mBottomSheet.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottomSheetInternal);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void initChooseMusic(View view,String title,  ChooseMusicAdapter chooseMusicAdapter){
        MediaManager.getInstance().setContext(getContext());
        FastScrollRecyclerView mRecyclerChooseMusic = view.findViewById(R.id.rc_choose_music);
        TextView textTitle = view.findViewById(R.id.text_title);
        LinearLayoutManager layoutManager = new SpeedyLinearLayoutManager(getContext(),
                SpeedyLinearLayoutManager.VERTICAL, false);
        View line = view.findViewById(R.id.line);
        if (!title.equals("")){
            line.setVisibility(View.VISIBLE);
            textTitle.setText(title);
        }else {
            line.setVisibility(View.GONE);
        }
        if (chooseMusicAdapter.getItemCount() > 5){
            mRecyclerChooseMusic.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 700));
        }else {
            mRecyclerChooseMusic.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        mRecyclerChooseMusic.setAdapter(chooseMusicAdapter);
        mRecyclerChooseMusic.setLayoutManager(layoutManager);

        mRecyclerChooseMusic.getLayoutManager().scrollToPosition(
                MusicLibrary.getPosition(chooseMusicAdapter.getQueueItems(),
                        mMediaManager.getCurrentMusic()));
    }

    private void initSelectOptions(View view){
        SelectOptionsAdapter mSelectOptionsAdapter = new SelectOptionsAdapter(getContext(), SelectOptionsAdapter.initData());

        RecyclerView mRecyclerOptions = view.findViewById(R.id.rc_selection_music);
        TextView mTextTitle = view.findViewById(R.id.text_title);
        Button mBtnCancel = view.findViewById(R.id.btn_cancel);

        mTextTitle.setText(mMediaManager.getCurrentMusic());
        mRecyclerOptions.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mRecyclerOptions.hasFixedSize();
        mRecyclerOptions.setAdapter(mSelectOptionsAdapter);

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initAddMusicToPlayList(View view, OnClickItemListener onClickItemListener){
        ArrayList<String> allPlayList = MediaManager.getInstance().getAllPlayList();
        if (allPlayList != null && allPlayList.size() > 0) {

            Button btnNewPlayList = view.findViewById(R.id.btn_create_playlist);
            FastScrollRecyclerView mRcAddPlayList = view.findViewById(R.id.rc_all_playlist);
            PlayListAdapter mPlayListAdapter = new PlayListAdapter(getContext(), allPlayList);
            mPlayListAdapter.setOnClickItemListener(onClickItemListener);

            mRcAddPlayList.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.VERTICAL, false));
            mRcAddPlayList.setAdapter(mPlayListAdapter);

            btnNewPlayList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogHelper.showCreatePlayList(getContext());
                }
            });
        }
    }

    private void initAllItemLibrary(View view, String  title ,Map<String, ArrayList<String>> itemLibrary,
                                 OnClickItemListener onClickItemListener){
        if (itemLibrary != null && itemLibrary.size() > 0) {
            Log.d("XXX", "initAllItem: "+title);
            ArrayList<String> items = new ArrayList<>(itemLibrary.get(title));
            TextView textTittle = view.findViewById(R.id.text_title);
            textTittle.setText(title);

            Button btnCancel = view.findViewById(R.id.btn_create_playlist);
            FastScrollRecyclerView mRcAddPlayList = view.findViewById(R.id.rc_all_item);
            PlayListAdapter mPlayListAdapter = new PlayListAdapter(getContext(), items);
            mPlayListAdapter.setOnClickItemListener(onClickItemListener);

            mRcAddPlayList.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.VERTICAL, false));
            mRcAddPlayList.setAdapter(mPlayListAdapter);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
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
                    if (mMediaManager.addPlayList(namePlayList)) {
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
