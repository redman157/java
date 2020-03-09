package com.example.booklist;

public class Book {
    private String Title;
    private String Info;
    private String HightLight;
    public Book(String title, String info, String hightLight) {
        Title = title;
        Info = info;
        HightLight = hightLight;
    }

    public void setHightLight(String hightLight) {
        HightLight = hightLight;
    }

    public String getHightLight() {
        return HightLight;
    }

    public String getTitle() {
        return Title;
    }

    public String getInfo() {
        return Info;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setInfo(String info) {
        Info = info;
    }
}
