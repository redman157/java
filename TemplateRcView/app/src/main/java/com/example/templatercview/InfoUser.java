package com.example.templatercview;

import android.content.Context;

import com.example.templatercview.design_view.UserRenderer;
import com.example.templatercview.lib_recycleview.Adapter;

import java.util.ArrayList;


public class InfoUser extends Template {
    private Context context;
    private Adapter adapter;
    private ArrayList<User> listUser;

    public InfoUser(Context context) {
        this.context = context;
    }

    @Override
    void addAdapter() {
        if (adapter == null){
            adapter = new Adapter(context);
        }
    }

    @Override
    void addRender() {
        if (adapter != null){
            adapter.addRenderer(0, new UserRenderer(listUser));
            adapter.addLayout(0, R.layout.item_info);
        }
    }

    @Override
    void addList() {
        if (listUser == null){
            listUser = new ArrayList<>();
        }
        listUser = new ArrayList<>();
        listUser.add(new User("hello", "phamson"));
        listUser.add(new User("hello", "phamson"));
        listUser.add(new User("hello", "phamson"));
        listUser.add(new User("hello", "phamson"));
    }

    @Override
    void showView() {
        if (MainActivity.recyclerView== null){
            return;
        }
        MainActivity.recyclerView.setAdapter(adapter);
        MainActivity.recyclerView.setLayoutManager(MainActivity.layoutManager);
    }
}
