package com.jvit.companycoin.objectbuild;

import java.io.Serializable;

public class GiftExchange implements Serializable {
    private boolean isNew;
    private boolean isRecommend;
    private String giftName;
    private String introGift;
    private String imageGift;
    private int priceGift;
    private int quantity;
    private int id;

    public GiftExchange(int id, String giftName, String introGift, String imageGift, int priceGift, int quantity, boolean isRecommend, boolean isNew) {
        this.id = id;
        this.giftName = giftName;
        this.introGift = introGift;
        this.imageGift = imageGift;
        this.priceGift = priceGift;
        this.quantity = quantity;
        this.isRecommend = isRecommend;
        this.isNew = isNew();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPriceGift() {
        return priceGift;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isRecommend() {
        return isRecommend;
    }

    public void setRecommend(boolean recommend) {
        isRecommend = recommend;
    }

    public void setPriceGift(int priceGift) {
        this.priceGift = priceGift;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public void setIntroGift(String introGift) {
        this.introGift = introGift;
    }

    public void setImageGift(String imageGift) {
        this.imageGift = imageGift;
    }


    public String getGiftName() {
        return giftName;
    }

    public String getIntroGift() {
        return introGift;
    }

    public String getImageGift() {
        return imageGift;
    }


}
