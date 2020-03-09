package com.droidheat.musicplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.core.app.ActivityCompat;

public class CustomDialog extends AlertDialog.Builder {

    @SuppressLint("StaticFieldLeak")
    private static CustomDialog instance;
    private Activity activity;
    private AlertDialog.Builder builder;
    public CustomDialog(Context activity) {
        super(activity);
    }

    public void newInstance(){
        builder = new AlertDialog.Builder(activity);
    }
    public void setTitle(String title){
        builder.setTitle(title);
    }

    public void setMessage(String title){
        builder.setMessage(title);
    }

    public void setOnClick(String Pos, String Neg){
        builder.setPositiveButton(Pos, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{
                                Constants.MANIFESTS.READ_EXTERNAL_STORAGE},
                                Constants.REQUEST_CODE.READ_EXTERNAL_STORAGE);
            }
        });
        builder.setNegativeButton(Neg, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.finish();
            }
        });
    }

    public void showDialog(){
        builder.show();
    }
    public Context getActivity(){
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
