package com.example.templatercview;

public class User {
    private String name;
    private String sdt;

    public User(String name, String sdt) {
        this.name = name;
        this.sdt = sdt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getName() {
        return name;
    }

    public String getSdt() {
        return sdt;
    }
}
