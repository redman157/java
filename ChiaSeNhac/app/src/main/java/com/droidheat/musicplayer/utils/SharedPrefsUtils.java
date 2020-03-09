package com.droidheat.musicplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SharedPrefsUtils {
    private SharedPreferences preferences;

    public SharedPrefsUtils(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void writeSharedPrefs(String str, String str2) {
        Editor edit = this.preferences.edit();
        edit.putString(str, str2);
        edit.apply();
    }

    public void writeSharedPrefs(String str, int i) {
        Editor edit = this.preferences.edit();
        edit.putInt(str, i);
        edit.apply();
    }

    public void writeSharedPrefs(String str, Boolean bool) {
        Editor edit = this.preferences.edit();
        edit.putBoolean(str, bool.booleanValue());
        edit.apply();
    }

    public String readSharedPrefsString(String str, String str2) {
        return this.preferences.getString(str, str2);
    }

    public Boolean readSharedPrefsBoolean(String str, Boolean bool) {
        return Boolean.valueOf(this.preferences.getBoolean(str, bool.booleanValue()));
    }

    public int readSharedPrefsInt(String str, int i) {
        return this.preferences.getInt(str, i);
    }
}
