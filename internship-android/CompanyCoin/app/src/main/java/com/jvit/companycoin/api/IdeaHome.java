package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IdeaHome {
    @SerializedName("data")
    private List<InfoIdea> ideaList;

    public List<InfoIdea> getIdeaList() {
        return ideaList;
    }

    public void setIdeaList(List<InfoIdea> ideaList) {
        this.ideaList = ideaList;
    }

    public class InfoIdea {
        @SerializedName("id")
        private int id;

        @SerializedName("type")
        private int type;

        @SerializedName("content")
        private String content;

        @SerializedName("has_more")
        private boolean has_more;

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

        @SerializedName("token_amount")
        private int token_amount;

        @SerializedName("created_at")
        private String created_at;

        @SerializedName("updated_at")
        private String updated_at;

        @SerializedName("user")
        private User user;

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

        public boolean getHas_more() {
            return has_more;
        }

        public void setHas_more(boolean has_more) {
            this.has_more = has_more;
        }

        public String getSent_at() {
            return sent_at;
        }

        public void setSent_at(String sent_at) {
            this.sent_at = sent_at;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getFeedbacked_at() {
            return feedbacked_at;
        }

        public void setFeedbacked_at(String feedbacked_at) {
            this.feedbacked_at = feedbacked_at;
        }

        public int getReactions_count() {
            return reactions_count;
        }

        public void setReactions_count(int reactions_count) {
            this.reactions_count = reactions_count;
        }

        public boolean getReacted() {
            return reacted;
        }

        public void setReacted(boolean reacted) {
            this.reacted = reacted;
        }

        public int getToken_amount() {
            return token_amount;
        }

        public void setToken_amount(int token_amount) {
            this.token_amount = token_amount;
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
    }

    public class User {
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
