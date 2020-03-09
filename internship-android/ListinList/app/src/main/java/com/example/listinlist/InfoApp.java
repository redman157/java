package com.example.listinlist;

public class InfoApp {
    private int hinh;
    private String tenApp;
    private String rating;

    public InfoApp(int hinh, String tenApp, String rating) {
        this.hinh = hinh;
        this.tenApp = tenApp;
        this.rating = rating;
    }

    public int getHinh() {
        return hinh;
    }

    public String getTenApp() {
        return tenApp;
    }

    public String getRating() {
        return rating;
    }

    public void setHinh(int hinh) {
        this.hinh = hinh;
    }

    public void setTenApp(String tenApp) {
        this.tenApp = tenApp;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
