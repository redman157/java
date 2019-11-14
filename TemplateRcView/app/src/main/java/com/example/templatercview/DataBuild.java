package com.example.templatercview;

public interface DataBuild  {
    DataBuild x(float x);
    DataBuild shadowH(float shadowH);
    DataBuild shadowL(float shadowL);
    DataBuild open(float open);
    DataBuild close(float close);
    Data build();
}
