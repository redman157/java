package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransactionsHistory {
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
    }
    public class Data{
        @SerializedName("id")
        private int id;
        @SerializedName("traded_at")
        private String traded_at;
        @SerializedName("token_amount")
        private int token_amount;
        @SerializedName("type")
        private int type;
        @SerializedName("gift_id")
        private int gift_id;
        @SerializedName("idea_id")
        private int idea_id;
        @SerializedName("status")
        private int status;
        @SerializedName("note")
        private String note;
        @SerializedName("extra_data")
        private int extra_data;
        @SerializedName("created_at")
        private String created_at;
        @SerializedName("updated_at")
        private String updated_at;
        @SerializedName("user")
        private User user;
        @SerializedName("target_user")
        private Target_user target_user;

        public int getId() {
            return id;
        }

        public String getTraded_at() {
            return traded_at;
        }

        public int getToken_amount() {
            return token_amount;
        }

        public int getType() {
            return type;
        }

        public int getGift_id() {
            return gift_id;
        }

        public int getIdea_id() {
            return idea_id;
        }

        public int getStatus() {
            return status;
        }

        public String getNote() {
            return note;
        }

        public int getExtra_data() {
            return extra_data;
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

        public Target_user getTarget_user() {
            return target_user;
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
    public class Target_user{
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
