package com.android.musicplayer.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.android.musicplayer.utils.Constants;
import com.android.musicplayer.tasks.PerformMusicTasks;
import com.android.musicplayer.R;
import com.android.musicplayer.utils.SharedPrefsUtils;
import com.android.musicplayer.managers.SongManager;
import com.android.musicplayer.utils.Utils;

public class SplashActivity extends AppCompatActivity {

    private String TAG = "SplashActivityLog";
    private boolean sync = false;
    private ProgressBar mProgressBar;
    public TextView mTextSync;
    private SongManager mSongManager;
    private SharedPrefsUtils mSharedPrefsUtils;

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
        mProgressBar = findViewById(R.id.progressBar1);
        mTextSync = findViewById(R.id.textView10);
        mSharedPrefsUtils = new SharedPrefsUtils(this);

        setTextStatus();

        checkPermission();
    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
                // No explanation needed, we can request the permission.

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Request for permissions");
                alertDialog.setMessage("For music player to work we need your permission to access" +
                        " files on your device.");
                alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(SplashActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);
                    }
                });
                alertDialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialog.show();
                Log.d(TAG, "asking permission");
            } else if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                new PerformMusicTasks(this, sync).execute("tasks");
            } else {
                Utils.ToastShort(this,"Please enable permission from " +
                        "Settings > Apps > Noad Player > Permissions.");
            }
        } else {
            new PerformMusicTasks(this, sync).execute("tasks");
        }
    }

    private void setTextStatus() {
        mSongManager = SongManager.getInstance();
        sync = getIntent().getBooleanExtra(Constants.VALUE.SYNC, false);
        if (sync) {
            mSongManager.setContext(this);
            mSongManager.isSync(sync);
            mTextSync.setText("Syncing...");
        } else {
            mTextSync.setText("Initiating...");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                new PerformMusicTasks(this, sync).execute("tasks");
                // weGotPermissions();
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {
                Toast.makeText(this, "Application needs permission to run. Go to Settings > Apps > " +
                        "Noad Player to allow permission.", Toast.LENGTH_SHORT).show();
                finish();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
