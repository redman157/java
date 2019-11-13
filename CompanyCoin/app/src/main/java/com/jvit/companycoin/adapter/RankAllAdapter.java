package com.jvit.companycoin.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jvit.companycoin.object.ItemUser;
import com.jvit.companycoin.R;
import com.jvit.companycoin.api.ApiService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RankAllAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Activity context;
    private ArrayList<ItemUser> itemUserList;
    private final int VIEW_TYPE_ITEM=0,VIEW_TYPE_LOADING=1;

    private class ItemUserRank extends RecyclerView.ViewHolder{
        // tạo 1 inner class kế thừa từ lớp RecyclerView.ItemUserRank
        TextView textStt, textName, textCoin;
        ImageView imgHinh, imgStatus;
        ItemUserRank(@NonNull View itemView) {
            super(itemView);
            textStt = itemView.findViewById(R.id.textSttRank);
            textName = itemView.findViewById(R.id.textNameUserRank);
            textCoin = itemView.findViewById(R.id.textCoinUserRank);
            imgHinh = itemView.findViewById(R.id.imgAvataUserRank);
            imgStatus = itemView.findViewById(R.id.imgStatusUserRank);

        }
    }
    private class LoadMoreUserRank extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadMoreUserRank(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBarLoadedRank);
        }
    }

    public RankAllAdapter( Activity context, ArrayList<ItemUser> itemUserList) {
        this.context = context;
        this.itemUserList = itemUserList;
    }

    @Override
    public int getItemViewType(int position) {
        return itemUserList.get(position) == null ? VIEW_TYPE_LOADING:VIEW_TYPE_ITEM;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_home_user, parent, false);

            return new ItemUserRank(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading_rankall, parent, false);

            return new LoadMoreUserRank(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemUserRank){
            ItemUser itemUser =  itemUserList.get(position);
            ItemUserRank itemUserRank = (ItemUserRank) holder;
            itemUserRank.textStt.setText(String.valueOf(itemUser.getStt()));
            itemUserRank.textName.setText(itemUser.getTen());
            itemUserRank.textCoin.setText(String.valueOf(itemUser.getCoin()));
            if (itemUser.getImage() == null){
                Picasso.get()
                        .load(ApiService.url +"_nuxt/img/a000933.png")
                        .fit()
                        .into(itemUserRank.imgHinh);
            }else {
                Picasso.get()
                        .load(ApiService.url_path + itemUser.getImage())
                        .fit()
                        .into(itemUserRank.imgHinh);
            }

            if (itemUser.getStatus() == 1){
                itemUserRank.imgStatus.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
            }else {
                itemUserRank.imgStatus.setImageResource(R.drawable.ic_arrow_downward_black_24dp);
            }
        }else if (holder instanceof LoadMoreUserRank){
            LoadMoreUserRank loadMoreUserRank = (LoadMoreUserRank) holder;
            loadMoreUserRank.progressBar.setIndeterminate(true);
        }
    }


    @Override
    public int getItemCount() {
        return itemUserList == null ? 0 : itemUserList.size();
    }

}
