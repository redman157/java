package com.android.music_player.utils;

import android.content.Intent;
import android.os.Bundle;

public class BundleUtils {
    private Bundle bundle;

    public BundleUtils(Bundle bundle){
        this.bundle = bundle;
    }

    public BundleUtils(Intent intent){
        bundle = intent.getExtras();
    }

    public static class Builder{
        private Bundle bundle;
        public Builder (){
            bundle = new Bundle();
        }
        public Builder putString(String key, String value){
            bundle.putString(key, value);
            return this;
        }

        public Builder putBoolean(String key, boolean value){
            bundle.putBoolean(key, value);
            return this;
        }

        public Builder putInteger(String key, int value){
            bundle.putInt(key, value);
            return this;
        }

        public BundleUtils generate(){
            return new BundleUtils(bundle);
        }
    }

    public Bundle getBundle() {
        return bundle;
    }

    public String getString(String key, String defaultValue){
        if (bundle!= null) {
            return bundle.getString(key);
        }else {
            return defaultValue;
        }
    }

    public void clear(){
        if(bundle!=null) {
            bundle.clear();

        }
    }

    public int getInteger(String key, int defaultValue){
        if (bundle!= null) {
            return bundle.getInt(key);
        }else {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue){
        if (bundle!= null) {
            return bundle.getBoolean(key);
        }else {
            return defaultValue;
        }
    }

}
