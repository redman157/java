package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

public class UserLogin {

    @SerializedName("data")
    private MyInfo data;

    public MyInfo getData() {
        return data;
    }

    public void setData(MyInfo data) {
        this.data = data;
    }
}
