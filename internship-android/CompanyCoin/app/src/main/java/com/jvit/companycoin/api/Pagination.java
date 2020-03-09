package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

public class Pagination {
    @SerializedName("total")
    private int total;
    @SerializedName("count")
    private int count;
    @SerializedName("current_page")
    private int current_page;
    @SerializedName("per_page")
    private int per_page;
    @SerializedName("total_pages")
    private int total_pages;
    @SerializedName("links")
    private Links links;

    public int getTotal() {
        return total;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
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
