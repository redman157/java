package com.example.templatercview.design_view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.example.templatercview.R;
import com.example.templatercview.lib_recycleview.Holder;

// frontend
public class UserHolder extends Holder {
    TextView textName, textUser;


    public UserHolder(@NonNull View itemView, int type) {
        super(itemView, type);

        textName = itemView.findViewById(R.id.textName);
        textUser = itemView.findViewById(R.id.textSdt);

    }

    @Override
    public int getType() {
        return 0;
    }
}
