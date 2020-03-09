package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

public class ItemUserRank{

    @SerializedName("rank_no")
    public Integer rank_no;

    @SerializedName("name")
    public String name;

    @SerializedName("avatar_path")
    public String avatar_path;

    @SerializedName("token_amount")
    public Integer token_amount;

    @SerializedName("is_token_increase")
    public Integer is_token_increase;

    public Integer getRank_no() {
        return rank_no;
    }

    public void setRank_no(Integer rank_no) {
        this.rank_no = rank_no;
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

    public Integer getToken_amount() {
        return token_amount;
    }

    public void setToken_amount(Integer token_amount) {
        this.token_amount = token_amount;
    }

    public Integer getIs_token_increase() {
        return is_token_increase;
    }

    public void setIs_token_increase(Integer is_token_increase) {
        this.is_token_increase = is_token_increase;
    }
}