package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

public class MyInfo {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("token_amount")
    private String coin;
    @SerializedName("avatar_path")
    private String avatar;
    @SerializedName("email")
    private String email;

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }
}
