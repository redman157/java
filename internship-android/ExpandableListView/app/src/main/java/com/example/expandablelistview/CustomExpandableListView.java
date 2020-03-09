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

public class CustomExpandableListView extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<String> listHeader;
    private HashMap<String, ArrayList<Flag>> listChild;

    public CustomExpandableListView(Context context,
                                    ArrayList<String> listHeader,
                                    HashMap<String, ArrayList<Flag>> listChild) {
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
        ImageView imageView = view.findViewById(R.id.imgselection);
        if (isExpanded){
            imageView.setImageResource(R.drawable.up);
        }else {
            imageView.setImageResource(R.drawable.bottm);
        }
        TextView textListTitle = view.findViewById(R.id.listTitle);
        textListTitle.setTypeface(null, Typeface.BOLD);
        textListTitle.setText(listTitle);
        return view;
    }

    // TODO set Child View
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {

        Flag flag = (Flag) getChild(groupPosition,childPosition);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
        }


        TextView textView = view.findViewById(R.id.textName);
        textView.setText(flag.getTenNuoc());
        ImageView imageView = view.findViewById(R.id.imgFlag);
        imageView.setImageResource(flag.getHinh());

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
