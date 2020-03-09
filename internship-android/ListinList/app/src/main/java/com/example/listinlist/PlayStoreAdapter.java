package com.example.listinlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlayStoreAdapter  extends RecyclerView.Adapter<PlayStoreAdapter.ViewHolder> {
    private Context context;
    private ArrayList<PlayStore> playStores;
    private RecyclerView.LayoutManager layoutManager;


    public PlayStoreAdapter(Context context, ArrayList<PlayStore> playStores) {
        this.context = context;
        this.playStores = playStores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.playstrore_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PlayStore playStore = playStores.get(position);
        holder.textApp.setText(playStore.getInfo());
        ArrayList list = playStore.getListInfo();
        InfoAppAdapter appAdapter = new InfoAppAdapter(context, list);

        holder.rcItem.setHasFixedSize(true);
        holder.rcItem.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.rcItem.setAdapter(appAdapter);
        holder.rcItem.setNestedScrollingEnabled(false);

    }

    @Override
    public int getItemCount() {
        return playStores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textApp;
        RecyclerView rcItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textApp = itemView.findViewById(R.id.textInfoApp);
            rcItem = itemView.findViewById(R.id.rcItem);
        }
    }
}
