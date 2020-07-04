package com.android.music_player.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.utils.DialogHelper;

import java.util.ArrayList;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {
    private ArrayList<String> mPlayLists;
    private Context mContext;

    public PlayListAdapter(Context context, ArrayList<String> playLists){
        mContext = context;
        mPlayLists = playLists;
    }
    private OnClickItemListener onClickItemListener;
    public void OnClickItem(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }
    public interface OnClickItemListener {
        void onClickAddMusic(String mediaID);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_add_to_play_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mPlayLists.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.addData(mPlayLists.get(position));
        holder.setOnClick(mPlayLists.get(position));
        holder.setOnLongClick(mPlayLists.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView mTextMediaID;
        LinearLayout mLinearAddPlayList;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextMediaID = itemView.findViewById(R.id.text_play_list);
            mLinearAddPlayList = itemView.findViewById(R.id.linear_add_play_list);
        }
        public void addData(String mediaID){
            mTextMediaID.setText(mediaID);
        }
        public void setOnClick(final String mediaID){
            mLinearAddPlayList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItemListener.onClickAddMusic(mediaID);
                }
            });
        }

        public void setOnLongClick(final String mediaID){
            mLinearAddPlayList.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    DialogHelper.showDeletePlayList(mContext, mediaID);
                    return false;
                }
            });
        }
    }
}
