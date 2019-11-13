package com.jvit.companycoin;

import com.jvit.companycoin.objectbuild.GiftExchange;

public interface GiftExchangeBuilder {
    GiftExchangeBuilder isNew(boolean isNew);
    GiftExchangeBuilder isRecommend(boolean isRecommend);
    GiftExchangeBuilder giftName(String giftName);
    GiftExchangeBuilder introGift(String introGift);
    GiftExchangeBuilder imageGift(String imageGift);
    GiftExchangeBuilder priceGift(int priceGift);
    GiftExchangeBuilder quantity(int quantity);
    GiftExchangeBuilder id(int id);

    GiftExchange build();
}
