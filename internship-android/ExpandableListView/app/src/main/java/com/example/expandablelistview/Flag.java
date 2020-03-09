package com.example.expandablelistview;

public class Flag {
    private int hinh;
    private String tenNuoc;

    public Flag(int hinh, String tenNuoc) {
        this.hinh = hinh;
        this.tenNuoc = tenNuoc;
    }

    public int getHinh() {
        return hinh;
    }

    public String getTenNuoc() {
        return tenNuoc;
    }

    public void setHinh(int hinh) {
        this.hinh = hinh;
    }

    public void setTenNuoc(String tenNuoc) {
        this.tenNuoc = tenNuoc;
    }
}
