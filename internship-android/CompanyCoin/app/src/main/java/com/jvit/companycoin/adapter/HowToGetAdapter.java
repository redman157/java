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
public class HowToGetAdapter extends RecyclerView.Adapter<HowToGetAdapter.ViewHolder> {
    private Context context;
    private ArrayList<HowToGet> listHowToGet;

    public HowToGetAdapter(Context context, ArrayList<HowToGet> listHowToGet) {
        this.context = context;
        this.listHowToGet = listHowToGet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feature_introduce, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HowToGet howToGet = listHowToGet.get(position);
        holder.imgAvatar.setImageResource(howToGet.getImage());
        holder.textTitle.setText(howToGet.getTitle());
        holder.textDescription.setText(howToGet.getDescription());
    }

    @Override
    public int getItemCount() {
        return listHowToGet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDescription;
        ImageView imgAvatar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgIconIntroduceFeature);
            textTitle = itemView.findViewById(R.id.textTitleIntroduceFeature);
            textDescription = itemView.findViewById(R.id.textinfoIntroduceFeature);

        }
    }
}
