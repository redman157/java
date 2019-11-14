package com.example.templatercview;

public class Builder implements DataBuild {
    private float x;
    private float shadowH;
    private float shadowL;
    private float open;
    private float close;
    @Override
    public DataBuild x(float x) {
        this.x = x;
        return this;
    }

    @Override
    public DataBuild shadowH(float shadowH) {
        this.shadowH = shadowH;
        return this;
    }

    @Override
    public DataBuild shadowL(float shadowL) {
        this.shadowL = shadowL;
        return this;
    }

    @Override
    public DataBuild open(float open) {
        this.open = open;
        return this;
    }

    @Override
    public DataBuild close(float close) {
        this.close = close;
        return this;
    }

    @Override
    public Data build() {
        return new Data(x,shadowH,shadowL,open,close);
    }
}
