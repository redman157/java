package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InfoLikeUser {
    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public class Data{
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
        @SerializedName("reactions_count")
        private int reactions_count;
        @SerializedName("reacted")
        private boolean reacted;
        @SerializedName("token_amount")
        private int token_amount;
        @SerializedName("created_at")
        private String created_at;
        @SerializedName("updated_at")
        private String updated_at;
        @SerializedName("user")
        private User user;
        @SerializedName("reactions")
        private List<Reactions> reactions;

        public int getId() {
            return id;
        }

        public int getType() {
            return type;
        }

        public String getContent() {
            return content;
        }

        public String getSent_at() {
            return sent_at;
        }

        public int getStatus() {
            return status;
        }

        public int getReactions_count() {
            return reactions_count;
        }

        public boolean isReacted() {
            return reacted;
        }

        public int getToken_amount() {
            return token_amount;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public User getUser() {
            return user;
        }

        public List<Reactions> getReactions() {
            return reactions;
        }
    }
    public class User{
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("avatar_path")
        private String avatar_path;
        @SerializedName("team")
        private String team;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getAvatar_path() {
            return avatar_path;
        }

        public String getTeam() {
            return team;
        }
    }
    public class Reactions{
        @SerializedName("id")
        private int id;
        @SerializedName("user_id")
        private int user_id;
        @SerializedName("user_name")
        private String user_name;
        @SerializedName("user_avatar")
        private String user_avatar;

        public int getId() {
            return id;
        }

        public int getUser_id() {
            return user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public String getUser_avatar() {
            return user_avatar;
        }
    }
}
