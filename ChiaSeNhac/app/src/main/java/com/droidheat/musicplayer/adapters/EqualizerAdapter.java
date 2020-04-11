package com.droidheat.musicplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.droidheat.musicplayer.R;

import java.util.ArrayList;
import java.util.HashMap;

public class EqualizerAdapter extends RecyclerView.Adapter<EqualizerAdapter.ViewHolder> {
    private ArrayList<String> equalizers;
    private Context mContext;
    public EqualizerAdapter( Context context,ArrayList<String> equalizers) {
        this.equalizers = equalizers;
        this.mContext = context;
    }

    private OnClickItemListener onClickItemListener;
    public interface OnClickItemListener{
        void onClickItem(String item, int pos);
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener){
        this.onClickItemListener = onClickItemListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_child_equalizer, parent
                ,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final String item = equalizers.get(position);
        holder.setData(item);

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickItemListener.onClickItem(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return equalizers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_child_equalizer);
        }
        public void setData(String item){
            textView.setText(item);

        }
    }
}
