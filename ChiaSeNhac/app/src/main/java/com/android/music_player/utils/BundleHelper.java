package com.android.music_player.utils;

import android.content.Intent;
import android.os.Bundle;

public class BundleHelper {
    private static BundleHelper.Builder builder;
    private Bundle bundle;
    public BundleHelper(Bundle bundle){
        this.bundle = bundle;
    }

    public BundleHelper(Intent intent){
        bundle = intent.getExtras();
    }
    public static class Builder{
        private Bundle bundle;
        public Builder (){
            bundle = new Bundle();
        }
        public BundleHelper.Builder putString(String key, String value){
            bundle.putString(key, value);
            return this;
        }

        public BundleHelper.Builder putBoolean(String key, boolean value){
            bundle.putBoolean(key, value);
            return this;
        }

        public BundleHelper.Builder putInteger(String key, int value){
            bundle.putInt(key, value);
            return this;
        }

        public BundleHelper generate(){
            return new BundleHelper(bundle);
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

