package com.droidheat.musicplayer.manager;

import android.content.Context;
import android.widget.Toast;

import com.droidheat.musicplayer.R;

public class CommonUtils {
    private Context context;

    public CommonUtils(Context context2) {
        this.context = context2;
    }

    public void showTheToast(String str) {
        Toast.makeText(this.context, str, Toast.LENGTH_LONG).show();
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int accentColor(SharedPrefsManager sharedPrefsManager) {
        switch (sharedPrefsManager.getString("accentColor", "pink")) {
            case "green":
                return R.color.green;
            case "orange":
                return R.color.orange;
            case "pink":
                return R.color.pink;
            case "cyan":
                return R.color.cyan;
            case "yellow":
                return R.color.yellow;
            case "purple":
                return R.color.purple;
            case "red":
                return R.color.red;
            case "grey":
                return R.color.grey;
            default:
                return R.color.pink;

        }
    }
}
