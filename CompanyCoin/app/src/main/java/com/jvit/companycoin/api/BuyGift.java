package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

public class BuyGift {
    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{
        @SerializedName("token_amount")
        private int token_amount;

        public int getToken_amount() {
            return token_amount;
        }

        public void setToken_amount(int token_amount) {
            this.token_amount = token_amount;
        }
    }
}
