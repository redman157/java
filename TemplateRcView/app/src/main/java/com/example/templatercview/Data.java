package com.example.templatercview;

public class Data  {
    private float x;
    private float shadowH;
    private float shadowL;
    private float open;
    private float close;

    public Data(float x, float shadowH, float shadowL, float open, float close) {
        this.x = x;
        this.shadowH = shadowH;
        this.shadowL = shadowL;
        this.open = open;
        this.close = close;
    }

    public float getX() {
        return x;
    }

    public float getShadowH() {
        return shadowH;
    }

    public float getShadowL() {
        return shadowL;
    }

    public float getOpen() {
        return open;
    }

    public float getClose() {
        return close;
    }
}
