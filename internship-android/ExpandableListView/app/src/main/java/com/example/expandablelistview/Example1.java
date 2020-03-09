package com.example.expandablelistview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class Example1 extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<String> listHeader;
    private HashMap<String, ArrayList<Flag>> listChild;

    public Example1(Context context, ArrayList<String> listHeader, HashMap<String, ArrayList<Flag>> listChild) {
        this.context = context;
        this.listHeader = listHeader;
        this.listChild = listChild;
    }

    @Override
    public int getGroupCount() {
        return listHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listChild.get(listHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listChild.get(listHeader.get(groupPosition)).get(childPosition);
    }

    // TODO set Position group 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // TODO set Position child 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    // TODO set Group View
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {

        String listTitle = (String) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_group_title, null);
        }
        // set isExpanded để thay đổi custom

        initViewGroup(view, listTitle);
        return view;
    }

    private void initViewGroup(View view, Object object) {
        // TODO set init view and set Value view Group
        Flag group = (Flag) object;
    }

    // TODO set Child View
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {

        Flag child = (Flag) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
        }
        // set isExpanded để thay đổi custom
        initViewChild(view, child);

        return view;
    }

    private void initViewChild(View view, Object object) {
        // TODO set init view and set Value view Child
        Flag child = (Flag) object;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}