package com.droidheat.musicplayer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.droidheat.musicplayer.utils.CommonUtils;
import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.PerformBackgroundTasks;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.utils.SharedPrefsUtils;
import com.droidheat.musicplayer.utils.SongsUtils;

public class SplashActivity extends AppCompatActivity {

    private String TAG = "SplashActivityLog";
    private boolean sync = false;
    private ProgressBar progressBar;
    private TextView textSync;
    private SongsUtils songsUtils;

    /* access modifiers changed from: protected */
    @SuppressLint({"SetTextI18n"})
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        if ((getIntent().getFlags() & 4194304) != 0) {
            finish();
            return;
        }
        progressBar = findViewById(R.id.progressBar);
        textSync = findViewById(R.id.textView10);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, (new CommonUtils(this).accentColor(new SharedPrefsUtils(this)))),
                PorterDuff.Mode.MULTIPLY);


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
                PerformBackgroundTasks.getInstance().initTask(this, sync);
                PerformBackgroundTasks.getInstance().execute(new String[]{Constants.VALUE.TASK});
            } else {
                (new CommonUtils(this)).showTheToast("Please enable permission from " +
                        "Settings > Apps > Noad Player > Permissions.");
            }
        } else {
            PerformBackgroundTasks.getInstance().initTask(this, sync);
            PerformBackgroundTasks.getInstance().execute(new String[]{Constants.VALUE.TASK});
        }
    }

    private void setTextStatus() {
        songsUtils = SongsUtils.getInstance();
        if (getIntent().getBooleanExtra(Constants.VALUE.SYNC, false)) {

            songsUtils.setContext(this);
            songsUtils.sync();
            textSync.setText("Syncing..");
            this.sync = true;
        } else {
            textSync.setText("Initiating..");
        }
    }

    private void setDialogBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Request for permissions");
        builder.setMessage("For music player to work we need your permission to access files on your device.");
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                SplashActivity.this.finish();
            }
        });
        builder.show();
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (i != 1) {
            return;
        }
        if (iArr.length <= 0 || iArr[0] != 0) {
            Toast.makeText(this, "Application needs permission to run. Go to Settings > Apps > Noad Player to allow permission.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        PerformBackgroundTasks.getInstance().initTask(this, sync);
        PerformBackgroundTasks.getInstance().execute(new String[]{Constants.VALUE.TASK});
    }

}
