package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

public class SendComment {
    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public class Data{
        @SerializedName("token")
        public int token;
        @SerializedName("idea")
        public Idea idea;

        public int getToken() {
            return token;
        }

        public Idea getIdea() {
            return idea;
        }
    }
    public class Idea {
        @SerializedName("id")
        private int id;
        @SerializedName("type")
        private int type;
        @SerializedName("content")
        private String content;
        @SerializedName("sent_at")
        private String sent_at;
        @SerializedName("status")
        private int status;
        @SerializedName("feedbacked_at")
        private String feedbacked_at;
        @SerializedName("reactions_count")
        private int reactions_count;
        @SerializedName("reacted")
        private boolean reacted;
        @SerializedName("created_at")
        private String created_at;
        @SerializedName("updated_at")
        private String updated_at;
        @SerializedName("token")
        private int token_amount;
    }
}
