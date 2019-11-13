package com.example.templatercview.lib_recycleview;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Holder extends RecyclerView.ViewHolder {
    private int _Type;

    public Holder(@NonNull View itemView, int type) {
        super(itemView);
        _Type = type;
    }

    public int getType() {
        return _Type;
    }
}
