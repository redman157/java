package com.android.music_player.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.music_player.R;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.tasks.PerformMusicTasks;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.utils.Utils;

public class SplashActivity extends AppCompatActivity {

    private String TAG = "SplashActivityLog";
    private boolean sync = false;
    private ProgressBar mProgressBar;
    public TextView mTextSync;
    private MediaManager mMediaManager;
    private SharedPrefsUtils mSharedPrefsUtils;
    private final int READ_FILES_CODE = 2588;
    /* access modifiers changed from: protected */
    //Binding this Client to the AudioPlayer Service


    @SuppressLint({"SetTextI18n"})
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        mProgressBar = findViewById(R.id.progressBar);
        mTextSync = findViewById(R.id.text_status);
        mSharedPrefsUtils = new SharedPrefsUtils(this);

        setTextStatus();
        checkReadStoragePermissions();
    }
    private void checkReadStoragePermissions() {
        if (Utils.isMarshmallow()) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                showPermissionRationale();
            } else {
                onPermissionGranted();
            }
        } else {
            onPermissionGranted();
        }
    }

    private void onPermissionGranted() {
        new PerformMusicTasks(this, sync).execute("tasks");
    }

    public void showPermissionRationale(){
        AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setIcon(R.drawable.ic_music_notes_padded);
        builder.setTitle("Request for permissions");
        builder.setMessage("For music player to work we need your permission to access files on your device.");
        builder.setButton(AlertDialog.BUTTON_POSITIVE, "Okey",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                                , READ_FILES_CODE);
                    }
                });
        builder.setButton(AlertDialog.BUTTON_NEGATIVE, "Exit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.setCanceledOnTouchOutside(false);
        try {
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTextStatus() {
        mMediaManager = MediaManager.getInstance();
        sync = getIntent().getBooleanExtra(Constants.VALUE.SYNC, false);
        if (sync) {
            mMediaManager.setContext(this);
            mMediaManager.isSync(sync);
            mTextSync.setText("Syncing...");
        } else {
            mTextSync.setText("Initiating...");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == READ_FILES_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("VVV", "onRequestPermissionsResult: enter");
//                showPermissionRationale();
               onPermissionGranted();
                // weGotPermissions();
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {
                Toast.makeText(this, "Application needs permission to run. Go to Settings > Apps > " +
                        "Noad Player to allow permission.", Toast.LENGTH_SHORT).show();

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
