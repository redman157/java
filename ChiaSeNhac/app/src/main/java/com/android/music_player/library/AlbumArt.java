package com.android.music_player.library;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;

public class AlbumArt extends ContentProvider {
    private Context context;
    public AlbumArt(Context context){
        this.context = context;
    }
    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        Context context = getContext();
        File file = new File(uri.getPath());
        if (!file.exists()){
            throw new FileNotFoundException(uri.getPath());
        }
        // Only allow access to files under cache path
        String cachePath = context.getCacheDir().getPath();
        if (!file.getPath().startsWith(cachePath)){
            throw new FileNotFoundException();
        }
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
