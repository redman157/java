package com.example.templatercview.design_view;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.templatercview.User;
import com.example.templatercview.lib_recycleview.Holder;
import com.example.templatercview.lib_recycleview.Renderer;

import java.util.List;

// backend: Retrofit -> List<User>
public class UserRenderer implements Renderer {
    List<User> _Users;

    public UserRenderer(List<User> users) {
        _Users =  users;
    }

    @Override
    public void onBinding(RecyclerView.ViewHolder holder, int position) {
        UserHolder userHolder = (UserHolder) holder;
        userHolder.textName.setText(_Users.get(position).getName());
        userHolder.textUser.setText(_Users.get(position).getSdt());
    }

    @Override
    public Holder onGenerate(View view) {
        return new UserHolder(view, 0);
    }

    public int onCounting() {
        return _Users.size();
    }
    // frontend


}
