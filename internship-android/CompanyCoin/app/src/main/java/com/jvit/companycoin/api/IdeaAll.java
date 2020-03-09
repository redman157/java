package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IdeaAll {
    @SerializedName("data")
    private List<InfoIdea> ideaList;
    @SerializedName("meta")
    private Meta meta;

    public List<InfoIdea> getIdeaList() {
        return ideaList;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public void setIdeaList(List<InfoIdea> ideaList) {
        this.ideaList = ideaList;
    }

    public class Meta {
        @SerializedName("pagination")
        private PaginationIdea paginationIdea;

        public PaginationIdea getPaginationIdea() {
            return paginationIdea;
        }

        public void setPaginationIdea(PaginationIdea paginationIdea) {
            this.paginationIdea = paginationIdea;
        }
    }

    public class PaginationIdea{
        @SerializedName("total")
        private int total;
        @SerializedName("count")
        private int count;
        @SerializedName("per_page")
        private int per_page;
        @SerializedName("current_page")
        private int current_page;
        @SerializedName("total_pages")
        private int total_pages;
        @SerializedName("links")
        private Links links;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getPer_page() {
            return per_page;
        }

        public void setPer_page(int per_page) {
            this.per_page = per_page;
        }

        public int getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(int current_page) {
            this.current_page = current_page;
        }

        public int getTotal_pages() {
            return total_pages;
        }

        public void setTotal_pages(int total_pages) {
            this.total_pages = total_pages;
        }

        public Links getLinks() {
            return links;
        }

        public void setLinks(Links links) {
            this.links = links;
        }
    }

    public class Links{
        @SerializedName("next")
        private String next;
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
        private IdeaHome.User user;

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

        public IdeaHome.User getUser() {
            return user;
        }

        public void setUser(IdeaHome.User user) {
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
