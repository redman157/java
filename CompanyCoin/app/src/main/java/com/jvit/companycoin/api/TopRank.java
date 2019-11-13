package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TopRank {
    @SerializedName("data")
    public List<ItemUserRank> dataTopRank;


    public List<ItemUserRank> getDataTopRank() {
        return dataTopRank;
    }

    public void setDataTopRank(List<ItemUserRank> dataTopRank) {
        this.dataTopRank = dataTopRank;
    }


}