package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
