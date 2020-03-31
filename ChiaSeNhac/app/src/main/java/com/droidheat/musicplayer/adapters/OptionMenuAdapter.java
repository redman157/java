package com.droidheat.musicplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.droidheat.musicplayer.R;

import java.util.ArrayList;

public class OptionMenuAdapter extends RecyclerView.Adapter<OptionMenuAdapter.ViewHolder> {
    private final Context context;
    private ArrayList<String> items;
    private OnClickItem onClickItem;
    public void OnClickItemMenu(OnClickItem onClickItem){
        this.onClickItem = onClickItem;
    }
    public interface OnClickItem {
        void onClickItemMenu(String item);
    }
    public OptionMenuAdapter(ArrayList<String> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_option_menu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = items.get(position);
        holder.set(item);
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textItem;
        public ViewHolder(View itemView) {
            super(itemView);
            textItem = itemView.findViewById(R.id.text_Title_Menu);
        }

        public void set(final String item) {
            //UI setting code
            textItem.setText(item);
            textItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem.onClickItemMenu(item);
                }
            });
        }
    }
}