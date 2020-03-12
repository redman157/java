package com.droidheat.musicplayer.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.manager.ImageUtils;
import com.droidheat.musicplayer.manager.SongsManager;
import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;

public class RecentlyAdderAdapter extends RecyclerView.Adapter<RecentlyAdderAdapter.ItemViewHolder> {
    private final Context context;
    private ArrayList<SongModel> items;
    private ArrayList<SongModel> newsongs;
    public RecentlyAdderAdapter(ArrayList<SongModel> items, Context context) {
        this.items = items;
        this.context = context;

    }

    public ArrayList<SongModel> getNewsongs() {
        return newsongs;
    }

    public void setNewsongs(ArrayList<SongModel> newsongs) {
        this.newsongs = newsongs;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recently_add, parent, false);
            return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        SongModel item = newsongs.get(position);

        holder.set(item);
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return 15;
    }



    public class ItemViewHolder extends RecyclerView.ViewHolder  {
        private ImageView img_Music;
        private TextView txt_Name, txt_Artist;
        private TextView txt_Time;
        private Button btn_Menu;

        public ItemViewHolder(View itemView) {
            super(itemView);
            txt_Artist = itemView.findViewById(R.id.item_text_artist_recently_add);
            img_Music = itemView.findViewById(R.id.item_img_recently_add);
            btn_Menu = itemView.findViewById(R.id.item_btn_menu_recently_add);
            txt_Name = itemView.findViewById(R.id.item_text_title_recently_add);
            txt_Time = itemView.findViewById(R.id.item_text_time_recently_add);
        }

        public void set(SongModel song) {
            //UI setting code
            txt_Name.setText(song.getTitle());
            txt_Artist.setText(song.getArtist());
            txt_Time.setText(song.getDuration());
            img_Music.setClipToOutline(true);

            (new ImageUtils(context)).getSmallImageByPicasso(song.getAlbumID(), img_Music);

        }
    }

}