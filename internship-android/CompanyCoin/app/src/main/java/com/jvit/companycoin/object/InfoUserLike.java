package com.jvit.companycoin.object;

public class InfoUserLike {
    private String avata;
    private String name;

    public InfoUserLike(String avata, String name) {
        this.avata = avata;
        this.name = name;
    }

    public String getAvata() {
        return avata;
    }

    public void setAvata(String avata) {
        this.avata = avata;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
