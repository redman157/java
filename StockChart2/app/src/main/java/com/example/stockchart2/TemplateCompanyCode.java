package com.example.stockchart2;

public abstract class TemplateCompanyCode {
    abstract void addAdapter();
    abstract void addRender();
    abstract void addList();
    abstract void showView();

    public final void show(){
        addAdapter();
        addList();
        addRender();
        showView();
    }

}
