package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

public class CardInfo {
    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public class Data {
        @SerializedName("last_transaction")
        private LastTransaction lastTransaction;
        @SerializedName("company")
        private Company company;
        @SerializedName("user")
        private User user;

        public LastTransaction getLastTransaction() {
            return lastTransaction;
        }

        public Company getCompany() {
            return company;
        }

        public User getUser() {
            return user;
        }
    }

    public class LastTransaction{
        @SerializedName("type")
        private int type;
        @SerializedName("token_amount")
        private int token_amount;
        @SerializedName("user_id")
        private int user_id;
        @SerializedName("target_user_id")
        private int target_user_id;

        public int getType() {
            return type;
        }

        public int getToken_amount() {
            return token_amount;
        }

        public int getUser_id() {
            return user_id;
        }

        public int getTarget_user_id() {
            return target_user_id;
        }
    }
    public class Company{
        @SerializedName("code")
        private String code;
        @SerializedName("time")
        private String time;

        public String getCode() {
            return code;
        }

        public String getTime() {
            return time;
        }
    }
    public class User{
        @SerializedName("checkin_opened_at")
        private String checkin_opened_at;
        @SerializedName("checkin_closed_at")
        private String checkin_closed_at;
        @SerializedName("has_checked_in")
        private boolean has_checked_in;
        @SerializedName("token_amount")
        private int token_amount;

        public String getCheckin_opened_at() {
            return checkin_opened_at;
        }

        public String getCheckin_closed_at() {
            return checkin_closed_at;
        }

        public boolean isHas_checked_in() {
            return has_checked_in;
        }

        public int getToken_amount() {
            return token_amount;
        }
    }

}
