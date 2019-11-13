package com.jvit.companycoin.objectbuild;

import com.jvit.companycoin.GiftExchangeBuilder;

public class BuildGiftExchange implements GiftExchangeBuilder {
    private boolean isNew;
    private boolean isRecommend;
    private String giftName;
    private String introGift;
    private String imageGift;
    private int priceGift;
    private int quantity;
    private int id;
    @Override
    public GiftExchangeBuilder isNew(boolean isNew) {
        this.isNew = isNew;
        return this;
    }

    @Override
    public GiftExchangeBuilder isRecommend(boolean isRecommend) {
        this.isRecommend = isRecommend;
        return this;
    }

    @Override
    public GiftExchangeBuilder giftName(String giftName) {
        this.giftName = giftName;
        return this;
    }

    @Override
    public GiftExchangeBuilder introGift(String introGift) {
        this.introGift = introGift;
        return this;
    }

    @Override
    public GiftExchangeBuilder imageGift(String imageGift) {
        this.imageGift = imageGift;
        return this;
    }

    @Override
    public GiftExchangeBuilder priceGift(int priceGift) {
        this.priceGift = priceGift;
        return this;
    }

    @Override
    public GiftExchangeBuilder quantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    @Override
    public GiftExchangeBuilder id(int id) {
        this.id = id;
        return this;
    }

    @Override
    public GiftExchange build() {
        return new GiftExchange(id,giftName, introGift, imageGift, priceGift, quantity, isRecommend, isNew);
    }
}
