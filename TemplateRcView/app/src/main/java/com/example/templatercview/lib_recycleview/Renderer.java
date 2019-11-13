package com.example.templatercview.lib_recycleview;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public interface Renderer {

    void onBinding(RecyclerView.ViewHolder holder, int position);
    Holder onGenerate(View view);
    int onCounting();
}
