package com.android.music_player.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.interfaces.OnClickItemListener;
import com.android.music_player.managers.MusicManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.DialogUtils;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ItemViewHolder> {
    private final Activity mActivity;
    private ArrayList<SongModel> mMusics;
    private SharedPrefsUtils mSharedPrefsUtils;
    private SimpleDateFormat mFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private int limit = 10;
    private boolean isLimit;
    public void setLimit(boolean isLimit){
        this.isLimit = isLimit;
    }
    public MusicAdapter(Activity activity, ArrayList<SongModel> musics) {
        this.mMusics = musics;
        mActivity = activity;
        mSharedPrefsUtils = new SharedPrefsUtils(mActivity);
    }

    private OnClickItemListener onClickItemListener;
    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }
    public interface OnClickListener {
        void onClick(String type, int position);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music_line, parent, false);
        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        final SongModel model;

        model = mMusics.get(position);
        holder.assignData(model);
        MusicManager.getInstance().setContext(mActivity);
        holder.mLinearMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItemListener.onClickMusic(mMusics.get(position).getSongName());
                mSharedPrefsUtils.setString(Constants.PREFERENCES.SAVE_ALBUM_ID, mMusics.get(position).getAlbumID());
            }
        });

        holder.mBtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showSetMusic(mActivity, model.getSongName());
            }
        });

    }

    @Override
    public int getItemCount() {
        if (isLimit){
            return limit;
        }else {
            return mMusics.size();
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder  {
        private ImageView mImgMusic;
        private TextView mTextNameMusic, mTextArtistMusic;
        private TextView mTextTimeMusic;
        private ImageButton mBtnMenu;
        private LinearLayout mLinearMusic;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mLinearMusic = itemView.findViewById(R.id.item_ll_music);
            mTextArtistMusic = itemView.findViewById(R.id.item_text_artist_music);
            mImgMusic = itemView.findViewById(R.id.item_img_music);
            mBtnMenu = itemView.findViewById(R.id.item_btn_music);
            mTextNameMusic = itemView.findViewById(R.id.item_text_title_music);
            mTextTimeMusic = itemView.findViewById(R.id.item_text_time_music);
        }

        public void assignData(final SongModel song) {
            //UI setting code
            mTextNameMusic.setText(song.getSongName());
            mTextArtistMusic.setText(song.getArtist());
            mTextTimeMusic.setText(mFormat.format(song.getTime()));
            mImgMusic.setClipToOutline(true);

            ImageUtils.getInstance(mActivity).getSmallImageByPicasso(song.getAlbumID(), mImgMusic);
        }
    }

}