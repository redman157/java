package com.example.lvbasic;

public class Language {
    private int img;
    private String name;
    private String language;

    public Language(String name, String language) {
        this.name = name;
        this.language = language;
    }

    public Language(){

    }

    public Language(int img, String name, String language) {
        this.img = img;
        this.name = name;
        this.language = language;
    }

    public int getImg() {
        return img;
    }

    public String getName() {
        return name;
    }

    public String getLanguage() {
        return language;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}