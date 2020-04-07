package com.droidheat.musicplayer.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.OnMusicChange;
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
    private int mPossitionMusic;
    private SimpleDateFormat format = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private int mOptionMusic;
    private Dialog dialog;
    public MusicAdapter(Context context, Dialog dialog) {
        mContext = context;
        this.dialog = dialog;
        mImageUtils = ImageUtils.getInstance(context);
    }


    public void setPosition(int position){
        mOptionMusic = position;
    }
    public int getPosition(){
        return mOptionMusic;
    }
    public ArrayList<SongModel> getListMusic() {
        return mListMusic;
    }

    public void setListMusic(ArrayList<SongModel> mListMusic) {
        this.mListMusic = mListMusic;
    }

    private OnMusicChange onMusicChange;
    public void setMusicChange(OnMusicChange onMusicChange){
        this.onMusicChange = onMusicChange;
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
    public boolean onFailedToRecycleView(@NonNull ViewHolder holder) {
        return super.onFailedToRecycleView(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        mSongModel = getListMusic().get(position);
        holder.setData(position);
        holder.ll_option_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMusicChange.onChange(position);
                if (dialog.isShowing()){
                    dialog.cancel();
                }
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private LinearLayout ll_option_music;
        private TextView textTime, textTitle, textArtist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_option_music = itemView.findViewById(R.id.ll_item_option_music);
            imageView = itemView.findViewById(R.id.item_music_img_music);
            textTime = itemView.findViewById(R.id.item_music_text_time);
            textTitle = itemView.findViewById(R.id.item_music_text_title);
            textArtist = itemView.findViewById(R.id.item_music_text_artists);
        }

        public void setData(int pos){
            SongModel songModel = getListMusic().get(pos);
            if (mOptionMusic == pos){
//                Log.d("KKK", "setData: "+getListMusic().get(pos).getTitle());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textTitle.setTextColor(mContext.getColor(R.color.red));
                }else {
                    textTitle.setTextColor(Color.parseColor("#FFFF0000"));
                }
            }else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textTitle.setTextColor(mContext.getColor(R.color.white));
                }else {
                    textTitle.setTextColor(Color.parseColor("#FFFFFFFF"));
                }
            }

            mImageUtils.getSmallImageByPicasso(songModel.getAlbumID(), imageView);
            textTime.setText(format.format(songModel.getTime()));
            textArtist.setText(songModel.getArtist());
            textTitle.setText(songModel.getTitle());
        }
    }
}
