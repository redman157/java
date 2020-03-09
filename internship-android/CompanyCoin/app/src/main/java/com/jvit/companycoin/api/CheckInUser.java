package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

public class CheckInUser {
    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public class  Data{
        @SerializedName("checkin_time")
        private String checkin_time;
        @SerializedName("token_amount")
        private int token_amount;

        public String getCheckin_time() {
            return checkin_time;
        }

        public int getToken_amount() {
            return token_amount;
        }
    }
}
