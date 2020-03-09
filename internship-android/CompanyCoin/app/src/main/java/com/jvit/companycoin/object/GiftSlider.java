package com.jvit.companycoin.object;

import java.io.Serializable;

public class GiftSlider implements Serializable {
    private int id;
    private String image;
    private String title;
    private int remain;
    private int exchangeCoin;
    private String description;
    public GiftSlider(int id, String image, String title, int remain,
                      int exchangeCoin, String description) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.remain = remain;
        this.exchangeCoin = exchangeCoin;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getExchangeCoin() {
        return exchangeCoin;
    }

    public void setExchangeCoin(int exchangeCoin) {
        this.exchangeCoin = exchangeCoin;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public int getRemain() {
        return remain;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }
}
