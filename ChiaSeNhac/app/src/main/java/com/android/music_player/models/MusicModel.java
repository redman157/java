package com.android.music_player.models;

import android.graphics.Bitmap;

import java.io.Serializable;

public class MusicModel implements Serializable {
    private String mAlbum;
    private String mAlbumID;
    private String mArtist;
    private String mFileName;
    private String mPath;
    private String mSongName;
    private Bitmap mBitmap;
    private String mGenres;
    private int time;

    public static class Builder {
        private String mAlbum;
        private String mAlbumID;
        private String mArtist;
        private String mFileName;
        private String mPath;
        private String mGenres;
        private String mSongName;
        private int time;

        public Builder() {
        }

        public Builder setSongName(String songName) {
            mSongName = songName;
            return this;
        }

        public Builder setGenres(String genres) {
            if (genres.equals("")){
                mGenres = "None";
            }else {
                mGenres = genres;
            }
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
        public MusicModel generate(){
            return new MusicModel(mSongName,mPath,mArtist,mAlbum,mAlbumID, mGenres,mFileName ,
                    time);
        }

    }

    public boolean equals(Object obj) {
        boolean matches = false;
        if (!(obj instanceof MusicModel)) {
            return false;
        }
        MusicModel musicModel = (MusicModel) obj;
        if (getAlbum().equals(musicModel.getAlbum()) && getAlbumID().equals(musicModel.getAlbumID()) && getArtist().equals(musicModel.getArtist()) && getTime() == (musicModel.getTime()) && getFileName().equals(musicModel.getFileName()) && getPath().equals(musicModel.getPath()) && getSongName().equals(musicModel.getSongName())) {
            matches = true;
        }
        return matches;
    }

    public MusicModel(String songName, String path, String artist,
                      String album, String albumID, String genres,
                      String fileName, int time) {
        mAlbum = album;
        mAlbumID = albumID;
        mArtist = artist;
        mGenres = genres;
        mFileName = fileName;
        mPath = path;
        mSongName = songName;
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public String getGenres() {
        return mGenres;
    }

    public void setGenres(String genres) {
        this.mGenres = genres;
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
