package com.jvit.companycoin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jvit.companycoin.api.ApiService;
import com.jvit.companycoin.R;
import com.jvit.companycoin.object.TransactionHistory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<TransactionHistory> historyArrayList;
    private final int VIEW_TYPE_ITEM=0,VIEW_TYPE_LOADING=1;

    public TransactionHistoryAdapter(Context context, ArrayList<TransactionHistory> historyArrayList) {
        this.context = context;
        this.historyArrayList = historyArrayList;
    }
    private class ItemUserHistory extends RecyclerView.ViewHolder{
        // tạo 1 inner class kế thừa từ lớp RecyclerView.ItemUserRank
        TextView textNameSend, textNameReceive, textDate, textCoin, textMessageSend;
        ImageView imgIcon, imgAvataSend, imgAvataReceive;
        public ItemUserHistory(@NonNull View itemView) {
            super(itemView);
            textMessageSend = itemView.findViewById(R.id.textHistoryMessageSend);
            imgIcon = itemView.findViewById(R.id.imgIconHistory);
            imgAvataSend = itemView.findViewById(R.id.imgAvataSendHistory);
            imgAvataReceive = itemView.findViewById(R.id.imgAvataReceiverHistory);
            textNameSend = itemView.findViewById(R.id.textNameSendHistory);
            textNameReceive = itemView.findViewById(R.id.textNameReceiverHistory);
            textDate = itemView.findViewById(R.id.textDateHistory);
            textCoin = itemView.findViewById(R.id.textCoinHistory);
        }
    }
     private class LoadMoreHistory extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadMoreHistory(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBarLoadedPostAll);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_transaction_history, parent, false);
            return new ItemUserHistory(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading_postall, parent, false);

            return new LoadMoreHistory(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemUserHistory) {
            final TransactionHistory transactionHistory = historyArrayList.get(position);
            final ItemUserHistory itemUserHistory = (ItemUserHistory) holder;
            itemUserHistory.imgIcon.setImageResource(transactionHistory.getIconTitle());
            if (!transactionHistory.getAvatarSend().equals("")) {
                itemUserHistory.imgAvataSend.setVisibility(View.VISIBLE);
                if (transactionHistory.getAvatarSend().equals( ApiService.url +"_nuxt/img/a000933.png")){
                    Picasso.get().load( ApiService.url +"_nuxt/img/a000933.png").into(itemUserHistory.imgAvataSend);
                }else {
                    Picasso.get()
                            .load(ApiService.url_path + transactionHistory.getAvatarSend())
                            .fit()
                            .into(itemUserHistory.imgAvataSend);
                }

            } else {
                itemUserHistory.imgAvataSend.setVisibility(View.GONE);
            }
            if (!transactionHistory.getAvatarReceive().equals("")) {
                itemUserHistory.imgAvataReceive.setVisibility(View.VISIBLE);
                if (transactionHistory.getAvatarReceive().equals(ApiService.url +"_nuxt/img/a000933.png")){
                    Picasso.get().load( ApiService.url +"_nuxt/img/a000933.png").into(itemUserHistory.imgAvataSend);
                }else {
                    Picasso.get()
                            .load(ApiService.url_path + transactionHistory.getAvatarReceive())
                            .fit()
                            .into(itemUserHistory.imgAvataReceive);
                }
            } else {
                itemUserHistory.imgAvataReceive.setVisibility(View.GONE);
            }
            if (!transactionHistory.getMessageSend().equals("")) {
                itemUserHistory.textMessageSend.setVisibility(View.VISIBLE);
                itemUserHistory.textMessageSend.setText(transactionHistory.getMessageSend());
            } else {
                itemUserHistory.textMessageSend.setVisibility(View.GONE);
            }
            itemUserHistory.textNameSend.setText(transactionHistory.getNameSend());
            itemUserHistory.textNameReceive.setText(transactionHistory.getNameReceive());
            itemUserHistory.textDate.setText(transactionHistory.getDate());
            itemUserHistory.textCoin.setText(String.valueOf(transactionHistory.getCoin()));
        }else if (holder instanceof LoadMoreHistory) {
            LoadMoreHistory loadMoreHistory = (LoadMoreHistory) holder;
            loadMoreHistory.progressBar.setIndeterminate(true);

        }
    }
    @Override
    public int getItemViewType(int position) {
        return historyArrayList.get(position) == null ? VIEW_TYPE_LOADING:VIEW_TYPE_ITEM;
    }
    @Override
    public int getItemCount() {
        return historyArrayList == null ? 0 : historyArrayList.size();

    }

}
