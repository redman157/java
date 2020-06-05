package com.android.music_player.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.managers.MusicManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.Map;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ItemViewHolder>  {
    private final Activity mActivity;
    private Map<String, ArrayList<SongModel>> mAlbums;
    private SharedPrefsUtils mSharedPrefsUtils;
    public AlbumAdapter(Activity activity,  Map<String, ArrayList<SongModel>> albums) {
        this.mAlbums = albums;
        mActivity = activity;
        mSharedPrefsUtils = new SharedPrefsUtils(mActivity);
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album_line, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        MusicManager.getInstance().setContext(mActivity);
        ArrayList<SongModel> model = new ArrayList<>(MusicLibrary.info);
        final SongModel item = model.get(position);
        ArrayList<SongModel> music = mAlbums.get(item.getAlbum());
        holder.assignData(item, music);

        holder.mLinearAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onClickItemListener.onClickMusic(mMusics.get(position).getSongName());
//                mSharedPrefsUtils.setString(Constants.PREFERENCES.SAVE_ALBUM_ID, mMusics.get(position).getAlbumID());
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder  {
        private ImageView mImgAlbum;
        private TextView mTextAlbum, mTextInfoAlbum;
        private TextView mTextTime;
        private LinearLayout mLinearAlbum;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mLinearAlbum = itemView.findViewById(R.id.item_ll_album);
            mTextInfoAlbum = itemView.findViewById(R.id.item_text_info_album);
            mImgAlbum = itemView.findViewById(R.id.item_img_album);
            mTextAlbum = itemView.findViewById(R.id.item_text_title_album);
        }

        public void assignData(final SongModel song, ArrayList<SongModel> models) {
            //UI setting code
            mTextAlbum.setText(song.getAlbum());
            mTextInfoAlbum.setText(models.size() + " bài hát");
            mImgAlbum.setClipToOutline(true);
            ImageUtils.getInstance(mActivity).getSmallImageByPicasso(song.getAlbumID(), mImgAlbum);
        }
    }
}
