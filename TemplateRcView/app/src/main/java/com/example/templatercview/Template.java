package com.example.templatercview;

public abstract class Template {

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
