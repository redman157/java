package com.android.music_player.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.utils.DialogUtils;

import java.util.ArrayList;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {
    private ArrayList<String> mPlayLists;
    private Context mContext;

    public PlayListAdapter(Context context, ArrayList<String> playLists){
        mContext = context;
        mPlayLists = playLists;
    }
    private OnClickItem onClickItem;
    public void OnClickItem(OnClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }
    public interface OnClickItem {
        void onClick(String title);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_play_list_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mPlayLists.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = mPlayLists.get(position);
        holder.addData(title);
        holder.setOnClick(title);
        holder.setOnLongClick(title);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_item_play_list);
        }
        public void addData(String title){
            textView.setText(title);
        }
        public void setOnClick(final String title){
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem.onClick(title);
                }
            });
        }

        public void setOnLongClick(final String title){
            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    DialogUtils.showDeletePlayList(mContext, title);
                    return false;
                }
            });
        }
    }
}
