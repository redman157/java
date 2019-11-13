package com.jvit.companycoin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jvit.companycoin.object.ItemUser;
import com.jvit.companycoin.R;
import com.jvit.companycoin.api.ApiService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserRankAdapter extends RecyclerView.Adapter<UserRankAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ItemUser> itemUserRankList;
    private int limit =  5;

    public UserRankAdapter(Context context, ArrayList<ItemUser> itemUserList) {
        this.context = context;
        this.itemUserRankList = itemUserList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemUser itemUser =  itemUserRankList.get(position);
        holder.textStt.setText(String.valueOf(itemUser.getStt()));
        holder.textName.setText(itemUser.getTen());
        holder.textCoin.setText(String.valueOf(itemUser.getCoin()));
        if (itemUser.getImage() == null ){
            Picasso.get().load(ApiService.url +"_nuxt/img/a000933.png").into(holder.imgHinh);
        }else {
            Picasso.get()
                    .load(ApiService.url_path + itemUser.getImage())
                    .fit()
                    .into(holder.imgHinh);
        }
        if (itemUser.getStatus() == 1){
            holder.imgStatus.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
        }else {
            holder.imgStatus.setImageResource(R.drawable.ic_arrow_downward_black_24dp);
        }
    }

    @Override
    public int getItemCount() {
        if (itemUserRankList.size() > limit){
            return limit;
        }else {
            return itemUserRankList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textStt, textName, textCoin;
        ImageView imgHinh, imgStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textStt = itemView.findViewById(R.id.textSttRank);
            textName = itemView.findViewById(R.id.textNameUserRank);
            textCoin = itemView.findViewById(R.id.textCoinUserRank);
            imgHinh = itemView.findViewById(R.id.imgAvataUserRank);
            imgStatus = itemView.findViewById(R.id.imgStatusUserRank);

        }
    }
}
