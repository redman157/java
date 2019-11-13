package com.jvit.companycoin.api;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class CreateToken {
    @SerializedName("token")
    private String token;

    @SerializedName("expired_at")
    private String expired_at;

    @SerializedName("refresh_expired_at")
    private String refresh_expired_at;


    public void setToken(String token) {
        this.token = token;
    }

    public void setExpired_at(String expired_at) {
        this.expired_at = expired_at;
    }

    public void setRefresh_expired_at(String refresh_expired_at) {
        this.refresh_expired_at = refresh_expired_at;
    }

    public String getToken() {
        return token;
    }

    public String getExpired_at() {
        return expired_at;
    }

    public String getRefresh_expired_at() {
        return refresh_expired_at;
    }
}
