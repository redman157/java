package com.android.music_player.models;

import android.graphics.Bitmap;

import java.io.Serializable;

public class SongModel implements Serializable {
    private String mAlbum;
    private String mAlbumID;
    private String mArtist;
    private String mFileName;
    private String mPath;
    private String mSongName;
    private Bitmap mBitmap;
    private String _ID;
    private int time;

    public static class Builder {
        private String mAlbum;
        private String mAlbumID;
        private String mArtist;
        private String mFileName;
        private String mPath;
        private String mSongName;
        private int time;
        private String _ID;

        public Builder() {
        }

        public Builder setID(String id){
            _ID = id;
            return this;
        }
        public Builder setSongName(String songName) {
            mSongName = songName;
            return this;
        }

        public Builder setPath(String path) {
            mPath = path;
            return this;
        }

        public Builder setAlbum(String album) {
            mAlbum = album;
            return this;
        }

        public Builder setAlbumID(String albumId) {
            mAlbumID = albumId;
            return this;
        }

        public Builder setArtist(String artist) {
            mArtist = artist;
            return this;
        }

        public Builder setFileName(String fileName) {
            mFileName = fileName;
            return this;
        }

        public Builder setTime(int time) {
            this.time = time;
            return this;
        }
        public SongModel generate(){
            return new SongModel(mSongName,mPath,mArtist,mAlbum,mAlbumID,mFileName,_ID ,
                    time);
        }

    }

    public boolean equals(Object obj) {
        boolean matches = false;
        if (!(obj instanceof SongModel)) {
            return false;
        }
        SongModel songModel = (SongModel) obj;
        if (getAlbum().equals(songModel.getAlbum()) && getAlbumID().equals(songModel.getAlbumID()) && getArtist().equals(songModel.getArtist()) && getTime() == (songModel.getTime()) && getFileName().equals(songModel.getFileName()) && getPath().equals(songModel.getPath()) && getSongName().equals(songModel.getSongName())) {
            matches = true;
        }
        return matches;
    }

    public SongModel(String songName, String path, String artist,
                     String album, String albumID,
                     String fileName,  String id, int time) {
        mAlbum = album;
        mAlbumID = albumID;
        mArtist = artist;

        mFileName = fileName;
        mPath = path;
        mSongName = songName;
        this.time = time;
        _ID = id;
    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public void setSongName(String str) {
        mSongName = str;
    }

    public void setArtist(String str) {
        mArtist = str;
    }

    public void setPath(String path) {
        mPath = path;
    }


    public void setFileName(String str) {
        mFileName = str;
    }

    public void setAlbumID(String str) {
        mAlbumID = str;
    }

    public void setAlbum(String str) {
        mAlbum = str;
    }

    public String getSongName() {
        return mSongName;
    }


    public String getArtist() {
        return mArtist;
    }

    public String getPath() {
        return mPath;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public String getAlbumID() {
        return mAlbumID;
    }
}
