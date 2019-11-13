package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllGiftExchange {
    @SerializedName("all")
    public List<AllGift>  allGifts;
    @SerializedName("new")
    public List<AllGift>  newGifts;
    @SerializedName("recommend")
    public List<AllGift>  recommendGifts;

    public List<AllGift> getAllGifts() {
        return allGifts;
    }

    public List<AllGift> getNewGifts() {
        return newGifts;
    }

    public List<AllGift> getRecommendGifts() {
        return recommendGifts;
    }

    public class AllGift{

        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
        @SerializedName("price")
        public int price;
        @SerializedName("quantity")
        public int quantity;
        @SerializedName("image_path")
        private String image_path;
        @SerializedName("is_recommend")
        private boolean recommend;
        @SerializedName("is_new")
        private boolean news;

        public boolean isNews() {
            return news;
        }

        public void setNews(boolean news) {
            this.news = news;
        }

        public boolean isRecommend() {
            return recommend;
        }

        public void setRecommend(boolean recommend) {
            this.recommend = recommend;
        }

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

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getImage_path() {
            return image_path;
        }

        public void setImage_path(String image_path) {
            this.image_path = image_path;
        }
    }

}
