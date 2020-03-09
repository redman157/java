package com.droidheat.musicplayer.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.utils.SongsUtils;

import java.util.ArrayList;
import java.util.HashMap;

/* renamed from: com.droidheat.musicplayer.ui.adapters.PlaylistFragmentAdapterSimple */
public class PlaylistFragmentAdapterSimple extends BaseAdapter {
    private ArrayList<HashMap<String, String>> data = this.songsUtils.getAllPlayLists();
    private LayoutInflater inflater;
    private SongsUtils songsUtils;

    /* renamed from: com.droidheat.musicplayer.ui.adapters.PlaylistFragmentAdapterSimple$ViewHolder */
    private static class ViewHolder {
        public TextView text;

        private ViewHolder() {
        }
    }

    public Object getItem(int i) {
        return null;
    }

    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("WrongConstant")
    public PlaylistFragmentAdapterSimple(Context context) {
        this.songsUtils = new SongsUtils((Activity) context);
        this.inflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        this.data.clear();
        this.data = this.songsUtils.getAllPlayLists();
    }

    public int getCount() {
        if (this.data.size() <= 0) {
            return 1;
        }
        return this.data.size();
    }

    @SuppressLint("WrongConstant")
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = this.inflater.inflate(R.layout.playlist_row_simple, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.titleTextView);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (this.data.size() <= 0) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
            viewHolder.text.setText((CharSequence) ((HashMap) this.data.get(i)).get("title"));
        }
        return view;
    }
}
