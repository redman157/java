package com.jvit.companycoin.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewGiftExchange {
    @SerializedName("data")
    private List<Gift> newGiftList;

    public List<Gift> ListGift() {
        return newGiftList;
    }

    public class Gift {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("price")
        private int price;

        @SerializedName("quantity")
        private int quantity;

        @SerializedName("status")
        private int status;

        @SerializedName("image_path")
        private String image_path;

        @SerializedName("created_at")
        private String created_at;

        @SerializedName("updated_at")
        private String updated_at;

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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getImage_path() {
            return image_path;
        }

        public void setImage_path(String image_path) {
            this.image_path = image_path;
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
    }
}
