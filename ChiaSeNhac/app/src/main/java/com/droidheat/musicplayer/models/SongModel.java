package com.droidheat.musicplayer.models;

import android.graphics.Bitmap;

import java.io.Serializable;

public class SongModel implements Serializable {
    private String Album;
    private String AlbumID;
    private String Artist;
    private String Duration;
    private String Name;
    private String Path;
    private String Title;
    private Bitmap bitmap;
    private int time;
    public boolean equals(Object obj) {
        boolean matches = false;
        if (!(obj instanceof SongModel)) {
            return false;
        }
        SongModel songModel = (SongModel) obj;
        if (getAlbum().equals(songModel.getAlbum()) && getAlbumID().equals(songModel.getAlbumID()) && getArtist().equals(songModel.getArtist()) && getDuration().equals(songModel.getDuration()) && getFileName().equals(songModel.getFileName()) && getPath().equals(songModel.getPath()) && getTitle().equals(songModel.getTitle())) {
            matches = true;
        }
        return matches;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setTitle(String str) {
        this.Title = str;
    }

    public void setArtist(String str) {
        this.Artist = str;
    }

    public void setPath(String str) {
        this.Path = str;
    }

    public void setDuration(String str) {
        this.Duration = str;
    }

    public void setFileName(String str) {
        this.Name = str;
    }

    public void setAlbumID(String str) {
        this.AlbumID = str;
    }

    public void setAlbum(String str) {
        this.Album = str;
    }

    public String getTitle() {
        return this.Title;
    }

    public String getDuration() {
        return this.Duration;
    }

    public String getArtist() {
        return this.Artist;
    }

    public String getPath() {
        return this.Path;
    }

    public String getFileName() {
        return this.Name;
    }

    public String getAlbum() {
        return this.Album;
    }

    public String getAlbumID() {
        return this.AlbumID;
    }
}
