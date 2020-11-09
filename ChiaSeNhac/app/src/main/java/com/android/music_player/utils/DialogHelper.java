package com.android.music_player.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.activities.HomeActivity;
import com.android.music_player.adapters.PlayListAdapter;
import com.android.music_player.adapters.SelectOptionsAdapter;
import com.android.music_player.interfaces.DialogType;
import com.android.music_player.interfaces.OnClickItemListener;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.models.MusicModel;

import java.io.File;
import java.util.ArrayList;

import static com.android.music_player.activities.HomeActivity.FRAGMENT_TAG;

public class DialogHelper {
    private static Dialog dialog;
    public static void initDialog(final Context context, int layout){
        dialog = new Dialog(context, R.style.MyCustomDialog);
        dialog.setTitle(null);
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

    public static void showDialogChangeMusic(final Context context,
                                             final MediaMetadataCompat metadataCompat){
        // fragment home -- > show dialog add music vào play list
        final String mediaID = metadataCompat.getString(Constants.METADATA.Title);
        final String album = metadataCompat.getString(Constants.METADATA.Album);
        final String artist = metadataCompat.getString(Constants.METADATA.Artist);
        final String path = metadataCompat.getString(Constants.METADATA.Path);
        final MediaManager manager = MediaManager.getInstance();
        manager.setContext(context);
        SelectOptionsAdapter selectOptionsAdapter = new SelectOptionsAdapter(context, SelectOptionsAdapter.initData());
        if (context instanceof HomeActivity) {
            final HomeActivity activity = (HomeActivity)context;
            selectOptionsAdapter.setOnClickItemListener(new SelectOptionsAdapter.OnClickItemListener() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 0:
                            if (manager.getCategorySongsDB().isFavorite(mediaID) != -1) {
                                Log.d("AAA", "enter if");
                                if (manager.getCategorySongsDB().isFavorite(mediaID) == 0) {
//                                    manager.getCategorySongsDB().favorite(mediaID, 1);
                                } else if (manager.getCategorySongsDB().isFavorite(mediaID) == 1) {
//                                    manager.getCategorySongsDB().favorite(mediaID, 0);
                                }
                            } else {
                                Log.d("AAA", "enter elseAAA: " + mediaID);
                            }
                            break;
                        case 1:
                            activity.bottomSheetHelper =
                                    new BottomSheetHelper(DialogType.ADD_MUSIC_TO_PLAYLIST, new OnClickItemListener() {
                                        @Override
                                        public void onAddMusicToPlayList(String namePlayList) {
                                            activity.bottomSheetHelper.dismiss();
                                            if (manager.addMusicToPlayList(namePlayList, mediaID)) {
                                                Utils.ToastShort(context, "Đã Add Bài: " + mediaID);
                                            } else {
                                                Utils.ToastShort(context, "Add Bài: " + mediaID);
                                            }
                                        }

                                        @Override
                                        public void onChooseItemLibrary(ArrayList<String> models) {

                                        }
                                    });
                            activity.bottomSheetHelper.show(activity.getSupportFragmentManager(),
                                    FRAGMENT_TAG);
                            break;
                        case 2:
                            if (activity instanceof HomeActivity) {
                                final HomeActivity mHomeActivity = (HomeActivity)activity;
                                mHomeActivity.bottomSheetHelper = new BottomSheetHelper(DialogType.CHOOSE_ITEM_LIBRARY,
                                        artist, MusicLibrary.artist, new OnClickItemListener() {
                                    @Override
                                    public void onAddMusicToPlayList(String namePlayList) {
                                        dialog.dismiss();
                                        mHomeActivity.bottomSheetHelper.dismiss();
                                        mHomeActivity.setTitle(namePlayList);
                                        mHomeActivity.getControllerActivity().getTransportControls().prepareFromMediaId(namePlayList, null);
                                    }

                                    @Override
                                    public void onChooseItemLibrary(ArrayList<String> models) {

                                    }
                                });
                                mHomeActivity.bottomSheetHelper.setTitle("All Alfolderbum In Device");
                                mHomeActivity.bottomSheetHelper.show(mHomeActivity.getSupportFragmentManager(), mHomeActivity.FRAGMENT_TAG);
                            }
                            Utils.ToastShort(context, position + "");

                            break;
                        case 3:
                            if (activity instanceof HomeActivity) {
                                final HomeActivity mHomeActivity = (HomeActivity)activity;
                                mHomeActivity.bottomSheetHelper = new BottomSheetHelper(DialogType.CHOOSE_ITEM_LIBRARY,
                                        album, MusicLibrary.album, new OnClickItemListener() {
                                    @Override
                                    public void onAddMusicToPlayList(String namePlayList) {
                                        dialog.dismiss();
                                        mHomeActivity.bottomSheetHelper.dismiss();
                                        mHomeActivity.setTitle(namePlayList);
                                        mHomeActivity.getControllerActivity().getTransportControls().prepareFromMediaId(namePlayList, null);
                                    }

                                    @Override
                                    public void onChooseItemLibrary(ArrayList<String> models) {

                                    }
                                });
                                mHomeActivity.bottomSheetHelper.setTitle("All Alfolderbum In Device");
                                mHomeActivity.bottomSheetHelper.show(mHomeActivity.getSupportFragmentManager(), mHomeActivity.FRAGMENT_TAG);
                            }
                            Utils.ToastShort(context, position + "");
                            break;
                        case 4:
                            Context c = context.getApplicationContext();
                            boolean settingsCanWrite = Settings.System.canWrite(c);
                            // Check whether has the write settings permission or not.
                            if(!settingsCanWrite) {
                                // If do not have write settings permission then open the Can modify system settings panel.
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                c.startActivity(intent);
                            }else {
                                // If has permission then show an alert dialog with message.
                                AlertDialog builder = new AlertDialog.Builder(context).create();
                                builder.setMessage("You want to select this song as the ringtone ?");
                                builder.setButton(AlertDialog.BUTTON_POSITIVE, "OKEY",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                File file = new File(path); // path is a file to /sdcard/media/ringtone
                                                Uri uri = Uri.fromFile(file);
                                                RingtoneManager.setActualDefaultRingtoneUri(
                                                    context,
                                                    RingtoneManager.TYPE_RINGTONE,
                                                    uri
                                                );
                                            }
                                        });
                                builder.setButton(AlertDialog.BUTTON_NEGATIVE, "Exit",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ((HomeActivity) context).finish();
                                            }
                                        });
                                builder.show();
                            }

                            Utils.ToastShort(context, position + "");
                            break;
                        case 5:

                            Utils.ToastShort(context,
                                    metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI) + "");
                            break;
                        case 6:
                            Utils.ToastShort(context, position + "");
                            break;
                        case 7:
                            Utils.ToastShort(context, position + "");
                            break;
                    }
                }

                ;
            });
        }
        initDialog(context, R.layout.dialog_change_music);

        RecyclerView options = dialog.findViewById(R.id.rc_selection_music);
        TextView textTitle = dialog.findViewById(R.id.text_title);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        textTitle.setText(mediaID);
        options.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        options.hasFixedSize();
        options.setAdapter(selectOptionsAdapter);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public static void showAllPlayList(final Context context, OnClickItemListener onClickItemListener){
        MediaManager.getInstance().setContext(context);
        initDialog(context,R.layout.dialog_show_all_play_list);

        ArrayList<String> allPlayList = MediaManager.getInstance().getAllPlayList();
        if (allPlayList != null && allPlayList.size() > 0) {
            RecyclerView recyclerView = dialog.findViewById(R.id.rc_all_playlist);
            PlayListAdapter playListAdapter = new PlayListAdapter(context, allPlayList);
            playListAdapter.setOnClickItemListener(onClickItemListener);
            recyclerView.setLayoutManager(new LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(playListAdapter);
        }
        dialog.show();
    }

    public static void showProgress(Context context, boolean isShow){
        Dialog progressDialog = new Dialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        ProgressBar progressBar = new ProgressBar(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(
            (int)context.getResources().getDimension(R.dimen._30dp),
            (int)context.getResources().getDimension(R.dimen._30dp),
            (int)context.getResources().getDimension(R.dimen._30dp),
            (int)context.getResources().getDimension(R.dimen._30dp)
        );
        progressBar.setLayoutParams(params);
        progressDialog.setContentView(progressBar);
        if (isShow){
            progressDialog.show();
        }else {
            progressDialog.dismiss();
        }
    }

    public static void showCreatePlayList(final Context context){
        MediaManager.getInstance().setContext(context);
        initDialog(context, R.layout.dialog_create_playlist);

        TextView textTitle = dialog.findViewById(R.id.text_title);
        final EditText mEditTitle = dialog.findViewById(R.id.edit_title);
        Button mBtnCreate = dialog.findViewById(R.id.btnCreate);
        Button mBtnCancel = dialog.findViewById(R.id.btnCancel);

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SSS", context instanceof HomeActivity? "true":"false");

                dialog.cancel();
            }
        });

        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namePlayList = mEditTitle.getText().toString();
                if (!namePlayList.isEmpty()){
                    if (MediaManager.getInstance().addPlayList(namePlayList)) {
                        if (context instanceof HomeActivity){
                            if (((HomeActivity)context).bottomSheetHelper != null && ((HomeActivity)context).bottomSheetHelper.getShowsDialog()) {
                                ((HomeActivity) context).bottomSheetHelper.dismiss();
                            }
                            ((HomeActivity)context).bottomSheetHelper = new BottomSheetHelper(DialogType.ADD_MUSIC_TO_PLAYLIST,
                                    new OnClickItemListener() {
                                        @Override
                                        public void onAddMusicToPlayList(String mediaID) {
                                            ((HomeActivity)context).bottomSheetHelper.dismiss();
                                            MediaManager.getInstance().getAllPlaylistDB().addRow(mediaID);
                                        }

                                        @Override
                                        public void onChooseItemLibrary(ArrayList<String> models) {

                                        }
                                    });
                            ((HomeActivity)context).bottomSheetHelper.show(
                                    ((HomeActivity)context).getSupportFragmentManager(),
                                    ((HomeActivity)context).FRAGMENT_TAG);
                        }
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

    @SuppressLint("SetTextI18n")
    public static void showAboutMusic(Context context, MusicModel musicModel){

        initDialog(context,R.layout.dialog_about_music);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
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

    public static void showDeletePlayList(final Context context, final String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Bạn có muốn xóa PlayList: "+title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MediaManager.getInstance().setContext(context);
                if (MediaManager.getInstance().getAllPlaylistDB().deletePlayList(title)){
                    if (context instanceof HomeActivity){
                        if (((HomeActivity)context).bottomSheetHelper!= null && ((HomeActivity)context).bottomSheetHelper.getShowsDialog()) {
                            ((HomeActivity) context).bottomSheetHelper.dismiss();
                        }
                        ((HomeActivity)context).bottomSheetHelper = new BottomSheetHelper(DialogType.ADD_MUSIC_TO_PLAYLIST,
                                new OnClickItemListener() {
                                    @Override
                                    public void onAddMusicToPlayList(String namePlayList) {
                                        if (MediaManager.getInstance().addMusicToPlayList(namePlayList,
                                                MediaManager.getInstance().getCurrentMusic())){
                                            Utils.ToastShort(context,"Đã Add Bài: "+ MediaManager.getInstance().getCurrentMusic());
                                        }else {
                                            Utils.ToastShort(context,"Add Bài: "+ MediaManager.getInstance().getCurrentMusic());
                                        }
                                    }

                                    @Override
                                    public void onChooseItemLibrary(ArrayList<String> models) {

                                    }
                                });
                        ((HomeActivity)context).bottomSheetHelper.show(
                                ((HomeActivity)context).getSupportFragmentManager(),
                                ((HomeActivity)context).FRAGMENT_TAG);
                    }
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
