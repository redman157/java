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

import com.android.music_player.OptionItem;
import com.android.music_player.R;

import java.util.ArrayList;

public class SelectMusicAdapter extends RecyclerView.Adapter<SelectMusicAdapter.ViewHolder> {
    private ArrayList<OptionItem> optionItems;
    private Context context;
    public SelectMusicAdapter(Context context) {
        this.context = context;
        initData();
    }

    private void initData(){
        optionItems = new ArrayList<>();
        optionItems.add(new OptionItem(R.drawable.app_heart, "Thêm vào mục ưa thích"));
        optionItems.add(new OptionItem(R.drawable.app_playlist, "Thêm vào danh sách phát"));
        optionItems.add(new OptionItem(R.drawable.ic_library_white_24dp, "Phát bài kế tiếp"));
        optionItems.add(new OptionItem(R.drawable.app_heart, "Xem nghệ sĩ"));
        optionItems.add(new OptionItem(R.drawable.app_heart, "Xem album"));
        optionItems.add(new OptionItem(R.drawable.app_heart, "Đặt làm nhạc chuông"));
        optionItems.add(new OptionItem(R.drawable.app_heart, "Tạo nhạc chuông"));
        optionItems.add(new OptionItem(R.drawable.app_heart, "Thông tin bài hát"));
        optionItems.add(new OptionItem(R.drawable.app_heart, "Xóa"));
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_selection_music, parent,
                false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OptionItem optionItem = optionItems.get(position);
        holder.setData(optionItem);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return optionItems.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView title;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.line_select_music);
            icon = itemView.findViewById(R.id.img_choose_music);
            title = itemView.findViewById(R.id.text_choose_music);
        }

        public void setData(OptionItem optionItem){
            icon.setImageDrawable(context.getResources().getDrawable(optionItem.getIcon()));
            title.setText(optionItem.getTitle());
        }
    }
}
