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

import com.android.music_player.R;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.models.OptionItem;
import com.android.music_player.utils.BottomSheetHelper;

import java.util.ArrayList;

public class SelectOptionsAdapter extends RecyclerView.Adapter<SelectOptionsAdapter.ViewHolder> {
    private ArrayList<OptionItem> mOptionItems;
    private Context context;
    private MediaManager mMediaManager;
    private BottomSheetHelper bottomSheetHelper;
    public SelectOptionsAdapter(Context context, ArrayList<OptionItem> optionItems) {
        this.context = context;
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(context);
        mOptionItems = optionItems;
    }

    private OnClickItemListener onClickItemListener;

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    public interface OnClickItemListener{
        void onClick(int pos);
    }

    public static ArrayList<OptionItem> initData(){
        ArrayList<OptionItem> mOptionItems = new ArrayList<>();
        mOptionItems.add(new OptionItem(R.drawable.app_heart, "Thêm vào mục ưa thích"));
        mOptionItems.add(new OptionItem(R.drawable.app_playlist, "Thêm vào danh sách phát"));
        mOptionItems.add(new OptionItem(R.drawable.ic_library_white_24dp, "Phát bài kế tiếp"));
        mOptionItems.add(new OptionItem(R.drawable.ic_musician, "Xem nghệ sĩ"));
        mOptionItems.add(new OptionItem(R.drawable.ic_album, "Xem album"));
        mOptionItems.add(new OptionItem(R.drawable.app_heart, "Đặt làm nhạc chuông"));
        mOptionItems.add(new OptionItem(R.drawable.ic_phone, "Tạo nhạc chuông"));
        mOptionItems.add(new OptionItem(R.drawable.app_heart, "Xóa"));
        return mOptionItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_selection_music, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        OptionItem optionItem = mOptionItems.get(position);
        holder.setData(optionItem);
        holder.mLinearOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItemListener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOptionItems.size();
    }


    protected class ViewHolder extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView title;
        LinearLayout mLinearOptions;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLinearOptions = itemView.findViewById(R.id.line_select_music);
            icon = itemView.findViewById(R.id.img_choose_music);
            title = itemView.findViewById(R.id.text_choose_music);
        }

        public void setData(OptionItem optionItem){
            icon.setImageDrawable(context.getResources().getDrawable(optionItem.getIcon()));
            title.setText(optionItem.getTitle());
        }
    }
}
