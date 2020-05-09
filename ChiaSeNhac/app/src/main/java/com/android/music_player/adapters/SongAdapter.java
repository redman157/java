package com.android.music_player.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.interfaces.OnClickItem;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ItemViewHolder> {
    private final Context mContext;
    private ArrayList<SongModel> items;
    private String type;
    private SharedPrefsUtils mSharedPrefsUtils;
    private SimpleDateFormat mFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private int limit = 10;
    private boolean isLimit;
    public void setLimit(boolean isLimit){
        this.isLimit = isLimit;
    }
    public SongAdapter(Context context, ArrayList<SongModel> items, String type) {
        this.items = items;
        this.type = type;
        mContext = context;
        mSharedPrefsUtils = new SharedPrefsUtils(context);

    }

    private OnClickItem onClickItem;
    public void setOnClickItem(OnClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }
    public interface OnClickListener {
        void onClick(String type, int position);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recently_add, parent, false);
        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        final SongModel item;

        item = items.get(position);
        holder.set(item);
        holder.mL_Recently_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickItem.onClick(type, position);

                mSharedPrefsUtils.setString(Constants.PREFERENCES.SAVE_ALBUM_ID, items.get(position).getAlbumID());
            }
        });

    }

    @Override
    public int getItemCount() {
        if (isLimit){
            return limit;
        }else {
            return items.size();
        }
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder  {
        private ImageView mImgMusic;
        private TextView mTextName, mTextArtist;
        private TextView mTextTime;
        private ImageButton mBtnMenu;
        private LinearLayout mL_Recently_Add;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mL_Recently_Add = itemView.findViewById(R.id.item_ll_Recently_Add);
            mTextArtist = itemView.findViewById(R.id.item_text_artist_recently_add);
            mImgMusic = itemView.findViewById(R.id.item_img_recently_add);
            mBtnMenu = itemView.findViewById(R.id.item_btn_menu_recently_add);
            mTextName = itemView.findViewById(R.id.item_text_title_recently_add);
            mTextTime = itemView.findViewById(R.id.item_text_time_recently_add);
        }

        public void set(final SongModel song) {
            //UI setting code
            mTextName.setText(song.getSongName());
            mTextArtist.setText(song.getArtist());
            mTextTime.setText(mFormat.format(song.getTime()));
            mImgMusic.setClipToOutline(true);

            ImageUtils.getInstance(mContext).getSmallImageByPicasso(song.getAlbumID(), mImgMusic);

        }
    }

}