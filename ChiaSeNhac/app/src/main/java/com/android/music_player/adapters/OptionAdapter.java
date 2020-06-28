package com.android.music_player.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.models.OptionItem;
import com.android.music_player.R;

import java.util.ArrayList;

public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.ViewHolder>  {
    private Context context;
    private ArrayList<OptionItem> optionItems;
    private OnClickListener onClickItemListener;
    public OptionAdapter(Context context, ArrayList<OptionItem> optionItems){
        this.optionItems = optionItems;
        this.context = context;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickItemListener = onClickListener;
    }
    public interface OnClickListener {
        void onClick(int position);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_option_choose, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return optionItems.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        OptionItem optionItem = optionItems.get(position);
        holder.setLine(optionItem);
        holder.line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItemListener.onClick(position);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textTitle;
        private LinearLayout line;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_choose_library);
            textTitle = itemView.findViewById(R.id.text_choose_library);
            line = itemView.findViewById(R.id.line_chose_library);
        }
        public void setLine(OptionItem optionItem){
            imageView.setImageResource(optionItem.getIcon());
            textTitle.setText(optionItem.getTitle());
        }


    }

}
