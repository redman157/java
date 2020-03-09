package com.jvit.companycoin.object;

import java.io.Serializable;

public class ItemUser implements Serializable, Comparable<ItemUser> {
    private Integer stt;
    private String image;
    private String ten;
    private Integer coin;
    private Integer status;

    public ItemUser(Integer stt, String image, String ten, Integer coin, Integer status) {
        this.stt = stt;
        this.image = image;
        this.ten = ten;
        this.coin = coin;
        this.status = status;
    }

    public int getStt() {
        return stt;
    }

    public void setStt(int stt) {
        this.stt = stt;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public void setStatus(int status) {
        this.status = status;
    }



    public String getImage() {
        return image;
    }

    public String getTen() {
        return ten;
    }

    public int getCoin() {
        return coin;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public int compareTo(ItemUser itemUser) {
        int coincp = ((ItemUser) itemUser).getCoin();
        return coincp-this.coin;
    }
}
