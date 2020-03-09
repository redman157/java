package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllRank {
    @SerializedName("data")
    public List<ItemUserRank> dataAllRank;
    @SerializedName("meta")
    public PaginationResponse meta;

    public List<ItemUserRank> getDataAllRank() {
        return dataAllRank;
    }

    public PaginationResponse getMeta() {
        return meta;
    }

    public void setMeta(PaginationResponse meta) {
        this.meta = meta;
    }

    public void setDataAllRank(List<ItemUserRank> dataAllRank) {
        this.dataAllRank = dataAllRank;
    }


    public class PaginationResponse{
        @SerializedName("pagination")
        private Pagination pagination;

        public Pagination getPagination() {
            return pagination;
        }

        public void setPagination(Pagination pagination) {
            this.pagination = pagination;
        }
    }
}
