package com.jvit.companycoin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jvit.companycoin.api.ApiService;
import com.jvit.companycoin.object.InfoUserLike;
import com.jvit.companycoin.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class InfoUserLikeAdapter extends RecyclerView.Adapter<InfoUserLikeAdapter.ViewHolder> {
    private Context context;
    private ArrayList<InfoUserLike> likeArrayList;

    public InfoUserLikeAdapter(Context context, ArrayList<InfoUserLike> likeArrayList) {
        this.context = context;
        this.likeArrayList = likeArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_info_idea, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InfoUserLike infoUserLike = likeArrayList.get(position);
        holder.textName.setText(infoUserLike.getName());
        Picasso.get().load(ApiService.url_path+ infoUserLike.getAvata()).into(holder.imgAvatar);
    }

    @Override
    public int getItemCount() {

        return likeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       TextView textName;
       ImageView imgAvatar;
       public ViewHolder(@NonNull View itemView) {
           super(itemView);
           textName = itemView.findViewById(R.id.textNameInfoIdea);
           imgAvatar = itemView.findViewById(R.id.imgAvataInfoIdea);
       }
   }
}
