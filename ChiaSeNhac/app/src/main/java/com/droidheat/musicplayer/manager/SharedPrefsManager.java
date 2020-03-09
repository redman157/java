package com.droidheat.musicplayer.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SharedPrefsManager {
    private SharedPreferences preferences;
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences("MY_DATA", Context.MODE_PRIVATE);
    }

    public SharedPrefsManager() {
    }

    public void setString(String key, String value) {
        Editor edit = this.preferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public void setInteger(String key, int value) {
        SharedPreferences.Editor edit = this.preferences.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor edit = this.preferences.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    public String getString(String key, String value) {
        return this.preferences.getString(key, value);
    }

    public boolean getBoolean(String key, boolean value) {
        return (this.preferences.getBoolean(key, value));
    }

    public int getInteger(String key, int value) {
        return this.preferences.getInt(key, value);
    }
}
