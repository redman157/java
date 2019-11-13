package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

public class LikeIdea {
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @SerializedName("data")
    private Data data;
    public class Data{
        @SerializedName("id")
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getReactions_count() {
            return reactions_count;
        }

        public void setReactions_count(int reactions_count) {
            this.reactions_count = reactions_count;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public boolean isReacted() {
            return reacted;
        }

        public void setReacted(boolean reacted) {
            this.reacted = reacted;
        }

        @SerializedName("type")
        private int type;
        @SerializedName("content")
        private String content;
        @SerializedName("reactions_count")
        private int reactions_count;
        @SerializedName("reacted")
        private boolean reacted;
        @SerializedName("created_at")
        private String created_at;
        @SerializedName("updated_at")
        private String updated_at;
        @SerializedName("user")
        private User user;
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

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar_path() {
            return avatar_path;
        }

        public void setAvatar_path(String avatar_path) {
            this.avatar_path = avatar_path;
        }

        public String getTeam() {
            return team;
        }

        public void setTeam(String team) {
            this.team = team;
        }
    }
}
