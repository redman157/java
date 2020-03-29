package com.droidheat.musicplayer.adapters;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.activities.RecentlyAllMusicActivity;
import com.droidheat.musicplayer.manager.ImageUtils;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongsManager;
import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;

public class RecentlyAdderAdapter extends RecyclerView.Adapter<RecentlyAdderAdapter.ItemViewHolder> {
    private final Context context;
    private ArrayList<SongModel> items;
    private String type;
    private SharedPrefsManager prefsManager;
    public RecentlyAdderAdapter(Context context, ArrayList<SongModel> items, String type) {
        this.items = items;
        this.type = type;
        this.context = context;
        prefsManager = new SharedPrefsManager();
        prefsManager.setContext(context);
    }

    private OnClickItem onClickItem;
    public void OnClickItem(OnClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }
    public interface OnClickItem {
        void onClick(String type, int index);
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

                prefsManager.setString(Constants.PREFERENCES.SaveAlbumID, items.get(position).getAlbumID());
            }
        });

    }

    @Override
    public int getItemCount() {
        if (type.equals(Constants.VALUE.ALL_NEW_SONGS)){
            return items.size();
        }else {
            if (SongsManager.getInstance().queue().size() < 15){
                return SongsManager.getInstance().queue().size();
            }else {
                return 15;
            }
        }
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder  {
        private ImageView mImgMusic;
        private TextView mTextName, mTextArtist;
        private TextView mTextTime;
        private Button mBtnMenu;
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
            mTextName.setText(song.getTitle());
            mTextArtist.setText(song.getArtist());
            mTextTime.setText(song.getDuration());
            mImgMusic.setClipToOutline(true);

            ImageUtils.getInstance(context).getSmallImageByPicasso(song.getAlbumID(), mImgMusic);

        }
    }

}