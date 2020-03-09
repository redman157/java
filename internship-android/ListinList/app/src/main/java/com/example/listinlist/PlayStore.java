package com.example.listinlist;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlayStore {
    private String info;
    private ArrayList<InfoApp> listInfo;

    public PlayStore(String info, ArrayList<InfoApp> listInfo) {
        this.info = info;
        this.listInfo = listInfo;
    }

    public String getInfo() {
        return info;
    }

    public ArrayList<InfoApp> getListInfo() {
        return listInfo;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setListInfo(ArrayList<InfoApp> listInfo) {
        this.listInfo = listInfo;
    }
}
