package com.android.music_player.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.adapters.ChooseMusicAdapter;
import com.android.music_player.adapters.PlayListAdapter;
import com.android.music_player.adapters.SelectMusicAdapter;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.models.MusicModel;

import java.util.ArrayList;

public class DialogHelper {
    private static Dialog dialog;
    public static void initDialog(final Context context, int layout){
        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.setContentView(layout);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static void cancelDialog(){
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    public static void showChangeMusic(final Context context, String title){
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
    }

    public static void showAllPlayList(final Context context, PlayListAdapter.OnClickItem onClickItem){
        MediaManager.getInstance().setContext(context);
        initDialog(context,R.layout.dialog_show_all_play_list);

        ArrayList<String> allPlayList = MediaManager.getInstance().getAllPlayList();
        if (allPlayList != null && allPlayList.size() > 0) {
            RecyclerView recyclerView = dialog.findViewById(R.id.rc_All_Play_List);
            PlayListAdapter playListAdapter = new PlayListAdapter(context, allPlayList);
            playListAdapter.OnClickItem(onClickItem);
            recyclerView.setLayoutManager(new LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(playListAdapter);
        }
        dialog.show();
    }

    private static void showCreatePlayList(final Context context){
        MediaManager.getInstance().setContext(context);
        initDialog(context, R.layout.dialog_create_playlist);

        TextView textTitle = dialog.findViewById(R.id.text_title);
        final EditText editTitle = dialog.findViewById(R.id.edit_title);
        Button btnCreate = dialog.findViewById(R.id.btnCreate);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String namePlayList = editTitle.getText().toString();
                if (!namePlayList.isEmpty()){
                    if (MediaManager.getInstance().addPlayList(namePlayList)) {
                        Utils.ToastShort(context, "Create NAME Name: "+namePlayList);
                    }
                    else {
                        Utils.ToastShort(context, "Bài hát đã add vào playlist: "+namePlayList);
                    }
                }else {
                    Utils.ToastShort(context, "Name play list not empty !!!");
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showAddSongs(final Context context, final MusicModel musicModel,
                                    final String title){
        MediaManager.getInstance().setContext(context);
        initDialog(context,R.layout.dialog_add_music);
        ImageView imageView = dialog.findViewById(R.id.img_add_music);
        TextView textTitle = dialog.findViewById(R.id.text_title_music);
        ImageButton btnAddMusic = dialog.findViewById(R.id.imgb_add_music);
        final Button btnAdd = dialog.findViewById(R.id.btnAddMusic);
        btnAdd.setText(title);
        ImageHelper.getInstance(context).getSmallImageByPicasso(musicModel.getAlbumID(), imageView);

        textTitle.setText(musicModel.getSongName());

        btnAddMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showCreatePlayList(context);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add music cần search playlist rồi mới add music

                // add bài hát
                if (MediaManager.getInstance().addMusicToPlayList(title, musicModel)){

                    Utils.ToastShort(context,"Đã Add Bài: "+ musicModel.getSongName());
                }else {
                    Utils.ToastShort(
                            context,"Add Bài: "+ musicModel.getSongName());
                }

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    public static void showAboutMusic(Context context, MusicModel musicModel){

        initDialog(context,R.layout.dialog_about_music);

        TextView textName = dialog.findViewById(R.id.dialog_about_music_name);
        TextView textFileName = dialog.findViewById(R.id.dialog_about_music_file_name);
        TextView textSong = dialog.findViewById(R.id.dialog_about_music_title);
        TextView textAlbum = dialog.findViewById(R.id.dialog_about_music_album);
        TextView textArtist = dialog.findViewById(R.id.dialog_about_music_artist);
        TextView textTime = dialog.findViewById(R.id.dialog_about_music_time);
        TextView textLocation = dialog.findViewById(R.id.dialog_about_music_location);
        Button btnDone = dialog.findViewById(R.id.dialog_about_close);

        textName.setText(musicModel.getSongName());
        textFileName.setText("File Name: " + musicModel.getFileName());
        textSong.setText("Song Title: " + musicModel.getSongName());
        textAlbum.setText("Album: " + musicModel.getAlbum());
        textArtist.setText("Artist: " + musicModel.getArtist());
        textTime.setText("Time Song: "+Utils.formatTime(musicModel.getTime()));
        textLocation.setText("File Location: "+ musicModel.getPath());

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public static void showChooseMusic(final Context context,
                                       ChooseMusicAdapter chooseMusicAdapter){
        initDialog(context,R.layout.dialog_choose_music);
        MediaManager.getInstance().setContext(context);
        RecyclerView mRcOptionMusic = dialog.findViewById(R.id.rc_choose_music);

        mRcOptionMusic.setAdapter(chooseMusicAdapter);
        mRcOptionMusic.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));

        mRcOptionMusic.getLayoutManager().scrollToPosition(
                MusicLibrary.getPosition(chooseMusicAdapter.getQueueItems(),
                MediaManager.getInstance().getCurrentMusic()));
        dialog.show();
    }

    public static void showDeletePlayList(final Context context, final String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MediaManager.getInstance().setContext(context);
                if (MediaManager.getInstance().getAllPlaylistDB().deletePlayList(title)){
                    cancelDialog();
                    Toast.makeText(context, "Đã xóa Play List: "+ title, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Chưa xóa Play List: "+ title, Toast.LENGTH_SHORT).show();
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
