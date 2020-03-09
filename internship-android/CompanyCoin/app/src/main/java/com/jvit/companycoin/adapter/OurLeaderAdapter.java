package com.jvit.companycoin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jvit.companycoin.object.HowToGet;
import com.jvit.companycoin.R;

import java.util.ArrayList;

public class OurLeaderAdapter extends RecyclerView.Adapter<OurLeaderAdapter.ViewHolder> {
    private Context context;
    private ArrayList<HowToGet> ourLeaderList;

    public OurLeaderAdapter(Context context, ArrayList<HowToGet> ourLeaderList) {
        this.context = context;
        this.ourLeaderList = ourLeaderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(context).inflate(R.layout.item_introduce_userleader, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HowToGet howToGet = ourLeaderList.get(position);
        holder.imgAvatarLeader.setImageResource(howToGet.getImage());
        holder.textNameLeader.setText(howToGet.getTitle());
        holder.textJobLeader.setText(howToGet.getDescription());
    }

    @Override
    public int getItemCount() {
        return ourLeaderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNameLeader, textJobLeader;
        ImageView imgAvatarLeader;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textNameLeader = itemView.findViewById(R.id.textNameIntroLeader);
            textJobLeader = itemView.findViewById(R.id.textJobIntroLeader);
            imgAvatarLeader = itemView.findViewById(R.id.imgAvataIntroLeader);
        }
    }


}
