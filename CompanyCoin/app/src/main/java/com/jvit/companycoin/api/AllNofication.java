package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllNofication {
    @SerializedName("data")
    private List<Data> data;
    @SerializedName("meta")
    private Meta meta;

    public Meta getMeta() {
        return meta;
    }

    public List<Data> getData() {
        return data;
    }

    public class Data {
        @SerializedName("id")
        private int id;
        @SerializedName("type")
        private String type;
        @SerializedName("token_amount")
        private int token_amount;

        @SerializedName("data")
        // type 6
        private CustomData customData;

        @SerializedName("read")
        private int read;
        @SerializedName("created_at")
        private String created_at;
        @SerializedName("user")
        private User user;
        @SerializedName("sender")
        private Sender sender;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getToken_amount() {
            return token_amount;
        }


        public CustomData getCustomData() {
            return customData;
        }

        public int getRead() {
            return read;
        }

        public String getCreated_at() {
            return created_at;
        }

        public User getUser() {
            return user;
        }

        public Sender getSender() {
            return sender;
        }

        public void setToken_amount(int token_amount) {
            this.token_amount = token_amount;
        }
    }

    public class Meta{
        @SerializedName("pagination")
        private Pagination pagination;

        public Pagination getPagination() {
            return pagination;
        }
    }
    public class Pagination{
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

        public int getCount() {
            return count;
        }

        public int getPer_page() {
            return per_page;
        }

        public int getCurrent_page() {
            return current_page;
        }

        public int getTotal_pages() {
            return total_pages;
        }

        public Links getLinks() {
            return links;
        }
    }
    public class Links{
        @SerializedName("next")
        private String next;

        public String getNext() {
            return next;
        }
    }



    public class CustomData {
        // type = 6
        @SerializedName("transaction_id")
        private int transaction_id;

        @SerializedName("idea_id")
        private int idea_id;

        @SerializedName("message")
        private String message;

        @SerializedName("reached_point")
        private int reached_point;

        @SerializedName("has_more")
        private boolean has_more;

        @SerializedName("gift_id")
        private int gift_id;

        @SerializedName("gift_name")
        private String gift_name;

        public int getTransaction_id() {
            return transaction_id;
        }

        public String getGift_name() {
            return gift_name;
        }

        public int getIdea_id() {
            return idea_id;
        }

        public int getGift_id() {
            return gift_id;
        }

        public String getMessage() {
            return message;
        }

        public int getReached_point() {
            return reached_point;
        }

        public boolean isHas_more() {
            return has_more;
        }
    }


    public class Sender{
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
    public  class User {
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
}
