package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

public class Login {

    @SerializedName("data")
    private CreateToken createToken;


    public void setCreateToken(CreateToken createToken) {
        this.createToken = createToken;
    }


    public CreateToken getCreateToken() {
        return createToken;
    }
}
