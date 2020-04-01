package com.droidheat.musicplayer.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.manager.ImageUtils;
import com.droidheat.musicplayer.models.SongModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private Context mContext;
    private SongModel mSongModel;
    private ArrayList<SongModel> mListMusic;
    private ImageUtils mImageUtils;
    private SimpleDateFormat format = new SimpleDateFormat("mm:ss", Locale.getDefault());
    public MusicAdapter(Context context) {
        mContext = context;
        mImageUtils = ImageUtils.getInstance(context);
    }

    public ArrayList<SongModel> getListMusic() {
        return mListMusic;
    }

    public void setListMusic(ArrayList<SongModel> mListMusic) {
        this.mListMusic = mListMusic;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_music, null);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mListMusic.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        notifyDataSetChanged();

        mSongModel = mListMusic.get(position);
        holder.setData(mSongModel);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textTime, textTitle, textArtist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_music_img_music);
            textTime = itemView.findViewById(R.id.item_music_text_time);
            textTitle = itemView.findViewById(R.id.item_music_text_title);
            textArtist = itemView.findViewById(R.id.item_music_text_artists);
        }
        public void setData(SongModel songModel){
            mImageUtils.getBitmapImageByPicasso(songModel.getAlbumID(), imageView);
            textTime.setText(format.format(songModel.getTime()));
            textArtist.setText(songModel.getArtist());
            textTitle.setText(songModel.getTitle());
        }
    }
}
