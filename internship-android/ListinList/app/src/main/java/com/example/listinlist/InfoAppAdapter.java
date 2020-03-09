package com.example.listinlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InfoAppAdapter extends  RecyclerView.Adapter<InfoAppAdapter.ViewHolder> {

    private Context context;
    private ArrayList<InfoApp> listAppInfo;

    public InfoAppAdapter(Context context, ArrayList<InfoApp> listAppInfo) {
        this.context = context;
        this.listAppInfo = listAppInfo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.infoapp_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InfoApp infoApp = listAppInfo.get(position);
        holder.imgHinh.setImageResource(infoApp.getHinh());
        holder.textInfo.setText(infoApp.getTenApp());
        holder.textRatting.setText(infoApp.getRating());
    }

    @Override
    public int getItemCount() {
        return listAppInfo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textRatting, textInfo;
        ImageView imgHinh;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textRatting = itemView.findViewById(R.id.numRatting);
            textInfo = itemView.findViewById(R.id.nameAppItem);
            imgHinh = itemView.findViewById(R.id.imgItem);
        }
    }
}
